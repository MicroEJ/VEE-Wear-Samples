/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.container;

import com.microej.example.wear.weather.activity.WeatherDesktop;
import com.microej.example.wear.weather.activity.model.Forecast;
import com.microej.example.wear.weather.activity.model.Weather;
import com.microej.example.wear.weather.activity.style.ClassIdentifiers;
import com.microej.example.wear.weather.activity.widget.HourForecastItemWidget;
import com.microej.example.wear.weather.activity.widget.scroll.Scroll;

import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.util.Alignment;
import ej.widget.basic.Label;
import ej.widget.container.Canvas;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.List;

/**
 * Responsible for displaying the UI components of an hourly weather forecast.
 */
public class HourlyForecastContainer extends Canvas {
	/* Layout constants */
	private static final float LIST_MARGIN_TOP_RATIO = 0.043f;
	private static final float CHART_HEIGHT_RATIO = 0.893f;
	private static final float TITLE_Y_RATIO = 0.04f;
	private static final String HOURLY_FORECAST_TITLE = "Hourly";
	private final Forecast[] forecasts;

	/**
	 * Constructs the widget responsible for displaying the hourly weather forecast.
	 *
	 * @param weather
	 *            the instance containing the weather data.
	 */
	public HourlyForecastContainer(Weather weather) {
		this.forecasts = weather.getNextForecastHours();
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
		int stringWidth = (int) font.measureStringWidth(HourlyForecastContainer.HOURLY_FORECAST_TITLE, fontSize);
		Label label = new Label(HourlyForecastContainer.HOURLY_FORECAST_TITLE);
		label.addClassSelector(ClassIdentifiers.FORECAST_TITLE_LABEL);
		int x = Alignment.computeLeftX(stringWidth, 0, displaySize, Alignment.HCENTER);
		int y = (int) (HourlyForecastContainer.TITLE_Y_RATIO * displaySize);
		addChild(label, x, y, stringWidth, (int) font.getHeight(fontSize));
	}

	private void addForecastList(int displaySize) {
		List list = new List(LayoutOrientation.HORIZONTAL);

		for (Forecast forecast : this.forecasts) {
			HourForecastItemWidget item = new HourForecastItemWidget(forecast.getTimestamp(), forecast.getTemperature(),
					forecast.getRain(), forecast.getWeatherCondition().getIconPath());
			item.addClassSelector(ClassIdentifiers.HOUR_FORECAST_ITEM);
			list.addChild(item);
		}

		Scroll scroll = new Scroll(LayoutOrientation.HORIZONTAL);
		scroll.setChild(list);

		int widgetWidth = Display.getDisplay().getWidth();
		int widgetHeight = (int) (CHART_HEIGHT_RATIO * displaySize);
		int yOffset = (int) (LIST_MARGIN_TOP_RATIO * displaySize);
		int scrollY = Alignment.computeTopY(widgetHeight, yOffset, displaySize, Alignment.VCENTER);

		addChild(scroll, 0, scrollY, widgetWidth, widgetHeight);
	}
}