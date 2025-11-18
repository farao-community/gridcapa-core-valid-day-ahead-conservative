/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeRequest;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services.FileImporter;
import com.farao_community.farao.minio_adapter.starter.MinioAdapter;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.GRIDCAPA_TASK_ID;

@Component
public class CoreValidD2ConservativeHandler {

    private final Logger eventsLogger;
    private final FileImporter fileImporter;
    private final MinioAdapter minioAdapter;

    public CoreValidD2ConservativeHandler(final FileImporter fileImporter,
                                          final MinioAdapter minioAdapter,
                                          final Logger eventsLogger) {
        this.fileImporter = fileImporter;
        this.minioAdapter = minioAdapter;
        this.eventsLogger = eventsLogger;
    }

    public String handleCoreValidD2ConservativeRequest(final CoreValidD2ConservativeRequest request) {
        setUpEventLogging(request);
        return request.getId();
    }

    private static void setUpEventLogging(final CoreValidD2ConservativeRequest request) {
        MDC.put(GRIDCAPA_TASK_ID, request.getId());
    }

}
