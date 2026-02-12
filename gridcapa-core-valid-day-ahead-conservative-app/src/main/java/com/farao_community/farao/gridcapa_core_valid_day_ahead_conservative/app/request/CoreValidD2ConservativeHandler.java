/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.VerticesUtils;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.IvaBranchData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInternalException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeRequest;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.BranchMaxIvaService;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.CnecRamFilter;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.FileExporter;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.FileImporter;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.ConservativeIvaCalculationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.GRIDCAPA_TASK_ID;

@Component
public class CoreValidD2ConservativeHandler {

    private final FileImporter fileImporter;
    private final FileExporter fileExporter;
    private final BranchMaxIvaService branchMaxIvaService;
    private final CoreHubsConfiguration coreHubsConfiguration;

    public CoreValidD2ConservativeHandler(final FileImporter fileImporter,
                                          final FileExporter fileExporter,
                                          final BranchMaxIvaService branchMaxIvaService,
                                          final CoreHubsConfiguration coreHubsConfiguration) {
        this.fileImporter = fileImporter;
        this.fileExporter = fileExporter;
        this.branchMaxIvaService = branchMaxIvaService;
        this.coreHubsConfiguration = coreHubsConfiguration;
    }

    public String handleCoreValidD2ConservativeRequest(final CoreValidD2ConservativeRequest request) {
        MDC.put(GRIDCAPA_TASK_ID, request.getId());
        final CoreValidD2TaskParameters iniParameters = new CoreValidD2TaskParameters(request.getTaskParameterList());
        final List<Vertex> importedVertices = fileImporter.importVertices(request.getVertices());
        final List<CnecRamData> cnecRams = fileImporter.importCnecRam(request.getCnecRam());
        final List<Vertex> verticesForCalculus = getVerticesForCalculus(importedVertices, cnecRams, iniParameters.shouldProjectVertices());
        final List<CnecRamData> filteredCnecRams = CnecRamFilter.filterBeforeIvaCalculus(cnecRams);
        final List<IvaBranchData> branches = branchMaxIvaService.computeBranchData(verticesForCalculus, filteredCnecRams, iniParameters);
        ConservativeIvaCalculationUtils.feedConservativeIVAs(branches, iniParameters);
        final byte[] jsonOutput = ivaBranchesToJson(branches);
        fileExporter.uploadOutputToMinio(jsonOutput, request.getTimestamp());
        return request.getId();
    }

    private List<Vertex> getVerticesForCalculus(final List<Vertex> importedVertices,
                                                final List<CnecRamData> cnecRamData,
                                                final boolean shouldProjectVertices) {
        return shouldProjectVertices
                ? VerticesUtils.getVerticesProjectedOnDomain(importedVertices, cnecRamData, coreHubsConfiguration.getCoreHubs())
                : importedVertices;
    }

    private byte[] ivaBranchesToJson(final List<IvaBranchData> branches) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(branches).getBytes();
        } catch (final JsonProcessingException e) {
            throw new CoreValidD2ConservativeInternalException("Error creating JSON from IVA branch data", e);
        }
    }
}
