/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.stopwatch.activity;

import com.microej.example.wear.stopwatch.model.Stopwatch;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;
import com.microej.wear.services.ResourceService;

import ej.annotation.Nullable;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.ResourceImage;

/**
 * {@link Activity} that displays a stopwatch.
 */
public class StopwatchActivity implements Activity {

	private static final String ICON_IMAGE_135 = "/images/icon_stopwatch_135px.png";
	private static final String ICON_IMAGE_314 = "/images/icon_stopwatch_314px.png";

	private @Nullable ResourceImage iconImage135;
	private @Nullable ResourceImage iconImage314;
	private final Stopwatch stopwatch;

	/**
	 * Creates a {@code StopwatchActivity}.
	 */
	public StopwatchActivity() {
		this.stopwatch = new Stopwatch();
	}

	@Override
	public String getName() {
		return "Stopwatch";
	}

	@Override
	public void onIconAttached() {
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.iconImage135 = ResourceImage.loadImage(resourceService.getImagePath(ICON_IMAGE_135));
		this.iconImage314 = ResourceImage.loadImage(resourceService.getImagePath(ICON_IMAGE_314));
	}

	@Override
	public void onIconDetached() {
		ResourceImage iconImage135 = this.iconImage135;
		if (iconImage135 != null) {
			iconImage135.close();
			this.iconImage135 = null;
		}

		ResourceImage iconImage314 = this.iconImage314;
		if (iconImage314 != null) {
			iconImage314.close();
			this.iconImage314 = null;
		}
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int size) {
		Image icon = size <= 135 ? this.iconImage135 : this.iconImage314;
		if (icon != null) {
			float scale = (float) size / icon.getWidth();
			int height = (int) (scale * icon.getHeight());

			TransformPainter.drawScaledImageBilinear(g, icon, x, y + (size - height) / 2, scale, scale);
		}
	}

	@Override
	public Renderable createRenderable() {
		return new StopwatchDesktop(this.stopwatch);
	}
}
