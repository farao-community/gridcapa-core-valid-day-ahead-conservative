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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SpringBootTest
class StudyPointServiceTest {

    private static final int POSITION = 3;
    @Autowired
    private StudyPointService studyPointService;

    @MockitoBean
    private CoreHubsConfiguration coreHubsConfiguration;

    private static final CoreHub CORE_HUB_1 = new CoreHub("France",
                                                        "FR",
                                                        "PTDF_FR",
                                                        "FR-CORE",
                                                        "FR",
                                                        false,
                                                        0.9);

    private static final CoreHub CORE_HUB_2 = new CoreHub("Allemagne",
                                                                "DE",
                                                                "PTDF_DE",
                                                                "DE-CORE",
                                                                "DE",
                                                                false,
                                                                0.5);

    @Test
    void testStudyPoint() {
        final List<CoreHub> coreHubs = getTestCoreHubs();
        Mockito.when(coreHubsConfiguration.getCoreHubs()).thenReturn(coreHubs);
        final List<StudyPoint> points1 = studyPointService.generateStudyPoints(getTestVertices(), getTestBranchesNoWorseVertices(), getTestNetPositions());
        Assertions.assertThat(points1).hasSize(1);
        Assertions.assertThat(points1.getFirst().position()).isEqualTo(POSITION);
        Assertions.assertThat(points1.getFirst().vertex().vertexId()).isEqualTo(1);

        final List<StudyPoint> points2 = studyPointService.generateStudyPoints(getTestVertices(), getTestBranches(), getTestNetPositions());
        Assertions.assertThat(points2).hasSize(2);
    }

    @Test
    void testStudyPointWithNoVertices() {
        Assertions.assertThatExceptionOfType(CoreValidD2ConservativeInvalidDataException.class)
                .isThrownBy(() -> studyPointService.generateStudyPoints(List.of(), getTestBranches(), getTestNetPositions()));
    }

    private List<CoreHub> getTestCoreHubs() {
        return List.of(CORE_HUB_1, CORE_HUB_2);
    }

    private List<Vertex> getTestVertices() {
        return List.of(new Vertex(1, Map.of("FR", 100, "DE", 100)),
                       new Vertex(2, Map.of("FR", 2000, "DE", 2000)),
                       new Vertex(3, Map.of("FR", 3000, "DE", 3000)),
                       new Vertex(4, Map.of("FR", 1000, "DE", 1000)),
                       new Vertex(5, Map.of("FR", 2000, "DE", 2000)),
                       new Vertex(6, Map.of("FR", 3000, "DE", 3000)));
    }

    private List<IvaBranchData> getTestBranchesNoWorseVertices() {
        return List.of(new IvaBranchData(null, 0, 0, List.of()));
    }

    private List<IvaBranchData> getTestBranches() {
        return List.of(new IvaBranchData(null, 0, 0, List.of(new RamVertex(750, new Vertex(1, Map.of("FR", 100, "DE", 100))),
                                                             new RamVertex(750, new Vertex(2, Map.of("FR", 2000, "DE", 2000))))));
    }

    private Map<CoreHub, Point> getTestNetPositions() {
        final Point point1 = new Point();
        point1.setPosition(POSITION);
        point1.setQuantity(BigDecimal.valueOf(-500));
        final Point point2 = new Point();
        point2.setPosition(POSITION);
        point2.setQuantity(BigDecimal.valueOf(750));
        return Map.of(CORE_HUB_1, point1,
                      CORE_HUB_2, point2);
    }
}

