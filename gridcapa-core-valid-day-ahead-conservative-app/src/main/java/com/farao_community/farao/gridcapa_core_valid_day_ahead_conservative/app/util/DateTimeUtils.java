/*
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import _351.iec62325.tc57wg16._451_n.reportinginformationdocument._2._1.ESMPDateTimeInterval;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;

import java.time.OffsetDateTime;
import java.util.function.Supplier;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public final class DateTimeUtils {
    private DateTimeUtils() {
        // utility class
    }

    public static OffsetDateTime getIntervalStart(final ESMPDateTimeInterval dateTimeInterval) {
        return OffsetDateTime.parse(dateTimeInterval.getStart(), ISO_DATE_TIME);
    }

    public static Supplier<CoreValidD2ConservativeInvalidDataException> errorGettingStart() {
        return () -> new CoreValidD2ConservativeInvalidDataException("Could not get start from interval");
    }
}
