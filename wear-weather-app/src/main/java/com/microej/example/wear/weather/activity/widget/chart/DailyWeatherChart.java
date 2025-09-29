/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

import java.time.LocalDateTime;

import com.microej.example.wear.weather.activity.WeatherActivity;
import com.microej.example.wear.weather.activity.WeatherDesktop;
import com.microej.example.wear.weather.activity.model.Weather;
import com.microej.example.wear.weather.activity.style.ClassIdentifiers;
import com.microej.example.wear.weather.activity.widget.DateTimeHelper;
import com.microej.wear.KernelServiceProvider;

import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.util.Alignment;
import ej.widget.container.Canvas;

/**
 * A chart for displaying daily weather data for a week, starting from the current day.
 * <p>
 * This chart is drawn on a {@link Canvas} and presents weather information over a 7-day period.
 */
public class DailyWeatherChart extends Canvas {

	/**
	 * Width ratio for the daily chart relative to its container.
	 */
	public static final float DAILY_CHART_WIDTH_RATIO = 0.923f;

	/**
	 * Height ratio for the daily chart relative to its container.
	 */
	public static final float DAILY_CHART_HEIGHT_RATIO = 0.429f;

	private static final int CHART_HORIZONTAL_STEP = 60;
	private static final float CHART_LABELS_TOP_MARGIN_RATIO = 0.086f;
	private static final float LINE_CHART_X_OFFSET_RATIO = 0.022f;
	private static final float X_LEGEND_OFFSET_RATIO = 0.010f;
	private static final float CHART_LABEL_HEIGHT_RATIO = 0.214f;
	private static final float LEGEND_START_X_RATIO = 0.311f;
	private static final float IMAGE_LEGEND_X_RATIO = 0.068f;
	private static final float IMAGE_LEGEND_Y_OFFSET_RATIO = 0.118f;
	private static final float IMAGE_LEGEND_WIDTH_OFFSET_RATIO = 0.086f;
	private static final float IMAGE_LEGEND_HEIGHT_RATIO = 0.064f;
	private static final float LEGEND_SPACING = 19.5f;
	private static final float CHART_HEIGHT_RATIO = 0.257f;
	private static final float CHART_WIDTH_RATIO = 0.794f;
	private static final float TEXT_LEGEND_X_RATIO = 0.068f;
	private static final float TEXT_LEGEND_Y_RATIO = 0.008f;
	private static final float TEXT_LEGEND_WIDTH_OFFSET_RATIO = 0.086f;

	private final OnPointSelectedListener onPointSelectedListener;
	private final Weather weather;
	private ChartTextLegend textLegend;

