package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.util;

import com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain.CnecRamData;
import org.apache.commons.lang3.StringUtils;

public final class CnecRamUtils {

    private CnecRamUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String FRENCH_TSO = "FR";
    public static final String PREFIX_NO_CURRENT_LIMIT = "[NCL]";
    public static final String EXCLUDE_SUFFIX_NEC_ID_BEFORE = "_SpannedBefore";
    public static final String EXCLUDE_SUFFIX_NEC_ID_AFTER = "_SpannedAfter";
    public static final String BRANCH_STATUS_OK = "OK";
    public static final int MIN_AMR_VALUE = 0;
    public static final String BASE_CASE = "BASECASE";
    public static final String ATL = "ATL";
    
    public static boolean isRteElement(final CnecRamData cnec) {
        return FRENCH_TSO.equalsIgnoreCase(cnec.tso());
    }

    public static boolean isAdjustable(final CnecRamData cnec) {
        return BRANCH_STATUS_OK.equalsIgnoreCase(cnec.branchStatus())
               && cnec.ramValues().amr() > MIN_AMR_VALUE;
    }

    public static boolean hasCurrentLimit(final CnecRamData cnec) {
        return !StringUtils.startsWithIgnoreCase(cnec.neName(), PREFIX_NO_CURRENT_LIMIT);
    }

    public static boolean isNotSpanned(final CnecRamData cnec) {
        return !StringUtils.endsWithIgnoreCase(cnec.necId(), EXCLUDE_SUFFIX_NEC_ID_BEFORE)
               && !StringUtils.endsWithIgnoreCase(cnec.necId(), EXCLUDE_SUFFIX_NEC_ID_AFTER);
    }

    public static boolean hasNoContingency(final CnecRamData cnec) {
        return BASE_CASE.equals(cnec.contingencyName());
    }

    public static boolean isAdmissibleTransmissionLoading(final CnecRamData cnec) {
        return cnec.necId().toUpperCase().endsWith(ATL);
    }
}
