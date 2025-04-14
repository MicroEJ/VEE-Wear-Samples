/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.stopwatch.activity.widget;

/**
 * Provides convenient methods for formatting a time.
 */
public class TimeFormatter {

	private static final int MILLIS_IN_SECOND = 1000;
	private static final int SECONDS_IN_MINUTE = 60;
	private static final int MINUTES_IN_HOUR = 60;

	private static final int MILLIS_IN_MINUTE = MILLIS_IN_SECOND * SECONDS_IN_MINUTE;
	private static final int MILLIS_IN_HOUR = MILLIS_IN_MINUTE * MINUTES_IN_HOUR;

	private static final char FORMAT_MAIN_SEPARATOR = ':';
	private static final char FORMAT_MILLIS_SEPARATOR = '.';

	/** Displays hours when formating */
	public static final short FORMAT_HOURS = 0b0001;
	/** Displays minutes when formating */
	public static final short FORMAT_MINUTES = 0b0010;
	/** Displays seconds when formating */
	public static final short FORMAT_SECONDS = 0b0100;
	/** Displays milliseconds when formating */
	public static final short FORMAT_MILLIS = 0b1000;
	/** Displays hours, minutes, seconds and milliseconds when formating */
	public static final short FORMAT_ALL = FORMAT_HOURS | FORMAT_MINUTES | FORMAT_SECONDS | FORMAT_MILLIS;

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
		long hours = time / MILLIS_IN_HOUR;
		long minutes = time / MILLIS_IN_MINUTE % 60;
		long seconds = time / MILLIS_IN_SECOND % 60;
		long millis = time % 1000;

		// build the string following the given format
		StringBuilder builder = new StringBuilder();
		if ((format & FORMAT_HOURS) != 0) {
			builder.append(pad(hours)).append(FORMAT_MAIN_SEPARATOR);
		}
		if ((format & FORMAT_MINUTES) != 0) {
			builder.append(pad(minutes)).append(FORMAT_MAIN_SEPARATOR);
		}
		if ((format & FORMAT_SECONDS) != 0) {
			builder.append(pad(seconds)).append(FORMAT_MILLIS_SEPARATOR);
		}
		if ((format & FORMAT_MILLIS) != 0) {
			// millis of length: 2 hardcoded for simplicity
			builder.append(pad(millis / 10));
		}

		// clean up tailing separator if exists
		int i = builder.length() - 1;
		char lastChar = builder.charAt(i);
		if (lastChar == FORMAT_MAIN_SEPARATOR || lastChar == FORMAT_MILLIS_SEPARATOR) {
			builder.deleteCharAt(i);
		}

		return builder.toString();
	}

	/**
	 * Formats the given time like {@code hh:mm:ss.ss} (specified by {@link #FORMAT_ALL}).
	 *
	 * @param time
	 *            The time to format.
	 * @return The formatted string.
	 * @see #format(long, int)
	 */
	public static String format(final long time) {
		return format(time, FORMAT_ALL);
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
}
