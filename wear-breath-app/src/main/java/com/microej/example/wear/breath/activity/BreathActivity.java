/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity;

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
 * {@link Activity} that shows the Breath menu .
 */
public class BreathActivity implements Activity {

	private static final String ICON_IMAGE_135 = "/images/ic_breath_135_b.png";
	private static final String ICON_IMAGE_314 = "/images/ic_breath_314_b.png";
	private static final String BREATH = "Breath";

	private @Nullable ResourceImage iconImage135;
	private @Nullable ResourceImage iconImage314;

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
	public String getName() {
		return BREATH;
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int size) {
		Image image = (size > 135 ? this.iconImage314 : this.iconImage135);
		if (image != null) {
			float scale = (float) size / image.getWidth();
			int imageScaledHeight = (int) (scale * image.getHeight());
			TransformPainter.drawScaledImageBilinear(g, image, x, y + (size - imageScaledHeight) / 2, scale, scale);
		}
	}

	@Override
	public Renderable createRenderable() {
		return new BreathDesktop();
	}
}
