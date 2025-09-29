/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget;

import ej.annotation.Nullable;
import ej.drawing.ShapePainter.Cap;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microvg.LinearGradient;
import ej.microvg.Matrix;
import ej.microvg.Path;
import ej.microvg.VectorGraphicsPainter;

/**
 * A class for rendering circle arcs using a vector path.
 */
public class CircleArc {

	private Path path;
	@Nullable
	private final LinearGradient gradient;
	private int color;
	private final Matrix matrix;
	private float arcAngle;
	private final float diameter;
	private final float startAngle;
	private final float thickness;
	private final Cap cap;

	private CircleArc(int color, @Nullable LinearGradient gradient, float diameter, float thickness, float startAngle,
			float arcAngle, Cap cap, float centerX, float centerY) {
		this.gradient = gradient;
		this.color = color;
		this.matrix = new Matrix();
		this.matrix.setTranslate(centerX, centerY);
		this.diameter = diameter;
		this.thickness = thickness;
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;
		this.cap = cap;
		this.path = updatePath();
	}

	/**
	 * Sets the arc angle.
	 *
	 * <p>
	 * The arc starts at <code>startAngle</code> up to <code>arcAngle</code> degrees. Angles are interpreted such that 0
	 * degrees is at the 3 o'clock position. A positive <code>arcAngle</code> value indicates a counter-clockwise
	 * rotation whereas a negative value indicates a clockwise rotation.
	 *
	 * @param arcAngle
	 *            the arc angle to set, in degrees.
	 */
	public void updateArcAngle(float arcAngle) {
		this.arcAngle = arcAngle;
		this.path = updatePath();
	}

	/**
	 * Sets the arc color.
	 *
	 * @param color
	 *            the color to set.
	 */
	public void updateColor(int color) {
		this.color = color;
	}

	/**
	 * Renders the content of a path that represents a circle arc.
	 *
	 * @param g
	 *            Graphic context.
	 */
	public void render(GraphicsContext g) {
		LinearGradient gradient = this.gradient;
		if (gradient != null) {
			VectorGraphicsPainter.fillGradientPath(g, this.path, this.matrix, gradient);
		} else {
			g.setColor(this.color);
			VectorGraphicsPainter.fillPath(g, this.path, this.matrix);
		}
	}

	private Path updatePath() {
		return PathHelper.computeThickShapeEllipseArc(this.diameter, this.diameter, this.thickness, this.startAngle,
				this.arcAngle, this.cap);
	}

	/**
	 * A builder that creates vector circle arcs, given the arc description.
	 */
	public static class CircleArcBuilder {

		/** A singleton builder instance. */
		public static final CircleArcBuilder DEFAULT_BUILDER = new CircleArcBuilder(Colors.WHITE, 10f, Cap.ROUNDED);

		private int color;
		private final float thickness;
		private final Cap cap;
		@Nullable
		private GradientStyle gradientStyle;

		/**
		 * Creates the builder for building an arc with the given fill color.
		 * 
		 * @param color
		 *            the fill color to use
		 * @param thickness
		 *            the thickness to use
		 * @param cap
		 *            the cap to use
		 */
		public CircleArcBuilder(int color, float thickness, Cap cap) {
			this.color = color;
			this.thickness = thickness;
			this.cap = cap;
		}

		/**
		 * Creates the builder for building an arc with the given gradient style.
		 * 
		 * @param gradientStyle
		 *            the style of gradient to use
		 * @param thickness
		 *            the thickness to use
		 * @param cap
		 *            the cap to use
		 */
		public CircleArcBuilder(GradientStyle gradientStyle, float thickness, Cap cap) {
			this.gradientStyle = gradientStyle;
			this.thickness = thickness;
			this.cap = cap;
		}

		/**
		 * Builds a new instance of vector circle arc using the arguments given to the builder through setter methods.
		 *
		 * @param startAngle
		 *            the start angle
		 * @param arcAngle
		 *            the circle arc angle
		 * @param diameter
		 *            the circle arc diameter
		 * @param centerX
		 *            the x coordinate of the circle arc center
		 * @param centerY
		 *            the y coordinate of the circle arc center
		 * @return a new instance of a vector circle arc
		 */
		public CircleArc build(float startAngle, float arcAngle, float diameter, float centerX, float centerY) {
			GradientStyle gradientStyle = this.gradientStyle;
			LinearGradient gradient = null;
			diameter -= (this.thickness + 1);
			if (gradientStyle != null) {
				float radians = (float) Math.toRadians(gradientStyle.getAngle());
				float gradientX = (float) (Math.cos(radians) * (diameter / 2));
				float gradientY = (float) (Math.sin(radians) * (diameter / 2));

				gradient = new LinearGradient(gradientX, gradientY, -gradientX, -gradientY, gradientStyle.getColors(),
						gradientStyle.getPositions());
			}
			return new CircleArc(this.color, gradient, diameter, this.thickness, startAngle, arcAngle, this.cap,
					centerX, centerY);
		}
	}

	/**
	 * Represents the gradient information: colors, colors anchor points, and angle.
	 */
	public static class GradientStyle {

		private final int[] colors;

		private final float[] positions;

		private final int angle;

		/**
		 * Creates a gradient style, given the colors to use, the position of colors in the gradient and the gradient
		 * angle.
		 *
		 * <p>
		 * Angles are interpreted such that 0 degrees is at the 3 o'clock position. A positive <code>angle</code> value
		 * indicates a counter-clockwise rotation whereas a negative value indicates a clockwise rotation.
		 *
		 * <p>
		 * The positions of the colors are relative to the gradient length from 0 (start) to 255 (end).
		 *
		 * @param colors
		 *            the colors to use.
		 * @param stops
		 *            the positions of colors in the gradient.
		 * @param angle
		 *            the gradient angle.
		 */
		public GradientStyle(int[] colors, float[] stops, int angle) {
			this.colors = colors.clone();
			this.positions = stops.clone();
			this.angle = angle;
		}

		/**
		 * Gets the colors.
		 *
		 * <p>
		 * The returned array is a shallow copy of the array field.
		 *
		 * @return the colors.
		 */
		public int[] getColors() {
			return this.colors.clone();
		}

		/**
		 * Gets the float stops.
		 *
		 * <p>
		 * The returned array is a shallow copy of the array field.
		 *
		 * @return the float stops.
		 */
		public float[] getPositions() {
			return this.positions.clone();
		}

		/**
		 * Gets the angle.
		 *
		 * <p>
		 * Angles are interpreted such that 0 degrees is at the 3 o'clock position. A positive angle value indicates a
		 * counter-clockwise rotation whereas a negative value indicates a clockwise rotation.
		 *
		 * @return the angle.
		 */
		public int getAngle() {
			return this.angle;
		}
	}
}
