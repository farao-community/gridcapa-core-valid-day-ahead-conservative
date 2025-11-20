package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request.CoreValidD2TaskParameters;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConservativeIvaService {

    public static final String BASE_CASE = "BASECASE";
    public static final String ATL = "ATL";

    public void feedConservativeIVAs(final List<BranchData> domainData,
                                     final CoreValidD2TaskParameters parameters) {

        final int ramThreshold = parameters.getRamThreshold();
        final int curativeMargin = parameters.getCurativeIvaMargin();
        final int preventiveMargin = parameters.getPreventiveIvaMargin();

        for (final BranchData branchData : domainData){
            Optional.ofNullable(computeConservativeIva(branchData, ramThreshold, curativeMargin, preventiveMargin))
                    .ifPresent(branchData::setConservativeIva);

        }

    }

    private Integer computeConservativeIva(final BranchData branchData,
                                           final int ramThreshold,
                                           final int curativeMargin,
                                           final int preventiveMargin) {
        final CnecRamData cnec = branchData.cnec();
        final int minRealRam = branchData.minRealRam();

        if (minRealRam >= ramThreshold) {
            return null;//??? 0 ?
        }

        final int conservativeIva;
        final int amr = cnec.getAmr();
        final int ivaMax = branchData.ivaMax();

        if (isAdmissibleTransmissionLoading(cnec)) {
            conservativeIva = Math.min(amr, ivaMax);
        } else {
            final int margin = hasNoContingency(cnec) ? preventiveMargin : curativeMargin;
            conservativeIva = Math.clamp(ivaMax, 0, amr - margin);
        }

        return conservativeIva < amr + minRealRam ? 0 : conservativeIva;
    }

    private boolean hasNoContingency(final CnecRamData cnec) {
        return BASE_CASE.equals(cnec.contingencyName());
    }

    private boolean isAdmissibleTransmissionLoading(final CnecRamData cnec) {
        return cnec.necId().toUpperCase().endsWith(ATL);
    }


    public record BranchData(CnecRamData cnec, int minRealRam,
                             int ivaMax, List<RamVertex> worstVertices) {
        void setConservativeIva(final Integer conservativeIva){}
    }

    public record RamVertex(int realRam, Vertex vertex) {
    }

}
