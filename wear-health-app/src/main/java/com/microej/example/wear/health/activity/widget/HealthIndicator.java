/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health.activity.widget;

import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Size;
import ej.widget.render.ImagePainter;

/**
 * {@link Widget} that renders a health indicator.
 * <p>
 * The indicator can represent 3 levels: bad, average or good.
 */
public class HealthIndicator extends Widget {

	/** Bad level. */
	public static final int BAD = 0;

	/** Average level. */
	public static final int AVERAGE = 1;

	/** * Good level. */
	public static final int GOOD = 2;

	private final Image image;

	private int level;

	/**
	 * Creates a health indicator.
	 *
	 * @param level
	 *            the level.
	 * @throws IllegalArgumentException
	 *             if the given level is not {@link #BAD}, {@link #AVERAGE} or {@link #GOOD}.
	 */
	public HealthIndicator(int level) {
		checkLevel(level);
		this.image = Image.getImage("/images/indicator_blank-alpha.png");
		this.level = level;
	}

	/**
	 * Sets the level.
	 *
	 * @param level
	 *            the level.
	 * @throws IllegalArgumentException
	 *             if the given level is not {@link #BAD}, {@link #AVERAGE} or {@link #GOOD}.
	 */
	public void setLevel(int level) {
		checkLevel(level);
		this.level = level;
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		g.setColor(computeColor());
		ImagePainter.drawImageInArea(g, this.image, 0, 0, contentWidth, contentHeight, style.getHorizontalAlignment(),
				style.getVerticalAlignment());
	}

	private int computeColor() {
		switch (this.level) {
		case BAD:
			return 0xEE502E;
		case AVERAGE:
			return 0xFFC845;
		case GOOD:
			return 0x6CC24A;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		ImagePainter.computeOptimalSize(this.image, size);
	}

	private static void checkLevel(int level) {
		if (level < BAD || level > GOOD) {
			throw new IllegalArgumentException();
		}
	}
}
