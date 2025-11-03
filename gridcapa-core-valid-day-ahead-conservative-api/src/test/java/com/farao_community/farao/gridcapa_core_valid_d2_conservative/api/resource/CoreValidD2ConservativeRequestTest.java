/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package com.farao_community.farao.gridcapa_core_valid_d2_conservative.api.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Antoine Limouzin {@literal <antoine.limouzin at rte-france.com>}
 */
class CoreValidD2ConservativeRequestTest {

    private CoreValidD2ConservativeFileResource cnecRam;
    private CoreValidD2ConservativeFileResource vertice;
    private OffsetDateTime dateTime;

    @BeforeEach
    void setUp() {
        cnecRam = new CoreValidD2ConservativeFileResource("cnecRam.txt", "http://path/to/cnecRam/file");
        vertice = new CoreValidD2ConservativeFileResource("vertice.txt", "http://path/to/vertice/file");
        dateTime = OffsetDateTime.parse("2025-10-01T00:30Z");
    }

    @Test
    void checkManualCoreValidRequest() {
        CoreValidD2ConservativeRequest coreValidD2ConservativeRequest = new CoreValidD2ConservativeRequest("id", "runId", dateTime, cnecRam, vertice);
        assertNotNull(coreValidD2ConservativeRequest);
        assertEquals("id", coreValidD2ConservativeRequest.getId());
        assertEquals("runId", coreValidD2ConservativeRequest.getCurrentRunId());
        assertEquals("2025-10-01T00:30Z", coreValidD2ConservativeRequest.getTimestamp().toString());
        assertEquals("cnecRam.txt", coreValidD2ConservativeRequest.getCnecRam().getFilename());
        assertEquals("vertice.txt", coreValidD2ConservativeRequest.getVertice().getFilename());
        assertFalse(coreValidD2ConservativeRequest.getLaunchedAutomatically());
    }

    @Test
    void checkAutoCoreValidRequest() {
        CoreValidD2ConservativeRequest coreValidD2ConservativeRequest = new CoreValidD2ConservativeRequest("id", "runId", dateTime, cnecRam, vertice, true);
        assertTrue(coreValidD2ConservativeRequest.getLaunchedAutomatically());
    }

}
