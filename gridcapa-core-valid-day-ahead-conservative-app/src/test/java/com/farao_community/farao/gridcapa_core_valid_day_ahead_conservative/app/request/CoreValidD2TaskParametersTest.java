/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request;

import com.farao_community.farao.gridcapa.task_manager.api.TaskParameterDto;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.List;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BOOLEAN;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.EXCLUDED_BRANCHES;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.USE_PROJECTION;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreValidD2TaskParametersTest {

    @Test
    void testValidBooleanParameterAsTrue() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter.getParameterType()).thenReturn(BOOLEAN);
        Mockito.when(parameter.getValue()).thenReturn("true");

        assertTrue(getParams(parameter).shouldProjectVertices());
    }

    @Test
    void testValidBooleanParameterAsFalse() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter.getParameterType()).thenReturn(BOOLEAN);
        Mockito.when(parameter.getValue()).thenReturn("false");

        assertFalse(getParams(parameter).shouldProjectVertices());
    }

    @Test
    void testInvalidBooleanParameterType() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter.getParameterType()).thenReturn("STRING");
        Mockito.when(parameter.getValue()).thenReturn("true");

        final CoreValidD2ConservativeInvalidDataException exception = assertThrows(
                CoreValidD2ConservativeInvalidDataException.class,
                () -> getParams(parameter)
        );
        assertThat(exception.getMessage())
                .contains("Parameter USE_PROJECTION was expected to be of type BOOLEAN, got STRING");
    }

    @Test
    void testUnknownParameter() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn("UNKNOWN_PARAMETER");
        Mockito.when(parameter.getParameterType()).thenReturn(BOOLEAN);
        Mockito.when(parameter.getValue()).thenReturn("true");
        assertFalse(getParams(parameter).shouldProjectVertices());
    }

    @Test
    void testToJsonString() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter.getParameterType()).thenReturn(BOOLEAN);
        Mockito.when(parameter.getValue()).thenReturn("true");

        assertEquals("""
                 {
                 \t"USE_PROJECTION": true, \

                 \t"MAX_VERTICES_PER_BRANCH": 0, \

                 \t"RAM_LIMIT": 0, \

                 \t"MIN_RAM_MCCC": 0, \

                 \t"EXCLUDED_BRANCHES": null\

                 }""",
                     getParams(parameter).toJsonString());
    }

    CoreValidD2TaskParameters getParams(final TaskParameterDto parameter) {
        return new CoreValidD2TaskParameters(singletonList(parameter));
    }

    @Test
    void coreValidD2TaskParametersEmptyTest() {
        final CoreValidD2TaskParameters parameters = new CoreValidD2TaskParameters(null);
        Assertions.assertThat(parameters)
                .isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
        "USE_PROJECTION,BOOLEAN,true,false",
        "MAX_VERTICES_PER_BRANCH,INT,2,5",
        "RAM_LIMIT,INT,-10,-100",
        "MIN_RAM_MCCC,INT,20,15",
        "EXCLUDED_BRANCHES,STRING,EXCLUDED,EMPTY"
    })
    void coreValidD2TaskParametersTest(String id, String parameterType, String value, String defaultValue) {
        TaskParameterDto parameter = new TaskParameterDto(id, parameterType, value, defaultValue);
        CoreValidD2TaskParameters parameters = new CoreValidD2TaskParameters(List.of(parameter));
        Assertions.assertThat(parameters)
                .isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
        "MAX_VERTICES_PER_BRANCH,INT,2,5,maxVerticesPerBranch,2",
        "RAM_LIMIT,INT,-10,-100,ramLimit,-10",
        "MIN_RAM_MCCC,INT,20,15,minRamMccc,20"
    })
    void coreValidD2TaskParametersGettersTest(String id, String parameterType, String value, String defaultValue, String getterMethod, int getterValue) {
        TaskParameterDto parameter = new TaskParameterDto(id, parameterType, value, defaultValue);
        CoreValidD2TaskParameters parameters = new CoreValidD2TaskParameters(List.of(parameter));
        Assertions.assertThat(parameters)
                .isNotNull()
                .hasFieldOrPropertyWithValue(getterMethod, getterValue);
    }

    @ParameterizedTest
    @CsvSource({
        "MAX_VERTICES_PER_BRANCH,INT,-2,-5",
        "RAM_LIMIT,INT,tough,luck",
        "MIN_RAM_MCCC,INT,quite,ko"
    })
    void coreValidD2TaskParametersThrowsInvalidTest(String id, String parameterType, String value, String defaultValue) {
        TaskParameterDto parameter = new TaskParameterDto(id, parameterType, value, defaultValue);
        List<TaskParameterDto> paramList = List.of(parameter);
        Assertions.assertThatExceptionOfType(CoreValidD2ConservativeInvalidDataException.class)
                .isThrownBy(() -> new CoreValidD2TaskParameters(paramList));
    }

    @Test
    void coreValidD2TaskParametersCheckStringTest() {
        final String excluded = "EXCLUDED";
        TaskParameterDto parameter = new TaskParameterDto(EXCLUDED_BRANCHES, "STRING", excluded, "EMPTY");
        CoreValidD2TaskParameters parameters = new CoreValidD2TaskParameters(List.of(parameter));
        Assertions.assertThat(parameters)
                .isNotNull()
                .hasFieldOrPropertyWithValue("excludedBranches", excluded);
    }
}
