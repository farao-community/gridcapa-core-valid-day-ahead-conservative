/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHubsConfiguration;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RamAndVertexService {
    private final List<CoreHub> coreHubs;

    public RamAndVertexService(final CoreHubsConfiguration coreHubsConfiguration) {
        this.coreHubs = Collections.unmodifiableList(coreHubsConfiguration.getCoreHubs());
    }

    public void
}
