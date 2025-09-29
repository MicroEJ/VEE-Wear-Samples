/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health.datasource;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ResourceService;

import ej.annotation.Nullable;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.ResourceImage;

/**
 * {@link com.microej.wear.components.ComplicationDataSource} which provides SpO2 data.
 */
public class Spo2Source extends DefaultComplicationDataSource {

	private @Nullable ResourceImage iconImage;

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
		int spo2 = KernelServiceProvider.getHealthService().getOxygenSaturation();
		return spo2 + "%";
	}

	@Override
	public void onIconAttached() {
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.iconImage = ResourceImage.loadImage(resourceService.getImagePath("/images/ic_spo2.png"));
	}

	@Override
	public void onIconDetached() {
		ResourceImage iconImage = this.iconImage;
		if (iconImage != null) {
			iconImage.close();
			this.iconImage = null;
		}
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int width, int height) {
		Image image = this.iconImage;
		if (image != null) {
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			float scale = Math.min((float) width / imageWidth, (float) height / imageHeight);
			int imageScaledWidth = (int) (scale * imageWidth);
			int imageScaledHeight = (int) (scale * imageHeight);
			TransformPainter.drawScaledImageBilinear(g, image, x + (width - imageScaledWidth) / 2,
					y + (height - imageScaledHeight) / 2, scale, scale);
		}
	}
}
