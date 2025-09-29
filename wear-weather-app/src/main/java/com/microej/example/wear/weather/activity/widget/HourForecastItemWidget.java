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
 * A widget for displaying an hourly weather forecast item.
 */
public class HourForecastItemWidget extends Widget {

	/** The constant value for the default temperature text size. */
	public static final int TEMP_DEFAULT_TEXT_SIZE = 20;

	/** The constant value for the default rain value text size. */
	public static final int RAIN_DEFAULT_TEXT_SIZE = 14;

	/** The extra field ID for the semi bold font. */
	public static final int SEMI_BOLD_FONT_STYLE = 0;

	/** The extra field ID for the temperature text size. */
	public static final int TEMPERATURE_TEXT_SIZE_STYLE = 1;

	/** The extra field ID for the title text size. */
	public static final int TITLE_TEXT_SIZE_STYLE = 2;

	/** The extra field ID for the rain value font. */
	public static final int RAIN_FONT_STYLE = 3;

	/** The extra field ID for the rain value text size. */
	public static final int RAIN_TEXT_SIZE_STYLE = 4;

	/** The extra field ID for the rounded background color. */
	public static final int BACKGROUND_COLOR_STYLE = 5;

	/** The extra field ID for the x offset. */
	public static final int X_OFFSET_STYLE = 6;

	private static final String WATER_DROPLET_PATH = "/images/humidity.xml";
	private static final String ROUNDED_RECTANGLE_PATH = "/images/rounded_rectangle.xml";
	private static final String PERCENT = "%";
	private static final int DEFAULT_X_OFFSET = -15;
	private static final int ICON_SCALE = 20;
	private static final int ICON_MARGIN_RIGHT = 10;
	private static final int TEMPERATURE_MARGIN_BOTTOM = 25;
	private static final float RAIN_MARGIN_BOTTOM = 10.0f;
	private static final int IMAGE_HEIGHT = 80;
	private static final int IMAGE_WIDTH = 80;
	private static final int IMAGE_SCALE = 80;
	private static final float DAY_OF_WEEK_MARGIN_TOP = 5.0f;
	private static final int DEFAULT_ROUNDED_BACKGROUND_COLOR = 0xff002A51;
	private final int temperature;
	private final long timestamp;
	private final int rain;
	private final String iconPath;
	@Nullable
	private BufferedVectorImage bufferedVectorImage;

	/**
	 * Constructs an {@code HourForecastItemWidget} with the specified forecast data.
	 *
	 * @param timestamp
	 *            the forecast time in seconds since the Unix epoch (UTC).
	 * @param temperature
	 *            the temperature in degrees Celsius at the specified timestamp.
	 * @param rain
	 *            the probability of precipitation.The parameter values range from 0 to 100.
	 * @param iconPath
	 *            the path of the weather icon.
	 */
	public HourForecastItemWidget(long timestamp, int temperature, int rain, String iconPath) {
		this.timestamp = timestamp;
		this.temperature = temperature;
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
			drawHour(g, contentWidth, contentHeight);
			drawForecastImage(g, contentWidth, contentHeight);
			drawTemperature(g, contentWidth, contentHeight);
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

	private void drawHour(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		VectorFont font = HourForecastItemWidget.getSemiBoldFont(style);
		int fontSize = HourForecastItemWidget.getTitleFontSize(style);

		String textValue = DateTimeHelper.getDisplayedHourFromTimestamp(this.timestamp);

		float textWidth = font.measureStringWidth(textValue, fontSize);

		int textHeight = (int) font.getHeight(fontSize);
		float x = Alignment.computeLeftX((int) textWidth, getXOffset(style), contentWidth, Alignment.HCENTER);

		float y = Alignment.computeTopY(textHeight, 0, contentHeight, Alignment.TOP);
		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, textValue, font, fontSize, x,
				y + HourForecastItemWidget.DAY_OF_WEEK_MARGIN_TOP);
	}

