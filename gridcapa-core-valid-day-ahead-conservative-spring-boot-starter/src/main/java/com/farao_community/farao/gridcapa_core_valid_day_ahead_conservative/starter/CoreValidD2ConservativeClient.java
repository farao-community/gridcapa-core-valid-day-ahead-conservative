/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.starter;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.JsonApiConverter;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Antoine Limouzin {@literal <antoine.limouzin at rte-france.com>}
 */
@Component
public class CoreValidD2ConservativeClient {
    private static final int DEFAULT_PRIORITY = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreValidD2ConservativeClient.class);
    private static final String CONTENT_ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/vnd.api+json";

    private final AmqpTemplate amqpTemplate;
    private final CoreValidD2ConservativeClientProperties coreValidD2ConservativeClientProperties;
    private final JsonApiConverter jsonConverter;

    public CoreValidD2ConservativeClient(final AmqpTemplate amqpTemplate,
                                         final CoreValidD2ConservativeClientProperties coreValidD2ConservativeClientProperties) {
        this.amqpTemplate = amqpTemplate;
        this.coreValidD2ConservativeClientProperties = coreValidD2ConservativeClientProperties;
        this.jsonConverter = new JsonApiConverter();
    }

    public void run(final CoreValidD2ConservativeRequest coreValidD2ConservativeRequest,
                    final int priority) {
        LOGGER.info("Core valid request sent: {}", coreValidD2ConservativeRequest);
        amqpTemplate.send(coreValidD2ConservativeClientProperties.binding().destination(),
                          coreValidD2ConservativeClientProperties.binding().routingKey(),
                          buildMessage(coreValidD2ConservativeRequest, priority));
    }

    public void run(final CoreValidD2ConservativeRequest coreValidD2ConservativeRequest) {
        run(coreValidD2ConservativeRequest, DEFAULT_PRIORITY);
    }

    public Message buildMessage(final CoreValidD2ConservativeRequest coreValidD2ConservativeRequest,
                                final int priority) {
        return MessageBuilder.withBody(jsonConverter.toJsonMessage(coreValidD2ConservativeRequest))
                .andProperties(buildMessageProperties(priority))
                .build();
    }

    private MessageProperties buildMessageProperties(final int priority) {
        return MessagePropertiesBuilder.newInstance()
                .setAppId(coreValidD2ConservativeClientProperties.binding().applicationId())
                .setContentEncoding(CONTENT_ENCODING)
                .setContentType(CONTENT_TYPE)
                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                .setExpiration(coreValidD2ConservativeClientProperties.binding().expiration())
                .setPriority(priority)
                .build();
    }
}
