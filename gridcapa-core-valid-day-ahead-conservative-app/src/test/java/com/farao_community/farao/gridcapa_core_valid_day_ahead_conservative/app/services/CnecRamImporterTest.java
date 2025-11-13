/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class CnecRamImporterTest {

    private static final String[] PTDF_STRINGS = {"PTDF_DE_AL", "PTDF_BE_AL", "PTDF_SK", "PTDF_SI", "PTDF_RO", "PTDF_PL",
                                                  "PTDF_NL", "PTDF_HU", "PTDF_HR", "PTDF_FR", "PTDF_DE", "PTDF_CZ",
                                                  "PTDF_BE", "PTDF_AT"};
    @Autowired
    CoreHubsConfiguration coreHubsConfiguration;

    @Test
    void testImportCnecRam() throws IOException {
        try (final InputStream inputStream = getClass().getResource("/cnecRamFileOk.csv").openStream()) {
            final List<CnecRamData> cnecRams = CnecRamImporter.importCnecRam(inputStream, coreHubsConfiguration.getCoreHubs());
            Assertions.assertThat(cnecRams)
                    .isNotNull()
                    .hasSize(3)
                    .element(0)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("necId", "testOK1")
                    .hasFieldOrPropertyWithValue("neName", "testOK1_NAME")
                    .hasFieldOrPropertyWithValue("tso", "AT");
            Assertions.assertThat(cnecRams.getFirst().getPtdfValues())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(14)
                    .containsOnlyKeys(PTDF_STRINGS)
                    .doesNotContainValue(new BigDecimal("0.2"))
                    .doesNotContainValue(new BigDecimal("0.3"))
                    .containsValue(new BigDecimal("0.1"));
            Assertions.assertThat(cnecRams.getFirst().getPtdfValues().get("PTDF_DE_AL")).isEqualByComparingTo(BigDecimal.ZERO);
            Assertions.assertThat(cnecRams.get(1).getPtdfValues())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(14)
                    .containsOnlyKeys(PTDF_STRINGS)
                    .doesNotContainValue(new BigDecimal("0.1"))
                    .doesNotContainValue(new BigDecimal("0.3"))
                    .containsValue(new BigDecimal("0.2"));
            Assertions.assertThat(cnecRams.get(2).getPtdfValues())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(14)
                    .containsOnlyKeys(PTDF_STRINGS)
                    .doesNotContainValue(new BigDecimal("0.1"))
                    .doesNotContainValue(new BigDecimal("0.2"))
                    .containsValue(new BigDecimal("0.3"));
            Assertions.assertThat(cnecRams.get(2).getPtdfValues().get("PTDF_BE_AL")).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Test
    void testFileImportThrowsException() throws IOException {
        try (final InputStream inputStream = getClass().getResource("/cnecRamFileKo.csv").openStream()) {
            final List<CoreHub> coreHubs = coreHubsConfiguration.getCoreHubs();
            Assertions.assertThatExceptionOfType(CoreValidD2ConservativeInvalidDataException.class).isThrownBy(() -> CnecRamImporter.importCnecRam(inputStream, coreHubs))
                    .withMessage("Exception occurred during parsing Cnec Ram file");
        }
    }
}
