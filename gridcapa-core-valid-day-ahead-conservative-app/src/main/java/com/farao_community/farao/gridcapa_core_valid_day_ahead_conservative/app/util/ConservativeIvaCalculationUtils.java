package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request.CoreValidD2TaskParameters;

import java.util.List;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BASE_CASE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT;

public final class ConservativeIvaCalculationUtils {

    private ConservativeIvaCalculationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void feedConservativeIVAs(final List<BranchData> domainData,
                                            final CoreValidD2TaskParameters parameters) {

        final int ramThreshold = parameters.getRamThreshold();
        final int curativeMargin = parameters.getCurativeIvaMargin();
        final int preventiveMargin = parameters.getPreventiveIvaMargin();

        domainData.forEach(branch -> branch.setConservativeIva(computeConservativeAdjustment(branch,
                                                                                             ramThreshold,
                                                                                             curativeMargin,
                                                                                             preventiveMargin)));

    }

    private static Integer computeConservativeAdjustment(final BranchData branchData,
                                                         final int ramThreshold,
                                                         final int curativeMargin,
                                                         final int preventiveMargin) {
        final CnecRamData cnec = branchData.cnec();
        final int minRealRam = branchData.minRealRam();

        if (minRealRam >= ramThreshold) {
            return null;//??? 0 ?
        }

        final int conservativeAdjustment;
        final int virtualMargin = cnec.getAmr();
        final int maxAdjustment = branchData.ivaMax();

        if (hasTransmissionThreshold(cnec)) {
            conservativeAdjustment = Math.min(virtualMargin, maxAdjustment);
        } else {
            final int inputMargin = hasNoContingency(cnec) ? preventiveMargin : curativeMargin;
            conservativeAdjustment = Math.clamp(maxAdjustment, 0, virtualMargin - inputMargin);
        }

        return conservativeAdjustment < virtualMargin + minRealRam ? 0 : conservativeAdjustment;
    }

    public static boolean hasNoContingency(final CnecRamData cnec) {
        return BASE_CASE.equals(cnec.contingencyName());
    }

    public static boolean hasTransmissionThreshold(final CnecRamData cnec) {
        return cnec.necId().toUpperCase().endsWith(SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT);
    }

    public record BranchData(CnecRamData cnec, int minRealRam,
                             int ivaMax, List<RamVertex> worstVertices) {
        void setConservativeIva(final Integer conservativeIva) {
        }
    }

    public record RamVertex(int realRam, Vertex vertex) {
    }

}
