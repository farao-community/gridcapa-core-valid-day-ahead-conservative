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
                .filter(belongsToRTE()
                                .and(hasCurrentLimit())
                                .and(isAdjustable())
                                .and(doesNotComeFromSpanning()))
                .toList();
    }

    /**
     * We only work on French lines, RTE being the French TSO
     *
     * @return a predicate to test this on a CnecRamData object
     */
    private static Predicate<CnecRamData> belongsToRTE() {
        return cnec -> FRENCH_TSO.equalsIgnoreCase(cnec.tso());
    }

    /**
     * The lines used for calculations must have a positive virtual margin (AMR), and an OK status
     *
     * @return a predicate to test this on a CnecRamData object
     */
    private static Predicate<CnecRamData> isAdjustable() {
        return cnec -> BRANCH_STATUS_OK.equalsIgnoreCase(cnec.branchStatus())
                       && cnec.ramValues().amr() > MIN_AMR_VALUE;
    }

    /**
     * Some elements are modelled has having no current limit, thus default values are present for several fields.
     * We filter these as they would lead to a false result
     *
     * @return a predicate to test this on a CnecRamData object
     */
    private static Predicate<CnecRamData> hasCurrentLimit() {
        return cnec -> !StringUtils.startsWithIgnoreCase(cnec.neName(), PREFIX_NO_CURRENT_LIMIT);
    }

    /**
     * From ACER :
     * "‘spanning’ means the pre-coupling backup solution in situations when the day-ahead capacity calculation
     * fails to provide the flow-based parameters for strictly less than three consecutive hours"
     * We do not use these branches for the calculations
     *
     * @return a predicate to test this on a CnecRamData object
     */
    private static Predicate<CnecRamData> doesNotComeFromSpanning() {
        return cnec -> !StringUtils.endsWithIgnoreCase(cnec.necId(), SUFFIX_NEC_ID_BEFORE)
                       && !StringUtils.endsWithIgnoreCase(cnec.necId(), SUFFIX_NEC_ID_AFTER);
    }

}
