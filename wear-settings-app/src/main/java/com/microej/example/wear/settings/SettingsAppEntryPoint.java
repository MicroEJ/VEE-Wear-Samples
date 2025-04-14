/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.settings;

import com.microej.example.wear.settings.activity.SettingsActivity;
import com.microej.example.wear.settings.datasource.BatterySource;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ComponentService;

import ej.kf.FeatureEntryPoint;

/**
 * Provides the entry point of the Feature.
 */
public class SettingsAppEntryPoint implements FeatureEntryPoint {

	@Override
	public void start() {
		ComponentService componentService = KernelServiceProvider.getComponentService();
		componentService.registerActivity(new SettingsActivity());
		componentService.registerComplicationDataSource(new BatterySource());
	}

	@Override
	public void stop() {
		// do nothing
	}
}
