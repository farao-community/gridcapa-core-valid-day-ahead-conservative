/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;

import java.util.List;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CnecRamUtils.hasCurrentLimit;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CnecRamUtils.isAdjustable;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CnecRamUtils.hadSpanningApplied;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CnecRamUtils.isRteElement;

public final class CnecRamFilter {

    private CnecRamFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<CnecRamData> filterBeforeIvaCalculus(final List<CnecRamData> unfiltered) {
        return unfiltered.stream()
                .filter(CnecRamFilter::shouldImport)
                .toList();
    }

    private static boolean shouldImport(final CnecRamData cnec) {
        return isRteElement(cnec) && isAdjustable(cnec) && hasCurrentLimit(cnec) && hadSpanningApplied(cnec);
    }

}
