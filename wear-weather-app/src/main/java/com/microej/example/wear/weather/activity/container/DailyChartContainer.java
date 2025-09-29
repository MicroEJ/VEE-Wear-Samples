/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.container;

import java.time.LocalDateTime;

import com.microej.example.wear.weather.activity.WeatherDesktop;
import com.microej.example.wear.weather.activity.model.DailyForecast;
import com.microej.example.wear.weather.activity.model.Weather;
import com.microej.example.wear.weather.activity.style.ClassIdentifiers;
import com.microej.example.wear.weather.activity.widget.DateTimeHelper;
import com.microej.example.wear.weather.activity.widget.IconLabelWidget;
import com.microej.example.wear.weather.activity.widget.VectorImageWidget;
import com.microej.example.wear.weather.activity.widget.chart.DailyWeatherChart;
import com.microej.example.wear.weather.activity.widget.chart.OnPointSelectedListener;
import com.microej.wear.KernelServiceProvider;

import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.util.Alignment;
import ej.widget.basic.Label;
import ej.widget.container.Canvas;

/**
 * Responsible for displaying the UI components of a daily weather chart.
 */
public class DailyChartContainer extends Canvas implements OnPointSelectedListener {

	/* Layout constants */
	private static final float WIDGET_Y_RATIO = 0.02f;
	private static final float IMAGE_WIDGET_Y_RATIO = 0.08f;
	private static final float DATE_OFFSET_Y_RATIO = 0.064f;
	private static final float TEMPERATURE_OFFSET_X_RATIO = 0.086f;
	private static final float CHART_OFFSET_Y_RATIO = 0.096f;
	private static final float CHART_OFFSET_X_RATIO = 0.021f;
	private static final int WEATHER_IMAGE_SCALE_VALUE = 120;
	private static final int WEATHER_IMAGE_LEFT_GAP = 90;
	private static final int RAIN_LABEL_ICON_SIZE = 24;
	private static final float RAIN_LABEL_X_OFFSET_RATIO = 0.118f;
	private static final float RAIN_LABEL_Y_OFFSET_RATIO = 0.15f;
	private static final float RAIN_LABEL_EXTRA_WIDTH_PADDING_RATIO = 0.107f;
	private static final int TEMPERATURE_LABELS_ICON_SIZE = 15;
	private static final float TEMPERATURE_Y_OFFSET_RATIO = 0.236f;
	private static final float MAX_TEMPERATURE_LABEL_X_OFFSET_RATIO = 0.129f;
	private static final float MAX_TEMPERATURE_LABEL_EXTRA_WIDTH_PADDING_RATIO = 0.086f;
	private static final float MIN_TEMPERATURE_LABEL_EXTRA_WIDTH_PADDING_RATIO = 0.064f;
	private static final String CURSOR_ICON_UP_PATH = "/images/cursor_up.xml";
	private static final String CURSOR_ICON_DOWN_PATH = "/images/cursor_down.xml";
	private static final String WATER_DROPLET_PATH = "/images/humidity.xml";

	private final DailyForecast[] dailyForecasts;
	private Label temperatureLabel;
	private IconLabelWidget rainLabel;
	private IconLabelWidget maxTemperatureLabel;
	private IconLabelWidget minTemperatureLabel;
	private VectorImageWidget weatherImage;
	private Label dateLabel;
	private DailyWeatherChart chart;

	/**
	 * Constructs a daily chart Container. Initializes the UI components upon creation.
	 *
	 * @param weather
	 *            the instance containing the weather data to be displayed in the chart.
	 */
	public DailyChartContainer(Weather weather) {
		this.dailyForecasts = Weather.reorderWeekForecastFromToday(weather);
		DailyForecast dailyForecast = this.dailyForecasts[0];
		assert (dailyForecast != null);
		addClassSelector(ClassIdentifiers.ROOT_WIDGET);

		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);

		this.chart = new DailyWeatherChart(weather, this);
		this.chart.addClassSelector(ClassIdentifiers.WEATHER_CHART);

		this.temperatureLabel = new Label(dailyForecast.getTemperature() + "°");
		this.weatherImage = new VectorImageWidget(dailyForecast.getWeatherCondition().getIconPath(),
				DailyChartContainer.WEATHER_IMAGE_SCALE_VALUE);

		this.rainLabel = createAndAddRain(dailyForecast, displaySize);
		this.rainLabel.addClassSelector(ClassIdentifiers.RAIN_ICON_LABEL);

