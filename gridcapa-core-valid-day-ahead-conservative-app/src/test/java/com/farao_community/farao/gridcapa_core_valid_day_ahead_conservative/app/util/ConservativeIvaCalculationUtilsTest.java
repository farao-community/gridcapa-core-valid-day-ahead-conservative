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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.ConservativeIvaCalculationUtils.computeConservativeIVA;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.BRANCH_STATUS_OK;
import static com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util.CoreValidD2Constants.FRENCH_TSO;

class ConservativeIvaCalculationUtilsTest {

    @ParameterizedTest
    @CsvSource({
        "CNAME,TEST_PATL,2,0,3,1,0,0,2", // case min
        "CNAME,TEST_TATL,3,0,2,1,0,0,0", // same but with IVAc < AMR + minRealRam
        "BASECASE,TEST,4,0,5,1,0,1,4",   // case clamp with curative
        "BASECASE,TEST,4,2,5,3,0,1,0",   // same but with IVAc < AMR + minRealRam
        "CNAME,TEST,4,0,5,1,1,0,4",      // case clamp with preventive
        "CNAME,TEST,4,2,5,3,1,0,0",      // same but with IVAc < AMR + minRealRam
        "CNAME,TEST,4,4,5,3,1,0,0"       // case minRealRam > RamThreshold
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

        final CnecRamData cnecRamData = getCnecRamData(contingencyName, cnecId, ramValues);

        final ConservativeIvaCalculationUtils.IvaBranchData branchData = new ConservativeIvaCalculationUtils.IvaBranchData(cnecRamData,
                                                                                                                           minRealRam,
                                                                                                                           ivaMax,
                                                                                                                           new ArrayList<>());
        final Integer result = computeConservativeIVA(branchData,
                                                      ramThreshold,
                                                      curativeMargin,
                                                      preventiveMargin);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    private static CnecRamData getCnecRamData(final String contingencyName,
                                              final String cnecId,
                                              final CnecRamValuesData ramValues) {
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
