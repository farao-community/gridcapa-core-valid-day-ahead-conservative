package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import _351.iec62325.tc57wg16._451_n.reportinginformationdocument._2._1.ReportingInformationMarketDocument;
import _351.iec62325.tc57wg16._451_n.reportinginformationdocument._2._1.TimeSeries;
/*
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.model.CoreNetPositions;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils;
import jakarta.xml.bind.JAXBContext;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class NetPositionsFileImporter {
    private NetPositionsFileImporter() {
        // utility class
    }

    public static CoreNetPositions getCoreNetPositions(final InputStream inputStream, final List<CoreHub> coreHubs) {
        final List<String> hubNames = coreHubs.stream().map(CoreHub::name).toList();
        final ReportingInformationMarketDocument npf = importNetPositionsForecast(inputStream);
        final CoreNetPositions netPositions = new CoreNetPositions(getDocumentStart(npf));
        final Predicate<TimeSeries> isHubTimeSeries = ts -> hubNames.contains(ts.getMRID());

        npf.getTimeSeries()
            .stream()
            .filter(isHubTimeSeries)
            .forEach(netPositions::put);

        return netPositions;
    }

    private static OffsetDateTime getDocumentStart(final ReportingInformationMarketDocument document) {
        return Optional.ofNullable(document.getTimePeriodTimeInterval())
            .map(DateTimeUtils::getIntervalStart)
            .orElseThrow();
    }

    private static ReportingInformationMarketDocument importNetPositionsForecast(final InputStream inputStream) {
        try {
            final Class<ReportingInformationMarketDocument> documentClass = ReportingInformationMarketDocument.class;
            return JAXBContext
                .newInstance(documentClass)
                .createUnmarshaller()
                .unmarshal(new StreamSource(inputStream), documentClass)
                .getValue();
        } catch (final Exception e) {
            throw new CoreValidD2ConservativeInvalidDataException("Cannot unmarshal ReportingInformationMarketDocument", e);
        }
    }

}
