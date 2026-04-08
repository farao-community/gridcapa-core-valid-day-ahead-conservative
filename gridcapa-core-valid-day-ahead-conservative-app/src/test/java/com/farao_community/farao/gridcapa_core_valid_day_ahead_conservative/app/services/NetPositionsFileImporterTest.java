/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.model.CoreNetPositions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class NetPositionsFileImporterTest {

    @Autowired
    CoreHubsConfiguration coreHubsConfiguration;

    @Test
    void shouldImportCoreNetPositions() throws IOException {
        try (final InputStream inputStream = getClass().getResource("/20250921-F230-v4-17XTSO-CS------W-to-10V1001C--00085T.xml").openStream()) {
            final CoreNetPositions result = NetPositionsFileImporter.getCoreNetPositions(inputStream, coreHubsConfiguration.getCoreHubs());
            Assertions.assertThat(result.getFrenchNetPosition()).isNotEmpty();
            Assertions.assertThat(result.getNetPositionsOf("DE-CORE")).isNotEmpty();
            Assertions.assertThat(result.getNetPositionsOf("NOT-CORE")).isEmpty();
        }
    }
}
