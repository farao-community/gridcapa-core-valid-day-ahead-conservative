/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

public final class CoreValidD2Constants {

    private CoreValidD2Constants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String TASK_STATUS_UPDATE = "task-status-update";
    public static final String GRIDCAPA_TASK_ID = "gridcapa-task-id";
    public static final String BUSINESS_LOGGER = "BUSINESS_LOGGER";
    public static final String USE_PROJECTION = "USE_PROJECTION";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String INT = "INT";
    public static final String STRING = "STRING";
    public static final String MAX_VERTICES_PER_BRANCH = "MAX_VERTICES_PER_BRANCH";
    public static final String RAM_LIMIT = "RAM_LIMIT";
    public static final String MIN_RAM_MCCC = "MIN_RAM_MCCC";
    public static final String EXCLUDED_BRANCHES = "EXCLUDED_BRANCHES";
    public static final String SEMICOLON = ";";
}
