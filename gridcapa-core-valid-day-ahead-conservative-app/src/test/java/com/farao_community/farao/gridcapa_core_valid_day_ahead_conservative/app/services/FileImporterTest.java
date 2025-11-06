/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeFileResource;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Amira Kahya {@literal <amira.kahya at rte-france.com>}
 */
@SpringBootTest
class FileImporterTest {

    @Mock
    private UrlValidationService urlValidationService;

    @Autowired
    private FileImporter fileImporter;

    private CoreValidD2ConservativeFileResource createFileResource(final String filename, final URL resource) {
        return new CoreValidD2ConservativeFileResource(filename, resource.toExternalForm());
    }

    @Test
    void shouldImportVerticesFromCoreHubSettings() {
        final CoreValidD2ConservativeFileResource verticesFile = createFileResource("vertex",  getClass().getResource("/fake-vertice.csv"));
        final List<Vertex> vertices = fileImporter.importVertices(verticesFile);
        Assertions.assertThat(vertices)
                .isNotNull()
                .hasSize(4);
        final Vertex vertex = vertices.getFirst();
        Assertions.assertThat(vertex.vertexId()).isEqualTo(1);
        final Map<String, Integer> entries = Map.of(
                "FR", 11,
                "BE", 111,
                "BE_AL", 0,
                "DE", 1111,
                "DE_AL", 11
        );
        final Map<String, Integer> positions = vertex.coordinates();
        Assertions.assertThat(positions)
                .hasSize(14)
                .containsAllEntriesOf(entries);
    }

    @Test
    void importVerticeShouldThrowCoreValidIntradayInvalidDataException() throws Exception {

        final CoreValidD2ConservativeFileResource verticesFile = createFileResource("vertex", new URI("https://example.com/vertice.csv").toURL());

        when(urlValidationService.openUrlStream(anyString())).thenThrow(new CoreValidD2ConservativeInvalidDataException("Connection failed"));

        Assertions.assertThatExceptionOfType(CoreValidD2ConservativeInvalidDataException.class)
                .isThrownBy(() -> fileImporter.importVertices(verticesFile))
                .withMessage("Cannot import vertices file from URL 'https://example.com/vertice.csv'");
    }

    @Test
    void shouldImportCnecRamFromCoreHubSettings() {
        final CoreValidD2ConservativeFileResource cnecRamFile = createFileResource("cnecRam",  getClass().getResource("/cnecRamFileOk.csv"));
        final List<CnecRamData> cnecRams = fileImporter.importCnecRam(cnecRamFile);
        Assertions.assertThat(cnecRams)
                .isNotNull()
                .hasSize(3);
    }

    @Test
    void shouldThrowExceptionWhenImportCnecRam() throws Exception {
        final CoreValidD2ConservativeFileResource cnecRamFile = createFileResource("cnecRam",  new URI("https://example.com/cnecRamFile.csv").toURL());
        when(urlValidationService.openUrlStream(anyString())).thenThrow(new CoreValidD2ConservativeInvalidDataException("Connection failed"));
        Assertions.assertThatExceptionOfType(CoreValidD2ConservativeInvalidDataException.class)
                .isThrownBy(() -> fileImporter.importCnecRam(cnecRamFile))
                .withMessage("Cannot import cnec ram file from URL 'https://example.com/cnecRamFile.csv'");

    }
}
