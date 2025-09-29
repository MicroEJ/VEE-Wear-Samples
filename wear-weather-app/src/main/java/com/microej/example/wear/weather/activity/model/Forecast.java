/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.model;

/**
 * Represents weather forecast for a given condition or time period. This class stores temperature, the time from Epoch
 * UTC of the forecast, weather condition id and the probability of rain.
 */
public class Forecast {

	private final int rain;
	private final WeatherCondition weatherCondition;
	private final int temperature;
	private final long timestamp;

	/**
	 * Constructs a {@code Forecast} object with the specified weather parameters.
	 *
	 * @param timestamp
	 *            the time from Epoch UTC of the forecast.
	 * @param temperature
	 *            the temperature in degrees Celsius.
	 * @param weatherCondition
	 *            the {@link WeatherCondition} enum representing the forecasted condition.
	 * @param rain
	 *            the probability of precipitation.The parameter values range from 0 to 100, corresponding to a
	 *            percentage.
	 */
	public Forecast(long timestamp, int temperature, WeatherCondition weatherCondition, int rain) {
		if (weatherCondition.getId() < 1 || weatherCondition.getId() > WeatherCondition.BROKEN_CLOUDS.getId()) {
			throw new IllegalArgumentException("Invalid weatherCondition: " + weatherCondition.getId());
		}
		if (rain < 0 || rain > 100) {
			throw new IllegalArgumentException("Invalid rain value : " + rain);
		}
		this.timestamp = timestamp;
		this.temperature = temperature;
		this.weatherCondition = weatherCondition;
		this.rain = rain;
	}

	/**
	 * Retrieves the current weather condition.
	 *
	 * @return the current {@link WeatherCondition}.
	 */
	public WeatherCondition getWeatherCondition() {
		return this.weatherCondition;
	}

	/**
	 * Returns the rain probability.
	 *
	 * @return the rain probability, ranging from 0 to 100, corresponding to a percentage.
	 */
	public int getRain() {
		return this.rain;
	}

	/**
	 * Returns the temperature.
	 *
	 * @return the temperature in degrees Celsius.
	 */
	public int getTemperature() {
		return this.temperature;
	}

	/**
	 * Returns the timestamp from Epoch UTC.
	 *
	 * @return the timestamp in milliseconds from Epoch UTC.
	 */
	public long getTimestamp() {
		return timestamp;
	}
}
