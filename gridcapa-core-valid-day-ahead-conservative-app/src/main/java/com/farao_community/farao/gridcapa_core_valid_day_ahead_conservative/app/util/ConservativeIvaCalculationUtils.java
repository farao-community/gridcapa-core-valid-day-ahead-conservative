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

        domainData.forEach(branch -> branch.setConservativeIva(computeConservativeAdjustment(branch,
                                                                                             ramThreshold,
                                                                                             curativeMargin,
                                                                                             preventiveMargin)));

    }

    static Integer computeConservativeAdjustment(final IvaBranchData branchData,
                                                         final int ramThreshold,
                                                         final int curativeMargin,
                                                         final int preventiveMargin) {
        final CnecRamData cnec = branchData.cnec();
        final int minRealRam = branchData.minRealRam();

        if (minRealRam >= ramThreshold) {
            return null; //TODO ??? 0 ?
        }

        final int conservativeAdjustment;
        final int virtualMargin = cnec.getAmr();
        final int maxAdjustment = branchData.ivaMax();

        if (hasTransmissionThreshold(cnec)) {
            conservativeAdjustment = Math.min(virtualMargin, maxAdjustment);
        } else {
            final int inputMargin = hasNoContingency(cnec) ? preventiveMargin : curativeMargin;
            // clamp(a,b,c)=max(min(a,c), b)
            conservativeAdjustment = Math.clamp(maxAdjustment, 0, virtualMargin - inputMargin);
        }

        return conservativeAdjustment < virtualMargin + minRealRam ? 0 : conservativeAdjustment;
    }

    /**
     * Used to know which margin to use for calculation (curative if there is a contigency, else preventive)
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
     * indicating whether the CNEC has this limit or not
     * -> whether we should use user input for margin or not
     *
     * @param cnec a network element
     * @return whether it has a transmission threshold or not
     */
    private static boolean hasTransmissionThreshold(final CnecRamData cnec) {
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
