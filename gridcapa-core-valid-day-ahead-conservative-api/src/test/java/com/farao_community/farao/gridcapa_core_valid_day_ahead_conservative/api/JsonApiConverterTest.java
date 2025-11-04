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
        JsonApiConverter jsonApiConverter = new JsonApiConverter();
        String inputMessage = Files.readString(Paths.get(getClass().getResource("/validRequest.json").toURI()));
        CoreValidD2ConservativeRequest coreValidD2ConservativeRequest = jsonApiConverter.fromJsonMessage(inputMessage.getBytes(), CoreValidD2ConservativeRequest.class);
        assertEquals("id", coreValidD2ConservativeRequest.getId());
        assertEquals("cnecRam.txt", coreValidD2ConservativeRequest.getCnecRam().getFilename());
        assertEquals("https://cnecRam/file/url", coreValidD2ConservativeRequest.getCnecRam().getUrl());
        assertEquals("vertice.txt", coreValidD2ConservativeRequest.getVertice().getFilename());
        assertEquals("https://vertice/file/url", coreValidD2ConservativeRequest.getVertice().getUrl());
    }

    @Test
    void checkInternalExceptionJsonConversion() throws URISyntaxException, IOException {
        JsonApiConverter jsonApiConverter = new JsonApiConverter();
        AbstractCoreValidD2ConservativeException exception = new CoreValidD2ConservativeInternalException("Something really bad happened");
        String expectedMessage = Files.readString(Paths.get(getClass().getResource("/coreValidD2ConservativeInternalError.json").toURI()));
        assertEquals(expectedMessage, new String(jsonApiConverter.toJsonMessage(exception)));
    }

}
