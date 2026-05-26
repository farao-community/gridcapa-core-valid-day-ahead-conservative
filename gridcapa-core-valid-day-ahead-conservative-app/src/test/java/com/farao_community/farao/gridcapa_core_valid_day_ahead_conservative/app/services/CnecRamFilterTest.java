/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamValuesData;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BASECASE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BRANCH_STATUS_OK;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.FRENCH_TSO;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.MIN_AMR_VALUE;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.PREFIX_NO_CURRENT_LIMIT;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_NEC_ID_AFTER;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.SUFFIX_NEC_ID_BEFORE;

class CnecRamFilterTest {

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
        data.add(new CnecRamData(ID, "empty", "AT", BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        data.add(new CnecRamData(ID, PREFIX_NO_CURRENT_LIMIT + " abbnndd", FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        data.add(new CnecRamData(ID, NE_NAME, FRENCH_TSO, BASECASE, "OUT",
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        data.add(new CnecRamData(ID + SUFFIX_NEC_ID_BEFORE, NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        data.add(new CnecRamData(ID + SUFFIX_NEC_ID_AFTER, NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        data.add(new CnecRamData(ID, NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, MIN_AMR_VALUE, 4),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        List<CnecRamData>  output = CnecRamFilter.filterBeforeIvaCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isEmpty();
    }

    private static @NotNull CnecRamFValuesData getDummyFValues() {
        return new CnecRamFValuesData(1, 2, 3);
    }

    private static @NotNull CnecRamValuesData getDummyRamValues() {
        return new CnecRamValuesData(3, 4, 6);
    }

    @Test
    void filterBeforeIvaCalculusFiltersGivesAllOK() {
        List<CnecRamData> data = new ArrayList<>();
        data.add(new CnecRamData(ID, NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 false
                 )
        );
        data.add(new CnecRamData("2", NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        data.add(new CnecRamData("3", NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 false
                 )
        );
        List<CnecRamData>  output = CnecRamFilter.filterBeforeIvaCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3);
    }

    @Test
    void filterBeforeVerticesCalculusEmptyGivesEmpty() {
        List<CnecRamData> data = new ArrayList<>();
        List<CnecRamData> output = CnecRamFilter.filterBeforeVerticesCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void filterBeforeVerticesCalculusFiltersGivesAllOK() {
        List<CnecRamData> data = new ArrayList<>();
        data.add(new CnecRamData(ID, NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 false
                 )
        );
        data.add(new CnecRamData("2", NE_NAME, FRENCH_TSO, BASECASE, BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of(),
                                 true
                 )
        );
        List<CnecRamData>  output = CnecRamFilter.filterBeforeVerticesCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
    }
}
