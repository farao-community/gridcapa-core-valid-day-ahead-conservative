/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.RamVertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.request.CoreValidD2TaskParameters;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class BranchMaxIvaServiceTest {

    private static final String TEST_NAME_FILTEREDOUT = "testName filteredout";
    private static final String EMPTY_STRING = "";
    private static final boolean IS_HVDC_HUB = false;
    private static final double COEFFICIENT = 1.0;
    private static final int ZERO_INT = 0;

    @MockitoBean
    CoreHubsConfiguration coreHubsConfiguration;

    @Autowired
    BranchMaxIvaService branchMaxIvaService;

    @Test
    void computeBranchDataEmptyTest() {
        Assertions.assertThat(branchMaxIvaService.computeBranchData(List.of(), List.of(), null))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void computeBranchDataOkTest() {
        final List<Vertex> vertices = getTestVertices();
        final CnecRamData cnec = getTestCnec();
        final List<CoreHub> coreHubs = getTestCoreHubs();
        Mockito.when(coreHubsConfiguration.getCoreHubs()).thenReturn(coreHubs);
        Assertions.assertThat(branchMaxIvaService.computeBranchData(vertices, List.of(cnec), getTestParameters()))
                .isNotNull()
                .isNotEmpty()
                .first()
                .isNotNull()
                .hasFieldOrPropertyWithValue("cnec", cnec)
                .hasFieldOrPropertyWithValue("minRealRam", -250)
                .hasFieldOrPropertyWithValue("ivaMax", 360);
    }

    @ParameterizedTest
    @CsvSource({
        "-10,10,4,-250",
        "-10,2,2,-250",
        "-100,2,1,-250"
    })
    void getWorstVerticesUnderRamThresholdTest(int ramLimit, int maxVertexPerBranch, int expectedCount, int expectedWorstValue) {
        final List<Vertex> vertices = getTestVertices();
        final CnecRamData cnec = getTestCnecPtdf();
        final List<CoreHub> coreHubs = getTestCoreHubs();
        Mockito.when(coreHubsConfiguration.getCoreHubs()).thenReturn(coreHubs);
        final List<RamVertex> worstVertices = ReflectionTestUtils.invokeMethod(branchMaxIvaService,
                                                                               "getWorstVerticesUnderRamThreshold",
                                                                               vertices,
                                                                               cnec,
                                                                               ramLimit,
                                                                               maxVertexPerBranch);
        Assertions.assertThat(worstVertices)
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedCount)
                .first()
                .isNotNull()
                .hasFieldOrPropertyWithValue("realRam", expectedWorstValue);
    }

    @Test
    void computeMaxIvaReturnsZero() {
        final CnecRamData cnec = Mockito.mock(CnecRamData.class);
        Mockito.when(cnec.neName()).thenReturn(TEST_NAME_FILTEREDOUT);
        final String[] excludedBranchNames = {TEST_NAME_FILTEREDOUT};
        final int maxIva = ReflectionTestUtils.invokeMethod(branchMaxIvaService, "computeMaxIva", cnec, excludedBranchNames, 20);
        Assertions.assertThat(maxIva)
                .isEqualTo(ZERO_INT);
    }

    @ParameterizedTest
    @CsvSource({
        "0,0,0,0,0,0,0",
        "0,0,0,1000,500,10,500",
        "1200,300,800,1000,500,20,360",
        "1200,300,800,700,500,20,60",
        "300,300,800,700,500,20,0",
        "-300,-300,-800,-700,-500,30,0"
    })
    void computeMaxIvaReturnsOK(int fmax, int frm, int f0Core, int amr, int cva, int percent, int expected) {
        final CnecRamData cnec = Mockito.mock(CnecRamData.class);
        Mockito.when(cnec.neName()).thenReturn(TEST_NAME_FILTEREDOUT);
        final CnecRamFValuesData fValues = Mockito.mock(CnecRamFValuesData.class);
        Mockito.when(fValues.fMax()).thenReturn(fmax);
        Mockito.when(fValues.frm()).thenReturn(frm);
        Mockito.when(fValues.f0Core()).thenReturn(f0Core);
        Mockito.when(cnec.fValues()).thenReturn(fValues);
        final CnecRamValuesData ramValues = Mockito.mock(CnecRamValuesData.class);
        Mockito.when(ramValues.amr()).thenReturn(amr);
        Mockito.when(ramValues.cva()).thenReturn(cva);
        Mockito.when(cnec.ramValues()).thenReturn(ramValues);

        final String[] excludedBranchNames = {"test1", "test2", "test3"};
        final int maxIva = ReflectionTestUtils.invokeMethod(branchMaxIvaService, "computeMaxIva", cnec, excludedBranchNames, percent);
        Assertions.assertThat(maxIva)
                .isEqualTo(expected);
    }

    private CnecRamData getTestCnec() {
        final CnecRamValuesData ram = new CnecRamValuesData(ZERO_INT, 227, BigDecimal.ZERO, 1000, ZERO_INT, 500, ZERO_INT);
        final CnecRamFValuesData fValues = new CnecRamFValuesData(1200, 300, ZERO_INT, 800, ZERO_INT, ZERO_INT, ZERO_INT);
        final Map<String, BigDecimal> ptdfs = getCnecTestPtdfs();
        return  new CnecRamData("testId",
                                           "testName",
                                           "testTSO",
                                           "testContignency",
                                           "testBS",
                                           ram,
                                           fValues,
                                           ptdfs);
    }

    private CnecRamData getTestCnecPtdf() {
        final CnecRamData cnec = Mockito.mock(CnecRamData.class);
        final CnecRamValuesData ram = Mockito.mock(CnecRamValuesData.class);
        Mockito.when(ram.ram0Core()).thenReturn(227);
        Mockito.when(cnec.ramValues()).thenReturn(ram);
        final Map<String, BigDecimal> ptdfs = getCnecTestPtdfs();
        Mockito.when(cnec.ptdfValues()).thenReturn(ptdfs);
        return cnec;
    }

    private Map<String, BigDecimal> getCnecTestPtdfs() {
        final Map<String, BigDecimal> ptdfs = new HashMap<>();
        ptdfs.put("PT_AAA", BigDecimal.valueOf(0.1));
        ptdfs.put("PT_BBB", BigDecimal.valueOf(0.25));
        ptdfs.put("PT_CCC", BigDecimal.valueOf(0.5));
        return ptdfs;
    }

    private List<CoreHub> getTestCoreHubs() {
        final List<CoreHub> corehubs = new ArrayList<>();
        corehubs.add(new CoreHub(EMPTY_STRING, EMPTY_STRING, "PT_AAA", EMPTY_STRING, "AA", IS_HVDC_HUB, COEFFICIENT));
        corehubs.add(new CoreHub(EMPTY_STRING, EMPTY_STRING, "PT_BBB", EMPTY_STRING, "BB", IS_HVDC_HUB, COEFFICIENT));
        corehubs.add(new CoreHub(EMPTY_STRING, EMPTY_STRING, "PT_CCC", EMPTY_STRING, "CC", IS_HVDC_HUB, COEFFICIENT));
        return corehubs;
    }

    private List<Vertex> getTestVertices() {
        final List<Vertex> tests = new ArrayList<>();

        tests.add(new Vertex(1, getNpTestMap(100, 200, 300)));
        tests.add(new Vertex(2, getNpTestMap(570, 400, 640)));
        tests.add(new Vertex(3, getNpTestMap(450, 200, 300)));
        tests.add(new Vertex(4, getNpTestMap(100, 200, 284)));
        tests.add(new Vertex(5, getNpTestMap(180, 400, 300)));
        tests.add(new Vertex(6, getNpTestMap(100, 200, 250)));
        tests.add(new Vertex(7, getNpTestMap(30, 200, 200)));
        tests.add(new Vertex(8, getNpTestMap(630, 200, 300)));
        tests.add(new Vertex(9, getNpTestMap(100, 200, 234)));
        tests.add(new Vertex(10, getNpTestMap(50, 200, 234)));
        return tests;
    }

    private Map<String, Integer> getNpTestMap(final int i1,
                                              final int i2,
                                              final int i3) {
        final Map<String, Integer> testNps = new HashMap<>();
        testNps.put("AA", i1);
        testNps.put("BB", i2);
        testNps.put("CC", i3);
        return testNps;
    }

    private CoreValidD2TaskParameters getTestParameters() {
        final CoreValidD2TaskParameters params = Mockito.mock(CoreValidD2TaskParameters.class);
        Mockito.when(params.getMaxVerticesPerBranch()).thenReturn(5);
        Mockito.when(params.getRamThreshold()).thenReturn(-10);
        Mockito.when(params.getMinRamMccc()).thenReturn(20);
        Mockito.when(params.getExcludedBranches()).thenReturn("[FR-FR] Creys - Saint-Vulbas 2 [DIR];[FR-FR] Creys - Saint-Vulbas 2 [OPP];"
                                                              + "[FR-CH] Cornier - Riddes [DIR];[FR-CH] Cornier - Riddes [OPP];"
                                                              + "[FR-FR] Creys - Genissiat 1 [DIR];[FR-FR] Creys - Genissiat 1 [OPP];[FR-FR] Creys - Saint-Vulbas 1 [DIR];"
                                                              + "[FR-FR] Creys - Saint-Vulbas 1 [OPP];[FR-FR] Frasnes - Genissiat [DIR];[FR-FR] Frasnes - Genissiat [OPP];"
                                                              + "[FR-FR] Creys - Genissiat 2 [DIR];[FR-FR] Creys - Genissiat 2 [OPP];[FR-CH] Cornier - Saint-Triphon [DIR];"
                                                              + "[FR-CH] Cornier - Saint-Triphon [OPP]");
        return params;
    }
}
