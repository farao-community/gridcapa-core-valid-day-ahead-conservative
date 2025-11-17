/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.AbstractCoreValidD2ConservativeException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInternalException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Antoine Limouzin {@literal <antoine.limouzin at rte-france.com>}
 */
class JsonApiConverterTest {
    @Test
    void checkCoreValidInputsJsonConversion() throws URISyntaxException, IOException {
        final JsonApiConverter jsonApiConverter = new JsonApiConverter();
        final String inputMessage = Files.readString(Paths.get(getClass().getResource("/validRequest.json").toURI()));
        final CoreValidD2ConservativeRequest request = jsonApiConverter.fromJsonMessage(inputMessage.getBytes(), CoreValidD2ConservativeRequest.class);
        assertEquals("id", request.getId());
        assertEquals("cnecRam.txt", request.getCnecRam().getFilename());
        assertEquals("https://cnecRam/file/url", request.getCnecRam().getUrl());
        assertEquals("vertice.txt", request.getVertices().getFilename());
        assertEquals("https://vertice/file/url", request.getVertices().getUrl());
    }

    @Test
    void checkInternalExceptionJsonConversion() throws URISyntaxException, IOException {
        final JsonApiConverter jsonApiConverter = new JsonApiConverter();
        final AbstractCoreValidD2ConservativeException exception = new CoreValidD2ConservativeInternalException("Something really bad happened");
        final String expectedMessage = Files.readString(Paths.get(getClass().getResource("/coreValidD2ConservativeInternalError.json").toURI()));
        assertEquals(expectedMessage, new String(jsonApiConverter.toJsonMessage(exception)));
    }

}
