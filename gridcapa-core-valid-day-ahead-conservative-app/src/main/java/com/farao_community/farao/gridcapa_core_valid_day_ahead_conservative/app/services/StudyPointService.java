/*
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.IvaBranchData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.RamVertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.StudyPoint;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.gridcapa_core_valid_day_ahead_conservative.xsd.f230.Point;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudyPointService {

    private final CoreHubsConfiguration coreHubsConfiguration;

    public StudyPointService(final CoreHubsConfiguration coreHubsConfiguration) {
        this.coreHubsConfiguration = coreHubsConfiguration;
    }

    public List<StudyPoint> generateStudyPoints(final List<Vertex> verticesForCalculus,
                                                final List<IvaBranchData> branches,
                                                final Map<CoreHub, Point> netPositions) {
        final int position = netPositions.values()
                .stream()
                .findAny()
                .orElseThrow(() -> new CoreValidD2ConservativeInvalidDataException("No net positions while generating study points"))
                .getPosition();

        final Optional<Tuple<Vertex, BigDecimal>> closestVertexTuple = verticesForCalculus.stream()
                .map(vertex -> new Tuple<>(vertex, calculateDistance(vertex, netPositions, coreHubsConfiguration)))
                .min(Comparator.comparing(Tuple::_2, BigDecimal::compareTo));

        if (closestVertexTuple.isPresent()) {
            final List<Vertex> worstVertices = branches.stream()
                    .flatMap(ivaBranchData -> ivaBranchData.getWorstVertices().stream())
                    .map(RamVertex::vertex)
                    .toList();

            final List<StudyPoint> returnedStudyPoints = new ArrayList<>(verticesToStudyPoints(worstVertices, position));
            final Vertex closestVertex = closestVertexTuple.get()._1();
            if (!worstVertices.contains(closestVertex)) {
                returnedStudyPoints.add(new StudyPoint(position, closestVertex));
            }
            return returnedStudyPoints;
        } else {
            throw new CoreValidD2ConservativeInvalidDataException("No vertices while generating study points");
        }
    }

    private List<StudyPoint> verticesToStudyPoints(final List<Vertex> worstVertices, final int position) {
        return worstVertices.stream()
                .map(vertex -> new StudyPoint(position, vertex))
                .toList();
    }

    private BigDecimal calculateDistance(final Vertex vertex,
                                         final Map<CoreHub, Point> netPositions,
                                         final CoreHubsConfiguration coreHubsConfiguration) {
        final List<CoreHub> coreHubs = coreHubsConfiguration.getCoreHubs();
        return coreHubs.stream()
                .map(coreHub -> {
                    final BigDecimal nps = netPositions.get(coreHub).getQuantity().subtract(BigDecimal.valueOf(vertex.coordinates().get(coreHub.clusterVerticeCode())));
                    return BigDecimal.valueOf(coreHub.coefficient()).multiply(nps.multiply(nps));
                })
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new CoreValidD2ConservativeInvalidDataException("Impossible to calculate distance for vertex id: " + vertex.vertexId()))
                .sqrt(new MathContext(5, RoundingMode.HALF_UP));
    }
}
