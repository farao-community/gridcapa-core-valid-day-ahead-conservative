/*
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.gridcapa_core_valid_day_ahead_conservative.xsd.f230.ESMPDateTimeInterval;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public final class DateTimeUtils {
    private DateTimeUtils() {
        // utility class
    }

    public static OffsetDateTime getIntervalStart(final ESMPDateTimeInterval dateTimeInterval) {
        return OffsetDateTime.parse(dateTimeInterval.getStart(), ISO_DATE_TIME);
    }

    /**
     *
     * @param targetDateTime Process target date time
     * @param intervalStart Net position forecast interval start
     * @return The position in the net position forecast file of the target date time (starts at one and not zero)
     */
    public static int getPositionInTimeSeries(final OffsetDateTime targetDateTime, final OffsetDateTime intervalStart) {
        final OffsetDateTime targetUtc = targetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
        final OffsetDateTime intervalStartUtc = intervalStart.withOffsetSameInstant(ZoneOffset.UTC);
        return Math.toIntExact(1 + Duration.between(intervalStartUtc, targetUtc).toHours());
    }

    public static Supplier<CoreValidD2ConservativeInvalidDataException> intervalStartExceptionSupplier() {
        return () -> new CoreValidD2ConservativeInvalidDataException("Could not get start from interval");
    }
}
