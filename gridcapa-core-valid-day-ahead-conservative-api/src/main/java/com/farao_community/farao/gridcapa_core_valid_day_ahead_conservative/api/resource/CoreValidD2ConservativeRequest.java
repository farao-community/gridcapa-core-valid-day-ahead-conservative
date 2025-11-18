/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource;

import com.farao_community.farao.gridcapa.task_manager.api.TaskParameterDto;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.OffsetDateTimeDeserializer;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.OffsetDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;

@Type("core-valid-day-ahead-conservative-request")
public class CoreValidD2ConservativeRequest {
    @Id
    private final String id;
    private final String currentRunId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private final OffsetDateTime timestamp;
    private final CoreValidD2ConservativeFileResource cnecRam;
    private final CoreValidD2ConservativeFileResource vertices;
    private final boolean launchedAutomatically;
    private final List<TaskParameterDto> taskParameterList;

    @JsonCreator
    public CoreValidD2ConservativeRequest(final @JsonProperty("id") String id,
                                          final @JsonProperty("currentRunId") String currentRunId,
                                          final @JsonProperty("timestamp") OffsetDateTime timestamp,
                                          final @JsonProperty("cnecRam") CoreValidD2ConservativeFileResource cnecRam,
                                          final @JsonProperty("vertices") CoreValidD2ConservativeFileResource vertices,
                                          final @JsonProperty("launchedAutomatically") boolean launchedAutomatically,
                                          final @JsonProperty("taskParameterList") List<TaskParameterDto> taskParameterList) {
        this.id = id;
        this.currentRunId = currentRunId;
        this.timestamp = timestamp;
        this.cnecRam = cnecRam;
        this.vertices = vertices;
        this.launchedAutomatically = launchedAutomatically;
        this.taskParameterList = taskParameterList;
    }

    public CoreValidD2ConservativeRequest(final String id,
                                          final String currentRunId,
                                          final OffsetDateTime timestamp,
                                          final CoreValidD2ConservativeFileResource cnecRam,
                                          final CoreValidD2ConservativeFileResource vertices,
                                          final List<TaskParameterDto> taskParameterList) {
        this(id, currentRunId, timestamp, cnecRam, vertices, false, taskParameterList);
    }

    public String getId() {
        return id;
    }

    public String getCurrentRunId() {
        return currentRunId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public CoreValidD2ConservativeFileResource getCnecRam() {
        return cnecRam;
    }

    public CoreValidD2ConservativeFileResource getVertices() {
        return vertices;
    }

    public boolean getLaunchedAutomatically() {
        return launchedAutomatically;
    }

    public List<TaskParameterDto> getTaskParameterList() {
        return taskParameterList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
