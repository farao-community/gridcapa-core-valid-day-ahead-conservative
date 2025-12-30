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
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.FileImporter;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.ConservativeIvaCalculationUtils;
import com.farao_community.farao.minio_adapter.starter.MinioAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.GRIDCAPA_TASK_ID;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.IVA_BRANCH_JSON_FILE_NAME;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.IVA_RESULT_FILE_TYPE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MINIO_DESTINATION_PATH_FORMAT;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.PROCESS_NAME;

@Component
public class CoreValidD2ConservativeHandler {

    private final Logger eventsLogger;
    private final FileImporter fileImporter;
    private final MinioAdapter minioAdapter;
    private final BranchMaxIvaService branchMaxIvaService;
    private final CoreHubsConfiguration coreHubsConfiguration;

    @Value("${core-valid-day-ahead-conservative-runner.zone-id}")
    private String zoneId;

    public CoreValidD2ConservativeHandler(final FileImporter fileImporter,
                                          final MinioAdapter minioAdapter,
                                          final Logger eventsLogger,
                                          final BranchMaxIvaService branchMaxIvaService,
                                          final CoreHubsConfiguration coreHubsConfiguration) {
        this.fileImporter = fileImporter;
        this.minioAdapter = minioAdapter;
        this.eventsLogger = eventsLogger;
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
        uploadOutputToMinio(jsonOutput, request.getTimestamp());
        return request.getId();
    }

    private List<Vertex> getVerticesForCalculus(final List<Vertex> importedVertices,
                                                final List<CnecRamData> filteredCnecRams,
                                                final boolean shouldProjectVertices) {
        return shouldProjectVertices
                ? VerticesUtils.getVerticesProjectedOnDomain(importedVertices, filteredCnecRams, coreHubsConfiguration.getCoreHubs())
                : importedVertices;
    }

    private void uploadOutputToMinio(final byte[] outputFile, final OffsetDateTime timestamp) {
        try (final InputStream inputStream = new ByteArrayInputStream(outputFile)) {
            final String minioOutputPath = makeDestinationMinioPath(timestamp) + IVA_BRANCH_JSON_FILE_NAME;
            minioAdapter.uploadOutputForTimestamp(minioOutputPath, inputStream, PROCESS_NAME, IVA_RESULT_FILE_TYPE, timestamp);
        } catch (final IOException e) {
            throw new CoreValidD2ConservativeInternalException("Error during output file upload", e);
        }
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

    private String makeDestinationMinioPath(final OffsetDateTime offsetDateTime) {
        final ZonedDateTime targetDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of(zoneId));
        final DateTimeFormatter df = DateTimeFormatter.ofPattern(MINIO_DESTINATION_PATH_FORMAT);
        return df.format(targetDateTime);
    }
}
