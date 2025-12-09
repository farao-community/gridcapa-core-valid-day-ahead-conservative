/*
 *  Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

public final class CoreValidD2Constants {

    private CoreValidD2Constants() {
        throw new IllegalStateException("Constants class");
    }

    //PROCESS
    public static final String PROCESS_NAME = "CORE_VALID_D2";
    public static final String MINIO_DESTINATION_PATH_REGEX = "yyyy'/'MM'/'dd'/'HH_mm'/'";
    public static final String IVA_RESULT_FILE_TYPE = "IVA-RESULT";
    public static final String IVA_BRANCH_JSON_FILE_NAME = "ivaBranch.json";
    //TASK MANAGING
    public static final String TASK_STATUS_UPDATE = "task-status-update";
    public static final String GRIDCAPA_TASK_ID = "gridcapa-task-id";
    public static final String BUSINESS_LOGGER = "BUSINESS_LOGGER";
    public static final String USE_PROJECTION = "USE_PROJECTION";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String INT = "INT";
    public static final String STRING = "STRING";
    public static final String MAX_VERTICES_PER_BRANCH = "MAX_VERTICES_PER_BRANCH";
    public static final String RAM_THRESHOLD = "RAM_THRESHOLD";
    public static final String MIN_RAM_MCCC = "MIN_RAM_MCCC";
    public static final String EXCLUDED_BRANCHES = "EXCLUDED_BRANCHES";
    public static final String CURATIVE_IVA_MARGIN = "CURATIVE_IVA_MARGIN";
    public static final String PREVENTIVE_IVA_MARGIN = "PREVENTIVE_IVA_MARGIN";
    public static final String SEMICOLON = ";";
    //CSV HEADERS
    public static final String IS_PRESOLVED_REGION_HEADER = "PresolvedRegion";
    public static final String IS_CNEC_HEADER = "CNEC";
    public static final String RAM0_CORE_HEADER = "RAM_0core";
    public static final String NEC_ID_HEADER = "NEC_ID";
    public static final String NE_NAME_HEADER = "NE_Name";
    public static final String TSO_HEADER = "TSO";
    public static final String F_MAX_HEADER = "F_max";
    public static final String FRM_HEADER = "FRM";
    public static final String F_REF_HEADER = "F_ref";
    public static final String RAM_HEADER = "RAM";
    public static final String F_0CORE_HEADER = "F_0core";
    public static final String MIN_RAM_FACTOR_HEADER = "MinRAMFactor";
    public static final String F_UAF_HEADER = "F_uaf";
    public static final String F_0ALL_HEADER = "F_0all";
    public static final String AMR_HEADER = "AMR";
    public static final String F_LTA_MAX_HEADER = "F_LTAmax";
    public static final String LTA_MARGIN_HEADER = "LTA_margin";
    public static final String CVA_HEADER = "CVA";
    public static final String IVA_HEADER = "IVA";
    public static final String CONTINGENCY_NAME_HEADER = "Contingency_Name";
    public static final String BRANCH_STATUS_HEADER = "BranchStatus";
    //CNEC FILTERING
    public static final String BASECASE = "BASECASE";
    public static final String SUFFIX_ADMISSIBLE_TRANSMISSION_LIMIT = "ATL";
    public static final String FRENCH_TSO = "FR";
    public static final String PREFIX_NO_CURRENT_LIMIT = "[NCL]";
    public static final String SUFFIX_NEC_ID_BEFORE = "_SpannedBefore";
    public static final String SUFFIX_NEC_ID_AFTER = "_SpannedAfter";
    public static final String BRANCH_STATUS_OK = "OK";
    public static final int MIN_AMR_VALUE = 0;

}
