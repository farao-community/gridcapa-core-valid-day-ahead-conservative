/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa_core_valid_day_ahead_conservative.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Objects;

public final class BranchData {
    private final CnecRamData cnec;
    private final int minRealRam;
    private int conservativeIva;
    private final int ivaMax;
    private final List<RamVertex> worstVertices;

    @JsonCreator
    public BranchData(final @JsonProperty("cnec") CnecRamData cnec,
                      final @JsonProperty("minRealRam") int minRealRam,
                      final @JsonProperty("conservativeIva") int conservativeIva,
                      final @JsonProperty("ivaMax") int ivaMax,
                      final @JsonProperty("worstVertices") List<RamVertex> worstVertices) {
        this.cnec = cnec;
        this.minRealRam = minRealRam;
        this.conservativeIva = conservativeIva;
        this.ivaMax = ivaMax;
        this.worstVertices = worstVertices;
    }

    public BranchData(CnecRamData cnec,
                      int minRealRam,
                      int ivaMax,
                      List<RamVertex> worstVertices) {
        this.cnec = cnec;
        this.minRealRam = minRealRam;
        this.ivaMax = ivaMax;
        this.worstVertices = worstVertices;
    }

    public void setConservativeIva(final int conservativeIva) {
        this.conservativeIva = conservativeIva;
    }

    public int getConservativeIva() {
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
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (BranchData) obj;
        return Objects.equals(this.cnec, that.cnec) &&
               this.minRealRam == that.minRealRam &&
               this.conservativeIva == that.conservativeIva &&
               this.ivaMax == that.ivaMax &&
               Objects.equals(this.worstVertices, that.worstVertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnec, minRealRam, conservativeIva, ivaMax, worstVertices);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
