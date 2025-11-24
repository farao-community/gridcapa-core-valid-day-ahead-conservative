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

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BASECASE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT;

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
            branch.setConservativeIva(computeConservativeIVA(branch,
                                                             ramThreshold,
                                                             curativeMargin,
                                                             preventiveMargin));
        }

    }

    /**
     * IVA => a value that can be added to our available margin while still being secure
     * conservative => without RAO use
     * @param branchData the branch for which we compute the conservative IVA
     * @param ramThreshold lower boundary of RAM
     * @param curativeIvaMargin IVA margin if the branch has contingencies
     * @param preventiveIvaMargin IVA margin if the branch has no contingencies
     * @return the conservative IVA
     */
    static Integer computeConservativeIVA(final IvaBranchData branchData,
                                          final int ramThreshold,
                                          final int curativeIvaMargin,
                                          final int preventiveIvaMargin) {

        final CnecRamData cnec = branchData.cnec();
        final int minRealRam = branchData.minRealRam();

        if (minRealRam >= ramThreshold) {
            // no need for adjustment if we are already over the threshold
            return 0;
        }

        final int conservativeIva;
        final int virtualMargin = cnec.getAmr();
        final int ivaMax = branchData.ivaMax();

        if (hasTransmissionLimit(cnec)) {
            conservativeIva = Math.min(virtualMargin, ivaMax);
        } else {
            // we adjust our virtual margin by a quantity defined in task/process parameters
            final int adjustedMargin = virtualMargin - (hasNoContingency(cnec) ? preventiveIvaMargin : curativeIvaMargin);
            // we do clamp(a,0,b) = max(min(a,b), 0) because adjusted margin can be < 0 given the substraction
            conservativeIva = Math.clamp(ivaMax, 0, adjustedMargin);
        }

        // if we gain something over minRealRam with our calculation,
        // we return it, else we return 0 (no adjustment)
        return conservativeIva - virtualMargin < minRealRam ? 0 : conservativeIva;
    }

    /**
     * BASECASE being the name of the contigencies-free scenario
     *
     * @param cnec a network element
     * @return whether it has a contigency or not
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
    private static boolean hasTransmissionLimit(final CnecRamData cnec) {
        return cnec.necId().toUpperCase().endsWith(SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT);
    }


    //TODO TO BE DELETED
    public record IvaBranchData(CnecRamData cnec, int minRealRam,
                                int ivaMax, List<RamVertex> worstVertices) {
        void setConservativeIva(final Integer conservativeIva) {
        }
    }

    public record RamVertex(int realRam, Vertex vertex) {
    }

}
