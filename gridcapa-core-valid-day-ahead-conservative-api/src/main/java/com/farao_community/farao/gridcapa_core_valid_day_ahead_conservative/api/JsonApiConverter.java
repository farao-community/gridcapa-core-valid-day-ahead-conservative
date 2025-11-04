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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.SerializationFeature;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.github.jasminb.jsonapi.models.errors.Error;

/**
 * JSON API conversion component
 * Allows automatic conversion from resources or exceptions towards JSON API formatted bytes.
 *
 * @author Antoine Limouzin {@literal <antoine.limouzin at rte-france.com>}
 * @author Marc Schwitzguebel {@literal <marc.schwitzguebel_external at rte-france.com>}
 */
public class JsonApiConverter {
    private final ObjectMapper objectMapper;
    private final ResourceConverter converter;

    public JsonApiConverter() {
        this.objectMapper = createObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        this.converter = createConverter();
    }

    public <T> T fromJsonMessage(final byte[] jsonMessage,
                                 final Class<T> tClass) {
        return converter.readDocument(jsonMessage, tClass).get();
    }

    public <T> byte[] toJsonMessage(final T jsonApiObject) {
        final JSONAPIDocument<?> jsonapiDocument = new JSONAPIDocument<>(jsonApiObject);
        try {
            return converter.writeDocument(jsonapiDocument);
        } catch (DocumentSerializationException e) {
            throw new CoreValidD2ConservativeInternalException("Exception occurred during object conversion", e);
        }
    }

    public byte[] toJsonMessage(final AbstractCoreValidD2ConservativeException exception) {
        final JSONAPIDocument<?> jsonapiDocument = new JSONAPIDocument<>(convertExceptionToJsonError(exception));
        try {
            return converter.writeDocument(jsonapiDocument);
        } catch (DocumentSerializationException e) {
            throw new CoreValidD2ConservativeInternalException("Exception occurred during exception message conversion", e);
        }
    }

    private ResourceConverter createConverter() {
        final ResourceConverter resourceConverter = new ResourceConverter(objectMapper, CoreValidD2ConservativeRequest.class);
        resourceConverter.disableSerializationOption(SerializationFeature.INCLUDE_META);
        return resourceConverter;
    }

    private Error convertExceptionToJsonError(final AbstractCoreValidD2ConservativeException exception) {
        final Error error = new Error();
        error.setStatus(Integer.toString(exception.getStatus()));
        error.setCode(exception.getCode());
        error.setTitle(exception.getTitle());
        error.setDetail(exception.getDetails());
        return error;
    }

    private ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
