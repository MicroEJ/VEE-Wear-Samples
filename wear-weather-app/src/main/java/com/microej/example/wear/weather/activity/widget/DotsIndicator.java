/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget;

import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A vertical dots indicator widget used to represent pages or steps.
 */
public class DotsIndicator extends Widget {

	private static final int FADE = 1;
	private static final String DOT_PATH = "/images/dot.xml";
	private int count;
	private int selected;
	private final VectorImage dot;

	/**
	 * Creates a dots indicator.
	 *
	 * @param count
	 *            the number of dots.
	 */
	public DotsIndicator(int count) {
		this.count = count;
		this.dot = VectorImage.getImage(DOT_PATH);
	}

	/**
	 * Sets the number of dots.
	 *
	 * @param count
	 *            the number of dots.
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Sets the current.
	 *
	 * @param selected
	 *            the selected dot index.
	 */
	public void setSelected(int selected) {
		this.selected = selected;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int dotSize = (int) this.dot.getWidth();
		size.setHeight(dotSize * this.count + (dotSize - 1) * this.count + 2 * FADE);
		size.setWidth(dotSize + 2 * FADE);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		int dotSize = (int) this.dot.getWidth();
		int totalHeight = dotSize * this.count + (dotSize - 1) * this.count + 2 * FADE;

		int leftX = Alignment.computeLeftX(dotSize + 2 * FADE, 0, contentWidth, style.getHorizontalAlignment());
		int topY = Alignment.computeTopY(totalHeight, 0, contentHeight, style.getVerticalAlignment());

		float[] vectorColor;
		for (int i = 0; i < this.count; i++) {
			if (i == this.selected) {
				// White color
				vectorColor = new float[] { 0, 0, 0, 0, 255, // red
						0, 0, 0, 0, 255, // green
						0, 0, 0, 0, 255, // blue
						0, 0, 0, 1, 0 // alpha
				};
			} else {
				// Gray color
				vectorColor = new float[] { 0, 0, 0, 0, 77, // red
						0, 0, 0, 0, 77, // green
						0, 0, 0, 0, 77, // blue
						0, 0, 0, 1, 0 // alpha
				};
			}
			Matrix sizeMatrix = new Matrix();
			sizeMatrix.setTranslate(leftX, topY);
			VectorGraphicsPainter.drawFilteredImage(g, this.dot, sizeMatrix, vectorColor);
			topY += 2 * dotSize + 1;
		}
	}
}