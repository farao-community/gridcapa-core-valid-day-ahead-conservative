/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.configuration;

import com.farao_community.farao.gridcapa.task_manager.api.TaskParameterDto;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BOOLEAN;
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

        assertEquals("{\n\t\"USE_PROJECTION\": true\n}",
                     getParams(parameter).toJsonString());
    }

    CoreValidD2TaskParameters getParams(final TaskParameterDto parameter) {
        return new CoreValidD2TaskParameters(singletonList(parameter));
    }
}
