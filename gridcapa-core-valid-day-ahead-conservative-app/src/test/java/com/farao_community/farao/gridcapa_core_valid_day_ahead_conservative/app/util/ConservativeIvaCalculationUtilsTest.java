package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamValuesData;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

class ConservativeIvaCalculationUtilsTest {
    @Test
    void shouldReturnNullIfBelowThreshold() {
        final ConservativeIvaCalculationUtils.IvaBranchData branchData = new ConservativeIvaCalculationUtils.IvaBranchData(
                null, 0, 0, new ArrayList<>()
        );
        final Integer result = ConservativeIvaCalculationUtils.computeConservativeAdjustment(
                branchData, 0, 0, 0
        );
        Assertions.assertThat(result)
                .isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "CNAME,TEST_PATL,2,0,3,1,0,0,2", // case min
        "CNAME,TEST_PATL,3,0,2,1,0,0,0", // same but with ivac < amr + minRealRam
        "BASECASE,TEST,4,0,5,1,0,1,4",   // case clamp with curative
        "BASECASE,TEST,4,2,5,3,0,1,0",   // same but with ivac < amr + minRealRam
        "CNAME,TEST,4,0,5,1,1,0,4",      // case clamp with preventive
        "CNAME,TEST,4,2,5,3,1,0,0"       // same but with ivac < amr + minRealRam
    })
    void shouldReturnValues(final String contingencyName,
                            final String cnecId,
                            final int amr,
                            final int minRealRam,
                            final int ivaMax,
                            final int ramThreshold,
                            final int preventiveMargin,
                            final int curativeMargin,
                            final int expected) {

        final CnecRamValuesData ramValues = new CnecRamValuesData(
                0, 0, BigDecimal.ZERO, amr, 0, 0, 0
        );

        final CnecRamData cnecRamData = getCnecRamData(contingencyName, cnecId, ramValues);

        final ConservativeIvaCalculationUtils.IvaBranchData branchData = new ConservativeIvaCalculationUtils.IvaBranchData(
                cnecRamData, minRealRam, ivaMax, new ArrayList<>()
        );
        final Integer result = ConservativeIvaCalculationUtils.computeConservativeAdjustment(
                branchData, ramThreshold, curativeMargin, preventiveMargin
        );

        Assertions.assertThat(result)
                .isEqualTo(expected);
    }

    private static @NotNull CnecRamData getCnecRamData(final String contingencyName,
                                                       final String cnecId,
                                                       final CnecRamValuesData ramValues) {
        final CnecRamFValuesData fValues = new CnecRamFValuesData(
                0, 0, 0, 0, 0, 0, 0
        );

        return new CnecRamData(cnecId,
                               "branch",
                               "FR",
                               contingencyName,
                               "OK",
                               ramValues,
                               fValues,
                               new HashMap<>());
    }
}
