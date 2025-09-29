/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath;

import com.microej.example.wear.breath.activity.BreathActivity;
import com.microej.wear.KernelServiceProvider;

import ej.kf.FeatureEntryPoint;

/**
 * Provides the entry point of the Feature.
 */
public class BreathAppEntryPoint implements FeatureEntryPoint {

	@Override
	public void start() {
		KernelServiceProvider.getComponentService().registerActivity(new BreathActivity());
	}

	@Override
	public void stop() {
		// do nothing
	}
}