	private void drawTemperature(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();

		VectorFont temperatureFont = HourForecastItemWidget.getSemiBoldFont(style);
		int fontSize = HourForecastItemWidget.getTemperatureFontSize(style);

		String temperatureValue = this.temperature + "°";

		float tempTextWidth = temperatureFont.measureStringWidth(temperatureValue, fontSize);

		int stringHeight = (int) temperatureFont.getHeight(fontSize);
		float xTemp = Alignment.computeLeftX((int) tempTextWidth, getXOffset(style), contentWidth, Alignment.HCENTER);

		int y = Alignment.computeTopY(stringHeight, 0, contentHeight, Alignment.BOTTOM) - TEMPERATURE_MARGIN_BOTTOM;

		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, temperatureValue, temperatureFont, fontSize, xTemp,
				y - (stringHeight / 2.0f));
	}

	private void drawRainForecast(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();

		VectorFont rainFont = HourForecastItemWidget.getRainFont(style);
		int fontSize = HourForecastItemWidget.getRainFontSize(style);

		String textValue = this.rain + PERCENT;

		float rainTextWidth = rainFont.measureStringWidth(textValue, fontSize);

		int textHeight = (int) rainFont.getHeight(fontSize);
		float x = Alignment.computeLeftX((int) rainTextWidth,
				HourForecastItemWidget.ICON_SCALE - HourForecastItemWidget.ICON_MARGIN_RIGHT + getXOffset(style),
				contentWidth, Alignment.HCENTER);

		float y = Alignment.computeTopY(textHeight, 0, contentHeight, Alignment.BOTTOM) - RAIN_MARGIN_BOTTOM;

		// Draw icon
		VectorImage imageIcon = VectorImage.getImage(HourForecastItemWidget.WATER_DROPLET_PATH);
		Matrix sizeMatrixCursor = new Matrix();
		sizeMatrixCursor.setTranslate(x - HourForecastItemWidget.ICON_SCALE - HourForecastItemWidget.ICON_MARGIN_RIGHT,
				y);
		sizeMatrixCursor.preScale(HourForecastItemWidget.ICON_SCALE / imageIcon.getWidth(),
				HourForecastItemWidget.ICON_SCALE / imageIcon.getHeight());
		VectorGraphicsPainter.drawImage(g, imageIcon, sizeMatrixCursor);

		// Draw text
		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, textValue, rainFont, fontSize, x, y - 5.0f);
	}

	private void drawForecastImage(GraphicsContext g, int contentWidth, int contentHeight) {
		VectorImage image = VectorImage.getImage(this.iconPath);
		Matrix sizeMatrixCursor = getMatrix(contentWidth, contentHeight, image);
		VectorGraphicsPainter.drawImage(g, image, sizeMatrixCursor);
	}

	private Matrix getMatrix(int contentWidth, int contentHeight, VectorImage imageCursor) {
		Matrix sizeMatrixCursor = new Matrix();
		int y = Alignment.computeTopY(HourForecastItemWidget.IMAGE_HEIGHT, 0, contentHeight, Alignment.VCENTER) - 40;
		int x = Alignment.computeLeftX(HourForecastItemWidget.IMAGE_WIDTH, getXOffset(getStyle()), contentWidth,
				Alignment.HCENTER);
		sizeMatrixCursor.setTranslate(x, y);
		sizeMatrixCursor.preScale(HourForecastItemWidget.IMAGE_SCALE / imageCursor.getWidth(),
				HourForecastItemWidget.IMAGE_SCALE / imageCursor.getHeight());

		return sizeMatrixCursor;
	}

	private static int getTemperatureFontSize(Style style) {
		return style.getExtraInt(HourForecastItemWidget.TEMPERATURE_TEXT_SIZE_STYLE,
				HourForecastItemWidget.TEMP_DEFAULT_TEXT_SIZE);
	}

	private static VectorFont getRainFont(Style style) {
		return style.getExtraObject(HourForecastItemWidget.RAIN_FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getRainFontSize(Style style) {
		return style.getExtraInt(HourForecastItemWidget.RAIN_TEXT_SIZE_STYLE,
				HourForecastItemWidget.RAIN_DEFAULT_TEXT_SIZE);
	}

	private static VectorFont getSemiBoldFont(Style style) {
		return style.getExtraObject(HourForecastItemWidget.SEMI_BOLD_FONT_STYLE, VectorFont.class,
				WeatherDesktop.getSemiBoldFont());
	}

	private static int getTitleFontSize(Style style) {
		return style.getExtraInt(HourForecastItemWidget.TITLE_TEXT_SIZE_STYLE,
				DayForecastItemWidget.TITLE_DEFAULT_TEXT_SIZE);
	}

	private static int getBackgroundColor(Style style) {
		return style.getExtraInt(HourForecastItemWidget.BACKGROUND_COLOR_STYLE,
				HourForecastItemWidget.DEFAULT_ROUNDED_BACKGROUND_COLOR);
	}

	private static int getXOffset(Style style) {
		return style.getExtraInt(X_OFFSET_STYLE, DEFAULT_X_OFFSET);
	}
}
