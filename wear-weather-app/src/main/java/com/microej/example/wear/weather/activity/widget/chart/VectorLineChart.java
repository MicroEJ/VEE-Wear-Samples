/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

import com.microej.example.wear.weather.activity.style.GradientStyle;

import ej.annotation.Nullable;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that displays a vector-based line chart for visualizing float data points. Supports configurable spacing and
 * point selection events.
 */
public class VectorLineChart extends Widget {

	/** The extra field ID for the curve color style. */
	public static final int CURVE_COLOR_STYLE = 0;

	/** The extra field ID for the curve gradient style. */
	public static final int CURVE_GRADIENT_STYLE = 1;

	/** The extra field ID for the curve thickness style. */
	public static final int CURVE_THICKNESS_STYLE = 2;

	/** The extra field ID for the font style. */
	public static final int FONT_STYLE = 3;

	private static final int HIGHLIGHT_COLOR = 0xffff8533;

	private static final int X_OFFSET = 5;

	private static final int SELECTION_THRESHOLD = 30;

	private static final int DEFAULT_CURVE_COLOR = Colors.RED;

	private static final float DEFAULT_CURVE_THICKNESS = 2.5f;

	private static final int POINT_DIAMETER = 10;

	private final float[] data;

	@Nullable
	private Point[] points;
	@Nullable
	private ChartCurve curve;
	@Nullable
	private ChartGradient gradient;
	private final VectorImage gridImage;
	@Nullable
	private final OnPointSelectedListener onPointSelectedListener;
	private final int horizontalStep;
	private final int maxValue;
	private int selectedIndex;

	/**
	 * Creates a vector line chart with the given data and configuration.
	 *
	 * @param data
	 *            the array of data points to plot.
	 * @param horizontalStep
	 *            the horizontal spacing in pixels between data points.
	 * @param maxValue
	 *            the maximum value to display in the chart.
	 * @param onPointSelectedListener
	 *            the listener to notify on point selection events.
	 */
	public VectorLineChart(float[] data, int horizontalStep, int maxValue,
			@Nullable OnPointSelectedListener onPointSelectedListener) {
		super(true);
		this.data = data;
		this.horizontalStep = horizontalStep;
		this.maxValue = maxValue;
		this.gridImage = VectorImage.getImage("/images/small_grid.xml");
		this.onPointSelectedListener = onPointSelectedListener;
		this.selectedIndex = 0;
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Pointer.EVENT_TYPE) {
			Pointer pointer = (Pointer) Event.getGenerator(event);
			if (Buttons.getAction(event) == Buttons.RELEASED) {
				Point[] points = this.points;
				if (points != null) {
					int newIndex = computePointIndex(points, pointer.getX() - getAbsoluteX());
					if (newIndex != this.selectedIndex) {
						this.selectedIndex = newIndex;
						OnPointSelectedListener onPointSelectedListener = this.onPointSelectedListener;
						if (onPointSelectedListener != null) {
							onPointSelectedListener.onPointSelected(this.selectedIndex);
						}
					}
					return true;
				}
			}
		}
		return super.handleEvent(event);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setWidth(this.horizontalStep * this.data.length);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		ChartGradient gradient = this.gradient;
		if (gradient != null) {
			gradient.render(g);
		}
		ChartCurve curve = this.curve;
		if (curve != null) {
			curve.render(g);
		}
		drawGrid(g, contentWidth, contentHeight);
		drawSelectedPoint(g);
	}

	@Override
	protected void onLaidOut() {
		super.onLaidOut();
		if (this.curve == null) {
			normalizeData();
			createCurve();
			createGradient();
			this.points = createPoints(this.data, VectorLineChart.this.horizontalStep, getHeight());
		}
	}

	private void createGradient() {
		Style style = getStyle();
		GradientStyle gradientStyle = style.getExtraObject(VectorLineChart.CURVE_GRADIENT_STYLE, GradientStyle.class,
				GradientStyle.DEFAULT_GRADIENT_STYLE);
		this.gradient = new ChartGradient(getHeight(), this.data, this.horizontalStep, gradientStyle);
	}

	private void createCurve() {
		Style style = getStyle();
		int color = style.getExtraInt(VectorLineChart.CURVE_COLOR_STYLE, VectorLineChart.DEFAULT_CURVE_COLOR);
		float thickness = style.getExtraFloat(VectorLineChart.CURVE_THICKNESS_STYLE,
				VectorLineChart.DEFAULT_CURVE_THICKNESS);
		this.curve = new ChartCurve(getHeight(), this.data, this.horizontalStep, color, thickness);
	}

	private void normalizeData() {
		float ratio = (float) getHeight() / (this.maxValue);
		float[] array = this.data;
		int length = array.length;
		for (int i = 0; i < length; i++) {
			array[i] *= ratio;
		}
	}

	private void drawGrid(GraphicsContext g, int contentWidth, int contentHeight) {
		VectorImage image = this.gridImage;

		int x = Alignment.computeLeftX((int) image.getWidth(), 0, contentWidth, Alignment.HCENTER);
		int y = Alignment.computeTopY((int) image.getHeight(), 0, contentHeight, Alignment.VCENTER);
		Matrix sizeMatrix = new Matrix();
		sizeMatrix.setTranslate(x, y);
		float[] color = new float[] { 0, 0, 0, 0, 255, // red → 255
				0, 0, 0, 0, 179, // green → 179
				0, 0, 0, 0, 102, // blue → 102
				0, 0, 0, 0, 76 // alpha (76 out of 255 = 30%)
		};

		VectorGraphicsPainter.drawFilteredImage(g, image, sizeMatrix, color);
	}

	private static int computePointIndex(Point[] points, int x) {
		for (int i = 0; i < points.length; i++) {
			int xToPlot = points[i].x + VectorLineChart.X_OFFSET;
			if (Math.abs(xToPlot - x) < VectorLineChart.SELECTION_THRESHOLD) {
				return i;
			}
		}
		return 0;
	}

	private void drawSelectedPoint(GraphicsContext g) {
		Point[] points = this.points;
		if (points != null) {
			Point point = points[this.selectedIndex];
			int x = point.x + VectorLineChart.X_OFFSET;
			int y = point.y;
			g.setColor(VectorLineChart.HIGHLIGHT_COLOR);
			Painter.fillCircle(g, x, y, VectorLineChart.POINT_DIAMETER);
		}
	}

	private static Point[] createPoints(float[] data, int step, int contentHeight) {
		int length = data.length;
		Point[] points = new Point[length];
		int x = step * length;

		for (int i = length - 1; i >= 0; i--) {
			x -= step;
			float y = contentHeight - VectorLineChart.getValueAtIndex(data, i);

			int xToPlot = x - (VectorLineChart.POINT_DIAMETER / 2);
			int yToPlot = (int) (y - VectorLineChart.POINT_DIAMETER);
			points[i] = new Point(xToPlot, yToPlot);
		}

		return points;
	}

	private static float getValueAtIndex(float[] data, int index) {
		int length = data.length;
		int wrappedIndex = ((index % length) + length) % length;
		return data[wrappedIndex];
	}

	/**
	 * Represents a point of the chart.
	 */
	private static class Point {
		private final int x;
		private final int y;

		/**
		 * Constructs a point with the specified x and y values.
		 *
		 * @param x
		 *            the x-coordinate
		 * @param y
		 *            the y-coordinate
		 */
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
