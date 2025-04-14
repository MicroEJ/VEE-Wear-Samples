/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health.datasource;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.ComplicationDataSource;
import ej.bon.XMath;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;

/**
 * {@link ComplicationDataSource} which provides steps data.
 */
public class StepsSource implements ComplicationDataSource {

	private static final int STEPS_GOAL = 10000;

	private final Image iconImage;

	/**
	 * Creates a steps source.
	 */
	public StepsSource() {
		this.iconImage = Image.getImage("/images/ic_step.png");
	}

	@Override
	public boolean hasText() {
		return true;
	}

	@Override
	public boolean hasIcon() {
		return true;
	}

	@Override
	public boolean hasProgress() {
		return true;
	}

	@Override
	public String getText() {
		int steps = KernelServiceProvider.getHealthService().getSteps();
		return Integer.toString(steps);
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int width, int height) {
		Image image = this.iconImage;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		float scale = Math.min((float) width / imageWidth, (float) height / imageHeight);
		int imageScaledWidth = (int) (scale * imageWidth);
		int imageScaledHeight = (int) (scale * imageHeight);
		TransformPainter.drawScaledImageBilinear(g, image, x + (width - imageScaledWidth) / 2,
				y + (height - imageScaledHeight) / 2, scale, scale);
	}

	@Override
	public float getProgress() {
		int steps = KernelServiceProvider.getHealthService().getSteps();
		float progress = (float) steps / STEPS_GOAL;
		return XMath.limit(progress, 0.0f, 1.0f);
	}
}
