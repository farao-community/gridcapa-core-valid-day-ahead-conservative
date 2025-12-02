/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.services;

import com.farao_community.farao.gridcapa_core_valid_commons.core_hub.CoreHub;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.exception.CoreValidD2ConservativeInvalidDataException;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamFValuesData;
import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain.CnecRamValuesData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

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

    private static final String IS_PRESOLVED_REGION_HEADER = "PresolvedRegion";
    private static final String IS_CNEC_HEADER = "CNEC";
    private static final String RAM0_CORE_HEADER = "RAM_0core";
    private static final String NEC_ID_HEADER = "NEC_ID";
    private static final String NE_NAME_HEADER = "NE_Name";
    private static final String TSO_HEADER = "TSO";
    private static final String F_MAX_HEADER = "F_max";
    private static final String FRM_HEADER = "FRM";
    private static final String F_REF_HEADER = "F_ref";
    private static final String RAM_HEADER = "RAM";
    private static final String F_0CORE_HEADER = "F_0core";
    private static final String MIN_RAM_FACTOR_HEADER = "MinRAMFactor";
    private static final String F_UAF_HEADER = "F_uaf";
    private static final String F_0ALL_HEADER = "F_0all";
    private static final String AMR_HEADER = "AMR";
    private static final String F_LTA_MAX_HEADER = "F_LTAmax";
    private static final String LTA_MARGIN_HEADER = "LTA_margin";
    private static final String CVA_HEADER = "CVA";
    private static final String IVA_HEADER = "IVA";
    private static final String CONTINGENCY_NAME_HEADER = "Contingency_Name";
    private static final String BRANCH_STATUS_HEADER = "BranchStatus";

    private CnecRamImporter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<CnecRamData> importCnecRam(final InputStream cnecRamsStream, final List<CoreHub> coreHubs) {
        return importCnecRam(new InputStreamReader(cnecRamsStream, StandardCharsets.UTF_8), coreHubs);
    }

    private static List<CnecRamData> importCnecRam(final Reader reader, final List<CoreHub> coreHubs) {
        try (reader) {
            final List<CnecRamData> cnecRams = new ArrayList<>();
            final Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);
            csvRecords.forEach(csvRecord -> importSingleCnecRamFiltered(coreHubs, csvRecord, cnecRams));
            return cnecRams;
        } catch (final IOException | IllegalArgumentException e) {
            throw new CoreValidD2ConservativeInvalidDataException("Exception occurred during parsing Cnec Ram file", e);
        }
    }

    private static void importSingleCnecRamFiltered(final List<CoreHub> coreHubs,
                                                    final CSVRecord csvRecord,
                                                    final List<CnecRamData> cnecRams) {
        final String ram0CoreString = csvRecord.get(RAM0_CORE_HEADER);
        if (shouldImport(csvRecord, ram0CoreString)) {
            cnecRams.add(new CnecRamData(csvRecord.get(NEC_ID_HEADER),
                                         csvRecord.get(NE_NAME_HEADER),
                                         csvRecord.get(TSO_HEADER),
                                         csvRecord.get(CONTINGENCY_NAME_HEADER),
                                         csvRecord.get(BRANCH_STATUS_HEADER),
                                         getRamValues(csvRecord),
                                         getFValues(csvRecord),
                                         getPtdfValues(coreHubs, csvRecord)));
        }
    }

    private static @NotNull Map<String, BigDecimal> getPtdfValues(final List<CoreHub> coreHubs,
                                                                  final CSVRecord csvRecord) {
        return coreHubs.stream().collect(Collectors.toMap(
                CoreHub::flowbasedCode,
                coreHub -> getPtdfValue(csvRecord, coreHub)));
    }

    private static CnecRamFValuesData getFValues(final CSVRecord csvRecord) {
        return new CnecRamFValuesData(get(csvRecord, F_MAX_HEADER),
                                      get(csvRecord, FRM_HEADER),
                                      get(csvRecord, F_REF_HEADER),
                                      get(csvRecord, F_0CORE_HEADER),
                                      get(csvRecord, F_UAF_HEADER),
                                      get(csvRecord, F_0ALL_HEADER),
                                      get(csvRecord, F_LTA_MAX_HEADER));
    }

    private static CnecRamValuesData getRamValues(final CSVRecord csvRecord) {
        return new CnecRamValuesData(get(csvRecord, RAM_HEADER),
                                     get(csvRecord, RAM0_CORE_HEADER),
                                     new BigDecimal(csvRecord.get(MIN_RAM_FACTOR_HEADER)),
                                     get(csvRecord, AMR_HEADER),
                                     get(csvRecord, LTA_MARGIN_HEADER),
                                     get(csvRecord, CVA_HEADER),
                                     get(csvRecord, IVA_HEADER));
    }

    private static boolean shouldImport(final CSVRecord csvRecord, final String ram0CoreString) {
        return Boolean.parseBoolean(csvRecord.get(IS_PRESOLVED_REGION_HEADER))
               && Boolean.parseBoolean(csvRecord.get(IS_CNEC_HEADER))
               && NumberUtils.isParsable(ram0CoreString);
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

    private static int get(final CSVRecord csvRecord, final String headerName) {
        return Integer.parseInt(csvRecord.get(headerName));
    }
}
