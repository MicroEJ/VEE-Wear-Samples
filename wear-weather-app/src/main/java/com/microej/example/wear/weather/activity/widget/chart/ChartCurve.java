/*
 * Java
 *
 * Copyright 2020-2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.Path;
import ej.microvg.VectorGraphicsPainter;

/**
 * A vector graphics that represents the curve of a line chart.
 */
public class ChartCurve {

	private static final double HALF_PI = Math.PI / 2;

	private static final float TOP_PADDING = 5.0f;

	private static final float LEFT_PADDING = 5.0f;

	private final int hStep;

	private final float thickness;

	private Path path;

	private final int color;

	private final Matrix matrix;

	/**
	 * Constructs the vector curve, given the data array to plot.
	 *
	 * @param height
	 *            the height of the curve's bounding-box.
	 * @param data
	 *            the data array which contains the values to plot.
	 * @param step
	 *            the horizontal spacing to use between each data point.
	 * @param color
	 *            the color of the curve.
	 * @param thickness
	 *            the thickness of the curve.
	 */
	public ChartCurve(int height, float[] data, int step, int color, float thickness) {
		this.path = computePath(data, step, thickness);
		this.color = color;
		this.hStep = step;
		this.thickness = thickness;

		this.matrix = new Matrix();
		this.matrix.setTranslate(ChartCurve.LEFT_PADDING, height - ChartCurve.TOP_PADDING);
	}

	private static Path computePath(float[] data, int hStep, float thickness) {
		Path path = new Path();
		int i = 0;
		int x = hStep;
		int width = hStep * data.length;

		float firstData = getValueAtIndex(data, i);
		i++;

		float halfThickness = thickness / 2;
		path.moveTo(0, (-firstData) - halfThickness);

		while (x < width) {
			float previous = getValueAtIndex(data, i - 1);
			float current = getValueAtIndex(data, i);
			float next = getValueAtIndex(data, i + 1);
			ChartCurve.plot(path, previous, current, next, x, hStep, halfThickness);
			i++;
			x += hStep;
		}

		path.lineToRelative(0F, thickness);

		while (i >= 0) {
			i--;
			x -= hStep;
			float previous = getValueAtIndex(data, i + 1);
			float current = getValueAtIndex(data, i);
			float next = getValueAtIndex(data, i - 1);
			plot(path, previous, current, next, x, -hStep, -halfThickness);
		}

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

	private static void plot(Path path, float previous, float current, float next, int x, int hStep, float offset) {
		double previousAngle = Math.atan2(previous - current, -hStep);
		double nextAngle = Math.atan2(next - current, hStep);
		double angle = (Math.PI + nextAngle + previousAngle) / 2;

		while (angle > HALF_PI) {
			angle -= Math.PI;
		}

		while (angle < (-HALF_PI)) {
			angle += Math.PI;
		}

		float xNew = (float) (x + offset * Math.cos(angle + HALF_PI));
		float yNew = (float) (current + offset * Math.sin(angle + HALF_PI));

		path.lineTo(xNew, -yNew);
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
		this.path = computePath(data, this.hStep, this.thickness);
	}

	/**
	 * Renders the content of the chart curve.
	 *
	 * @param g
	 *            Graphic context.
	 */
	public void render(GraphicsContext g) {
		g.setColor(this.color);
		VectorGraphicsPainter.fillPath(g, this.path, this.matrix);
	}

}
