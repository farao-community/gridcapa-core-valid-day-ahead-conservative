/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request;

import com.farao_community.farao.gridcapa.task_manager.api.TaskParameterDto;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BOOLEAN;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.EXCLUDED_BRANCHES;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.INT;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MAX_VERTICES_PER_BRANCH;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MIN_RAM_MCCC;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.RAM_LIMIT;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.STRING;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.USE_PROJECTION;

public class CoreValidD2TaskParameters {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreValidD2TaskParameters.class);
    private static final String KEY_VALUE_FORMAT = "%n\t\"%s\": %s";
    private boolean useProjection;
    private int maxVerticesPerBranch;
    private int ramLimit;
    private int minRamMccc;
    private String excludedBranches;

    public CoreValidD2TaskParameters(final List<TaskParameterDto> parameters) {
        final List<String> errors = new ArrayList<>();
        for (final TaskParameterDto parameter : Optional.ofNullable(parameters).orElse(List.of())) {
            switch (parameter.getId()) {
                case USE_PROJECTION -> useProjection = validateIsBooleanAndGet(parameter, errors);
                case MAX_VERTICES_PER_BRANCH -> maxVerticesPerBranch = validateIsPositiveIntegerAndGet(parameter, errors);
                case RAM_LIMIT -> ramLimit = validateIsIntegerAndGet(parameter, errors);
                case MIN_RAM_MCCC -> minRamMccc = validateIsPositiveIntegerAndGet(parameter, errors);
                case EXCLUDED_BRANCHES -> excludedBranches = validateIsStringAndGet(parameter, errors);
                default -> LOGGER.warn("Unknown parameter {} (value: {}) will be ignored",
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
        if (BOOLEAN.equals(parameter.getParameterType())) {
            return Boolean.parseBoolean(Optional.ofNullable(parameter.getValue())
                                                .orElse(parameter.getDefaultValue()));
        } else {
            errors.add(String.format("Parameter %s was expected to be of type BOOLEAN, got %s",
                                     parameter.getId(),
                                     parameter.getParameterType()));
            return false;
        }
    }

    private int validateIsIntegerAndGet(TaskParameterDto parameter, List<String> errors) {
        if (StringUtils.equals(INT, parameter.getParameterType())) {
            String value = parameter.getValue() != null ? parameter.getValue() : parameter.getDefaultValue();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                errors.add(String.format("Parameter %s could not be parsed as integer (value: %s)", parameter.getId(), parameter.getValue()));
            }
        } else {
            errors.add(String.format("Parameter %s was expected to be of type INT, got %s", parameter.getId(), parameter.getParameterType()));
        }
        return 0; // default return value, won't be used as this return can be reached only in case of validation error
    }

    private int validateIsPositiveIntegerAndGet(TaskParameterDto parameter, List<String> errors) {
        int value = validateIsIntegerAndGet(parameter, errors);
        if (value < 0) {
            errors.add(String.format("Parameter %s should be positive (value: %s)", parameter.getId(), parameter.getValue()));
            return 0; // default return value, won't be used as this return can be reached only in case of validation error
        }
        return value;
    }

    private String validateIsStringAndGet(TaskParameterDto parameter, List<String> errors) {
        if (StringUtils.equals(STRING, parameter.getParameterType())) {
            return parameter.getValue() != null ? parameter.getValue() : parameter.getDefaultValue();
        } else {
            errors.add(String.format("Parameter %s was expected to be of type INT, got %s", parameter.getId(), parameter.getParameterType()));
        }
        return ""; // default return value, won't be used as this return can be reached only in case of validation error
    }

    public boolean shouldProjectVertices() {
        return useProjection;
    }

    public int getMaxVerticesPerBranch() {
        return maxVerticesPerBranch;
    }

    public int getRamLimit() {
        return ramLimit;
    }

    public int getMinRamMccc() {
        return minRamMccc;
    }

    public String getExcludedBranches() {
        return excludedBranches;
    }

    public String toJsonString() {
        final List<String> appender = new ArrayList<>();
        appender.add(String.format(KEY_VALUE_FORMAT, USE_PROJECTION, useProjection));
        appender.add(String.format(KEY_VALUE_FORMAT, MAX_VERTICES_PER_BRANCH, maxVerticesPerBranch));
        appender.add(String.format(KEY_VALUE_FORMAT, RAM_LIMIT, ramLimit));
        appender.add(String.format(KEY_VALUE_FORMAT, MIN_RAM_MCCC, minRamMccc));
        appender.add(String.format(KEY_VALUE_FORMAT, EXCLUDED_BRANCHES, excludedBranches));
        return String.format("{%s%n}", String.join(", ", appender));
    }
}
