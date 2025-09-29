/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.container;

import com.microej.example.wear.weather.activity.WeatherDesktop;
import com.microej.example.wear.weather.activity.model.DailyForecast;
import com.microej.example.wear.weather.activity.model.Weather;
import com.microej.example.wear.weather.activity.style.ClassIdentifiers;
import com.microej.example.wear.weather.activity.widget.DayForecastItemWidget;
import com.microej.example.wear.weather.activity.widget.scroll.Scroll;

import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.util.Alignment;
import ej.widget.basic.Label;
import ej.widget.container.Canvas;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.List;

/**
 * Responsible for displaying the UI components of a daily weather forecast.
 */
public class DailyForecastContainer extends Canvas {
	/* Layout constants */
	private static final float LIST_MARGIN_TOP_RATIO = 0.043f;
	private static final float VERTICAL_OFFSET_RATIO = 0.893f;
	private static final float TITLE_Y_RATIO = 0.04f;
	private static final String DAILY_FORECAST_TITLE = "Daily";
	private final DailyForecast[] dailyForecast;

	/**
	 * Constructs a DailyForecastContainer. Initializes the UI components upon creation.
	 *
	 * @param weather
	 *            the weather instance containing the temperature data to be displayed in the list.
	 */
	public DailyForecastContainer(Weather weather) {
		this.dailyForecast = weather.getReorderedWeekForecast();
		addClassSelector(ClassIdentifiers.ROOT_WIDGET);

		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);

		addTitle(displaySize);
		addForecastList(displaySize);
	}

	private void addTitle(int displaySize) {
		VectorFont font = WeatherDesktop.getSemiBoldFont();
		float fontSize = WeatherDesktop.FORECAST_CONTAINER_TITLE_FONT_SIZE_RATIO * displaySize;
		int stringWidth = (int) font.measureStringWidth(DailyForecastContainer.DAILY_FORECAST_TITLE, fontSize);
		Label label = new Label(DailyForecastContainer.DAILY_FORECAST_TITLE);

		int x = Alignment.computeLeftX(stringWidth, 0, displaySize, Alignment.HCENTER);
		label.addClassSelector(ClassIdentifiers.FORECAST_TITLE_LABEL);
		int y = (int) (DailyForecastContainer.TITLE_Y_RATIO * displaySize);
		addChild(label, x, y, stringWidth, (int) font.getHeight(fontSize));
	}

	private void addForecastList(int displaySize) {
		List list = new List(LayoutOrientation.HORIZONTAL);

		for (DailyForecast forecast : this.dailyForecast) {
			DayForecastItemWidget item = new DayForecastItemWidget(forecast.getTimestamp(),
					forecast.getMinTemperature(), forecast.getMaxTemperature(), forecast.getRain(),
					forecast.getWeatherCondition().getIconPath());
			item.addClassSelector(ClassIdentifiers.DAY_FORECAST_ITEM);
			list.addChild(item);
		}

		Scroll scroll = new Scroll(LayoutOrientation.HORIZONTAL);
		scroll.setChild(list);

		int widgetHeight = (int) (VERTICAL_OFFSET_RATIO * displaySize);
		int yOffset = (int) (LIST_MARGIN_TOP_RATIO * displaySize);
		int y = Alignment.computeTopY(widgetHeight, yOffset, displaySize, Alignment.VCENTER);

		addChild(scroll, 0, y, displaySize, widgetHeight);
	}
}
