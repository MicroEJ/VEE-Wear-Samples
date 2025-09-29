/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system;

import com.microej.example.wear.system.navigation.SystemNavigator;
import com.microej.wear.KernelServiceProvider;

import ej.kf.FeatureEntryPoint;

/**
 * Provides the entry point of the Feature.
 */
public class SystemAppEntryPoint implements FeatureEntryPoint {

	@Override
	public void start() {
		SystemNavigator navigator = SystemNavigator.getInstance();
		KernelServiceProvider.getComponentService().setNavigator(navigator);
		navigator.handleDisplayReady();
	}

	@Override
	public void stop() {
		// do nothing
	}
}
