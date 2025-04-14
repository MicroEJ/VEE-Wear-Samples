/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health.datasource;

import com.microej.wear.KernelServiceProvider;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;

/**
 * {@link com.microej.wear.components.ComplicationDataSource} which provides heart rate data.
 */
public class HeartRateSource extends DefaultComplicationDataSource {

	private final Image iconImage;

	/**
	 * Creates a heart rate source.
	 */
	public HeartRateSource() {
		this.iconImage = Image.getImage("/images/ic_hr.png");
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
	public String getText() {
		int heartRate = KernelServiceProvider.getHealthService().getHeartRate();
		return heartRate + " bpm";
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
}
