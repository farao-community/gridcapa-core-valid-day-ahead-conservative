/*
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.model;

import _351.iec62325.tc57wg16._451_n.reportinginformationdocument._2._1.SeriesPeriod;
import _351.iec62325.tc57wg16._451_n.reportinginformationdocument._2._1.TimeSeries;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils.errorGettingStart;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils.getIntervalStart;

public class FrenchCoreNetPositions {
    private static final String FRENCH_CODE = "FR";

    private final OffsetDateTime targetDate;
    private final Map<String, List<Point>> netPositionsToFranceByCode;
    private final Map<String, List<Point>> netPositionsFromFranceByCode;

    public FrenchCoreNetPositions(final OffsetDateTime targetDate) {
        this.targetDate = targetDate;
        this.netPositionsToFranceByCode = new HashMap<>();
        this.netPositionsFromFranceByCode = new HashMap<>();
    }

    public void putIfFrench(final TimeSeries timeSeries) {
        final String[] splitMrid = timeSeries.getMRID().split("-");

        if (splitMrid.length != 2) {
            return;
        }

        final String from = splitMrid[0];
        final String to = splitMrid[1];

        if (FRENCH_CODE.equals(from) && FRENCH_CODE.equals(to)) {
            throw new CoreValidD2ConservativeInvalidDataException("FR-FR not expected in NPF timeseries");
        } else if (FRENCH_CODE.equals(from)) {
            netPositionsFromFranceByCode.put(to, extractNetPositionsFromTimeSeries(timeSeries));
        } else if (FRENCH_CODE.equals(to)) {
            netPositionsToFranceByCode.put(from, extractNetPositionsFromTimeSeries(timeSeries));
        }
    }

    public List<Point> getNetPositionFromFranceTo(final String zoneCode) {
        return netPositionsFromFranceByCode.getOrDefault(zoneCode, new ArrayList<>());
    }

    public List<Point> getNetPositionToFranceFrom(final String zoneCode) {
        return netPositionsToFranceByCode.getOrDefault(zoneCode, new ArrayList<>());
    }

    private List<Point> extractNetPositionsFromTimeSeries(final TimeSeries timeSeries) {
        return timeSeries
            .getPeriod()
            .stream()
            .filter(p -> targetDate.isEqual(getIntervalStart(p.getTimeInterval())))
            .findFirst()// there should be only one
            .map(SeriesPeriod::getPoint)
            .orElseThrow(errorGettingStart())
            .stream()
            .map(p -> new Point(p.getPosition(), p.getQuantity()))
            .toList();

    }
}
