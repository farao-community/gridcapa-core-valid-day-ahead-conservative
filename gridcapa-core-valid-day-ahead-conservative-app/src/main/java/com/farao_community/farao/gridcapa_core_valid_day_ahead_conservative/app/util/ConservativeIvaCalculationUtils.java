/*
 *  Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request.CoreValidD2TaskParameters;

import java.util.List;
import java.util.Optional;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BASECASE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT;
import static java.lang.Math.max;
import static java.lang.Math.min;

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
    static Integer computeConservativeIVA(final IvaBranchData branchData,
                                          final int ramThreshold,
                                          final int curativeIvaMargin,
                                          final int preventiveIvaMargin) {

        final CnecRamData cnec = branchData.cnec();
        final int minRealRam = branchData.minRealRam();

        if (minRealRam >= ramThreshold) {
            // no need for adjustment if we are already over the threshold
            return null;
        }

        final int conservativeIva = min(branchData.ivaMax(),
                                        getVirtualMargin(cnec,
                                                         curativeIvaMargin,
                                                         preventiveIvaMargin));

        // Once we have the value, if it's still greater than the min RAM after having removed AMR,
        // we gained capacity so we return the (lower than initial) IVA
        return conservativeIva - cnec.getAmr() < minRealRam ? 0 : conservativeIva;
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
    private static int getVirtualMargin(final CnecRamData cnec,
                                        final int curativeIvaMargin,
                                        final int preventiveIvaMargin) {
        int virtualMargin = cnec.getAmr();

        if (hasNoTransmissionLimit(cnec)) {
            // we adjust our virtual margin by a quantity defined in task/process parameters
            virtualMargin -= hasNoContingency(cnec) ? preventiveIvaMargin : curativeIvaMargin;
            // adjusted margin can be < 0 given the subtraction
            virtualMargin = max(virtualMargin, 0);
        }

        return virtualMargin;
    }

    /**
     * BASECASE being the name of the contingencies-free scenario
     *
     * @param cnec a network element
     * @return whether it has a contingency or not
     */
    private static boolean hasNoContingency(final CnecRamData cnec) {
        return BASECASE.equals(cnec.contingencyName());
    }

    /**
     * NecID could end with PATL/TATL (Permanent/Temporary Admissible Transmission Limit),
     * indicating whether the CNEC has one of these limits or not
     *
     * @param cnec a network element
     * @return whether it has a transmission threshold or not
     */
    private static boolean hasNoTransmissionLimit(final CnecRamData cnec) {
        return !cnec.necId().toUpperCase().endsWith(SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT);
    }


    //TODO TO BE DELETED
    public record IvaBranchData(CnecRamData cnec, int minRealRam,
                                int ivaMax, List<RamVertex> worstVertices) {
        void setConservativeIva(final Integer conservativeIva) {
            //WILL BE DELETED
        }
    }

    public record RamVertex(int realRam, Vertex vertex) {
    }

}
