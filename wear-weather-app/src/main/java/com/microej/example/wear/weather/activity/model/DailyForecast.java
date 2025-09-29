/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.model;

/**
 * Represents daily weather forecast for a specific day of the week. Extends the {@link Forecast} class by adding
 * minimum and maximum temperatures, and a numeric representation of the day of the week.
 */
public class DailyForecast extends Forecast {
	private final int minTemperature;
	private final int maxTemperature;

	/**
	 * Constructs a {@code DailyForecast} object with the specified parameters.
	 *
	 * @param timestamp
	 *            the time from Epoch UTC of the forecast.
	 * @param temperature
	 *            the temperature at midday, in degrees Celsius.
	 * @param minTemperature
	 *            the minimum temperature for the day in degrees Celsius.
	 * @param maxTemperature
	 *            the maximum temperature for the day in degrees Celsius.
	 * @param weatherCondition
	 *            the {@link WeatherCondition} enum representing the forecasted condition.
	 * @param rain
	 *            the probability of rain.The parameter values range from 0 to 100.
	 */
	public DailyForecast(long timestamp, int temperature, int minTemperature, int maxTemperature,
			WeatherCondition weatherCondition, int rain) {
		super(timestamp, temperature, weatherCondition, rain);
		this.minTemperature = minTemperature;
		this.maxTemperature = maxTemperature;
	}

	/**
	 * Returns the minimum temperature for the day.
	 *
	 * @return the minimum temperature in degrees Celsius.
	 */
	public int getMinTemperature() {
		return this.minTemperature;
	}

	/**
	 * Returns the maximum temperature for the day.
	 *
	 * @return the maximum temperature in degrees Celsius.
	 */
	public int getMaxTemperature() {
		return this.maxTemperature;
	}

}
