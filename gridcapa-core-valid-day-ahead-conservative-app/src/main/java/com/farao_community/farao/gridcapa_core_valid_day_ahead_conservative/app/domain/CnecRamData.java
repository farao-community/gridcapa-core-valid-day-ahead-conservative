/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.app.domain;

import com.farao_community.farao.gridcapa_core_valid_commons.vertex.FlowBasedDomainBranchData;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Marc Schwitzguebel {@literal <marc.schwitzguebel_externe at rte-france.com>}
 */
public record CnecRamData(String necId, String neName, String tso, String contingencyName, String branchStatus,
                          CnecRamValuesData ramValues, CnecRamFValuesData fValues,
                          Map<String, BigDecimal> ptdfValues) implements FlowBasedDomainBranchData {

    @Override
    public int getRam0Core() {
        return ramValues.ram0Core();
    }

    @Override
    public int getAmr() {
        return ramValues.amr();
    }

    @Override
    public Map<String, BigDecimal> getPtdfValues() {
        return ptdfValues;
    }

}
