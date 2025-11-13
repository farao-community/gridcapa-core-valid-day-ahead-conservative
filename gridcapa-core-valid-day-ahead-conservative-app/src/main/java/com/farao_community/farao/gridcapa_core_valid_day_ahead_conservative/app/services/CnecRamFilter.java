/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public final class CnecRamFilter {

    public static final String FRENCH_TSO = "FR";
    public static final String EXCLUDE_NE_NAME = "[NCL]";
    public static final String EXCLUDE_SUFFIX_NEC_ID_BEFORE = "_SpannedBefore";
    public static final String EXCLUDE_SUFFIX_NEC_ID_AFTER = "_SpannedAfter";
    public static final String BRANCH_STATUS_OK = "OK";
    public static final int MIN_AMR_VALUE = 0;

    private CnecRamFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<CnecRamData> filterBeforeIvaCalculus(final List<CnecRamData> unfiltered) {
        return unfiltered.stream()
                .filter(CnecRamFilter::shouldImport)
                .toList();
    }

    private static boolean shouldImport(final CnecRamData cnecRamData) {
        return FRENCH_TSO.equalsIgnoreCase(cnecRamData.tso())
               && BRANCH_STATUS_OK.equalsIgnoreCase(cnecRamData.branchStatus())
               && !StringUtils.startsWithIgnoreCase(cnecRamData.neName(), EXCLUDE_NE_NAME)
               && !StringUtils.endsWithIgnoreCase(cnecRamData.necId(), EXCLUDE_SUFFIX_NEC_ID_BEFORE)
               && !StringUtils.endsWithIgnoreCase(cnecRamData.necId(), EXCLUDE_SUFFIX_NEC_ID_AFTER)
               && cnecRamData.ramValues().amr() > MIN_AMR_VALUE;
    }
}
