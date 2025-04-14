/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.helloworld;

import com.microej.example.wear.helloworld.activity.HelloWorldActivity;
import com.microej.wear.KernelServiceProvider;
import ej.kf.FeatureEntryPoint;

/**
 * Provides the entry point of the Feature.
 */
public class HelloWorldAppEntryPoint implements FeatureEntryPoint {

	@Override
	public void start() {
		KernelServiceProvider.getComponentService().registerActivity(new HelloWorldActivity());
	}

	@Override
	public void stop() {
		// do nothing
	}
}
