/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamValuesData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Marc Schwitzguebel {@literal <marc.schwitzguebel_externe at rte-france.com>}
 */
public final class CnecRamImporter {

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

    private CnecRamImporter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<CnecRamData> importCnecRam(final InputStream cnecRamsStream, final List<CoreHub> coreHubs) {
        return importCnecRam(new InputStreamReader(cnecRamsStream, StandardCharsets.UTF_8), coreHubs);
    }

    private static List<CnecRamData> importCnecRam(final Reader reader, final List<CoreHub> coreHubs) {
        try {
            final List<CnecRamData> cnecRams = new ArrayList<>();
            final Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);
            csvRecords.forEach(csvRecord -> {
                final String ram0CoreString = csvRecord.get(RAM0_CORE_HEADER);
                if (shouldImport(csvRecord, ram0CoreString)) {
                    final Map<String, BigDecimal> ptdfValues = coreHubs.stream().collect(Collectors.toMap(
                            CoreHub::flowbasedCode,
                            coreHub -> getPtdfValue(csvRecord, coreHub)));
                    cnecRams.add(new CnecRamData(csvRecord.get(NEC_ID_HEADER),
                                                 csvRecord.get(NE_NAME_HEADER),
                                                 csvRecord.get(TSO_HEADER),
                                                 csvRecord.get(CONTINGENCY_NAME_HEADER),
                                                 csvRecord.get(BRANCH_STATUS_HEADER),
                                                 new CnecRamValuesData(get(csvRecord, RAM_HEADER),
                                                                       Integer.parseInt(ram0CoreString),
                                                                       new BigDecimal(csvRecord.get(MIN_RAM_FACTOR_HEADER)),
                                                                       get(csvRecord, AMR_HEADER),
                                                                       get(csvRecord, LTA_MARGIN_HEADER),
                                                                       get(csvRecord, CVA_HEADER),
                                                                       get(csvRecord, IVA_HEADER)),
                                                 new CnecRamFValuesData(get(csvRecord, F_MAX_HEADER),
                                                                        get(csvRecord, FRM_HEADER),
                                                                        get(csvRecord, F_REF_HEADER),
                                                                        get(csvRecord, F_0CORE_HEADER),
                                                                        get(csvRecord, F_UAF_HEADER),
                                                                        get(csvRecord, F_0ALL_HEADER),
                                                                        get(csvRecord, F_LTA_MAX_HEADER)),
                                                 ptdfValues));
                }
            });
            return cnecRams;
        } catch (IOException | IllegalArgumentException e) {
            throw new CoreValidD2ConservativeInvalidDataException("Exception occurred during parsing Cnec Ram file", e);
        }
    }

    private static boolean shouldImport(CSVRecord csvRecord, String ram0CoreString) {
        return Boolean.parseBoolean(csvRecord.get(IS_PRESOLVED_REGION_HEADER))
               && Boolean.parseBoolean(csvRecord.get(IS_CNEC_HEADER))
               && StringUtils.isNumeric(ram0CoreString);
    }

    /**
     * Set a ptdf value to zero only if it is an empty HVDC ptdf value.
     * This is the only case where a ptdf value should be empty or blank.
     * Otherwise, should launch an exception.
     */
    private static BigDecimal getPtdfValue(final CSVRecord csvRecord,
                                         final CoreHub corehub) {
        final String ptdfValueString = csvRecord.get(corehub.flowbasedCode());
        if (corehub.isHvdcHub() && StringUtils.isBlank(ptdfValueString)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(ptdfValueString);
    }

    private static int get(CSVRecord csvRecord, String headerName) {
        return Integer.parseInt(csvRecord.get(headerName));
    }
}
