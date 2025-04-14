/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.flower;

import com.microej.example.wear.flower.watchface.FlowerWatchface;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ComponentService;
import ej.kf.FeatureEntryPoint;

/**
 * Provides the entry point of the Feature.
 */
public class FlowerAppEntryPoint implements FeatureEntryPoint {

	@Override
	public void start() {
		ComponentService componentService = KernelServiceProvider.getComponentService();
		componentService.registerWatchface(new FlowerWatchface());
	}

	@Override
	public void stop() {
		// do nothing
	}
}
