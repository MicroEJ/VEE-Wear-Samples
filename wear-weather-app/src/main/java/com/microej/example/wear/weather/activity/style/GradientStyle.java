/*
 * Java
 *
 * Copyright 2020-2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.style;

/**
 * Represents the gradient information: colors, colors anchor points, and angle.
 */
public class GradientStyle {

	/** The default gradient style to use when none was specified. */
	public static final GradientStyle DEFAULT_GRADIENT_STYLE = new GradientStyle(new int[] { 0xFFFFFFFF, 0x00FFFFFF },
			new float[] { 0, 1 }, 0);

	private final int[] colors;

	private final float[] floatStops;

	private final int angle;

	/**
	 * Creates a gradient style, given the colors to use, the position of colors in the gradient and the gradient angle.
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
		this.floatStops = stops.clone();
		this.angle = angle;
	}

	/**
	 * Gets the number of colors in this gradient.
	 *
	 * @return the number of colors of this gradient.
	 */
	public int getColorCount() {
		return this.colors.length;
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
	public float[] getFloatStops() {
		return this.floatStops.clone();
	}

	/**
	 * Gets the angle.
	 *
	 * @return the angle.
	 */
	public int getAngle() {
		return this.angle;
	}

}
