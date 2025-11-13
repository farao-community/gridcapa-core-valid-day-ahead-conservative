/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RamAndVertexService {
    private final List<CoreHub> coreHubs;

    public RamAndVertexService(final CoreHubsConfiguration coreHubsConfiguration) {
        this.coreHubs = Collections.unmodifiableList(coreHubsConfiguration.getCoreHubs());
    }

    public List<BranchData> computeBranchData(List<Vertex> vertices, List<CnecRamData> branches) {
        final List<BranchData> branchData = new ArrayList<>();
        if (branches.isEmpty()) {
            return branchData;
        }
        //TODO USE APPLICATION PROPERTIES
        final int maxVertexPerBranch = 5;
        final int ramLimit = -10;
        final int minRamMccc = 20;
        final String [] excludedBranches = {
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

        branches.forEach(branch -> {
            final List<RamVertex> filteredRamVertices = getFilteredSortedWorseVertices(vertices, branch, ramLimit, maxVertexPerBranch);
            final int maxIva = computeMaxIva(branch, excludedBranches, minRamMccc);
            final RamVertex worstVertice = filteredRamVertices.getFirst();
            branchData.add(new BranchData(branch, worstVertice.reelRam(), maxIva, worstVertice.verticeId(), filteredRamVertices));
        });
        return branchData;
    }

    private List<RamVertex> getFilteredSortedWorseVertices(final List<Vertex> vertices,
                                                           final CnecRamData branch,
                                                           final int ramLimit,
                                                           final int maxVertexPerBranch) {
        return vertices.stream()
                .map(vertex -> computeReelVertexRam(vertex, branch))
                .filter(ramVertex -> ramVertex.reelRam() < ramLimit)
                .sorted((rv1, rv2) -> Integer.compare(rv1.reelRam(), rv2.reelRam()))
                .limit(maxVertexPerBranch)
                .toList();
    }

    private RamVertex computeReelVertexRam(Vertex vertex, CnecRamData branch) {
        final int reelVertexRam =  branch.ramValues().ram0Core() - sumVerticeBranchs(vertex.coordinates(), branch.ptdfValues());
        return new RamVertex(reelVertexRam, vertex.vertexId());
    }

    private int sumVerticeBranchs(Map<String, Integer> verticesNPs, Map<String, BigDecimal> branchPtdfs) {
        return coreHubs.stream()
                .map(coreHub -> branchPtdfs.get(coreHub.flowbasedCode()).multiply(new BigDecimal(verticesNPs.get(coreHub.clusterVerticeCode()))))
                .reduce(BigDecimal::add)
                .map(BigDecimal::intValue)
                .orElse(0);

    }

    private int computeMaxIva(CnecRamData branch, String[] excludedBranches, int minRamMccc) {
        if(StringUtils.equalsAnyIgnoreCase(branch.neName(), excludedBranches)){
            return 0;
        }
        final CnecRamFValuesData fValues = branch.fValues();
        final CnecRamValuesData ramValues = branch.ramValues();
        final int fMax = fValues.fMax();
        final BigDecimal fMaxPercentage = new BigDecimal(minRamMccc).multiply(new BigDecimal(fMax)).divide(new BigDecimal(100));
        final int positiveMax = Math.max(0,fMaxPercentage.subtract( new BigDecimal(fMax - fValues.frm() - fValues.f0Core())).intValue());
        return Math.max(0, ramValues.amr() - ramValues.cva() - positiveMax);
    }
}
