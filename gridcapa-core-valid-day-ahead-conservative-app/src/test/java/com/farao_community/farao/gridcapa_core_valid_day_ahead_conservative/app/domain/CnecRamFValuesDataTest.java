/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CnecRamFValuesDataTest {

    @Test
    void testDataValues() {
        final CnecRamFValuesData fValues = new CnecRamFValuesData(200, 300, 400, 500, 600, 700, 800);
        Assertions.assertThat(fValues)
                .isNotNull()
                .hasFieldOrPropertyWithValue("fMax", 200)
                .hasFieldOrPropertyWithValue("frm", 300)
                .hasFieldOrPropertyWithValue("fRef", 400)
                .hasFieldOrPropertyWithValue("f0Core", 500)
                .hasFieldOrPropertyWithValue("fUaf", 600)
                .hasFieldOrPropertyWithValue("f0All", 700)
                .hasFieldOrPropertyWithValue("fLtaMax", 800);
    }
}
