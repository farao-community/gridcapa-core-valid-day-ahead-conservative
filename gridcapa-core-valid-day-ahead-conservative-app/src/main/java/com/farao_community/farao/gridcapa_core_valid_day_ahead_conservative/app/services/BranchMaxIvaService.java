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
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request.CoreValidD2TaskParameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BranchMaxIvaService {

    private static final String SEMICOLON = ";";
    private final CoreHubsConfiguration coreHubsConfiguration;

    public BranchMaxIvaService(final CoreHubsConfiguration coreHubsConfiguration) {
        this.coreHubsConfiguration = coreHubsConfiguration;
    }

    public List<BranchData> computeBranchData(final List<Vertex> vertices,
                                              final List<CnecRamData> cnecs,
                                              final CoreValidD2TaskParameters parameters) {
        final List<BranchData> branchData = new ArrayList<>();
        if (cnecs.isEmpty()) {
            return branchData;
        }
        final int maxVerticesPerBranch = parameters.getMaxVerticesPerBranch();
        final int ramLimit = parameters.getRamLimit();
        final int minRamMccc = parameters.getMinRamMccc();
        final String excludedBranchesString = parameters.getExcludedBranches();
        final String[] excludedBranches = excludedBranchesString != null ? excludedBranchesString.split(SEMICOLON) : new String[0];
        cnecs.forEach(cnec -> {
            final List<RamVertex> filteredRamVertices = getFilteredSortedWorseVertices(vertices, cnec, ramLimit, maxVerticesPerBranch);
            final int maxIva = computeMaxIva(cnec, excludedBranches, minRamMccc);
            final RamVertex worstVertice = filteredRamVertices.isEmpty() ? new RamVertex(0, 0) : filteredRamVertices.getFirst();
            branchData.add(new BranchData(cnec, worstVertice.reelRam(), maxIva, worstVertice.verticeId(), filteredRamVertices));
        });
        return branchData;
    }

    private List<RamVertex> getFilteredSortedWorseVertices(final List<Vertex> vertices,
                                                           final CnecRamData cnec,
                                                           final int ramLimit,
                                                           final int maxVerticesPerBranch) {
        return vertices.stream()
                .map(vertex -> computeReelVertexRam(vertex, cnec))
                .filter(ramVertex -> ramVertex.reelRam() < ramLimit)
                .sorted((rv1, rv2) -> Integer.compare(rv1.reelRam(), rv2.reelRam()))
                .limit(maxVerticesPerBranch)
                .toList();
    }

    private RamVertex computeReelVertexRam(final Vertex vertex, final CnecRamData cnec) {
        final int reelVertexRam = cnec.ramValues().ram0Core() - sumVertexBranches(vertex.coordinates(), cnec.ptdfValues());
        return new RamVertex(reelVertexRam, vertex.vertexId());
    }

    private int sumVertexBranches(final Map<String, Integer> verticesNPs, final Map<String, BigDecimal> cnecPtdfs) {
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