		this.maxTemperatureLabel = createAndAddMaxTemperature(dailyForecast, displaySize);
		this.maxTemperatureLabel.addClassSelector(ClassIdentifiers.ICON_LABEL);

		this.minTemperatureLabel = createAndAddMinTemperature(dailyForecast, displaySize);
		this.minTemperatureLabel.addClassSelector(ClassIdentifiers.ICON_LABEL);

		this.dateLabel = new Label(formatDate(0));

		addTemperature(displaySize);
		addWeatherIcon(displaySize);
		addWeatherChart(displaySize);
		addDate(displaySize);
	}

	private IconLabelWidget createAndAddRain(DailyForecast forecast, int displaySize) {
		String rainProbability = forecast.getRain() + "%";
		int xOffset = (int) (-RAIN_LABEL_X_OFFSET_RATIO * displaySize);
		int yOffset = (int) (RAIN_LABEL_Y_OFFSET_RATIO * displaySize);
		int extraWidthPadding = (int) (RAIN_LABEL_EXTRA_WIDTH_PADDING_RATIO * displaySize);

		return addIconLabelWidget(rainProbability, DailyChartContainer.WATER_DROPLET_PATH, RAIN_LABEL_ICON_SIZE,
				displaySize, xOffset, yOffset, extraWidthPadding);
	}

	private IconLabelWidget createAndAddMaxTemperature(DailyForecast forecast, int displaySize) {
		String maxTemperature = forecast.getMaxTemperature() + "°";
		int xOffset = (int) (-MAX_TEMPERATURE_LABEL_X_OFFSET_RATIO * displaySize);
		int yOffset = (int) (TEMPERATURE_Y_OFFSET_RATIO * displaySize);
		int extraWidthPadding = (int) (MAX_TEMPERATURE_LABEL_EXTRA_WIDTH_PADDING_RATIO * displaySize);

		return addIconLabelWidget(maxTemperature, DailyChartContainer.CURSOR_ICON_UP_PATH, TEMPERATURE_LABELS_ICON_SIZE,
				displaySize, xOffset, yOffset, extraWidthPadding);
	}

	private IconLabelWidget createAndAddMinTemperature(DailyForecast forecast, int displaySize) {
		String minTemperature = forecast.getMinTemperature() + "°";
		int yOffset = (int) (TEMPERATURE_Y_OFFSET_RATIO * displaySize);
		int extraWidthPadding = (int) (MIN_TEMPERATURE_LABEL_EXTRA_WIDTH_PADDING_RATIO * displaySize);
		return addIconLabelWidget(minTemperature, DailyChartContainer.CURSOR_ICON_DOWN_PATH,
				TEMPERATURE_LABELS_ICON_SIZE, displaySize, 0, yOffset, extraWidthPadding);

	}

	private void addTemperature(int displaySize) {
		VectorFont tempFont = WeatherDesktop.getSemiBoldFont();
		float tempFontSize = WeatherDesktop.TEMPERATURE_FONT_SIZE_RATIO * displaySize;
		int tempFontHeight = (int) tempFont.getHeight(tempFontSize);
		int tempWidth = (int) tempFont.measureStringWidth("00°", tempFontSize);
		int xOffset = (int) -(TEMPERATURE_OFFSET_X_RATIO * displaySize);
		int dayX = Alignment.computeLeftX(tempWidth + DailyChartContainer.WEATHER_IMAGE_SCALE_VALUE, xOffset,
				displaySize, Alignment.RIGHT);
		this.temperatureLabel.addClassSelector(ClassIdentifiers.TEMPERATURE_LABEL);
		int dayY = (int) (DailyChartContainer.WIDGET_Y_RATIO * displaySize) * 2;
		addChild(this.temperatureLabel, dayX, dayY, tempWidth, tempFontHeight);
	}

	private void addWeatherIcon(int displaySize) {
		int imageY = (int) (DailyChartContainer.IMAGE_WIDGET_Y_RATIO * displaySize);
		int imageX = Alignment.computeLeftX(DailyChartContainer.WEATHER_IMAGE_SCALE_VALUE,
				DailyChartContainer.WEATHER_IMAGE_LEFT_GAP, displaySize, Alignment.LEFT);
		addChild(this.weatherImage, imageX, imageY, DailyChartContainer.WEATHER_IMAGE_SCALE_VALUE,
				DailyChartContainer.WEATHER_IMAGE_SCALE_VALUE);
	}

	private void addWeatherChart(int displaySize) {
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int widgetHeight = (int) (DailyWeatherChart.DAILY_CHART_HEIGHT_RATIO * displayHeight);
		int widgetWidth = (int) (DailyWeatherChart.DAILY_CHART_WIDTH_RATIO * displayWidth);
		int yOffset = (int) (CHART_OFFSET_Y_RATIO * displaySize);
		int xOffset = -(int) (CHART_OFFSET_X_RATIO * displaySize);
		int weatherChartY = Alignment.computeTopY(widgetHeight, 0, displaySize, Alignment.VCENTER) + yOffset;
		int weatherChartX = Alignment.computeLeftX(widgetWidth, xOffset, displaySize, Alignment.HCENTER);
		addChild(this.chart, weatherChartX, weatherChartY, widgetWidth, widgetHeight);
	}

	private void addDate(int displaySize) {
		VectorFont dateFont = WeatherDesktop.getSemiBoldFont();
		float dateFontSize = WeatherDesktop.DATE_FONT_SIZE_RATIO * displaySize;
		int tempFontHeight = (int) dateFont.getHeight(dateFontSize);
		int dateWidth = (int) dateFont.measureStringWidth("XXX 00 XXX", dateFontSize);
		int dayX = Alignment.computeLeftX(dateWidth, 0, displaySize, Alignment.HCENTER);
		this.dateLabel.addClassSelector(ClassIdentifiers.DATE_LABEL);
		int yOffset = (int) -(DATE_OFFSET_Y_RATIO * displaySize);
		int dayY = Alignment.computeTopY(tempFontHeight, yOffset, displaySize, Alignment.BOTTOM);
		addChild(this.dateLabel, dayX, dayY, dateWidth, tempFontHeight);
	}

	private IconLabelWidget addIconLabelWidget(String labelText, String iconPath, int iconSize, int displaySize,
			int xOffset, int yOffset, int extraWidthPadding) {
		VectorFont font = KernelServiceProvider.getFontService().getRegularFont();
		float fontSize = WeatherDesktop.WEATHER_ICON_LABELS_FONT_SIZE_RATIO * displaySize;
		int fontHeight = (int) font.getHeight(fontSize);
		int fontWidth = (int) font.measureStringWidth("000", fontSize) + extraWidthPadding;
		int dayX = Alignment.computeLeftX(fontWidth + DailyChartContainer.WEATHER_IMAGE_SCALE_VALUE, xOffset,
				displaySize, Alignment.RIGHT);
		int dayY = (int) (DailyChartContainer.WIDGET_Y_RATIO * displaySize) * 2 + yOffset;
		IconLabelWidget labelWidget = new IconLabelWidget(labelText, iconPath, iconSize);
		addChild(labelWidget, dayX, dayY, fontWidth, fontHeight);
		return labelWidget;
	}

	@Override
	public void onPointSelected(int index) {
		DailyForecast forecast = this.dailyForecasts[index];

		String temperature = forecast.getTemperature() + "°";
		this.temperatureLabel.setText(temperature);

		String rainProbability = forecast.getRain() + "%";
		this.rainLabel.setText(rainProbability);

		String maxTemperature = forecast.getMaxTemperature() + "°";
		this.maxTemperatureLabel.setText(maxTemperature);

		String minTemperature = forecast.getMinTemperature() + "°";
		this.minTemperatureLabel.setText(minTemperature);

		String weatherIcon = forecast.getWeatherCondition().getIconPath();
		this.weatherImage.setImagePath(weatherIcon);

		this.dateLabel.setText(formatDate(index));
		this.chart.setSelectedIndex(index);

		requestRender();
	}

	private static String formatDate(int dayOffset) {
		LocalDateTime localDate = DateTimeHelper.getLocalDateTime();
		LocalDateTime dateTime = localDate.plusDays(dayOffset);
		String month = DateTimeHelper.getMonthAbbreviation(dateTime);
		String dayOfWeek = DateTimeHelper.getDayAbbreviation(dateTime);
		StringBuilder builder = new StringBuilder();
		builder.append(dayOfWeek);
		builder.append(" ");
		builder.append(dateTime.getDayOfMonth());
		builder.append(" ");
		builder.append(month);
		return builder.toString();
	}
}
