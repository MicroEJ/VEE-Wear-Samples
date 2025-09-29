/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget;

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

	private int scale;
	private VectorImage image;
	private boolean scaled;

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
	public VectorImageWidget(String imagePath, int scale) {
		this.scale = scale;
		this.image = VectorImage.getImage(imagePath);
		this.scaled = true;
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
		this.scale = 1;
		this.image = VectorImage.getImage(imagePath);
		this.scaled = false;
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
		this.scale = 1;
		this.image = VectorImage.getImage(imagePath);
		this.scaled = false;
	}

	/**
	 * Sets the image path.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 */
	public void setImagePath(String imagePath) {
		this.image = VectorImage.getImage(imagePath);
	}

	/**
	 * Sets the scaling factor for the image.
	 *
	 * @param scale
	 *            the scaling factor to apply.
	 */
	public void setScale(int scale) {
		this.scale = scale;
		this.scaled = true;
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		VectorImage image = this.image;
		Style style = getStyle();
		g.setColor(style.getColor());
		Matrix sizeMatrix = new Matrix();
		if (this.scaled) {
			float sx = this.scale / image.getWidth();
			float sy = this.scale / image.getHeight();
			int x = Alignment.computeLeftX(this.scale, 0, contentWidth, Alignment.HCENTER);
			int y = Alignment.computeTopY(this.scale, 0, contentHeight, Alignment.VCENTER);
			sizeMatrix.setTranslate(x, y);
			sizeMatrix.preScale(sx, sy);
		}
		VectorGraphicsPainter.drawImage(g, image, sizeMatrix);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		VectorImage image = this.image;
		size.setSize((int) image.getWidth(), (int) image.getHeight());
	}
}
