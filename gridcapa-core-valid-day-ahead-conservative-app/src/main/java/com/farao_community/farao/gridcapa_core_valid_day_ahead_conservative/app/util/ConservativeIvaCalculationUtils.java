/*
 *  Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.IvaBranchData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request.CoreValidD2TaskParameters;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BASECASE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT;
import static java.math.BigDecimal.ZERO;

public final class ConservativeIvaCalculationUtils {

    private ConservativeIvaCalculationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void feedConservativeIVAs(final List<IvaBranchData> domainData,
                                            final CoreValidD2TaskParameters parameters) {

        final int ramThreshold = parameters.getRamThreshold();
        final int curativeMargin = parameters.getCurativeIvaMargin();
        final int preventiveMargin = parameters.getPreventiveIvaMargin();

        for (final IvaBranchData branch : domainData) {
            Optional.ofNullable(computeConservativeIVA(branch, ramThreshold, curativeMargin, preventiveMargin))
                .ifPresent(branch::setConservativeIva);
        }

    }

    /**
     * IVA => a value that is being subtracted to our available margin to make it secure while gaining a few MW
     * conservative => without RAO use
     *
     * @param branchData          the branch for which we compute the conservative IVA
     * @param ramThreshold        lower boundary of RAM
     * @param curativeIvaMargin   IVA margin if the branch has contingencies
     * @param preventiveIvaMargin IVA margin if the branch has no contingencies
     * @return the conservative IVA / null if non-applicable
     */
    static BigDecimal computeConservativeIVA(final IvaBranchData branchData,
                                             final int ramThreshold,
                                             final int curativeIvaMargin,
                                             final int preventiveIvaMargin) {

        final CnecRamData cnec = branchData.getCnec();
        final int minRealRam = branchData.getMinRealRam();

        if (minRealRam >= ramThreshold) {
            // no need for adjustment if we are already over the threshold
            return null;
        }

        final BigDecimal conservativeIva = BigDecimal.valueOf(branchData.getIvaMax())
            .min(getVirtualMargin(cnec,
                                  curativeIvaMargin,
                                  preventiveIvaMargin));

        final BigDecimal amr = BigDecimal.valueOf(cnec.getAmr());
        final BigDecimal minRam = BigDecimal.valueOf(minRealRam);

        // Once we have the value, if it's still greater than the min RAM after having removed AMR,
        // we gained capacity so we return the (lower than initial) IVA
        return conservativeIva.subtract(amr).compareTo(minRam) <= 0 ? ZERO : conservativeIva;
    }

    /**
     * Depending on the line, the calculation uses a different 'virtual' margin for the adjustment
     * (virtual because it's used for calculation but has no physical meaning)
     *
     * @param cnec                the considered network element
     * @param curativeIvaMargin   user input margin with contingencies
     * @param preventiveIvaMargin user input margin without contingency
     * @return the virtual margin used for the conservative IVA calculation
     */
    private static BigDecimal getVirtualMargin(final CnecRamData cnec,
                                               final int curativeIvaMargin,
                                               final int preventiveIvaMargin) {
        BigDecimal virtualMargin = BigDecimal.valueOf(cnec.getAmr());

        if (hasNoTransmissionLimit(cnec)) {
            // we adjust our virtual margin by a quantity defined in task/process parameters
            final int inputMargin = hasNoContingency(cnec) ? preventiveIvaMargin : curativeIvaMargin;
            // adjusted margin can be < 0 given the subtraction
            virtualMargin = virtualMargin.subtract(BigDecimal.valueOf(inputMargin)).max(ZERO);
        }

        return virtualMargin;
    }

    /**
     * BASECASE being the name of the contingencies-free scenario
     *
     * @param cnec a network element
     * @return true if the CNEC has no contingency (BASECASE), false otherwise
     */
    private static boolean hasNoContingency(final CnecRamData cnec) {
        return BASECASE.equals(cnec.contingencyName());
    }

    /**
     * NecID could end with PATL/TATL (Permanent/Temporary Admissible Transmission Limit),
     * indicating whether the CNEC has one of these limits or not
     *
     * @param cnec a network element
     * @return true if the CNEC has no admissible transmission limit, false otherwise
     */
    private static boolean hasNoTransmissionLimit(final CnecRamData cnec) {
        return !cnec.necId().toUpperCase().endsWith(SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT);
    }

}
