/*
 * Java
 *
 * Copyright 2020-2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

import com.microej.example.wear.weather.activity.style.GradientStyle;

import ej.microui.display.GraphicsContext;
import ej.microvg.LinearGradient;
import ej.microvg.Matrix;
import ej.microvg.Path;
import ej.microvg.VectorGraphicsPainter;

/**
 * A vector graphics that represents the gradient under the curve of a line chart.
 *
 * <p>
 * The path is built using Bézier curve from the data array in input.
 */
public class ChartGradient {

	private static final float TOP_PADDING = 5.0f;

	private static final float LEFT_PADDING = 5.0f;

	private final int hStep;

	private Path path;

	private final Matrix matrix;

	private final LinearGradient gradient;

	/**
	 * Constructs the vector curve, given the data array to plot.
	 *
	 * @param height
	 *            the height of the vector graphics' bounding-box.
	 * @param data
	 *            the data array which contains the values to plot.
	 * @param step
	 *            the horizontal spacing to use between each data point.
	 * @param gradientStyle
	 *            the gradient information.
	 */
	public ChartGradient(int height, float[] data, int step, GradientStyle gradientStyle) {
		this.path = computePath(data, step);
		this.matrix = new Matrix();
		this.matrix.setTranslate(LEFT_PADDING, height - ChartGradient.TOP_PADDING);

		this.gradient = new LinearGradient(0, 0, height, 0, gradientStyle.getColors(), gradientStyle.getFloatStops());
		Matrix gradientMatrix = this.gradient.getMatrix();
		gradientMatrix.setTranslate(0, 0);
		gradientMatrix.preRotate(gradientStyle.getAngle());
		gradientMatrix.postTranslate(0, -(height - ChartGradient.TOP_PADDING));

		this.hStep = step;
	}

	private static Path computePath(float[] data, int hStep) {
		Path path = new Path();
		path.moveTo(0, 0);

		int i = 0;
		int x = hStep;
		int width = hStep * data.length;

		float firstData = getValueAtIndex(data, i);
		i++;
		path.lineTo(0, -firstData);

		while (x < width) {
			float current = getValueAtIndex(data, i);
			path.lineTo(x, -current);
			i++;
			x += hStep;
		}
		path.lineTo(x, 0);
		return path;
	}

	private static float getValueAtIndex(float[] data, int index) {
		int length = data.length;
		while (index < 0) {
			index += length;
		}

		while (index >= length) {
			index -= length;
		}
		return data[index];
	}

	/**
	 * Sets the data to plot.
	 *
	 * <p>
	 * This method updates the vector graphics' path to represent the specified data points.
	 *
	 * <p>
	 * This method should be called in the UI thread.
	 *
	 * @param data
	 *            the data to plot.
	 */
	public void setData(float[] data) {
		this.path = computePath(data, this.hStep);
	}

	/**
	 * Renders the content of the chart gradient.
	 *
	 * @param g
	 *            Graphic context.
	 */
	public void render(GraphicsContext g) {
		VectorGraphicsPainter.fillGradientPath(g, this.path, this.matrix, this.gradient);
	}

}
