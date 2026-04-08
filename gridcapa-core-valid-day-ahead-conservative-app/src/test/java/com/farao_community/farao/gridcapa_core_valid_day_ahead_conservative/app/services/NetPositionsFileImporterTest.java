/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
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
    void shouldFailOnInvalidFile() throws IOException {
        try (final InputStream inputStream = getFailingInputStream()) {
            Assertions.assertThatThrownBy(
                () -> NetPositionsFileImporter.getCoreNetPositions(inputStream, coreHubsConfiguration.getCoreHubs())
            ).hasMessage("Cannot unmarshal ReportingInformationMarketDocument");
        }
    }

    private static InputStream getFailingInputStream() {
        return new InputStream() {
            public int read() throws IOException {
                throw new IOException();
            }
        };
    }
}
