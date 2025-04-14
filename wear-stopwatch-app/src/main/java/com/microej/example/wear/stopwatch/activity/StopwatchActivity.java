/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.stopwatch.activity;

import com.microej.example.wear.stopwatch.model.Stopwatch;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;

/**
 * {@link Activity} that displays a stopwatch.
 */
public class StopwatchActivity implements Activity {

	private static final String ICON_135 = "/images/icon_stopwatch_135px.png";
	private static final String ICON_314 = "/images/icon_stopwatch_314px.png";

	private final Image iconImage135;
	private final Image iconImage314;
	private final Stopwatch stopwatch;

	/**
	 * Creates a {@code StopwatchActivity}.
	 */
	public StopwatchActivity() {
		super();

		this.iconImage135 = Image.getImage(ICON_135);
		this.iconImage314 = Image.getImage(ICON_314);
		this.stopwatch = new Stopwatch();
	}

	@Override
	public String getName() {
		return "Stopwatch";
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int size) {
		Image icon = size <= 135 ? this.iconImage135 : this.iconImage314;
		float scale = (float) size / icon.getWidth();
		int height = (int) (scale * icon.getHeight());

		TransformPainter.drawScaledImageBilinear(g, icon, x, y + (size - height) / 2, scale, scale);
	}

	@Override
	public Renderable createRenderable() {
		return new StopwatchDesktop(this.stopwatch);
	}
}
