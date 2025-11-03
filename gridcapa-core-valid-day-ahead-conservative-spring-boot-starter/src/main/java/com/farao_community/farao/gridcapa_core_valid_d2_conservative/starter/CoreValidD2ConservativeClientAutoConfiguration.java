/*
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.farao_community.farao.gridcapa_core_valid_d2_conservative.starter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Antoine Limouzin {@literal <antoine.limouzin at rte-france.com>}
 * This config class allows the scanning of the package by Spring Boot, hence declaring CoreValidD2ConservativeClient as a bean
 */
@Configuration
@EnableConfigurationProperties(CoreValidD2ConservativeClientProperties.class)
@ComponentScan
public class CoreValidD2ConservativeClientAutoConfiguration {
}
