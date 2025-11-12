package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamValuesData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CnecRamFilterTest {

    @Test
    void filterBeforeIvaCalculusEmptyGivesEmptty() {
        List<CnecRamData> data = new ArrayList<>();
        List<CnecRamData> output = CnecRamFilter.filterBeforeIvaCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void filterBeforeIvaCalculusFiltersGivesEmptty() {
        List<CnecRamData> data = new ArrayList<>();
        data.add(new CnecRamData("1", "empty", "AT", "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1", CnecRamFilter.EXCLUDE_NE_NAME + " abbnndd", CnecRamFilter.TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1", "abbnndd", CnecRamFilter.TSO, "BASECASE", "OUT",
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1" + CnecRamFilter.EXCLUDE_SUFFIXE_NEC_ID_1, "abbnndd", CnecRamFilter.TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1" + CnecRamFilter.EXCLUDE_SUFFIXE_NEC_ID_2, "abbnndd", CnecRamFilter.TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("1", "abbnndd", CnecRamFilter.TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), CnecRamFilter.MIN_AMR_VALUE, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        List<CnecRamData>  output = CnecRamFilter.filterBeforeIvaCalculus(data);
        Assertions.assertThat(output)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void filterBeforeIvaCalculusFiltersGivesAllOK() {
        List<CnecRamData> data = new ArrayList<>();
        data.add(new CnecRamData("1", "abbnndd", CnecRamFilter.TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("2", "abbnndd", CnecRamFilter.TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
                                 Map.of()
                 )
        );
        data.add(new CnecRamData("3", "abbnndd", CnecRamFilter.TSO, "BASECASE", CnecRamFilter.BRANCH_STATUS_OK,
                                 new CnecRamValuesData(3, 4, BigDecimal.valueOf(5), 6, 7, 8, 9),
                                 new CnecRamFValuesData(1, 2, 3, 4, 5, 6, 7),
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
