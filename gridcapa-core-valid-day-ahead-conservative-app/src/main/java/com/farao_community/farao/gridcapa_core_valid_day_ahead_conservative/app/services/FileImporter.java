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
import com.farao_community.farao.gridcapa_core_valid_commons.vertex.VerticesUtils;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource.CoreValidD2ConservativeFileResource;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
        return importFile(verticesFile, is -> VerticesUtils.importVertices(is, coreHubs));
    }

    public List<CnecRamData> importCnecRam(final CoreValidD2ConservativeFileResource cnecRamFile) {
        return importFile(cnecRamFile, is -> CnecRamImporter.importCnecRam(is, coreHubs));
    }

    public <T> T importFile(final CoreValidD2ConservativeFileResource file,
                            final Function<InputStream, T> inputStreamMapper) {
        try (final InputStream fileContentStream = urlValidationService.openUrlStream(file.getUrl())) {
            return inputStreamMapper.apply(fileContentStream);
        } catch (final Exception e) {
            throw new CoreValidD2ConservativeInvalidDataException(String.format("Cannot import %s file from URL '%s'", file.getFilename(), file.getUrl()), e);
        }
    }
}
