/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_d2_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_d2_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_d2_conservative.api.resource.CoreValidD2ConservativeFileResource;
import com.powsybl.openrao.data.refprog.referenceprogram.ReferenceProgram;
import com.powsybl.openrao.data.refprog.refprogxmlimporter.RefProgImporter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

/**
 * @author Amira Kahya {@literal <amira.kahya at rte-france.com>}
 * @author Antoine Limouzin {@literal <marc.schwitzguebel_external at rte-france.com>}
 */
@Service
public class FileImporter {
    private final UrlValidationService urlValidationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileImporter.class);

    public FileImporter(final UrlValidationService urlValidationService) {
        this.urlValidationService = urlValidationService;
    }

    public ReferenceProgram importReferenceProgram(
            final CoreValidD2ConservativeFileResource refProgFile,
            final OffsetDateTime timestamp) {
        try (final InputStream refProgStream = urlValidationService.openUrlStream(refProgFile.getUrl())) {
            return RefProgImporter.importRefProg(refProgStream, timestamp);
        } catch (final Exception e) {
            throw new CoreValidD2ConservativeInvalidDataException(String.format("Cannot import reference program file from URL '%s'", refProgFile.getUrl()), e);
        }
    }

    String getFilenameFromUrl(final String url) {
        try {
            return FilenameUtils.getName(new URI(url).toURL().getPath());
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            throw new CoreValidD2ConservativeInvalidDataException(String.format("URL is invalid: %s", url), e);
        }
    }
}