	/**
	 * Constructs a new {@code DailyWeatherChart} with the given weather data and point selection listener.
	 *
	 * @param weather
	 *            the weather data to display on the chart.
	 * @param onPointSelectedListener
	 *            the listener for responding to user selection of data points.
	 */
	public DailyWeatherChart(Weather weather, OnPointSelectedListener onPointSelectedListener) {
		setEnabled(true);
		this.weather = weather;
		this.onPointSelectedListener = onPointSelectedListener;
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);
		int widgetHeight = (int) (DAILY_CHART_HEIGHT_RATIO * displaySize);
		int widgetWidth = (int) (DAILY_CHART_WIDTH_RATIO * displaySize);
		this.textLegend = new ChartTextLegend(getWeekLegend(), 1, DailyWeatherChart.LEGEND_SPACING);
		addLineChart(widgetWidth, widgetHeight, displaySize);
		addTextLegend(displaySize);
		addImageLegend(widgetHeight, displaySize);
		addChartLabels(displaySize);
	}

	private void addTextLegend(int displaySize) {
		int lineChartWidth = (int) (displaySize * CHART_WIDTH_RATIO);
		int legendFontSize = (int) (WeatherDesktop.LEGEND_FONT_SIZE_RATIO * displaySize);
		int xStart = (int) (DailyWeatherChart.LEGEND_START_X_RATIO * displaySize);
		this.textLegend = new ChartTextLegend(getWeekLegend(), xStart, DailyWeatherChart.LEGEND_SPACING);
		this.textLegend.addClassSelector(ClassIdentifiers.CHART_HORIZONTAL_LEGEND);
		int x = (int) (TEXT_LEGEND_X_RATIO * displaySize);
		int y = (int) (TEXT_LEGEND_Y_RATIO * displaySize);
		int widthOffset = (int) (TEXT_LEGEND_WIDTH_OFFSET_RATIO * displaySize);
		int height = (int) getFont().getHeight(legendFontSize) + WeatherDesktop.LEGEND_LINE_Y_OFFSET;
		addChild(this.textLegend, x, y, lineChartWidth + widthOffset, height);
	}

	private void addImageLegend(int contentHeight, int displaySize) {
		int lineChartWidth = (int) (displaySize * CHART_WIDTH_RATIO);
		int xStart = (int) (DailyWeatherChart.LEGEND_START_X_RATIO * displaySize);
		ChartImageLegend imageLegend = new ChartImageLegend(Weather.getDailyIconsFromToday(this.weather), xStart,
				DailyWeatherChart.LEGEND_SPACING);
		int x = (int) (IMAGE_LEGEND_X_RATIO * displaySize);
		int yOffset = (int) (IMAGE_LEGEND_Y_OFFSET_RATIO * displaySize);
		int widthOffset = (int) (IMAGE_LEGEND_WIDTH_OFFSET_RATIO * displaySize);
		int height = (int) (IMAGE_LEGEND_HEIGHT_RATIO * displaySize);
		addChild(imageLegend, x, contentHeight - yOffset, lineChartWidth + widthOffset, height);
	}

	private void addChartLabels(int displaySize) {
		int legendFontSize = (int) (WeatherDesktop.LEGEND_FONT_SIZE_RATIO * displaySize);
		int textWidth = (int) getFont().measureStringWidth("XX°C", legendFontSize);
		String topValue = this.weather.getMaximumTemperatureOfWeek() + "°C";
		String bottomValue = this.weather.getMinimumTemperatureOfWeek() + "°C";
		ChartLabels textLegend = new ChartLabels(topValue, bottomValue);
		textLegend.addClassSelector(ClassIdentifiers.CHART_VERTICAL_LEGEND);
		int yTextLegend = (int) (DailyWeatherChart.CHART_LABELS_TOP_MARGIN_RATIO * displaySize);
		int xTextLegend = (int) (X_LEGEND_OFFSET_RATIO * displaySize);
		int height = (int) (CHART_LABEL_HEIGHT_RATIO * displaySize);
		addChild(textLegend, xTextLegend, yTextLegend, textWidth, height);
	}

	private void addLineChart(int contentWidth, int contentHeight, int displaySize) {
		VectorLineChart vectorChart = new VectorLineChart(Weather.getDailyTemperaturesFromToday(this.weather),
				DailyWeatherChart.CHART_HORIZONTAL_STEP, this.weather.getMaximumTemperatureOfWeek() + 2,
				this.onPointSelectedListener);
		vectorChart.addClassSelector(ClassIdentifiers.WEATHER_CHART);
		int lineChartWidth = (int) (displaySize * CHART_WIDTH_RATIO);
		int lineChartHeight = (int) (displaySize * CHART_HEIGHT_RATIO);
		int xOffset = (int) -(LINE_CHART_X_OFFSET_RATIO * displaySize);
		int imageX = Alignment.computeLeftX(lineChartWidth, xOffset, contentWidth, Alignment.RIGHT);
		int imageY = Alignment.computeTopY(lineChartHeight, 0, contentHeight, Alignment.VCENTER) + 7;
		addChild(vectorChart, imageX, imageY, lineChartWidth, lineChartHeight);
	}

	public void setSelectedIndex(int index) {
		this.textLegend.setSelectedLegendIndex(index);
	}

	private String[] getWeekLegend() {
		String[] weekLegend = new String[WeatherActivity.DAYS_FORECAST];
		LocalDateTime dateTime = DateTimeHelper.getLocalDateTime();

		for (int i = 0; i < WeatherActivity.DAYS_FORECAST; i++) {
			String dayOfWeek = DateTimeHelper.getDayAbbreviation(dateTime);
			weekLegend[i] = dayOfWeek;
			dateTime = dateTime.plusDays(1);
		}
		return weekLegend;
	}

	private VectorFont getFont() {
		return KernelServiceProvider.getFontService().getRegularFont();
	}
}
