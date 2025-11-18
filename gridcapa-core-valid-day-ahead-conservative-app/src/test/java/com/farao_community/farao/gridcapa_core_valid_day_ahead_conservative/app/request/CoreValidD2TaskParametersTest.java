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

import java.util.ArrayList;
import java.util.List;

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
        List<TaskParameterDto> parameters = new ArrayList<>();
        final TaskParameterDto parameter1 = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter1.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter1.getParameterType()).thenReturn(BOOLEAN);
        Mockito.when(parameter1.getValue()).thenReturn("true");
        parameters.add(parameter1);
        final TaskParameterDto parameter2 = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter2.getId()).thenReturn(MAX_VERTICES_PER_BRANCH);
        Mockito.when(parameter2.getParameterType()).thenReturn(INT);
        Mockito.when(parameter2.getValue()).thenReturn("5");
        parameters.add(parameter2);
        final TaskParameterDto parameter3 = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter3.getId()).thenReturn(RAM_THRESHOLD);
        Mockito.when(parameter3.getParameterType()).thenReturn(INT);
        Mockito.when(parameter3.getValue()).thenReturn("-500");
        parameters.add(parameter3);
        final TaskParameterDto parameter4 = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter4.getId()).thenReturn(MIN_RAM_MCCC);
        Mockito.when(parameter4.getParameterType()).thenReturn(INT);
        Mockito.when(parameter4.getValue()).thenReturn("20");
        parameters.add(parameter4);
        final TaskParameterDto parameter5 = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter5.getId()).thenReturn(EXCLUDED_BRANCHES);
        Mockito.when(parameter5.getParameterType()).thenReturn(STRING);
        Mockito.when(parameter5.getValue()).thenReturn("A List;of;Strings");
        parameters.add(parameter5);
        final TaskParameterDto parameter6 = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter6.getId()).thenReturn(CURATIVE_IVA_MARGIN);
        Mockito.when(parameter6.getParameterType()).thenReturn(INT);
        Mockito.when(parameter6.getValue()).thenReturn("100");
        parameters.add(parameter6);
        final TaskParameterDto parameter7 = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter7.getId()).thenReturn(PREVENTIVE_IVA_MARGIN);
        Mockito.when(parameter7.getParameterType()).thenReturn(INT);
        Mockito.when(parameter7.getValue()).thenReturn("50");
        parameters.add(parameter7);
        assertEquals("""
                         {
                         \t"USE_PROJECTION": true,\s
                         \t"MAX_VERTICES_PER_BRANCH": 5,\s
                         \t"RAM_THRESHOLD": -500,\s
                         \t"MIN_RAM_MCCC": 20,\s
                         \t"EXCLUDED_BRANCHES": "A List;of;Strings",\s
                         \t"CURATIVE_IVA_MARGIN": 100,\s
                         \t"PREVENTIVE_IVA_MARGIN": 50
                         }""",
                     new CoreValidD2TaskParameters(parameters).toJsonString());
    }

    @Test
    void testEmptyParametersToJsonString() {
        assertEquals(
            """
                {
                \t"USE_PROJECTION": false,\s
                \t"MAX_VERTICES_PER_BRANCH": 0,\s
                \t"RAM_THRESHOLD": 0,\s
                \t"MIN_RAM_MCCC": 0,\s
                \t"EXCLUDED_BRANCHES": null,\s
                \t"CURATIVE_IVA_MARGIN": 0,\s
                \t"PREVENTIVE_IVA_MARGIN": 0
                }""",
            new CoreValidD2TaskParameters(null).toJsonString());
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
        "RAM_THRESHOLD,INT,-10,-100",
        "MIN_RAM_MCCC,INT,20,15",
        "EXCLUDED_BRANCHES,STRING,EXCLUDED,EMPTY",
        "CURATIVE_IVA_MARGIN,INT,100,0",
        "PREVENTIVE_IVA_MARGIN,INT,50,0"
    })
    void coreValidD2TaskParametersTest(final String id,
                                       final String parameterType,
                                       final String value,
                                       final String defaultValue) {
        final TaskParameterDto parameter = new TaskParameterDto(id, parameterType, value, defaultValue);
        final CoreValidD2TaskParameters parameters = new CoreValidD2TaskParameters(List.of(parameter));
        Assertions.assertThat(parameters)
            .isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
        "MAX_VERTICES_PER_BRANCH,INT,2,5,maxVerticesPerBranch,2",
        "RAM_THRESHOLD,INT,-10,-100,ramThreshold,-10",
        "MIN_RAM_MCCC,INT,20,15,minRamMccc,20",
        "CURATIVE_IVA_MARGIN,INT,100,0,curativeIvaMargin,100",
        "PREVENTIVE_IVA_MARGIN,INT,50,0,preventiveIvaMargin,50"
    })
    void coreValidD2TaskParametersGettersTest(final String id,
                                              final String parameterType,
                                              final String value,
                                              final String defaultValue,
                                              final String getterMethod,
                                              final int getterValue) {
        final TaskParameterDto parameter = new TaskParameterDto(id, parameterType, value, defaultValue);
        final CoreValidD2TaskParameters parameters = new CoreValidD2TaskParameters(List.of(parameter));
        Assertions.assertThat(parameters)
            .isNotNull()
            .hasFieldOrPropertyWithValue(getterMethod, getterValue);
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
        "RAM_THRESHOLD,INT,tough,luck",
        "MIN_RAM_MCCC,INT,quite,ko",
        "CURATIVE_IVA_MARGIN,INT,-100,-1",
        "PREVENTIVE_IVA_MARGIN,INT,fifty,zero"
    })
    void coreValidD2TaskParametersThrowsInvalidTest(final String id,
                                                    final String parameterType,
                                                    final String value,
                                                    final String defaultValue) {
        final TaskParameterDto parameter = new TaskParameterDto(id, parameterType, value, defaultValue);
        final List<TaskParameterDto> paramList = List.of(parameter);
        Assertions.assertThatExceptionOfType(CoreValidD2ConservativeInvalidDataException.class)
            .isThrownBy(() -> new CoreValidD2TaskParameters(paramList));
    }

    @Test
    void coreValidD2TaskParametersCheckStringTest() {
        final String excluded = "EXCLUDED";
        final TaskParameterDto parameter = new TaskParameterDto(EXCLUDED_BRANCHES, "STRING", excluded, "EMPTY");
        final CoreValidD2TaskParameters parameters = new CoreValidD2TaskParameters(List.of(parameter));
        Assertions.assertThat(parameters)
            .isNotNull()
            .hasFieldOrPropertyWithValue("excludedBranches", excluded);
    }
}
