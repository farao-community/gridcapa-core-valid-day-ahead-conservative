/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils;
import com.farao_community.gridcapa_core_valid_day_ahead_conservative.xsd.f230.ReportingInformationMarketDocument;
import com.farao_community.gridcapa_core_valid_day_ahead_conservative.xsd.f230.SeriesPeriod;
import com.farao_community.gridcapa_core_valid_day_ahead_conservative.xsd.f230.TimeSeries;
import com.farao_community.gridcapa_core_valid_day_ahead_conservative.xsd.f230.Point;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.FRENCH_FORECAST_CODE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.FRENCH_FORECAST_WITH_AHC_CODE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils.intervalStartExceptionSupplier;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils.getIntervalStart;
import static javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES;
import static javax.xml.stream.XMLInputFactory.SUPPORT_DTD;

public final class NetPositionsFileImporter {
    private NetPositionsFileImporter() {
        // utility class
    }

    public static List<Point> getFrenchCoreNetPositions(final InputStream inputStream, final boolean withAhc) {
        final ReportingInformationMarketDocument npf = importNetPositionsForecast(inputStream);
        final String expectedMrid = withAhc ? FRENCH_FORECAST_WITH_AHC_CODE : FRENCH_FORECAST_CODE;
        final Optional<TimeSeries> frenchTimeSerie = npf.getTimeSeries()
            .stream()
            .filter(timeSeries -> expectedMrid.equals(timeSeries.getMRID()))
            .findFirst(); // there should be only one

        if (frenchTimeSerie.isPresent()) {
            return extractNetPositions(getDocumentStart(npf), frenchTimeSerie.get());
        } else {
            return new ArrayList<>();
        }
    }

    private static List<Point> extractNetPositions(final OffsetDateTime targetDate, final TimeSeries timeSeries) {
        return timeSeries
            .getPeriod()
            .stream()
            .filter(p -> targetDate.isEqual(getIntervalStart(p.getTimeInterval())))
            .findFirst()// there should be only one
            .map(SeriesPeriod::getPoint)
            .orElseThrow(intervalStartExceptionSupplier());

    }

    private static OffsetDateTime getDocumentStart(final ReportingInformationMarketDocument document) {
        return Optional.ofNullable(document.getTimePeriodTimeInterval())
            .map(DateTimeUtils::getIntervalStart)
            .orElseThrow(intervalStartExceptionSupplier());
    }

    private static ReportingInformationMarketDocument importNetPositionsForecast(final InputStream inputStream) {
        try {
            final Class<ReportingInformationMarketDocument> documentClass = ReportingInformationMarketDocument.class;
            final Unmarshaller unmarshaller = JAXBContext.newInstance(documentClass).createUnmarshaller();
            final XMLInputFactory xif = XMLInputFactory.newFactory();
            xif.setProperty(SUPPORT_DTD, false);
            xif.setProperty(IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            final XMLStreamReader xsr = xif.createXMLStreamReader(inputStream);
            return unmarshaller.unmarshal(xsr, documentClass).getValue();
        } catch (final Exception e) {
            throw new CoreValidD2ConservativeInvalidDataException("Cannot unmarshal ReportingInformationMarketDocument", e);
        }
    }

}
