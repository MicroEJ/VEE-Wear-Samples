/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.microej.example.wear.weather.activity.WeatherActivity;
import com.microej.example.wear.weather.activity.widget.DateTimeHelper;

/**
 * Manages and provides weather data to be shared between app components, primarily the UI.
 */
public class Weather {
	private final DailyForecast[] dailyForecasts;
	private final Forecast[] hourlyForecasts;

	/**
	 * Constructs a {@code Weather} object with the specified daily weather data.
	 */
	public Weather() {
		this.dailyForecasts = generateWeekForecast();
		this.hourlyForecasts = generateDayForecast();
	}

	private DailyForecast[] generateWeekForecast() {
		DailyForecast[] dailyForecast = new DailyForecast[WeatherActivity.DAYS_FORECAST];
		LocalDate today = LocalDate.now(ZoneOffset.UTC);

		int[] temperatures = { 18, 5, 5, 12, 20, 19, 14 };
		int[] minTemperatures = { 14, 5, 5, 8, 15, 15, 11 };
		int[] maxTemperatures = { 20, 12, 10, 14, 22, 23, 22 };
		WeatherCondition[] conditions = { WeatherCondition.SUNNY, WeatherCondition.SNOWY,
				WeatherCondition.HEAVY_SHOWERS, WeatherCondition.STORMY, WeatherCondition.PARTLY_CLOUDY_DAY,
				WeatherCondition.MISTY, WeatherCondition.CLOUDY };
		int[] rains = { 10, 92, 9, 38, 5, 50, 63 };

		for (int i = 0; i < WeatherActivity.DAYS_FORECAST; i++) {
			LocalDate date = today.plusDays(i);
			ZonedDateTime middayUtc = date.atTime(12, 0).atZone(ZoneOffset.UTC);
			long timestamp = middayUtc.toEpochSecond();
			WeatherCondition condition = conditions[i];
			assert (condition != null);
			dailyForecast[i] = new DailyForecast(timestamp, temperatures[i], minTemperatures[i], maxTemperatures[i],
					condition, rains[i]);
		}
		return dailyForecast;
	}

	private Forecast[] generateDayForecast() {
		Forecast[] forecasts = new Forecast[WeatherActivity.HOURS_FORECAST];
		LocalDate today = LocalDate.now(ZoneOffset.UTC);
		ZonedDateTime midnightUtc = today.atStartOfDay(ZoneOffset.UTC);

		int[] temperatures = { 20, 19, 18, 17, 17, 16, 16, 14, 14, 12, 12, 12, 20, 19, 18, 17, 17, 16, 16, 14, 14, 12,
				12, 12 };

		WeatherCondition[] conditions = { WeatherCondition.CLEAR_NIGHT, WeatherCondition.CLEAR_NIGHT,
				WeatherCondition.PARTLY_CLOUDY_NIGHT, WeatherCondition.PARTLY_CLOUDY_NIGHT,
				WeatherCondition.HEAVY_SHOWERS, WeatherCondition.HEAVY_SHOWERS, WeatherCondition.HEAVY_SHOWERS,
				WeatherCondition.SHOWERS, WeatherCondition.RAINY, WeatherCondition.BROKEN_CLOUDS,
				WeatherCondition.BROKEN_CLOUDS, WeatherCondition.BROKEN_CLOUDS, WeatherCondition.CLOUDY,
				WeatherCondition.CLOUDY, WeatherCondition.CLOUDY, WeatherCondition.SHOWERS, WeatherCondition.RAINY,
				WeatherCondition.SHOWERS, WeatherCondition.HEAVY_SHOWERS, WeatherCondition.HEAVY_SHOWERS,
				WeatherCondition.PARTLY_CLOUDY_NIGHT, WeatherCondition.PARTLY_CLOUDY_NIGHT,
				WeatherCondition.CLEAR_NIGHT, WeatherCondition.CLEAR_NIGHT };

		int[] rains = { 50, 81, 85, 60, 50, 40, 63, 88, 92, 90, 38, 5, 1, 2, 2, 4, 5, 50, 63, 88, 92, 90, 38, 5 };

		for (int i = 0; i < WeatherActivity.HOURS_FORECAST; i++) {
			ZonedDateTime hourUtc = midnightUtc.plusHours(i);
			long timestamp = hourUtc.toEpochSecond();
			WeatherCondition condition = conditions[i];
			assert (condition != null);
			forecasts[i] = new Forecast(timestamp, temperatures[i], condition, rains[i]);
		}
		return forecasts;
	}

	/**
	 * Returns the weather forecast for the week.
	 * <p>
	 * Each {@link DailyForecast} includes the day of the week, main temperature (°C), min/max temperatures, weather
	 * condition ID, and probability of rain (0.0 to 1.0). Covering the next 7 days.
	 * </p>
	 *
	 * @return an array of {@link DailyForecast} objects starting from today.
	 */
	public DailyForecast[] getWeekForecast() {
		return this.dailyForecasts;
	}

	/**
	 * Returns the weather forecast for the day starting from the current hour.
	 * <p>
	 * Each {@link Forecast} includes the hour, temperature (°C), weather condition ID, and probability of rain (0.0 to
	 * 1.0). Covering the next 24 hours.
	 * </p>
	 *
	 * @return an array of {@link Forecast} objects starting from the current hour.
	 */
	public Forecast[] getDayForecast() {
		return this.hourlyForecasts;
	}

