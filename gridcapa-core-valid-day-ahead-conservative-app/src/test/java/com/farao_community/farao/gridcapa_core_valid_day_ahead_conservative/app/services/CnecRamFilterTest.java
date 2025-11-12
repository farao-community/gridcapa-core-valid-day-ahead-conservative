package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamValuesData;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CnecRamFilterTest {

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
        data.add(new CnecRamData("1", "empty", "AT", "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1", CnecRamFilter.EXCLUDE_NE_NAME + " abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1", "abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", "OUT",
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1" + CnecRamFilter.EXCLUDE_SUFFIX_NEC_ID_BEFORE, "abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1" + CnecRamFilter.EXCLUDE_SUFFIX_NEC_ID_AFTER, "abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1", "abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), CnecRamFilter.MIN_AMR_VALUE, 7, 8, 9),
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
        data.add(new CnecRamData("1", "abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("2", "abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 getDummyRamValues(),
                                 getDummyFValues(),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("3", "abbnndd", CnecRamFilter.FRENCH_TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
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
