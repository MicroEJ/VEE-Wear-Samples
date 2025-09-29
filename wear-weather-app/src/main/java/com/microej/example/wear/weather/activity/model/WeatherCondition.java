/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.model;

/**
 * Represents different weather conditions with an associated ID and name.
 */
public enum WeatherCondition {

	/**
	 * Stormy weather condition.
	 */
	STORMY(1, "thunderstorm"),

	/**
	 * Partly cloudy daytime weather condition.
	 */
	PARTLY_CLOUDY_DAY(2, "sun_lit_cloud"),

	/**
	 * Sunny weather condition.
	 */
	SUNNY(3, "sun"),

	/**
	 * Snowy weather condition.
	 */
	SNOWY(4, "snow"),

	/**
	 * Heavy showers weather condition.
	 */
	HEAVY_SHOWERS(5, "shower_rain_2"),

	/**
	 * Light to moderate showers weather condition.
	 */
	SHOWERS(6, "shower_rain"),

	/**
	 * Rainy weather condition.
	 */
	RAINY(7, "rain"),

	/**
	 * Partly cloudy weather condition at night.
	 */
	PARTLY_CLOUDY_NIGHT(8, "night_lt_cloud"),

	/**
	 * Clear night weather condition.
	 */
	CLEAR_NIGHT(9, "night"),

	/**
	 * Misty weather condition.
	 */
	MISTY(10, "mist"),

	/**
	 * Cloudy weather condition.
	 */
	CLOUDY(11, "cloud"),

	/**
	 * Broken clouds weather condition.
	 */
	BROKEN_CLOUDS(12, "broken_cloud");

	private final int id;
	private final String name;

	/**
	 * Constructs a WeatherCondition with the specified ID and name.
	 *
	 * @param id
	 *            the unique identifier of the weather condition
	 * @param name
	 *            the name of the weather condition
	 */
	WeatherCondition(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the unique identifier of this weather condition.
	 *
	 * @return the weather condition ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the name of this weather condition.
	 *
	 * @return the weather condition name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the relative path to the icon resource representing this weather condition.
	 *
	 * @return the icon path as a string
	 */
	public String getIconPath() {
		return "/images/" + name + ".xml";
	}

}
