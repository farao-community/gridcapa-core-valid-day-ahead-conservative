/*
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.model;

import _351.iec62325.tc57wg16._451_n.reportinginformationdocument._2._1.SeriesPeriod;
import _351.iec62325.tc57wg16._451_n.reportinginformationdocument._2._1.TimeSeries;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils.errorGettingStart;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.DateTimeUtils.getIntervalStart;

public class CoreNetPositions {
    private static final String FRENCH_CORE_HUB_NAME = "FR-CORE";

    private final OffsetDateTime targetDate;
    private final Map<String, List<Point>> netPositionsByHub;

    public CoreNetPositions(final OffsetDateTime targetDate) {
        this.targetDate = targetDate;
        this.netPositionsByHub = new HashMap<>();
    }

    public List<Point> getFrenchNetPosition() {
        return getNetPositionsOf(FRENCH_CORE_HUB_NAME);
    }

    public List<Point> getNetPositionsOf(final String hubName) {
        return netPositionsByHub.getOrDefault(hubName, new ArrayList<>());
    }

    public void put(final TimeSeries timeSeries) {
        final SeriesPeriod period = timeSeries
            .getPeriod()
            .stream()
            .filter(p -> targetDate.isEqual(getIntervalStart(p.getTimeInterval())))
            .findFirst() // there should be only one
            .orElseThrow(errorGettingStart());

        final List<Point> netPositions = period
            .getPoint()
            .stream()
            .map(p -> new Point(p.getPosition(), p.getQuantity()))
            .toList();

        netPositionsByHub.put(timeSeries.getMRID(), netPositions);
    }
}
