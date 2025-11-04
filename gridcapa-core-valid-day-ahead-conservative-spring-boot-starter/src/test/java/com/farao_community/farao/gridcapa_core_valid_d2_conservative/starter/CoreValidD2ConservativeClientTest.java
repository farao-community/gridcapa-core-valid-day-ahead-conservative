/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_d2_conservative.starter;

import com.farao_community.farao.gridcapa_core_valid_d2_conservative.api.JsonApiConverter;
import com.farao_community.farao.gridcapa_core_valid_d2_conservative.api.resource.CoreValidD2ConservativeRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;

import java.io.IOException;

/**
 * @author Antoine Limouzin {@literal <antoine.limouzin at rte-france.com>}
 * @author Marc Schwitzguebel {@literal <marc.schwitzguebel_external at rte-france.com>}
 */
class CoreValidD2ConservativeClientTest {
    private final JsonApiConverter jsonApiConverter = new JsonApiConverter();

    @Test
    void checkThatClientHandleMessageCorrectly() throws IOException {
        AmqpTemplate amqpTemplate = Mockito.mock(AmqpTemplate.class);
        CoreValidD2ConservativeClient client = new CoreValidD2ConservativeClient(amqpTemplate, buildProperties());
        CoreValidD2ConservativeRequest request = jsonApiConverter
                .fromJsonMessage(getClass().getResourceAsStream("/coreValidD2ConservativeRequest.json")
                                         .readAllBytes(),
                                 CoreValidD2ConservativeRequest.class);

        Mockito.doNothing().when(amqpTemplate).send(Mockito.same("my-queue"), Mockito.any());
        Assertions.assertDoesNotThrow(() -> client.run(request));
    }

    private CoreValidD2ConservativeClientProperties buildProperties() {
        return new CoreValidD2ConservativeClientProperties(
                new CoreValidD2ConservativeClientProperties.BindingConfiguration("my-queue",
                                                                           null,
                                                                           "60000",
                                                                           "application-id"));
    }
}
