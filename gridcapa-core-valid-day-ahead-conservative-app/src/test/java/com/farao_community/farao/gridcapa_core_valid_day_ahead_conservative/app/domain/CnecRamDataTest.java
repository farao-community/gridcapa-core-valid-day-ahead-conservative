/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

class CnecRamDataTest {

    @Test
    void testCnecRamInitialization() {
        final Map<String, BigDecimal> ptdfs = Map.of(
                "AA", BigDecimal.valueOf(0.00100),
                "BBB", BigDecimal.valueOf(0.00200),
                "CCCC", BigDecimal.valueOf(0.02500)
        );
        final CnecRamData testData = new CnecRamData("testId", 223, ptdfs);
        Assertions.assertThat(testData)
                .isNotNull()
                .hasFieldOrPropertyWithValue("necId", "testId")
                .hasFieldOrPropertyWithValue("ram0Core", 223);
        Assertions.assertThat(testData.ptdfValues())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyInAnyOrderEntriesOf(ptdfs);
    }

}