	/**
	 * Returns the maximum temperature from the available daily forecasts.
	 *
	 * @return the highest temperature found in the daily forecast data.
	 */
	public int getMaximumTemperatureOfWeek() {
		int max = Integer.MIN_VALUE;
		for (DailyForecast dailyForecast : this.dailyForecasts) {
			if (dailyForecast.getTemperature() > max) {
				max = dailyForecast.getTemperature();
			}
		}
		return max;
	}

	/**
	 * Returns the minimum temperature from the available daily forecasts.
	 *
	 * @return the lowest temperature found in the daily forecast data.
	 */
	public int getMinimumTemperatureOfWeek() {
		int min = Integer.MAX_VALUE;
		for (DailyForecast dailyForecast : this.dailyForecasts) {
			if (dailyForecast.getTemperature() < min) {
				min = dailyForecast.getTemperature();
			}
		}
		return min;
	}

	/**
	 * Returns today's weather forecast based on the local date.
	 *
	 * @return the forecast for the current local day.
	 */
	public DailyForecast getLocalDateWeather() {
		int dayOfWeek = DateTimeHelper.getLocalDateTime().getDayOfWeek().getValue();
		// Subtract 1 because DayOfWeek values start at 1 (Monday) and array indices start at 0.
		DailyForecast forecast = getWeekForecast()[dayOfWeek - 1];
		assert (forecast != null);
		return forecast;
	}

	/**
	 * Reorders the 7-day forecast so that today's forecast (by local date) is first, preserving chronological order.
	 *
	 * @param weather
	 *            the weather data containing the weekly forecast.
	 * @return the reordered forecast array starting from today.
	 */
	public static DailyForecast[] reorderWeekForecastFromToday(Weather weather) {
		DailyForecast[] dailyForecasts = weather.getWeekForecast();
		DailyForecast[] orderedForecasts = new DailyForecast[dailyForecasts.length];

		// Get today's local date (midnight)
		LocalDate today = DateTimeHelper.getLocalDateTime().toLocalDate();

		// Find the index in the forecast array that matches today's date
		int startIndex = 0;
		for (int i = 0; i < dailyForecasts.length; i++) {
			long forecastEpoch = dailyForecasts[i].getTimestamp(); // in seconds
			LocalDate forecastDate = Instant.ofEpochSecond(forecastEpoch).atZone(ZoneId.systemDefault()).toLocalDate();

			if (forecastDate.equals(today)) {
				startIndex = i;
				break;
			}
		}

		// Rotate forecasts so that today is first
		int j = startIndex;
		int length = dailyForecasts.length;
		for (int i = 0; i < length; i++) {
			if (j >= length) {
				j = 0;
			}
			orderedForecasts[i] = dailyForecasts[j];
			j++;
		}

		return orderedForecasts;
	}

	/**
	 * Returns an array of daily temperatures starting from today's forecast (by local date).
	 *
	 * @param weather
	 *            the weather data containing the weekly forecast.
	 * @return an array of floats representing daily temperatures starting from today.
	 */
	public static float[] getDailyTemperaturesFromToday(Weather weather) {
		DailyForecast[] orderedForecasts = reorderWeekForecastFromToday(weather);
		float[] dailyTemperatures = new float[orderedForecasts.length];

		for (int i = 0; i < orderedForecasts.length; i++) {
			dailyTemperatures[i] = orderedForecasts[i].getTemperature();
		}

		return dailyTemperatures;
	}

	/**
	 * Returns an array of daily icon paths starting from today's forecast (by local date).
	 *
	 * @param weather
	 *            the weather data containing the weekly forecast.
	 * @return an array of icon path strings starting from today.
	 */
	public static String[] getDailyIconsFromToday(Weather weather) {
		DailyForecast[] orderedForecasts = reorderWeekForecastFromToday(weather);
		String[] dailyIcons = new String[orderedForecasts.length];

		for (int i = 0; i < orderedForecasts.length; i++) {
			dailyIcons[i] = orderedForecasts[i].getWeatherCondition().getIconPath();
		}

		return dailyIcons;
	}

	/**
	 * Returns the 7-day forecast reordered to start from today.
	 *
	 * @return an array of {@link DailyForecast} starting from the current local day.
	 */
	public DailyForecast[] getReorderedWeekForecast() {
		DailyForecast[] daylyWeathers = getWeekForecast();
		int dayOfWeek = DateTimeHelper.getLocalDateTime().getDayOfWeek().getValue();
		DailyForecast[] forecastList = new DailyForecast[WeatherActivity.DAYS_FORECAST];
		// Subtract 1 because DayOfWeek values start at 1 (Monday) and array indices start at 0.
		int index = dayOfWeek - 1;
		for (int i = 0; i < forecastList.length; i++) {
			forecastList[i] = daylyWeathers[index];
			index++;
			if (index > 6) {
				index = 0;
			}
		}
		return forecastList;
	}

	/**
	 * Returns upcoming hourly forecasts starting from the current hour.
	 *
	 * @return an array of {@link Forecast} covering half a day.
	 */
	public Forecast[] getNextForecastHours() {
		Forecast[] hourlyWeathers = getDayForecast();
		Forecast[] forecastList = new Forecast[WeatherActivity.HOURS_FORECAST / 2];

		int index = DateTimeHelper.getLocalDateTime().getHour();
		for (int i = 0; i < forecastList.length; i++) {
			forecastList[i] = hourlyWeathers[index];
			index++;
			if (index >= WeatherActivity.HOURS_FORECAST) {
				index = 0;
			}
		}
		return forecastList;
	}
}