/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.breath.activity.style;

/**
 * Provides theme constants.
 */
public class Theme {

	/** Control Y position in %. */
	public static final float CONTROL_BUTTON_POS_Y = 0.9f;
	/** The color of the control buttons border. */
	public static final int CONTROL_BUTTON_BORDER_COLOR = 0x717D83;
	/** The border width of the buttons border in px. */
	public static final short CONTROL_BUTTON_BORDER_WIDTH = 3;
	/** Control button width in %. */
	public static final float CONTROL_BUTTON_SIDE = 0.2f;
	/** Control button icon scale in %. */
	public static final float CONTROL_BUTTON_ICON_SCALE = 0.7f;
	/** Start angle of the timer circular progress in degree. */
	public static final float TIMER_CIRCULAR_PROGRESS_BAR_START_ANGLE = 90;
	/** Max angle of the timer circular progress in degree. */
	public static final float TIMER_CIRCULAR_PROGRESS_BAR_MAX_ANGLE = -360;
	/** Ratio of the timer circular progress in %. */
	public static final float TIMER_CIRCULAR_TIME_PROGRESS_RATIO = 0.96f;
	/** Arc thickness of the timer circular progress in px. */
	public static final float TIMER_CIRCULAR_ARC_THICKNESS = 11f;
	/** The background color of the timer circular progress. */
	public static final int TIMER_CIRCULAR_ARC_BACKGROUND_COLOR = 0x1D1D1D;
	/** The progress start color of the timer circular progress. */
	public static final int TIMER_CIRCULAR_ARC_PROGRESS_START_COLOR = 0xee502e;
	/** The progress end color of the timer circular progress. */
	public static final int TIMER_CIRCULAR_ARC_PROGRESS_END_COLOR = 0x0095FF;
	/** The progress middle color of the timer circular progress. */
	public static final int TIMER_CIRCULAR_ARC_PROGRESS_MIDDLE_COLOR = 0x800080;
	/** Timer list width in %. */
	public static final float TIMER_LIST_WIDTH = 0.6f;
	/** Timer list height in %. */
	public static final float TIMER_LIST_HEIGHT = 0.31f;
	/** the top padding of the timer list in %. */
	public static final float TIMER_LIST_TOP_PADDING = 0.065f;
	/** Timer list Y position in %. */
	public static final float TIMER_LIST_POS_Y = 0.385f;
	/** the color of linear radial gradient in timer list. */
	public static final int TIMER_LIST_LINEAR_GRADIENT_COLOR = 0x000000;
	/** Timer label width in %. */
	public static final float TIMER_LABEL_WIDTH = 0.45f;
	/** Timer label height in %. */
	public static final float TIMER_LABEL_HEIGHT = 0.15f;
	/** The color of the timer label. */
	public static final int TIMER_LABEL_COLOR = 0x717d83;
	/** Timer label font size in %. */
	public static final float TIMER_LABEL_FONT_SIZE = 0.16f;
	/** Breath animation scale. */
	public static final float BREATH_ANIMATION_SCALE = 1.0f;

	private Theme() {
		// private constructor
	}
}
