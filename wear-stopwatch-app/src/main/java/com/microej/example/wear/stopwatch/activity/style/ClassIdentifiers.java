/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.stopwatch.activity.style;

/**
 * Lists all the application's stylesheet classes.
 */
public final class ClassIdentifiers {

	/** The stylesheet class of the desktop's root widget. */
	public static final short DESKTOP_ROOT = 0;
	/** The stylesheet class of the desktop's control widget. */
	public static final short DESKTOP_CONTROLS = 2;

	/** The stylesheet class of a controls' button. */
	public static final short CONTROL_BUTTON = 15;
	/** The stylesheet class of a controls' inner margin between buttons. */
	public static final short CONTROL_INNER_MARGIN = 16;
	/** The stylesheet class of a controls' disabled button */
	public static final short DISABLED_CONTROL_BUTTON = 17;

	/** The stylesheet class of a timer's central display (minutes/seconds). */
	public static final short TIMER_CENTRAL = 20;
	/** The stylesheet class of a timer's side display (hour or millis). */
	public static final short TIMER_SIDE = 21;

	/** The stylesheet class of a lap. */
	public static final short LAP = 30;
	/** The stylesheet class of a lap's id. */
	public static final short LAP_ID = 31;
	/** The stylesheet class of a lap's time difference from previous lap. */
	public static final short LAP_DIFF = 32;
	/** The stylesheet class of a lap's time. */
	public static final short LAP_TIME = 33;

	private ClassIdentifiers() {
		// private constructor
	}
}
