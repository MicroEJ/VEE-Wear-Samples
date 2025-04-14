/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health.activity.widget;

import ej.bon.XMath;
import ej.drawing.ShapePainter;
import ej.drawing.ShapePainter.Cap;
import ej.microui.display.GraphicsContext;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Size;

/**
 * A progress bar is a widget which displays an animated bar indicating that the user should wait for an estimated
 * amount of time.
 */
public class ProgressBar extends Widget {

	/** Extra field ID for the progress color. */
	public static final int EXTRA_FIELD_PROGRESS_COLOR = 0;

	/** Extra field ID for the thickness. */
	public static final int EXTRA_FIELD_THICKNESS = 1;

	private float progress;

	/**
	 * Creates a progress bar. The progress value is initialized to 0.
	 */
	public ProgressBar() {
		this.progress = 0.0f;
	}

	/**
	 * Sets the progress value.
	 * <p>
	 * The given progress value is clamped between <code>0.0f</code> and <code>1.0f</code>.
	 *
	 * @param progress
	 *            the progress value to set.
	 */
	public void setProgress(float progress) {
		this.progress = XMath.limit(progress, 0.0f, 1.0f);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();

		// fill rectangle
		g.setColor(style.getColor());
		int thickness = style.getExtraInt(EXTRA_FIELD_THICKNESS, 5);
		int radius = thickness / 2;
		ShapePainter.drawThickFadedLine(g, radius, contentHeight / 2, contentWidth - radius, contentHeight / 2,
				thickness, 0, Cap.ROUNDED, Cap.ROUNDED);

		// fill rectangle
		int filledWidth = Math.round(contentWidth * this.progress);
		int progressColor = style.getExtraInt(EXTRA_FIELD_PROGRESS_COLOR, 5);
		g.setColor(progressColor);
		if (filledWidth > 0) {
			ShapePainter.drawThickFadedLine(g, radius, contentHeight / 2, filledWidth - radius, contentHeight / 2,
					thickness, 0, Cap.ROUNDED, Cap.ROUNDED);
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();
		int fontHeight = style.getFont().getHeight();
		size.setSize(fontHeight, fontHeight);
	}
}
