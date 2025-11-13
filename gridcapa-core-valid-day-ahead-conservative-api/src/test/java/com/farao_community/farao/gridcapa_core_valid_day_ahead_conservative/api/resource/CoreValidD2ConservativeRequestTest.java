/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;

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
        final CoreValidD2ConservativeRequest request = new CoreValidD2ConservativeRequest("id", "runId", dateTime, cnecRam, vertice, new ArrayList<>());
        assertNotNull(request);
        assertEquals("id", request.getId());
        assertEquals("runId", request.getCurrentRunId());
        assertEquals("2025-10-01T00:30Z", request.getTimestamp().toString());
        assertEquals("cnecRam.txt", request.getCnecRam().getFilename());
        assertEquals("vertice.txt", request.getVertice().getFilename());
        assertFalse(request.getLaunchedAutomatically());
    }

    @Test
    void checkAutoCoreValidRequest() {
        final CoreValidD2ConservativeRequest request = new CoreValidD2ConservativeRequest("id", "runId", dateTime, cnecRam, vertice, true, new ArrayList<>());
        assertTrue(request.getLaunchedAutomatically());
    }

}
