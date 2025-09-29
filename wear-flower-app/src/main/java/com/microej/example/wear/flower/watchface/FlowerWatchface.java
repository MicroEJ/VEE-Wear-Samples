/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.flower.watchface;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Renderable;
import com.microej.wear.components.Watchface;
import com.microej.wear.services.ResourceService;

import ej.annotation.Nullable;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.ResourceImage;

/**
 * {@link Watchface} which renders a simple analog watchface.
 */
public class FlowerWatchface implements Watchface {

	private static final String PREVIEW_IMAGE = "/images/flower_preview.png";

	private @Nullable ResourceImage previewImage;

	@Override
	public void onPreviewAttached() {
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.previewImage = ResourceImage.loadImage(resourceService.getImagePath(PREVIEW_IMAGE));
	}

	@Override
	public void onPreviewDetached() {
		ResourceImage previewImage = this.previewImage;
		if (previewImage != null) {
			previewImage.close();
			this.previewImage = null;
		}
	}

	@Override
	public void renderPreview(GraphicsContext g, int x, int y, int size) {
		Image image = this.previewImage;
		if (image != null) {
			TransformPainter.drawScaledImageBilinear(g, image, x, y, (float) size / image.getWidth(),
					(float) size / image.getHeight());
		}
	}

	@Override
	public Renderable createRenderable() {
		return new FlowerDisplayable();
	}
}
