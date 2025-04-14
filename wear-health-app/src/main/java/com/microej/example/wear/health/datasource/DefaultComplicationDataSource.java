/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health.datasource;

import com.microej.wear.components.ComplicationDataSource;
import ej.microui.display.GraphicsContext;

/**
 * {@link ComplicationDataSource} which doesn't provide any data.
 */
public class DefaultComplicationDataSource implements ComplicationDataSource {

	@Override
	public boolean hasText() {
		return false;
	}

	@Override
	public boolean hasIcon() {
		return false;
	}

	@Override
	public boolean hasProgress() {
		return false;
	}

	@Override
	public String getText() {
		throw new IllegalStateException();
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int width, int height) {
		throw new IllegalStateException();
	}

	@Override
	public float getProgress() {
		throw new IllegalStateException();
	}
}
