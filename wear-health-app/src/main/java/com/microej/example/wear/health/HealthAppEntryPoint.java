/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health;

import com.microej.example.wear.health.activity.HealthActivity;
import com.microej.example.wear.health.datasource.CaloriesSource;
import com.microej.example.wear.health.datasource.HeartRateSource;
import com.microej.example.wear.health.datasource.Spo2Source;
import com.microej.example.wear.health.datasource.StepsSource;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ComponentService;
import ej.kf.FeatureEntryPoint;

/**
 * Provides the entry point of the Feature.
 */
public class HealthAppEntryPoint implements FeatureEntryPoint {

	@Override
	public void start() {
		ComponentService componentService = KernelServiceProvider.getComponentService();
		componentService.registerActivity(new HealthActivity());
		componentService.registerComplicationDataSource(new StepsSource());
		componentService.registerComplicationDataSource(new HeartRateSource());
		componentService.registerComplicationDataSource(new CaloriesSource());
		componentService.registerComplicationDataSource(new Spo2Source());
	}

	@Override
	public void stop() {
		// do nothing
	}
}
