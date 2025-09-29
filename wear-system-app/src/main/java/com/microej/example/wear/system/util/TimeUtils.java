/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.util;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.TimeService;

/**
 * Provides utility methods and constants related to time.
 */
public class TimeUtils {

	/** The number of milliseconds in a second. */
	public static final int MILLIS_IN_SECOND = 1000;
	/** The number of minutes in an hour. */
	public static final int MINUTES_IN_HOUR = 60;
	/** The number of hours in a day. */
	public static final int HOURS_IN_DAY = 24;

	/** The number of milliseconds in a minute. */
	public static final int MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60;
	/** The number of milliseconds in an hour. */
	public static final int MILLIS_IN_HOUR = MILLIS_IN_MINUTE * MINUTES_IN_HOUR;

	private TimeUtils() {
		// private constructor
	}

	/**
	 * Returns the current local time.
	 *
	 * <p>
	 * The local time is the time adjusted for the current time-zone and DST, in other words, the time that would be
	 * displayed to the user.
	 *
	 * @return the current local time (in milliseconds)
	 */
	public static long getCurrentLocalTime() {
		TimeService timeService = KernelServiceProvider.getTimeService();
		long currentTime = timeService.getCurrentTime();
		int currentZoneOffset = timeService.getTimeZoneOffset();
		return currentTime + (long) currentZoneOffset * MILLIS_IN_SECOND;
	}
}
