/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.configuration;

import com.farao_community.farao.gridcapa.task_manager.api.TaskParameterDto;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.configuration.CoreValidD2Constants.USE_PROJECTION;

public class CoreValidD2TaskParameters {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreValidD2TaskParameters.class);
    private boolean useProjection;

    public CoreValidD2TaskParameters(final List<TaskParameterDto> parameters) {
        final List<String> errors = new ArrayList<>();
        for (final TaskParameterDto parameter : Optional.ofNullable(parameters).orElse(List.of())) {
            if (USE_PROJECTION.equals(parameter.getId())) {
                useProjection = validateIsBooleanAndGet(parameter, errors);
            } else {
                LOGGER.warn("Unknown parameter {} (value: {}) will be ignored",
                            parameter.getId(),
                            parameter.getValue());
            }
        }
        if (!errors.isEmpty()) {
            final String message = String.format("Validation of parameters failed. Failure reasons are: [\"%s\"].",
                                                 String.join("\" ; \"",
                                                             errors));
            throw new CoreValidD2ConservativeInvalidDataException(message);
        }
    }

    private boolean validateIsBooleanAndGet(final TaskParameterDto parameter,
                                            final List<String> errors) {
        if ("BOOLEAN".equals(parameter.getParameterType())) {
            return Boolean.parseBoolean(Optional.ofNullable(parameter.getValue())
                                                .orElse(parameter.getDefaultValue()));
        } else {
            errors.add(String.format("Parameter %s was expected to be of type BOOLEAN, got %s",
                                     parameter.getId(),
                                     parameter.getParameterType()));
            return false;
        }
    }

    public boolean shouldProjectVertices() {
        return useProjection;
    }

    public String toJsonString() {
        final List<String> appender = new ArrayList<>();
        appender.add(String.format("%n\t\"%s\": %s", USE_PROJECTION, useProjection));
        return String.format("{%s%n}", String.join(", ", appender));
    }
}
