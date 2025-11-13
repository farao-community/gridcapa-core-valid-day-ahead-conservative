/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app;

import com.farao_community.farao.gridcapa.task_manager.api.TaskStatus;
import com.farao_community.farao.gridcapa.task_manager.api.TaskStatusUpdate;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.JsonApiConverter;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.AbstractCoreValidD2ConservativeException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static com.farao_community.farao.gridcapa.task_manager.api.TaskStatus.ERROR;
import static com.farao_community.farao.gridcapa.task_manager.api.TaskStatus.RUNNING;
import static com.farao_community.farao.gridcapa.task_manager.api.TaskStatus.SUCCESS;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.configuration.CoreValidD2Constants.TASK_STATUS_UPDATE;

@Component
public class CoreValidD2ConservativeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreValidD2ConservativeListener.class);


    private final JsonApiConverter jsonApiConverter;
    private final CoreValidD2ConservativeHandler coreValidD2ConservativeHandler;
    private final StreamBridge streamBridge;

    public CoreValidD2ConservativeListener(final CoreValidD2ConservativeHandler coreValidD2ConservativeHandler,
                                           final StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
        this.jsonApiConverter = new JsonApiConverter();
        this.coreValidD2ConservativeHandler = coreValidD2ConservativeHandler;
    }

    @Bean
    public Consumer<Flux<byte[]>> request() {
        return flux -> flux
                .doOnNext(this::onMessage)
                .subscribe();
    }

    public void onMessage(final byte[] req) {
        try {
            final CoreValidD2ConservativeRequest coreValidD2ConservativeRequest = jsonApiConverter.fromJsonMessage(req, CoreValidD2ConservativeRequest.class);
            runCoreValidD2ConservativeRequest(coreValidD2ConservativeRequest);
        } catch (final RuntimeException e) {
            LOGGER.error("Core valid day ahead conservative exception occurred", e);
        }
    }

    private void runCoreValidD2ConservativeRequest(final CoreValidD2ConservativeRequest request) {
        final OffsetDateTime timestamp = request.getTimestamp();
        try {
            LOGGER.info("Core valid day ahead conservative request received: {}", request);
            updateTaskStatus(request.getId(), timestamp, RUNNING);
            final String responseId = coreValidD2ConservativeHandler.handleCoreValidD2ConservativeRequest(request);
            updateTaskStatus(responseId, timestamp, SUCCESS);
        } catch (final AbstractCoreValidD2ConservativeException e) {
            LOGGER.error("Core valid day ahead conservative exception occurred", e);
            updateTaskStatus(request.getId(), timestamp, ERROR);
        } catch (final RuntimeException e) {
            LOGGER.error("Unknown exception occurred", e);
            updateTaskStatus(request.getId(), timestamp, ERROR);
        }
    }

    private void updateTaskStatus(final String requestId,
                                  final OffsetDateTime timestamp,
                                  final TaskStatus targetStatus) {
        streamBridge.send(TASK_STATUS_UPDATE, new TaskStatusUpdate(UUID.fromString(requestId), targetStatus));
        LOGGER.info("Updating task status to {} for timestamp {}", targetStatus, timestamp);
    }

}
