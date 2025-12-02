/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.List;

public final class IvaBranchData {
    private final CnecRamData cnec;
    private final int minRealRam;
    private final int ivaMax;
    private final List<RamVertex> worstVertices;
    private BigDecimal conservativeIva;

    @JsonCreator
    public IvaBranchData(final @JsonProperty("cnec") CnecRamData cnec,
                         final @JsonProperty("minRealRam") int minRealRam,
                         final @JsonProperty("conservativeIva") BigDecimal conservativeIva,
                         final @JsonProperty("ivaMax") int ivaMax,
                         final @JsonProperty("worstVertices") List<RamVertex> worstVertices) {
        this.cnec = cnec;
        this.minRealRam = minRealRam;
        this.conservativeIva = conservativeIva;
        this.ivaMax = ivaMax;
        this.worstVertices = worstVertices;
    }

    public IvaBranchData(final CnecRamData cnec,
                         final int minRealRam,
                         final int ivaMax,
                         final List<RamVertex> worstVertices) {
        this.cnec = cnec;
        this.minRealRam = minRealRam;
        this.ivaMax = ivaMax;
        this.worstVertices = worstVertices;
    }

    public void setConservativeIva(final BigDecimal conservativeIva) {
        this.conservativeIva = conservativeIva;
    }

    public BigDecimal getConservativeIva() {
        return conservativeIva;
    }

    public CnecRamData getCnec() {
        return cnec;
    }

    public int getMinRealRam() {
        return minRealRam;
    }

    public int getIvaMax() {
        return ivaMax;
    }

    public List<RamVertex> getWorstVertices() {
        return worstVertices;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
