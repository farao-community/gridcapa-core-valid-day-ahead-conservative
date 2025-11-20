/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CnecRamUtils;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CnecRamFilterTest {

    private static final String BASECASE = "BASE_CASE";
    private static final String NE_NAME = "abbnndd";
    private static final String ID = "1";

    @Test
    void filterBeforeIvaCalculusEmptyGivesEmpty() {
        List<CnecRamData> data = new ArrayList<>();
        List<CnecRamData> output = CnecRamFilter.filterBeforeIvaCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void filterBeforeIvaCalculusFiltersGivesEmpty() {
        List<CnecRamData> data = new ArrayList<>();
        data.add(new CnecRamData(ID, "empty", "AT", BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData(ID, CnecRamUtils.PREFIX_NO_CURRENT_LIMIT + " abbnndd", CnecRamUtils.FRENCH_TSO, BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData(ID, NE_NAME, CnecRamUtils.FRENCH_TSO, BASECASE, "OUT",
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData(ID + CnecRamUtils.EXCLUDE_SUFFIX_NEC_ID_BEFORE, NE_NAME, CnecRamUtils.FRENCH_TSO, BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData(ID + CnecRamUtils.EXCLUDE_SUFFIX_NEC_ID_AFTER, NE_NAME, CnecRamUtils.FRENCH_TSO, BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData(ID, NE_NAME, CnecRamUtils.FRENCH_TSO, BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), CnecRamUtils.MIN_AMR_VALUE, 7, 8, 9),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        List<CnecRamData>  output = CnecRamFilter.filterBeforeIvaCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isEmpty();
    }

    private static @NotNull CnecRamFValuesData getDummyFValues() {
        return new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7);
    }

    private static @NotNull CnecRamValuesData getDummyRamValues() {
        return new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9);
    }

    @Test
    void filterBeforeIvaCalculusFiltersGivesAllOK() {
        List<CnecRamData> data = new ArrayList<>();
        data.add(new CnecRamData(ID, NE_NAME, CnecRamUtils.FRENCH_TSO, BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("2", NE_NAME, CnecRamUtils.FRENCH_TSO, BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("3", NE_NAME, CnecRamUtils.FRENCH_TSO, BASECASE, CnecRamUtils.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        List<CnecRamData>  output = CnecRamFilter.filterBeforeIvaCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3);
    }
}
