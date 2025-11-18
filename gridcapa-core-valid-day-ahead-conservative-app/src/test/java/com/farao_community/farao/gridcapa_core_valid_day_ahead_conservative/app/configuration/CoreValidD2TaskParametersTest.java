package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.configuration;

import com.farao_community.farao.gridcapa.task_manager.api.TaskParameterDto;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.configuration.CoreValidD2Constants.USE_PROJECTION;
import static org.junit.jupiter.api.Assertions.*;

class CoreValidD2TaskParametersTest {

    @Test
    void testValidBooleanParameter() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter.getParameterType()).thenReturn("BOOLEAN");
        Mockito.when(parameter.getValue()).thenReturn("true");

        final CoreValidD2TaskParameters taskParameters = new CoreValidD2TaskParameters(Collections.singletonList(parameter));
        assertTrue(taskParameters.shouldProjectVertices());
    }

    @Test
    void testInvalidBooleanParameterType() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter.getParameterType()).thenReturn("STRING");
        Mockito.when(parameter.getValue()).thenReturn("true");

        final List<TaskParameterDto> parameters = Collections.singletonList(parameter);
        final CoreValidD2ConservativeInvalidDataException exception = assertThrows(CoreValidD2ConservativeInvalidDataException.class, () -> new CoreValidD2TaskParameters(parameters));
        assertTrue(exception.getMessage().contains("Parameter USE_PROJECTION was expected to be of type BOOLEAN, got STRING"));
    }

    @Test
    void testUnknownParameter() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn("UNKNOWN_PARAMETER");
        Mockito.when(parameter.getParameterType()).thenReturn("BOOLEAN");
        Mockito.when(parameter.getValue()).thenReturn("true");

        final CoreValidD2TaskParameters taskParameters = new CoreValidD2TaskParameters(Collections.singletonList(parameter));
        assertFalse(taskParameters.shouldProjectVertices());
    }

    @Test
    void testToJsonString() {
        final TaskParameterDto parameter = Mockito.mock(TaskParameterDto.class);
        Mockito.when(parameter.getId()).thenReturn(USE_PROJECTION);
        Mockito.when(parameter.getParameterType()).thenReturn("BOOLEAN");
        Mockito.when(parameter.getValue()).thenReturn("true");

        final CoreValidD2TaskParameters taskParameters = new CoreValidD2TaskParameters(Collections.singletonList(parameter));
        final String jsonString = taskParameters.toJsonString();
        assertEquals("{\n\t\"USE_PROJECTION\": true\n}", jsonString);
    }
}
