/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.TimeService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Provides convenient methods for formatting a time.
 */
public class TimeFormatter {

	private static final int MILLIS_IN_SECOND = 1000;
	private static final int SECONDS_IN_MINUTE = 60;
	private static final int MINUTES_IN_HOUR = 60;

	private static final int MILLIS_IN_MINUTE = TimeFormatter.MILLIS_IN_SECOND * TimeFormatter.SECONDS_IN_MINUTE;
	private static final int MILLIS_IN_HOUR = TimeFormatter.MILLIS_IN_MINUTE * TimeFormatter.MINUTES_IN_HOUR;

	private static final char FORMAT_MAIN_SEPARATOR = ':';
	private static final char FORMAT_MILLIS_SEPARATOR = '.';

	/**
	 * Displays hours when formating
	 */
	public static final short FORMAT_HOURS = 0b0001;
	/**
	 * Displays minutes when formating
	 */
	public static final short FORMAT_MINUTES = 0b0010;
	/**
	 * Displays seconds when formating
	 */
	public static final short FORMAT_SECONDS = 0b0100;
	/**
	 * Displays milliseconds when formating
	 */
	public static final short FORMAT_MILLIS = 0b1000;
	/**
	 * Displays hours, minutes, seconds and milliseconds when formating
	 */
	public static final short FORMAT_ALL = TimeFormatter.FORMAT_HOURS | TimeFormatter.FORMAT_MINUTES
			| TimeFormatter.FORMAT_SECONDS | TimeFormatter.FORMAT_MILLIS;

	private TimeFormatter() {
		// private constructor
	}

	/**
	 * Formats the given time in a given format.
	 * <p>
	 * Use {@link #FORMAT_HOURS TimeUtils.FORMAT_HOURS}, {@link #FORMAT_MINUTES TimeUtils.FORMAT_MINUTES},
	 * {@link #FORMAT_SECONDS TimeUtils.FORMAT_SECONDS} and {@link #FORMAT_MILLIS} to indicate what unit to return in
	 * the string.
	 * <p>
	 * For example:
	 * <p>
	 * {@code format(aTime, FORMAT_MINUTES | FORMAT_SECONDS | FORMAT_MILLIS);}
	 * <p>
	 * returns {@code mm:ss.ss}.
	 *
	 * @param time
	 *            The time to format.
	 * @param format
	 *            The format to use.
	 * @return The formated string.
	 */
	public static String format(final long time, final int format) {
		long hours = time / TimeFormatter.MILLIS_IN_HOUR;
		long minutes = time / TimeFormatter.MILLIS_IN_MINUTE % 60;
		long seconds = time / TimeFormatter.MILLIS_IN_SECOND % 60;
		long millis = time % 1000;

		// build the string following the given format
		StringBuilder builder = new StringBuilder();
		if ((format & TimeFormatter.FORMAT_HOURS) != 0) {
			builder.append(TimeFormatter.pad(hours)).append(TimeFormatter.FORMAT_MAIN_SEPARATOR);
		}
		if ((format & TimeFormatter.FORMAT_MINUTES) != 0) {
			builder.append(TimeFormatter.pad(minutes)).append(TimeFormatter.FORMAT_MAIN_SEPARATOR);
		}
		if ((format & TimeFormatter.FORMAT_SECONDS) != 0) {
			builder.append(TimeFormatter.pad(seconds)).append(TimeFormatter.FORMAT_MILLIS_SEPARATOR);
		}
		if ((format & TimeFormatter.FORMAT_MILLIS) != 0) {
			// millis of length: 2 hardcoded for simplicity
			builder.append(TimeFormatter.pad(millis / 10));
		}

		// clean up tailing separator if exists
		int i = builder.length() - 1;
		char lastChar = builder.charAt(i);
		if (lastChar == TimeFormatter.FORMAT_MAIN_SEPARATOR || lastChar == TimeFormatter.FORMAT_MILLIS_SEPARATOR) {
			builder.deleteCharAt(i);
		}

		return builder.toString();
	}

	/**
	 * Pads a long integer with a {@code 0} if it is greater than {@code 10}.
	 *
	 * @param value
	 *            The number to pad.
	 * @return The padded number.
	 */
	private static String pad(long value) {
		return (value < 10 ? "0" + value : Long.toString(value));
	}

	/**
	 * Formats the given time duration into a structured string representation. The formatted duration includes hours,
	 * minutes, seconds, separated by predefined separators.
	 *
	 * @param time
	 *            The duration in milliseconds to format.
	 * @return A formatted time string in the format: "HH:mm:ss".
	 */
	public static String getFormatedDuration(long time) {
		String hours = TimeFormatter.format(time, TimeFormatter.FORMAT_HOURS);
		String central = TimeFormatter.format(time, TimeFormatter.FORMAT_MINUTES | TimeFormatter.FORMAT_SECONDS);
		return hours + TimeFormatter.FORMAT_MAIN_SEPARATOR + central;
	}

	/**
	 * Retrieves the current local time formatted as "HH:MM".
	 * <p>
	 * This method obtains the current time from the {@link TimeService}, applies the time zone offset, and formats it
	 * as a zero-padded hour and minute string in 24-hour format.
	 * </p>
	 *
	 * @return A string representing the current local time in "HH:MM" format.
	 */
	public static String getCurrentTimeFormatted() {
		TimeService timeService = KernelServiceProvider.getTimeService();
		long currentLocalTime = timeService.getCurrentTime()
				+ timeService.getTimeZoneOffset() * TimeFormatter.MILLIS_IN_SECOND;
		Instant now = Instant.ofEpochMilli(currentLocalTime);

		// Convert to LocalDateTime using UTC
		LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
		int hours = localDateTime.getHour();
		int minutes = localDateTime.getMinute();

		// Manually format HH:MM
		String hoursStr = (hours < 10) ? "0" + hours : Integer.toString(hours);
		String minutesStr = (minutes < 10) ? "0" + minutes : Integer.toString(minutes);

		return hoursStr + ":" + minutesStr;
	}
}
