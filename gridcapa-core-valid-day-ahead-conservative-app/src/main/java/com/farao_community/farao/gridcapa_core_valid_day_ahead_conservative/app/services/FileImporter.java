/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.Vertex;
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.VerticesImporter;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeFileResource;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class FileImporter {
    private final List<CoreHub> coreHubs;
    private final UrlValidationService urlValidationService;

    public FileImporter(final UrlValidationService urlValidationService,
                        final CoreHubsConfiguration coreHubsConfiguration) {
        this.coreHubs = Collections.unmodifiableList(coreHubsConfiguration.getCoreHubs());
        this.urlValidationService = urlValidationService;
    }

    public List<Vertex> importVertices(final CoreValidD2ConservativeFileResource verticesFile) {
        try (final InputStream verticefileInputStream = urlValidationService.openUrlStream(verticesFile.getUrl())) {
            return VerticesImporter.importVertices(verticefileInputStream, coreHubs);
        } catch (final Exception e) {
            throw new CoreValidD2ConservativeInvalidDataException(String.format("Cannot import vertices file from URL '%s'", verticesFile.getUrl()), e);
        }
    }

    public List<CnecRamData> importCnecRam(final CoreValidD2ConservativeFileResource cnecRamFile) {
        try (final InputStream cnecRamInputStream = urlValidationService.openUrlStream(cnecRamFile.getUrl())) {
            return CnecRamImporter.importCnecRam(cnecRamInputStream, coreHubs);
        } catch (Exception e) {
            throw new CoreValidD2ConservativeInvalidDataException(String.format("Cannot import cnec ram file from URL '%s'", cnecRamFile.getUrl()), e);
        }
    }
}
