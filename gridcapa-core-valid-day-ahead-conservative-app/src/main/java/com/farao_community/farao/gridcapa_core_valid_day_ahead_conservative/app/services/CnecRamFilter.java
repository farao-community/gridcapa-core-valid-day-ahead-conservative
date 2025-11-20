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
import java.util.function.Predicate;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BRANCH_STATUS_OK;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.FRENCH_TSO;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MIN_AMR_VALUE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.PREFIX_NO_CURRENT_LIMIT;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_NEC_ID_AFTER;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_NEC_ID_BEFORE;

public final class CnecRamFilter {

    private CnecRamFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<CnecRamData> filterBeforeIvaCalculus(final List<CnecRamData> unfiltered) {
        return unfiltered.stream()
                .filter(isRteElement().and(hasCurrentLimit()).and(isAdjustable()).and(hadNoSpanningApplied()))
                .toList();
    }

    public static Predicate<CnecRamData> isRteElement() {
        return cnec -> FRENCH_TSO.equalsIgnoreCase(cnec.tso());
    }

    public static Predicate<CnecRamData> isAdjustable() {
        return cnec -> BRANCH_STATUS_OK.equalsIgnoreCase(cnec.branchStatus())
                       && cnec.ramValues().amr() > MIN_AMR_VALUE;
    }

    public static Predicate<CnecRamData> hasCurrentLimit() {
        return cnec -> !StringUtils.startsWithIgnoreCase(cnec.neName(), PREFIX_NO_CURRENT_LIMIT);
    }

    public static Predicate<CnecRamData> hadNoSpanningApplied() {
        return cnec -> !StringUtils.endsWithIgnoreCase(cnec.necId(), SUFFIX_NEC_ID_BEFORE)
                       && !StringUtils.endsWithIgnoreCase(cnec.necId(), SUFFIX_NEC_ID_AFTER);
    }

}
