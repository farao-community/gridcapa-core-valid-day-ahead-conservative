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
import java.util.function.Function;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BOOLEAN;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.CURATIVE_IVA_MARGIN;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.EXCLUDED_BRANCHES;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.INT;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MAX_VERTICES_PER_BRANCH;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MIN_RAM_MCCC;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.PREVENTIVE_IVA_MARGIN;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.RAM_THRESHOLD;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.STRING;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.USE_PROJECTION;

public class CoreValidD2TaskParameters {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreValidD2TaskParameters.class);
    private static final String KEY_VALUE_JSON_FORMAT = "%n\t\"%s\": %s";
    private boolean useProjection;
    private int maxVerticesPerBranch;
    private int ramThreshold;
    private int minRamMccc;
    private String excludedBranches;
    private int curativeIvaMargin;
    private int preventiveIvaMargin;

    public CoreValidD2TaskParameters(final List<TaskParameterDto> parameters) {
        final List<String> errors = new ArrayList<>();
        for (final TaskParameterDto parameter : Optional.ofNullable(parameters).orElse(List.of())) {
            switch (parameter.getId()) {
                case USE_PROJECTION -> useProjection = validateIsBooleanAndGet(parameter, errors);
                case MAX_VERTICES_PER_BRANCH ->
                        maxVerticesPerBranch = validateIsPositiveIntegerAndGet(parameter, errors);
                case RAM_THRESHOLD -> ramThreshold = validateIsIntegerAndGet(parameter, errors);
                case MIN_RAM_MCCC -> minRamMccc = validateIsPositiveIntegerAndGet(parameter, errors);
                case EXCLUDED_BRANCHES -> excludedBranches = validateIsStringAndGet(parameter, errors);
                case CURATIVE_IVA_MARGIN -> curativeIvaMargin = validateIsPositiveIntegerAndGet(parameter, errors);
                case PREVENTIVE_IVA_MARGIN -> preventiveIvaMargin = validateIsPositiveIntegerAndGet(parameter, errors);
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
        return validateIsTypeAndGet(parameter, BOOLEAN, Boolean::parseBoolean, errors, false);
    }

    private int validateIsIntegerAndGet(final TaskParameterDto parameter,
                                        final List<String> errors) {
        return validateIsTypeAndGet(parameter, INT, Integer::parseInt, errors, 0);
    }

    private int validateIsPositiveIntegerAndGet(final TaskParameterDto parameter,
                                                final List<String> errors) {
        final int value = validateIsIntegerAndGet(parameter, errors);
        if (value < 0) {
            errors.add(String.format("Parameter %s should be positive (value: %s)", parameter.getId(), parameter.getValue()));
            return 0; // default return value, won't be used as this return can be reached only in case of validation error
        }
        return value;
    }

    private String validateIsStringAndGet(final TaskParameterDto parameter,
                                          final List<String> errors) {
        return validateIsTypeAndGet(parameter, STRING, Function.identity(), errors, "");
    }

    private <T> T validateIsTypeAndGet(final TaskParameterDto parameter,
                                       final String paramType,
                                       final Function<String, T> parser,
                                       final List<String> errors,
                                       final T defaultValue) {
        if (StringUtils.equals(paramType, parameter.getParameterType())) {
            try {
                return parser.apply(Optional.ofNullable(parameter.getValue())
                                            .orElse(parameter.getDefaultValue()));
            } catch (final Exception e) {
                errors.add(String.format("Parameter %s could not be parsed as %s (value: %s)",
                                         parameter.getId(),
                                         paramType,
                                         parameter.getValue()));
            }
        } else {
            errors.add(String.format("Parameter %s was expected to be of type %s, got %s",
                                     parameter.getId(),
                                     paramType,
                                     parameter.getParameterType()));
        }
        return defaultValue; // Won't be used as this return can be reached only in case of validation error
    }

    public boolean shouldProjectVertices() {
        return useProjection;
    }

    public int getMaxVerticesPerBranch() {
        return maxVerticesPerBranch;
    }

    public int getRamThreshold() {
        return ramThreshold;
    }

    public int getMinRamMccc() {
        return minRamMccc;
    }

    public String getExcludedBranches() {
        return excludedBranches;
    }

    public int getCurativeIvaMargin() {
        return curativeIvaMargin;
    }

    public int getPreventiveIvaMargin() {
        return preventiveIvaMargin;
    }

    public String toJsonString() {
        final List<String> appender = new ArrayList<>();
        appender.add(formatForJson(USE_PROJECTION, useProjection));
        appender.add(formatForJson(MAX_VERTICES_PER_BRANCH, maxVerticesPerBranch));
        appender.add(formatForJson(RAM_THRESHOLD, ramThreshold));
        appender.add(formatForJson(MIN_RAM_MCCC, minRamMccc));
        final String excludedValue = excludedBranches == null ? "null" : "\"" + excludedBranches + "\"";
        appender.add(formatForJson(EXCLUDED_BRANCHES, excludedValue));
        appender.add(formatForJson(CURATIVE_IVA_MARGIN, curativeIvaMargin));
        appender.add(formatForJson(PREVENTIVE_IVA_MARGIN, preventiveIvaMargin));
        return String.format("{%s%n}", String.join(", ", appender));
    }

    private static String formatForJson(final String paramName, final Object parameter) {
        return String.format(KEY_VALUE_JSON_FORMAT, paramName, parameter);
    }
}
