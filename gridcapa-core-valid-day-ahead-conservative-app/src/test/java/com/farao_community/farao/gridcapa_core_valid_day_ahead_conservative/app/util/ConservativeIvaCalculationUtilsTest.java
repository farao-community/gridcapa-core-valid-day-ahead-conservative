/*
 *  Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamValuesData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.ConservativeIvaCalculationUtils.computeConservativeIVA;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BRANCH_STATUS_OK;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.FRENCH_TSO;

class ConservativeIvaCalculationUtilsTest {

    @Test
    void shouldReturnNullIfBelowThreshold() {
        final ConservativeIvaCalculationUtils.IvaBranchData branchData = new ConservativeIvaCalculationUtils.IvaBranchData(
            null, 0, 0, new ArrayList<>()
        );
        final BigDecimal result = ConservativeIvaCalculationUtils.computeConservativeIVA(
            branchData, 0, 0, 0
        );
        Assertions.assertThat(result)
            .isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "CNAME,TEST_PATL,2,0,3,1,0,0,2", // case with transmission limit
        "CNAME,TEST_TATL,3,0,2,1,0,0,0", // same but with IVAc < AMR + minRealRam
        "BASECASE,TEST,4,0,5,1,0,1,4",   // case using curative margin input
        "BASECASE,TEST,4,2,5,3,0,1,0",   // same but with IVAc < AMR + minRealRam
        "CNAME,TEST,4,0,5,1,1,0,4",      // case using preventive margin input
        "CNAME,TEST,4,2,5,3,1,0,0",      // same but with IVAc < AMR + minRealRam
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

        final CnecRamValuesData ramValues = new CnecRamValuesData(0, 0, BigDecimal.ZERO,
                                                                  amr,
                                                                  0, 0, 0);

        final CnecRamData cnecRamData = getTestCnecRamData(contingencyName, cnecId, ramValues);

        final ConservativeIvaCalculationUtils.IvaBranchData branchData = new ConservativeIvaCalculationUtils.IvaBranchData(cnecRamData,
                                                                                                                           minRealRam,
                                                                                                                           ivaMax,
                                                                                                                           new ArrayList<>());
        final BigDecimal result = computeConservativeIVA(branchData,
                                                      ramThreshold,
                                                      curativeMargin,
                                                      preventiveMargin);

        Assertions.assertThat(result).isEqualTo(BigDecimal.valueOf(expected));
    }

    private static CnecRamData getTestCnecRamData(final String contingencyName,
                                                  final String cnecId,
                                                  final CnecRamValuesData ramValues) {
        //not used here, any values will do
        final CnecRamFValuesData fValues = new CnecRamFValuesData(0, 0, 0, 0, 0, 0, 0);

        return new CnecRamData(cnecId,
                               "test_branch",
                               FRENCH_TSO,
                               contingencyName,
                               BRANCH_STATUS_OK,
                               ramValues,
                               fValues,
                               new HashMap<>());
    }
}
