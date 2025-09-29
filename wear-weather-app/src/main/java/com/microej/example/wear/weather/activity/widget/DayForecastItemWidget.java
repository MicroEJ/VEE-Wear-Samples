/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget;

import com.microej.example.wear.weather.activity.WeatherDesktop;
import com.microej.wear.KernelServiceProvider;

import ej.annotation.Nullable;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microvg.BufferedVectorImage;
import ej.microvg.Matrix;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget for displaying a single day's weather forecast.
 */
public class DayForecastItemWidget extends Widget {

	/** The constant value for the default value text size. */
	public static final int VALUE_DEFAULT_TEXT_SIZE = 20;

	/** The constant value for the default title text size. */
	public static final int TITLE_DEFAULT_TEXT_SIZE = 18;

	/** The constant value for the default label text size. */
	public static final int LABEL_DEFAULT_TEXT_SIZE = 10;

	/** The extra field ID for the semi bold font. */
	public static final int SEMI_BOLD_FONT_STYLE = 0;

	/** The extra field ID for the regular font. */
	public static final int REGULAR_FONT_STYLE = 1;

	/** The extra field ID for the text size. */
	public static final int TITLE_TEXT_SIZE_STYLE = 2;

	/** The extra field ID for the text size. */
	public static final int VALUE_TEXT_SIZE_STYLE = 3;

	/** The extra field ID for the text size. */
	public static final int LABEL_TEXT_SIZE_STYLE = 4;

	/** The extra field ID for the rounded background color. */
	public static final int BACKGROUND_COLOR_STYLE = 5;

	/** The extra field ID for the x offset. */
	public static final int X_OFFSET_STYLE = 6;

	private static final String WATER_DROPLET_PATH = "/images/humidity.xml";
	private static final String ROUNDED_RECTANGLE_PATH = "/images/rounded_rectangle.xml";
	private static final String MAX = "Max";
	private static final String MIN = "Min";
	private static final String TEMPERATURE_PLACEHOLDER = "XXX";
	private static final String PERCENT = "%";
	private static final float SPACING_RATIO = 0.85f;
	private static final int ICON_SCALE = 20;
	private static final int ICON_MARGIN_RIGHT = 5;
	private static final int IMAGE_HEIGHT = 45;
	private static final int IMAGE_WIDTH = 45;
	private static final int IMAGE_SCALE = 45;
	private static final int TEMPERATURES_Y_OFFSET = 15;
	private static final int DEFAULT_X_OFFSET = -15;
	private static final int TEMPERATURE_Y_OFFSET = 22;
	private static final float RAIN_TEXT_Y_OFFSET = 5.0f;
	private static final float DAY_OF_WEEK_MARGIN_TOP = 5.0f;
	private static final int LABEL_COLOR = 0xd6d6d6;
	private static final int DEFAULT_ROUNDED_BACKGROUND_COLOR = 0xff002A51;
	private final int minTemperature;
	private final int maxTemperature;
	private final int rain;
	private final long timestamp;
	private final String iconPath;
	@Nullable
	private BufferedVectorImage bufferedVectorImage;

	/**
	 * Constructs a {@code DayForecastItemWidget} with the specified daily forecast data.
	 *
	 * @param timestamp
	 *            the forecast date in seconds since the Unix epoch (UTC), corresponding to midnight of the day.
	 * @param minTemperature
	 *            the minimum temperature (in degrees Celsius) forecasted for the day.
	 * @param maxTemperature
	 *            the maximum temperature (in degrees Celsius) forecasted for the day.
	 * @param rain
	 *            the probability of precipitation.The parameter values range from 0 to 100.
	 * @param iconPath
	 *            the path of the weather icon.
	 */
	public DayForecastItemWidget(long timestamp, int minTemperature, int maxTemperature, int rain, String iconPath) {
		this.timestamp = timestamp;
		this.minTemperature = minTemperature;
		this.maxTemperature = maxTemperature;
		this.rain = rain;
		this.iconPath = iconPath;
	}

	@Override
	protected void onDetached() {
		super.onDetached();
		BufferedVectorImage vectorImage = this.bufferedVectorImage;
		if (vectorImage != null && !vectorImage.isClosed()) {
			vectorImage.clear();
			vectorImage.close();
			this.bufferedVectorImage = null;
		}
	}

