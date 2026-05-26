/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request;

import com.farao_community.farao.gridcapa.task_manager.api.TaskParameterDto;
import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeRequest;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.BranchMaxIvaService;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.FileImporter;
import com.farao_community.farao.minio_adapter.starter.MinioAdapter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.IVA_RESULT_FILE_TYPE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.PROCESS_NAME;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.USE_AHC_HUBS;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.USE_PROJECTION;

@SpringBootTest
class CoreValidD2ConservativeHandlerTest {
    private static final String TEST_ID = "testId";
    @MockitoBean
    private FileImporter fileImporter;
    @MockitoBean
    private MinioAdapter minioAdapter;
    @MockitoBean
    private BranchMaxIvaService branchMaxIvaService;
    @MockitoBean
    private CoreHubsConfiguration coreHubsConfiguration;

    @Autowired
    private CoreValidD2ConservativeHandler coreValidD2ConservativeHandler;

    /**
     * Tests if request is correctly treated with and without AHC coore hubs
     * @param isProjected
     * @param isAhc
     */
    @ParameterizedTest
    @CsvSource({
        "false,false",
        "true,false",
        "false,true",
        "true,true"
    })
    void handleCoreValidD2ConservativeRequestUnprojected(final boolean isProjected, final boolean isAhc) {
        final CoreValidD2ConservativeRequest request = getTestRequest(isProjected, isAhc);
        final String id = coreValidD2ConservativeHandler.handleCoreValidD2ConservativeRequest(request);
        Assertions.assertThat(id).isEqualTo(TEST_ID);
        Mockito.verify(minioAdapter, Mockito.atLeastOnce()).uploadOutputForTimestamp(Mockito.eq("2025/12/08/15_00/ivaBranch.json"), Mockito.any(InputStream.class), Mockito.eq(PROCESS_NAME), Mockito.eq(IVA_RESULT_FILE_TYPE), Mockito.eq(request.getTimestamp()));
    }

    private CoreValidD2ConservativeRequest getTestRequest(final boolean isProjected, final boolean isAhc) {
        final OffsetDateTime timestamp = OffsetDateTime.parse("2025-12-08T14:00Z");
        return  new CoreValidD2ConservativeRequest(TEST_ID,
                                                   "currentRunId",
                                                   timestamp,
                                                   null,
                                                   null,
                                                   List.of(new TaskParameterDto(USE_PROJECTION, "BOOLEAN", Boolean.toString(isProjected), "true"),
                                                           new TaskParameterDto(USE_AHC_HUBS, "BOOLEAN", Boolean.toString(isAhc), "true"))

        );
    }
}
