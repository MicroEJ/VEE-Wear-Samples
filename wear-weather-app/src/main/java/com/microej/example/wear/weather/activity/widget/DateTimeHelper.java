/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.TimeService;

/**
 * A utility class that offers convenient methods for date and time conversion.
 */
public class DateTimeHelper {

	private static final long MILLIS_IN_SECOND = 1000L;
	private static final long MILLISECONDS_IN_SECOND = 1000L;
	private static final int NANOS_IN_MILLISECOND = 1_000_000;
	private static final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
			"Nov", "Dec" };
	private static final String[] DAYS = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

	private DateTimeHelper() {
		// private constructor
	}

	/**
	 * Converts a numeric month value (1-12) to its corresponding three-letter abbreviation.
	 *
	 * @param dateTime
	 *            the {@link LocalDateTime} to extract the month from.
	 * @return the three-letter abbreviation of the month (e.g., "Jan" for 1, "Feb" for 2, etc.).
	 */
	public static String getMonthAbbreviation(LocalDateTime dateTime) {
		String month = MONTHS[dateTime.getMonthValue() - 1];
		assert (month != null);
		return month;
	}

	/**
	 * Returns the 3-letter day abbreviation (e.g., "Mon", "Tue") for the given timestamp.
	 *
	 * @param timestamp
	 *            the UNIX timestamp in seconds.
	 * @return the day abbreviation corresponding to the timestamp
	 * @throws IllegalArgumentException
	 *             if the computed day is not between 1 (Monday) and 7 (Sunday)
	 */
	public static String getDayAbbreviationFromTimestamp(long timestamp) {
		int dayOfWeek = getDayOfWeekFromTimestamp(timestamp);
		if (dayOfWeek < 1 || dayOfWeek > DAYS.length) {
			throw new IllegalArgumentException("Invalid dayOfWeek: " + dayOfWeek + ". It must be between 1 and 7.");
		}
		String day = DAYS[dayOfWeek - 1];
		assert (day != null);
		return day;
	}

	/**
	 * Converts a numeric day of the week value (1-7) to its corresponding three-letter abbreviation.
	 *
	 * @param dateTime
	 *            the {@link LocalDateTime} to extract the day of the week from.
	 * @return the three-letter abbreviation of the day (e.g., "Mon" for 1, "Tue" for 2, etc.).
	 */
	public static String getDayAbbreviation(LocalDateTime dateTime) {
		int dayOfWeek = dateTime.getDayOfWeek().getValue();
		String day = DAYS[dayOfWeek - 1];
		assert (day != null);
		return day;
	}

	/**
	 * Returns the current local date and time based on the system time and time zone offset.
	 *
	 * <p>
	 * Uses {@link TimeService} to get the current time in milliseconds and time zone offset in seconds, then computes a
	 * {@link LocalDateTime} in UTC.
	 * </p>
	 *
	 * @return the current local {@link LocalDateTime}.
	 */
	public static LocalDateTime getLocalDateTime() {
		TimeService timeService = KernelServiceProvider.getTimeService();
		long currentTime = timeService.getCurrentTime();
		int currentZoneOffset = timeService.getTimeZoneOffset();
		long currentLocalTime = currentTime + currentZoneOffset * DateTimeHelper.MILLIS_IN_SECOND;
		return LocalDateTime.ofEpochSecond(currentLocalTime / DateTimeHelper.MILLISECONDS_IN_SECOND,
				(int) ((currentLocalTime % DateTimeHelper.MILLISECONDS_IN_SECOND)
						* DateTimeHelper.NANOS_IN_MILLISECOND),
				ZoneOffset.UTC);
	}

	/**
	 * Returns the day of the week for a given Unix timestamp (in seconds), using the system's default time zone.
	 *
	 * @param timestampEpoch
	 *            the epoch timestamp in seconds since the Unix epoch (UTC).
	 * @return the day of the week as an integer from 1 (Monday) to 7 (Sunday).
	 */
	public static int getDayOfWeekFromTimestamp(long timestampEpoch) {
		ZonedDateTime dateTime = Instant.ofEpochSecond(timestampEpoch).atZone(ZoneId.systemDefault());
		return dateTime.getDayOfWeek().getValue();
	}

	/**
	 * Returns the hour of a Unix timestamp (in seconds) formatted as "HH:00" in the system's default time zone.
	 *
	 * @param timestamp
	 *            the epoch timestamp in seconds since the Unix epoch (UTC).
	 * @return the formatted hour string in "HH:00" format (e.g., "09:00", "14:00").
	 */
	public static String getDisplayedHourFromTimestamp(long timestamp) {
		ZonedDateTime dateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault());
		int hours = dateTime.getHour();
		String hoursStr = (hours < 10) ? "0" + hours : Integer.toString(hours);
		return hoursStr + ":00";
	}

}