	@Override
	protected void onLaidOut() {
		super.onLaidOut();
		if (this.bufferedVectorImage == null) {
			int contentWidth = getWidth();
			int contentHeight = getHeight();
			BufferedVectorImage bufferedVectorImage = new BufferedVectorImage(getWidth(), getHeight());
			this.bufferedVectorImage = bufferedVectorImage;
			GraphicsContext g = bufferedVectorImage.getGraphicsContext();
			Style style = getStyle();
			g.setColor(style.getColor());
			drawRoundedBackground(g);
			drawDayOfWeek(g, contentWidth, contentHeight);
			drawTemperatures(g, contentWidth, contentHeight);
			drawForecastImage(g, contentWidth, contentHeight);
			drawRainForecast(g, contentWidth, contentHeight);
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Always use full size defined with dimension in style . No change to size needed.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		VectorImage bufferedVectorImage = this.bufferedVectorImage;
		if (bufferedVectorImage != null) {
			VectorGraphicsPainter.drawImage(g, bufferedVectorImage, 0, 0);
		}
	}

	private void drawRoundedBackground(GraphicsContext g) {
		Style style = getStyle();
		g.setColor(getBackgroundColor(style));
		VectorImage image = VectorImage.getImage(ROUNDED_RECTANGLE_PATH);
		VectorGraphicsPainter.drawImage(g, image, new Matrix());
	}

	private void drawDayOfWeek(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		VectorFont font = DayForecastItemWidget.getSemiBoldFont(style);
		int fontSize = DayForecastItemWidget.getTitleFontSize(style);

		String textValue = DateTimeHelper.getDayAbbreviationFromTimestamp(this.timestamp);

		float textWidth = font.measureStringWidth(textValue, fontSize);

		int textHeight = (int) font.getHeight(fontSize);
		float x = Alignment.computeLeftX((int) textWidth, getXOffset(style), contentWidth, Alignment.HCENTER);

		float y = Alignment.computeTopY(textHeight, 0, contentHeight, Alignment.TOP);
		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, textValue, font, fontSize, x,
				y + DayForecastItemWidget.DAY_OF_WEEK_MARGIN_TOP);
	}

	private void drawTemperatures(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		VectorFont valueFont = DayForecastItemWidget.getSemiBoldFont(style);
		VectorFont labelFont = DayForecastItemWidget.getRegularFont(style);
		int labelSize = DayForecastItemWidget.getLabelFontSize(style);

		int valueSize = DayForecastItemWidget.getValueFontSize(style);

		String minValue = this.minTemperature + "°";
		String maxValue = this.maxTemperature + "°";

		float minTextWidth = valueFont.measureStringWidth(minValue, valueSize);
		float maxTextWidth = valueFont.measureStringWidth(maxValue, valueSize);
		float labelTextWidth = labelFont.measureStringWidth(TEMPERATURE_PLACEHOLDER, labelSize);
		int valueHeight = (int) valueFont.getHeight(valueSize);
		int xMinValue = Alignment.computeLeftX((int) minTextWidth, getXOffset(style), contentWidth, Alignment.HCENTER);
		int xMaxValue = Alignment.computeLeftX((int) maxTextWidth, getXOffset(style), contentWidth, Alignment.HCENTER);
		int xLabel = Alignment.computeLeftX((int) labelTextWidth, getXOffset(style), contentWidth, Alignment.HCENTER);
		int y = Alignment.computeTopY(valueHeight, 0, contentHeight, Alignment.VCENTER)
				- DayForecastItemWidget.TEMPERATURES_Y_OFFSET;
		g.setColor(DayForecastItemWidget.LABEL_COLOR);
		VectorGraphicsPainter.drawString(g, MAX, labelFont, labelSize, xLabel,
				y - (valueHeight * DayForecastItemWidget.SPACING_RATIO) - TEMPERATURE_Y_OFFSET);
		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, maxValue, valueFont, valueSize, xMaxValue,
				y - (valueHeight * DayForecastItemWidget.SPACING_RATIO));

