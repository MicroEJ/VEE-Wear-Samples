/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget;

import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsException;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that displays a vector image from a specified resource path.
 */
public class VectorImageWidget extends Widget {

	private static final float DEFAULT_SCALE = 1.0f;
	private float scale;
	private final VectorImage image;

	/**
	 * Creates an image widget with the resource path of the image to display.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 * @param scale
	 *            the scaling factor for the image.
	 * @throws VectorGraphicsException
	 *             if the image cannot be loaded.
	 */
	public VectorImageWidget(String imagePath, float scale) {
		this.scale = scale;
		this.image = VectorImage.getImage(imagePath);
	}

	/**
	 * Creates an image widget with the resource path of the image to display.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 * @throws VectorGraphicsException
	 *             if the image cannot be loaded.
	 */
	public VectorImageWidget(String imagePath) {
		this.scale = DEFAULT_SCALE;
		this.image = VectorImage.getImage(imagePath);
	}

	/**
	 * Creates an image widget with the resource path of the image to display and its enabled state.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 * @param enabled
	 *            <code>true</code> if this image widget is to be enabled, <code>false</code> otherwise.
	 */
	protected VectorImageWidget(String imagePath, boolean enabled) {
		super(enabled);
		this.image = VectorImage.getImage(imagePath);
	}

	/**
	 * Returns the width of the image.
	 *
	 * @return The image width.
	 */
	public int getOriginalImageWidth() {
		return (int) this.image.getWidth();
	}

	/**
	 * Returns the height of the image.
	 *
	 * @return The image height.
	 */
	public int getOriginalImageHeight() {
		return (int) this.image.getHeight();
	}

	/**
	 * Sets the scaling factor for the image.
	 *
	 * @param scale
	 *            the scaling factor to apply.
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		VectorImage image = this.image;
		float baseScale = Math.min(contentWidth / image.getWidth(), contentHeight / image.getHeight());
		float finalScale = this.scale * baseScale;
		int scaledWidth = (int) (finalScale * image.getWidth());
		int scaledHeight = (int) (finalScale * image.getHeight());
		int x = Alignment.computeLeftX(scaledWidth, 0, contentWidth, Alignment.HCENTER);
		int y = Alignment.computeTopY(scaledHeight, 0, contentHeight, Alignment.VCENTER);

		Style style = getStyle();
		g.setColor(style.getColor());
		Matrix sizeMatrix = new Matrix();
		sizeMatrix.setTranslate(x, y);
		sizeMatrix.preScale(finalScale, finalScale);
		VectorGraphicsPainter.drawImage(g, image, sizeMatrix);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		VectorImage image = this.image;
		float baseScale = Math.min(size.getWidth() / image.getWidth(), size.getHeight() / image.getHeight());
		float finalScale = this.scale * baseScale;
		int scaledWidth = (int) (finalScale * image.getWidth());
		int scaledHeight = (int) (finalScale * image.getHeight());
		size.setSize(scaledWidth, scaledHeight);
	}
}
