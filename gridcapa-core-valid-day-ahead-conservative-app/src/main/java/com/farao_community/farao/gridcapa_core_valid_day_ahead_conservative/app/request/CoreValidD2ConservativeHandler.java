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
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.IVA_RESULT_FILE_TYPE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MINIO_DESTINATION_PATH_REGEX;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.PROCESS_NAME;

@Component
public class CoreValidD2ConservativeHandler {

    private final Logger eventsLogger;
    private final FileImporter fileImporter;
    private final MinioAdapter minioAdapter;
    private final BranchMaxIvaService branchMaxIvaService;
    private final CoreHubsConfiguration coreHubsConfiguration;

    @Value("core-valid-d2.zone-id:")
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
        setUpEventLogging(request);
        final CoreValidD2TaskParameters iniParameters = new CoreValidD2TaskParameters(request.getTaskParameterList());
        final List<Vertex> importedVertices = fileImporter.importVertices(request.getVertices());
        final List<CnecRamData> cnecRams = fileImporter.importCnecRam(request.getCnecRam());
        final List<CnecRamData> filteredCnecRams = CnecRamFilter.filterBeforeIvaCalculus(cnecRams);
        final List<Vertex> verticesForCalculus = getVerticesForCalculus(importedVertices, filteredCnecRams, iniParameters.shouldProjectVertices());
        final List<IvaBranchData> branches = branchMaxIvaService.computeBranchData(verticesForCalculus, filteredCnecRams, iniParameters);
        ConservativeIvaCalculationUtils.feedConservativeIVAs(branches, iniParameters);
        final byte[] jsonOutput = ivaBranchToJson(branches);
        uploadOutputToMinio(jsonOutput, request);
        return request.getId();
    }

    private List<Vertex> getVerticesForCalculus(final List<Vertex> importedVertices,
                                                final List<CnecRamData> filteredCnecRams,
                                                final boolean shouldProjectVertices) {
        if (shouldProjectVertices) {
            return VerticesUtils.getVerticesProjectedOnDomain(importedVertices, filteredCnecRams, coreHubsConfiguration.getCoreHubs());
        } else {
            return importedVertices;
        }
    }

    private static void setUpEventLogging(final CoreValidD2ConservativeRequest request) {
        MDC.put(GRIDCAPA_TASK_ID, request.getId());
    }

    private void uploadOutputToMinio(final byte[] outputFile, final CoreValidD2ConservativeRequest request) {
        try (final InputStream inputStream = new ByteArrayInputStream(outputFile)) {
            final OffsetDateTime timestamp = request.getTimestamp();
            final String minioOutputPath = makeDestinationMinioPath(timestamp);
            minioAdapter.uploadOutputForTimestamp(minioOutputPath, inputStream, PROCESS_NAME, IVA_RESULT_FILE_TYPE, timestamp);
        } catch (IOException e) {
            throw new CoreValidD2ConservativeInternalException("Error processing upload of output file", e);
        }
    }

    private byte[] ivaBranchToJson(final List<IvaBranchData> branches) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(branches).getBytes();
        } catch (JsonProcessingException e) {
            throw new CoreValidD2ConservativeInternalException("Error creating JSON from IVA branch data", e);
        }
    }

    private String makeDestinationMinioPath(final OffsetDateTime offsetDateTime) {
        ZonedDateTime targetDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of(zoneId));
        DateTimeFormatter df = DateTimeFormatter.ofPattern(MINIO_DESTINATION_PATH_REGEX);
        return df.format(targetDateTime);
    }
}
