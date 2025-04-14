/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.fitness.activity.widget;

import ej.annotation.Nullable;
import ej.bon.XMath;
import ej.microui.MicroUI;
import ej.microui.display.GraphicsContext;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Rectangle;
import ej.mwt.util.Size;

/**
 * A widget that displays a circular progress bar using vector paths.
 */
public class VectorCircularProgressBar extends Widget {

	/** The extra field ID for the style of the circle arc. */
	public static final int CIRCLE_ARC_STYLE = 0;
	/** The extra field ID for the style of the background circle arc. */
	public static final int BACKGROUND_CIRCLE_ARC_STYLE = 1;

	private float progress;
	private final float startAngle;
	private final float maxArcAngle;
	@Nullable
	private CircleArc circleArc;
	@Nullable
	private CircleArc circleArcBackground;

	/**
	 * Creates a circular progress bar given the progress range (minimum, maximum), the initial value, and the circle
	 * arc start and maximum angles.
	 *
	 * <p>
	 * The arc is drawn from <code>startAngle</code> up to <code>maximumArcAngle</code> degrees. The center of the arc
	 * is defined as the center of the widget's bounds.<br>
	 * <br>
	 * Angles are interpreted such that 0 degrees is at the 3 o'clock position. A positive <code>maximumArcAngle</code>
	 * value indicates a counter-clockwise rotation while a negative value indicates a clockwise rotation.<br>
	 * 
	 * @param initialProgress
	 *            the initial progress
	 * @param startAngle
	 *            the start angle of the arc to draw (in degrees)
	 * @param maximumArcAngle
	 *            the maximum angular extent of the arc from <code>startAngle</code> (in degrees)
	 */
	public VectorCircularProgressBar(float initialProgress, float startAngle, float maximumArcAngle) {
		this.progress = XMath.limit(initialProgress, 0f, 1f);
		this.startAngle = startAngle;
		this.maxArcAngle = maximumArcAngle;
	}

	/**
	 * Sets the current progress value.
	 *
	 * @param value
	 *            the current progress value
	 */
	public void setProgress(float value) {
		this.progress = XMath.limit(value, 0f, 1f);
		MicroUI.callSerially(new Runnable() {

			@Override
			public void run() {
				updateArcAngle();
			}
		});
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// take all available size given by parent.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		CircleArc background = this.circleArcBackground;
		if (background != null) {
			background.render(g);
		}

		CircleArc arc = this.circleArc;
		assert arc != null;
		arc.render(g);
	}

	@Override
	protected void onLaidOut() {
		super.onLaidOut();
		createCircleArc();
	}

	private void createCircleArc() {
		Style style = getStyle();
		CircleArc.CircleArcBuilder builder = style.getExtraObject(CIRCLE_ARC_STYLE, CircleArc.CircleArcBuilder.class,
				CircleArc.CircleArcBuilder.DEFAULT_BUILDER);
		Rectangle contentBounds = getContentBounds();
		int contentWidth = contentBounds.getWidth();
		int contentHeight = contentBounds.getHeight();
		float size = Math.min(contentWidth, contentHeight);
		int centerX = contentWidth / 2;
		int centerY = contentHeight / 2;
		float currentAngle = getCurrentAngle();
		float startAngle = this.startAngle;
		this.circleArc = builder.build(startAngle, currentAngle, size, centerX, centerY);

		builder = style.getExtraObject(BACKGROUND_CIRCLE_ARC_STYLE, CircleArc.CircleArcBuilder.class,
				CircleArc.CircleArcBuilder.DEFAULT_BUILDER);
		if (builder != CircleArc.CircleArcBuilder.DEFAULT_BUILDER) {
			this.circleArcBackground = builder.build(startAngle, this.maxArcAngle, size, centerX, centerY);
		} else {
			this.circleArcBackground = null;
		}
	}

	private void updateArcAngle() {
		CircleArc arc = this.circleArc;
		if (arc != null) {
			arc.updateArcAngle(getCurrentAngle());
		}
	}

	private float getCurrentAngle() {
		return this.maxArcAngle * this.progress;
	}

	@Override
	protected void onDetached() {
		super.onDetached();
		CircleArc arc = this.circleArc;
		if (arc != null) {
			this.circleArc = null;
			this.circleArcBackground = null;
		}
	}
}
