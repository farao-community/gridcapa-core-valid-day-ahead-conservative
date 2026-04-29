package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain;

import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.fasterxml.jackson.annotation.JsonProperty;

public record StudyPoint(@JsonProperty("position") int position, @JsonProperty("vertex") Vertex vertex) {
}
