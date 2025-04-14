/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget;

import ej.annotation.Nullable;
import ej.microui.MicroUI;
import ej.microui.display.GraphicsContext;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Rectangle;
import ej.mwt.util.Size;

/**
 * A widget that displays a radial progress circle using vector paths.
 */
public class VectorRadialProgressWidget extends Widget {

	/**
	 * The extra field ID for the style of the circle arc.
	 */
	public static final int CIRCLE_ARC_STYLE = 0;
	/**
	 * The extra field ID for the style of the background circle arc.
	 */
	public static final int BACKGROUND_CIRCLE_ARC_STYLE = 1;
	private static final int PROGRESS_BAR_START_ANGLE = 90;
	private static final int PROGRESS_BAR_END_ANGLE = -359;
	private static final float INITIAL_PROGRESS_VALUE = 0f;
	private float progress;
	private final int cycleDuration;
	@Nullable
	private CircleArc circleArc;
	@Nullable
	private CircleArc circleArcBackground;

	/**
	 * Creates a radial progress widget with the specified cycle duration.
	 *
	 * @param cycleDuration
	 *            The duration of a full progress cycle in milliseconds. This defines how long it takes for the progress
	 *            indicator to complete one full rotation.
	 */
	public VectorRadialProgressWidget(int cycleDuration) {
		this.progress = VectorRadialProgressWidget.INITIAL_PROGRESS_VALUE;
		this.cycleDuration = cycleDuration;
	}

	/**
	 * Updates the progress value and triggers a UI update for the radial progress.
	 *
	 * @param value
	 *            The elapsed time in milliseconds since the start of the current cycle. This value is used to update
	 *            the progress of the animation.
	 */
	public void updateValue(int value) {
		updateProgress(value);
		MicroUI.callSerially(new Runnable() {

			@Override
			public void run() {
				updateArcAngleFromPercent();
			}
		});
	}

	private void updateProgress(int value) {
		this.progress = value;
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
		CircleArc.CircleArcBuilder builder = style.getExtraObject(VectorRadialProgressWidget.CIRCLE_ARC_STYLE,
				CircleArc.CircleArcBuilder.class, CircleArc.CircleArcBuilder.DEFAULT_BUILDER);
		Rectangle contentBounds = getContentBounds();
		int contentWidth = contentBounds.getWidth();
		int contentHeight = contentBounds.getHeight();
		float size = Math.min(contentWidth, contentHeight);
		int centerX = contentWidth / 2;
		int centerY = contentHeight / 2;
		float currentAngle = getCurrentAngle();
		float startAngle = VectorRadialProgressWidget.PROGRESS_BAR_START_ANGLE;
		this.circleArc = builder.build(startAngle, currentAngle, size, centerX, centerY);

		builder = style.getExtraObject(VectorRadialProgressWidget.BACKGROUND_CIRCLE_ARC_STYLE,
				CircleArc.CircleArcBuilder.class, CircleArc.CircleArcBuilder.DEFAULT_BUILDER);
		if (builder != CircleArc.CircleArcBuilder.DEFAULT_BUILDER) {
			this.circleArcBackground = builder.build(startAngle, VectorRadialProgressWidget.PROGRESS_BAR_END_ANGLE,
					size, centerX, centerY);
		} else {
			this.circleArcBackground = null;
		}
	}

	private void updateArcAngleFromPercent() {
		CircleArc arc = this.circleArc;
		if (arc != null) {
			float angle = (VectorRadialProgressWidget.PROGRESS_BAR_END_ANGLE
					- VectorRadialProgressWidget.PROGRESS_BAR_START_ANGLE) * getPercentComplete();
			arc.updateArcAngle(angle);
		}
	}

	private float getPercentComplete() {
		return (this.progress) / this.cycleDuration;
	}

	private float getCurrentAngle() {
		return VectorRadialProgressWidget.PROGRESS_BAR_END_ANGLE * this.progress;
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
