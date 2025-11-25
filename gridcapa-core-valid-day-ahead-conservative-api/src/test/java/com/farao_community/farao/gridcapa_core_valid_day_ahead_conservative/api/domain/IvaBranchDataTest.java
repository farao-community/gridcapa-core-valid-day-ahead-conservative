/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain;

import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class IvaBranchDataTest {

    private static final int ZERO_INT = 0;
    private static final int MIN_REAL_RAM = 350;
    private static final int IVA_MAX = 200;
    private static final int CONSERVATIVE_IVA = 60;

    @Test
    void testCreateBranchData() {
        final IvaBranchData branch = getTestBranch();
        branch.setConservativeIva(CONSERVATIVE_IVA);
        Assertions.assertThat(branch)
                .isNotNull()
                .hasFieldOrPropertyWithValue("conservativeIva", CONSERVATIVE_IVA)
                .hasFieldOrPropertyWithValue("ivaMax", IVA_MAX)
                .hasFieldOrPropertyWithValue("minRealRam", MIN_REAL_RAM);
    }

    @Test
    void testCnecRamDataApi() {
        final CnecRamData cnec =  getTestCnec();
        Assertions.assertThat(cnec)
                .isNotNull();
        Assertions.assertThat(cnec.getRam0Core())
                .isEqualTo(cnec.ramValues().ram0Core());
        Assertions.assertThat(cnec.getAmr())
                .isEqualTo(cnec.ramValues().amr());
    }

    @Test
    void testJson() throws IOException {
        final IvaBranchData branch = getTestBranch();
        branch.setConservativeIva(CONSERVATIVE_IVA);
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        final String json = ow.writeValueAsString(branch);
        final IvaBranchData jsonBranch = objectMapper.reader().readValue(new StringReader(json), IvaBranchData.class);
        Assertions.assertThat(jsonBranch)
                .isNotNull()
                .isExactlyInstanceOf(IvaBranchData.class)
                .isEqualTo(branch);
    }

    @Test
    void testEquals() {
        final IvaBranchData branch = getTestBranch();
        Assertions.assertThat(branch.equals(getTestBranch())).isTrue();
        final IvaBranchData other1 =  new IvaBranchData(null, MIN_REAL_RAM, IVA_MAX, getTestRamVertices());
        final IvaBranchData other2 =  new IvaBranchData(getTestCnec(), MIN_REAL_RAM, IVA_MAX, null);
        final IvaBranchData other3 =  new IvaBranchData(getTestCnec(), 120, IVA_MAX, getTestRamVertices());
        final IvaBranchData other4 =  new IvaBranchData(getTestCnec(), MIN_REAL_RAM, 120, getTestRamVertices());
        Assertions.assertThat(branch.equals(other1)).isFalse();
        Assertions.assertThat(branch.equals(other2)).isFalse();
        Assertions.assertThat(branch.equals(other3)).isFalse();
        Assertions.assertThat(branch.equals(other4)).isFalse();
    }

    @Test
    void testHash() {
        final IvaBranchData branch = getTestBranch();
        final IvaBranchData other1 =  new IvaBranchData(null, MIN_REAL_RAM, IVA_MAX, getTestRamVertices());
        final IvaBranchData other2 =  new IvaBranchData(getTestCnec(), 120, IVA_MAX, getTestRamVertices());
        final IvaBranchData other3 =  new IvaBranchData(getTestCnec(), MIN_REAL_RAM, 120, getTestRamVertices());
        final IvaBranchData other4 =  new IvaBranchData(getTestCnec(), MIN_REAL_RAM, IVA_MAX, null);
        Assertions.assertThat(branch.hashCode())
                .isEqualTo(branch.hashCode())
                .isNotEqualTo(other1.hashCode())
                .isNotEqualTo(other2.hashCode())
                .isNotEqualTo(other3.hashCode())
                .isNotEqualTo(other4.hashCode());
    }

    private @NotNull IvaBranchData getTestBranch() {
        return new IvaBranchData(getTestCnec(), MIN_REAL_RAM, IVA_MAX, getTestRamVertices());
    }

    private CnecRamData getTestCnec() {
        final CnecRamValuesData ram = new CnecRamValuesData(ZERO_INT, 227, BigDecimal.ZERO, 1000, ZERO_INT, 500, ZERO_INT);
        final CnecRamFValuesData fValues = new CnecRamFValuesData(1200, 300, ZERO_INT, 800, ZERO_INT, ZERO_INT, ZERO_INT);
        final Map<String, BigDecimal> ptdfs = getCnecTestPtdfs();
        return  new CnecRamData("testId",
                                "testName",
                                "testTSO",
                                "testContingency",
                                "testBS",
                                ram,
                                fValues,
                                ptdfs);
    }

    private List<RamVertex> getTestRamVertices() {
        final List<RamVertex> tests = new ArrayList<>();

        tests.add(new RamVertex(-250, new Vertex(2, getNpTestMap(570, 400, 640))));
        tests.add(new RamVertex(-70, new Vertex(8, getNpTestMap(630, IVA_MAX, 300))));
        tests.add(new RamVertex(-25, new Vertex(5, getNpTestMap(180, 400, 300))));
        tests.add(new RamVertex(-18, new Vertex(3, getNpTestMap(450, IVA_MAX, 300))));
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

    private Map<String, BigDecimal> getCnecTestPtdfs() {
        final Map<String, BigDecimal> ptdfs = new HashMap<>();
        ptdfs.put("PT_AAA", BigDecimal.valueOf(0.1));
        ptdfs.put("PT_BBB", BigDecimal.valueOf(0.25));
        ptdfs.put("PT_CCC", BigDecimal.valueOf(0.5));
        return ptdfs;
    }
}
