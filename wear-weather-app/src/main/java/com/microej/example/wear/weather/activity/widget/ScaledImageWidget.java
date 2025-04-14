/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget;

import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;
import ej.widget.render.ImagePainter;

/**
 * A widget that shows an image.
 *
 * <p>
 * The image is scaled to fill the available space (stretching or reducing as necessary).
 */
public class ScaledImageWidget extends Widget {

	private final Image image;
	private final boolean allowScaleUp;

	/**
	 * Creates an image widget with the path to the image to display.
	 * 
	 * @param imagePath
	 *            the path of the image to display
	 * @param allowScaleUp
	 *            {@code true} if up-scaling is allowed, {@code false} otherwise
	 */
	public ScaledImageWidget(String imagePath, boolean allowScaleUp) {
		this.image = Image.getImage(imagePath);
		this.allowScaleUp = allowScaleUp;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		ImagePainter.computeOptimalSize(this.image, size);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Image image = this.image;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		Style style = getStyle();
		g.setColor(style.getColor());
		float scaleX = (float) contentWidth / imageWidth;
		float scaleY = (float) contentHeight / imageHeight;
		float scale = Math.min(scaleX, scaleY);
		if (scale > 1f && !this.allowScaleUp) {
			scale = 1f;
		}
		int imageX = Alignment.computeLeftX((int) (imageWidth * scale), 0, contentWidth,
				style.getHorizontalAlignment());
		int imageY = Alignment.computeTopY((int) (imageHeight * scale), 0, contentHeight, style.getVerticalAlignment());
		if (scale != 1f) {
			TransformPainter.drawScaledImageBilinear(g, image, imageX, imageY, scale, scale);
		} else {
			Painter.drawImage(g, image, imageX, imageY);
		}

	}
}