		g.setColor(DayForecastItemWidget.LABEL_COLOR);
		VectorGraphicsPainter.drawString(g, MIN, labelFont, labelSize, xLabel,
				y + (valueHeight * DayForecastItemWidget.SPACING_RATIO) - TEMPERATURE_Y_OFFSET);
		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, minValue, valueFont, valueSize, xMinValue,
				y + (valueHeight * DayForecastItemWidget.SPACING_RATIO));
	}

	private void drawRainForecast(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();

		VectorFont font = DayForecastItemWidget.getRegularFont(style);
		int fontSize = DayForecastItemWidget.getLabelFontSize(style);

		String textValue = this.rain + PERCENT;

		float rainTextWidth = font.measureStringWidth(textValue, fontSize);

		int textHeight = (int) font.getHeight(fontSize);
		float x = Alignment.computeLeftX((int) rainTextWidth,
				DayForecastItemWidget.ICON_SCALE - DayForecastItemWidget.ICON_MARGIN_RIGHT + getXOffset(style),
				contentWidth, Alignment.HCENTER);

		float y = Alignment.computeTopY(textHeight, 0, contentHeight, Alignment.BOTTOM);

		// Draw icon
		VectorImage imageIcon = VectorImage.getImage(DayForecastItemWidget.WATER_DROPLET_PATH);
		Matrix sizeMatrixCursor = new Matrix();
		sizeMatrixCursor.setTranslate(x - DayForecastItemWidget.ICON_SCALE - DayForecastItemWidget.ICON_MARGIN_RIGHT,
				y);
		sizeMatrixCursor.preScale(DayForecastItemWidget.ICON_SCALE / imageIcon.getWidth(),
				DayForecastItemWidget.ICON_SCALE / imageIcon.getHeight());
		VectorGraphicsPainter.drawImage(g, imageIcon, sizeMatrixCursor);

		// Draw text
		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, textValue, font, fontSize, x, y - RAIN_TEXT_Y_OFFSET);
	}

	private void drawForecastImage(GraphicsContext g, int contentWidth, int contentHeight) {
		VectorImage image = VectorImage.getImage(this.iconPath);
		Matrix sizeMatrixCursor = getMatrix(contentWidth, contentHeight, image);
		VectorGraphicsPainter.drawImage(g, image, sizeMatrixCursor);
	}

	private Matrix getMatrix(int contentWidth, int contentHeight, VectorImage imageCursor) {
		Matrix sizeMatrixCursor = new Matrix();
		int y = Alignment.computeTopY(DayForecastItemWidget.IMAGE_HEIGHT, 0, contentHeight, Alignment.VCENTER)
				+ (contentHeight / 4) + 10;
		int x = Alignment.computeLeftX(DayForecastItemWidget.IMAGE_WIDTH, getXOffset(getStyle()), contentWidth,
				Alignment.HCENTER);
		sizeMatrixCursor.setTranslate(x, y);
		sizeMatrixCursor.preScale(DayForecastItemWidget.IMAGE_SCALE / imageCursor.getWidth(),
				DayForecastItemWidget.IMAGE_SCALE / imageCursor.getHeight());
		return sizeMatrixCursor;
	}

	private static VectorFont getRegularFont(Style style) {
		return style.getExtraObject(DayForecastItemWidget.REGULAR_FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static VectorFont getSemiBoldFont(Style style) {
		return style.getExtraObject(DayForecastItemWidget.SEMI_BOLD_FONT_STYLE, VectorFont.class,
				WeatherDesktop.getSemiBoldFont());
	}

	private static int getTitleFontSize(Style style) {
		return style.getExtraInt(DayForecastItemWidget.TITLE_TEXT_SIZE_STYLE,
				DayForecastItemWidget.TITLE_DEFAULT_TEXT_SIZE);
	}

	private static int getLabelFontSize(Style style) {
		return style.getExtraInt(DayForecastItemWidget.LABEL_TEXT_SIZE_STYLE,
				DayForecastItemWidget.LABEL_DEFAULT_TEXT_SIZE);
	}

	private static int getValueFontSize(Style style) {
		return style.getExtraInt(DayForecastItemWidget.VALUE_TEXT_SIZE_STYLE,
				DayForecastItemWidget.VALUE_DEFAULT_TEXT_SIZE);
	}

	private static int getBackgroundColor(Style style) {
		return style.getExtraInt(DayForecastItemWidget.BACKGROUND_COLOR_STYLE,
				DayForecastItemWidget.DEFAULT_ROUNDED_BACKGROUND_COLOR);
	}

	private static int getXOffset(Style style) {
		return style.getExtraInt(X_OFFSET_STYLE, DEFAULT_X_OFFSET);
	}
}
