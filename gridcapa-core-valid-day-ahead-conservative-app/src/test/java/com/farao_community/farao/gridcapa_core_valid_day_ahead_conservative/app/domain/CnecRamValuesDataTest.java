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

class CnecRamValuesDataTest {

    @Test
    void testRamValues() {
        final BigDecimal minRamFactor = new BigDecimal("44.44");
        final CnecRamValuesData ramValues = new CnecRamValuesData(22, 33, minRamFactor, 55, 66, 77, 88);
        Assertions.assertThat(ramValues)
                .isNotNull()
                .hasFieldOrPropertyWithValue("ram", 22)
                .hasFieldOrPropertyWithValue("ram0Core", 33)
                .hasFieldOrPropertyWithValue("minRamFactor", minRamFactor)
                .hasFieldOrPropertyWithValue("amr", 55)
                .hasFieldOrPropertyWithValue("ltaMargin", 66)
                .hasFieldOrPropertyWithValue("cva", 77)
                .hasFieldOrPropertyWithValue("iva", 88);
    }
}
