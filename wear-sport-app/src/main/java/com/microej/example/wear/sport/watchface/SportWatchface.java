/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.sport.watchface;

import com.microej.wear.components.Renderable;
import com.microej.wear.components.Watchface;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;

/**
 * {@link Watchface} which renders an analog watchface with complications.
 */
public class SportWatchface implements Watchface {

	private static final String PREVIEW_IMAGE = "/images/sport_preview.png";

	private final Image previewImage;

	/**
	 * Creates a Sport watchface.
	 */
	public SportWatchface() {
		this.previewImage = Image.getImage(PREVIEW_IMAGE);
	}

	@Override
	public void renderPreview(GraphicsContext g, int x, int y, int size) {
		Image image = this.previewImage;
		TransformPainter.drawScaledImageBilinear(g, image, x, y, (float) size / image.getWidth(),
				(float) size / image.getHeight());
	}

	@Override
	public Renderable createRenderable() {
		return new SportDisplayable();
	}
}
