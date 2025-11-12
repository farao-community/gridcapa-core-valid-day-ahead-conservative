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

/**
 * @author Marc Schwitzguebel {@literal <marc.schwitzguebel_externe at rte-france.com>}
 */
public final class CnecRamFilter {

    public static final String TSO = "FR";
    public static final String EXCLUDE_NE_NAME = "[NCL]";
    public static final String EXCLUDE_SUFFIXE_NEC_ID_1 = "_SpannedBefore";
    public static final String EXCLUDE_SUFFIXE_NEC_ID_2 = "_SpannedAfter";
    public static final String BRANCH_STATU8S_OK = "OK";
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
        return StringUtils.equalsIgnoreCase(cnecRamData.tso(), TSO)
                && !StringUtils.startsWithIgnoreCase(cnecRamData.neName(), EXCLUDE_NE_NAME)
                && !StringUtils.endsWithAny(cnecRamData.necId(), EXCLUDE_SUFFIXE_NEC_ID_1, EXCLUDE_SUFFIXE_NEC_ID_2)
                && StringUtils.equalsIgnoreCase(cnecRamData.branchStatus(), BRANCH_STATU8S_OK)
                && cnecRamData.ramValues().amr() > MIN_AMR_VALUE;
    }


}
