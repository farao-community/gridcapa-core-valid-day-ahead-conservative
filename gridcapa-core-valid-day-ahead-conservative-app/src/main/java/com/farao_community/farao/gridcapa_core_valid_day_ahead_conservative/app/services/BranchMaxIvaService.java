/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.BranchData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.RamVertex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BranchMaxIvaService {

    private final CoreHubsConfiguration coreHubsConfiguration;

    public BranchMaxIvaService(final CoreHubsConfiguration coreHubsConfiguration) {
        this.coreHubsConfiguration = coreHubsConfiguration;
    }

    public List<BranchData> computeBranchData(final List<Vertex> vertices, final List<CnecRamData> cnecs) {
        final List<BranchData> branchData = new ArrayList<>();
        if (cnecs.isEmpty()) {
            return branchData;
        }
        //TODO USE APPLICATION PROPERTIES
        final int maxVertexPerBranch = 5;
        final int ramLimit = -10;
        final int minRamMccc = 20;
        final String[] excludedBranches = {
            "[FR-FR] Creys - Saint-Vulbas 2 [DIR]",
            "[FR-FR] Creys - Saint-Vulbas 2 [OPP]",
            "[FR-CH] Cornier - Riddes [DIR]",
            "[FR-CH] Cornier - Riddes [OPP]",
            "[FR-FR] Creys - Genissiat 1 [DIR]",
            "[FR-FR] Creys - Genissiat 1 [OPP]",
            "[FR-FR] Creys - Saint-Vulbas 1 [DIR]",
            "[FR-FR] Creys - Saint-Vulbas 1 [OPP]",
            "[FR-FR] Frasnes - Genissiat [DIR]",
            "[FR-FR] Frasnes - Genissiat [OPP]",
            "[FR-FR] Creys - Genissiat 2 [DIR]",
            "[FR-FR] Creys - Genissiat 2 [OPP]",
            "[FR-CH] Cornier - Saint-Triphon [DIR]",
            "[FR-CH] Cornier - Saint-Triphon [OPP]"
        };
       //TODO end

        cnecs.forEach(cnec -> {
            final List<RamVertex> filteredRamVertices = getFilteredSortedWorseVertices(vertices, cnec, ramLimit, maxVertexPerBranch);
            final int maxIva = computeMaxIva(cnec, excludedBranches, minRamMccc);
            final RamVertex worstVertice = filteredRamVertices.getFirst();
            branchData.add(new BranchData(cnec, worstVertice.reelRam(), maxIva, worstVertice.verticeId(), filteredRamVertices));
        });
        return branchData;
    }

    private List<RamVertex> getFilteredSortedWorseVertices(final List<Vertex> vertices,
                                                           final CnecRamData cnec,
                                                           final int ramLimit,
                                                           final int maxVertexPerBranch) {
        return vertices.stream()
                .map(vertex -> computeReelVertexRam(vertex, cnec))
                .filter(ramVertex -> ramVertex.reelRam() < ramLimit)
                .sorted((rv1, rv2) -> Integer.compare(rv1.reelRam(), rv2.reelRam()))
                .limit(maxVertexPerBranch)
                .toList();
    }

    private RamVertex computeReelVertexRam(final Vertex vertex, final CnecRamData cnec) {
        final int reelVertexRam =  cnec.ramValues().ram0Core() - sumVerticeBranchs(vertex.coordinates(), cnec.ptdfValues());
        return new RamVertex(reelVertexRam, vertex.vertexId());
    }

    private int sumVerticeBranchs(final Map<String, Integer> verticesNPs, final Map<String, BigDecimal> cnecPtdfs) {
        return coreHubsConfiguration.getCoreHubs().stream()
                .map(coreHub -> cnecPtdfs.get(coreHub.flowbasedCode()).multiply(new BigDecimal(verticesNPs.get(coreHub.clusterVerticeCode()))))
                .reduce(BigDecimal::add)
                .map(BigDecimal::intValue)
                .orElse(0);

    }

    private int computeMaxIva(final CnecRamData cnec, final String[] excludedBranches, final int minRamMccc) {
        if (StringUtils.equalsAnyIgnoreCase(cnec.neName(), excludedBranches)) {
            return 0;
        }
        final CnecRamFValuesData fValues = cnec.fValues();
        final CnecRamValuesData ramValues = cnec.ramValues();
        final int fMax = fValues.fMax();
        final BigDecimal fMaxPercentage = new BigDecimal(minRamMccc).multiply(new BigDecimal(fMax)).divide(new BigDecimal(100));
        final int positiveMax = Math.max(0, fMaxPercentage.subtract(new BigDecimal(fMax - fValues.frm() - fValues.f0Core())).intValue());
        return Math.max(0, ramValues.amr() - ramValues.cva() - positiveMax);
    }
}
