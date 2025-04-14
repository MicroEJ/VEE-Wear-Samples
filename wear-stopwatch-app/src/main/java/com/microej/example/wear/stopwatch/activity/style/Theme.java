/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.stopwatch.activity.style;

/**
 * Provides theme constants.
 */
public class Theme {

	/** Timer X position in %. */
	public static final float TIMER_POS_X = 0.5f;
	/** Timer Y position in %. */
	public static final float TIMER_POS_Y = 0.55f;
	/** The size of the central part of the timer in px. */
	public static final short TIMER_CENTRAL_FONT_SIZE = 110;
	/** The size of the side parts of the timer in px. */
	public static final short TIMER_SIDE_FONT_SIZE = 80;
	/** The width of the central part of the timer in px. */
	public static final short TIMER_CENTRAL_WIDTH = 190;
	/** The width of the side parts of the timer in px. */
	public static final short TIMER_SIDE_WIDTH = 80;

	/** Control X position in %. */
	public static final float CONTROL_POS_X = 0.5f;
	/** Control Y position in %. */
	public static final float CONTROL_POS_Y = 0.9f;

	/** The color of the buttons border. */
	public static final int BUTTON_BORDER_COLOR = 0x4B5357;
	/** The color of the disabled buttons border */
	public static final int DISABLED_BUTTON_BORDER_COLOR = 0x25292b;
	/** The color of the button when pressed. */
	public static final int BUTTON_PRESSED_COLOR = BUTTON_BORDER_COLOR;
	/** The border width of the buttons border in px. */
	public static final short BUTTON_BORDER_WIDTH = 2;
	/** Button width in pixel. */
	public static final short BUTTON_WIDTH = 100;
	/** Button height in pixel. */
	public static final short BUTTON_HEIGHT = 100;
	/** Margin between buttons in %. */
	public static final float BUTTON_INNER_MARGIN = 0.08f;

	/** Lap height in %. */
	public static final float LAP_HEIGHT = 0.15f;
	/** Lap top margin in px. */
	public static final short LAP_MARGIN = 0;
	/** Lap font color. */
	public static final int LAP_FONT_COLOR = 0x717D83;
	/** Lap's icon size (width and height). */
	public static final float LAP_ICON_SIZE = 0.11f;
	/** Lap's ID width in %. */
	public static final float LAP_ID_WIDTH = 0.1f;
	/** Lap's ID height in %. */
	public static final float LAP_ID_HEIGHT = 0.1f;
	/** Lap's ID font size in px. */
	public static final short LAP_ID_FONT_SIZE = 26;
	/** Lap's ID offset from the center of the lap widget in %. */
	public static final float LAP_ID_OFFSET = 0.015f;
	/** Lap's time font size in px. */
	public static final short LAP_TIME_FONT_SIZE = 44;

	/** Lap list X position in %. */
	public static final float LAP_LIST_POS_X = 0.5f;
	/** Lap list Y position in %. */
	public static final float LAP_LIST_POS_Y = 0f;
	/** Lap list width in %. */
	public static final float LAP_LIST_WIDTH = 0.65f;
	/** Lap list height in %. */
	public static final float LAP_LIST_HEIGHT = 0.38f;
	/** Lap's time width in %. */
	public static final float LAP_TIME_WIDTH = LAP_LIST_WIDTH - LAP_ID_WIDTH - LAP_ICON_SIZE;

	/** The width of the side graphics */
	public static final float SIDE_GRAPHIC_WIDTH = 0.34f;
	/** The height of the side graphics */
	public static final float SIDE_GRAPHIC_HEIGHT = 1f;

	private Theme() {
		// private constructor
	}
}
