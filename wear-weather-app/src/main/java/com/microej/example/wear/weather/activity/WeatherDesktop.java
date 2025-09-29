/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity;

import com.microej.example.wear.weather.activity.container.DailyChartContainer;
import com.microej.example.wear.weather.activity.container.DailyForecastContainer;
import com.microej.example.wear.weather.activity.container.HourlyForecastContainer;
import com.microej.example.wear.weather.activity.model.Weather;
import com.microej.example.wear.weather.activity.style.ClassIdentifiers;
import com.microej.example.wear.weather.activity.style.GradientStyle;
import com.microej.example.wear.weather.activity.widget.DayForecastItemWidget;
import com.microej.example.wear.weather.activity.widget.DotsIndicator;
import com.microej.example.wear.weather.activity.widget.HourForecastItemWidget;
import com.microej.example.wear.weather.activity.widget.IconLabelWidget;
import com.microej.example.wear.weather.activity.widget.SwipeContainer;
import com.microej.example.wear.weather.activity.widget.chart.ChartImageLegend;
import com.microej.example.wear.weather.activity.widget.chart.ChartLabels;
import com.microej.example.wear.weather.activity.widget.chart.ChartTextLegend;
import com.microej.example.wear.weather.activity.widget.chart.VectorLineChart;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.util.renderable.RenderableDesktop;

import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.dimension.FixedDimension;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.FirstChildSelector;
import ej.mwt.stylesheet.selector.LastChildSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.stylesheet.selector.combinator.AndCombinator;
import ej.mwt.util.Alignment;

/**
 * A desktop which renders weather forecast.
 */
public final class WeatherDesktop extends RenderableDesktop {

	/** Font size ratio for the main title. */
	public static final float FORECAST_CONTAINER_TITLE_FONT_SIZE_RATIO = 0.10f;

	/** Font size ratio for weather icon label at the top in daily chart container. */
	public static final float WEATHER_ICON_LABELS_FONT_SIZE_RATIO = 0.07f;

	/** Font size ratio for temperature labels. */
	public static final float TEMPERATURE_FONT_SIZE_RATIO = 0.14f;

	/** Font size ratio for date labels. */
	public static final float DATE_FONT_SIZE_RATIO = 0.08f;

	/** Font size ratio used for chart legends. */
	public static final float LEGEND_FONT_SIZE_RATIO = 0.05f;

	/** Vertical offset of the legend line. */
	public static final int LEGEND_LINE_Y_OFFSET = 7;
	private static final int HORIZONTAL_ITEM_PADDING = 7;
	private static final float FIRST_LAST_ITEM_PADDING_RATIO = 0.107f;
	private static final int ITEM_ROUNDED_BACKGROUND_COLOR = 0xff002A51;
	private static final int ITEM_LIST_HEIGHT = 280;
	private static final int ITEM_LIST_WIDTH = 120;
	private static final int LIST_ITEM_MARGIN_SIDES = 0;
	private static final int CURVE_COLOR = 0xffffa64d;
	private static final GradientStyle CURVE_GRADIENT = new GradientStyle(
			new int[] { 0xffff8c1a, 0xff000000, 0xff000000 }, new float[] { 0, 0.6f, 1 }, 90);
	private static final int UNIT_COLOR = 0x656565;
	private static final float CURVE_THICKNESS = 4f;
	private static final float FORECAST_ITEM_TITLE_FONT_SIZE_RATIO = 0.07f;
	private static final float FORECAST_ITEM_LABEL_FONT_SIZE_RATIO = 0.05f;
	private static final float FORECAST_TEMP_FONT_SIZE_RATIO = 0.08f;
	private static final float FORECAST_RAIN_FONT_SIZE_RATIO = 0.05f;
	private static final float FORECAST_ITEM_X_OFFSET_RATIO = -0.017f;
	private static final float FORECAST_FIRST_OR_LAST_ITEM_X_OFFSET_RATIO = -0.0686f;
	private static final float INDICATOR_HEIGHT_RATIO = 0.214f;
	private static final int INDICATOR_SIZE = 20;
	private static final int ICON_LABEL_TEXT_SPACING = 8;
	private static final int RAIN_ICON_LABEL_TEXT_SPACING = 4;
	private static final int CHART_IMAGE_LEGEND_Y_OFFSET = 25;
	private final Weather weather;

	/**
	 * Creates the desktop for the weather app.
	 *
	 * @param weather
	 *            the weather instance containing the temperature data to be displayed in the chart.
	 */
	public WeatherDesktop(Weather weather) {
		this.weather = weather;
		setStylesheet(createStylesheet());
		setWidget(createContentWidget());
	}

	private static CascadingStylesheet createStylesheet() {
		CascadingStylesheet stylesheet = new CascadingStylesheet();
		VectorFont regularFont = WeatherDesktop.getRegularFont();
		VectorFont semiBoldFont = WeatherDesktop.getSemiBoldFont();
		Display display = Display.getDisplay();

		int displaySize = Math.min(display.getWidth(), display.getHeight());
		int temperatureFontSize = (int) (TEMPERATURE_FONT_SIZE_RATIO * displaySize);
		int iconLabelFontSize = (int) (WEATHER_ICON_LABELS_FONT_SIZE_RATIO * displaySize);
		int dateFontSize = (int) (DATE_FONT_SIZE_RATIO * displaySize);
		int legendFontSize = (int) (LEGEND_FONT_SIZE_RATIO * displaySize);
		int rainFontSize = (int) (WeatherDesktop.FORECAST_RAIN_FONT_SIZE_RATIO * displaySize);

		// default style
		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);

		// root widget style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.ROOT_WIDGET));
		style.setDimension(new FixedDimension(display.getWidth(), display.getHeight()));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		// Vertical item style (Carousel item style)
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.ITEM_CAROUSEL));
		style.setDimension(new FixedDimension(display.getWidth(), display.getHeight()));
		style.setPadding(new FlexibleOutline(0, LIST_ITEM_MARGIN_SIDES, 0, 0));
		style.setHorizontalAlignment(Alignment.HCENTER);

		int indicatorHeight = (int) (INDICATOR_HEIGHT_RATIO * displaySize);
		// Dots indicator style
		style = stylesheet.getSelectorStyle(new TypeSelector(DotsIndicator.class));
		style.setDimension(new FixedDimension(INDICATOR_SIZE, indicatorHeight));
		style.setVerticalAlignment(Alignment.VCENTER);
		style.setHorizontalAlignment(Alignment.HCENTER);

		// Daily forecast chart style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.WEATHER_CHART));
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setExtraObject(VectorLineChart.CURVE_COLOR_STYLE, CURVE_COLOR);
		style.setExtraObject(VectorLineChart.CURVE_THICKNESS_STYLE, CURVE_THICKNESS);
		style.setExtraObject(VectorLineChart.CURVE_GRADIENT_STYLE, CURVE_GRADIENT);
		style.setExtraObject(VectorLineChart.FONT_STYLE, regularFont);
		style.setColor(UNIT_COLOR);

		// Top horizontal legend in DailyWeatherChart style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.CHART_HORIZONTAL_LEGEND));
		style.setExtraObject(ChartTextLegend.FONT_STYLE, regularFont);
		style.setExtraInt(ChartTextLegend.TEXT_SIZE_STYLE, legendFontSize);
		style.setExtraInt(ChartTextLegend.LEGEND_LINE_OFFSET_STYLE, LEGEND_LINE_Y_OFFSET);
		style.setColor(Colors.WHITE);

		// Left Vertical legend in DailyWeatherChart style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.CHART_VERTICAL_LEGEND));
		style.setExtraObject(ChartLabels.FONT_STYLE, regularFont);
		style.setExtraInt(ChartLabels.TEXT_SIZE_STYLE, legendFontSize);
		style.setColor(Colors.WHITE);

		// Temperature style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TEMPERATURE_LABEL));
		style.setFont(semiBoldFont.getFont(temperatureFontSize));
		style.setColor(Colors.WHITE);

		// Icon label style (min temperature , max temperature )
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.ICON_LABEL));
		style.setExtraObject(IconLabelWidget.FONT_STYLE, regularFont);
		style.setExtraInt(IconLabelWidget.TEXT_SIZE_STYLE, iconLabelFontSize);
		style.setExtraInt(IconLabelWidget.TEXT_SPACING_STYLE, ICON_LABEL_TEXT_SPACING);
		style.setColor(Colors.WHITE);

		// Icon rain label style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.RAIN_ICON_LABEL));
		style.setExtraObject(IconLabelWidget.FONT_STYLE, regularFont);
		style.setExtraInt(IconLabelWidget.TEXT_SIZE_STYLE, iconLabelFontSize);
		style.setExtraInt(IconLabelWidget.TEXT_SPACING_STYLE, RAIN_ICON_LABEL_TEXT_SPACING);
		style.setColor(Colors.WHITE);

		// Date style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DATE_LABEL));
		style.setFont(semiBoldFont.getFont(dateFontSize));
		style.setColor(Colors.WHITE);
		int forecastTemperatureFontSize = (int) (WeatherDesktop.FORECAST_TEMP_FONT_SIZE_RATIO * displaySize);
		int itemTitleFontSize = (int) (WeatherDesktop.FORECAST_ITEM_TITLE_FONT_SIZE_RATIO * displaySize);
		int labelFontSize = (int) (WeatherDesktop.FORECAST_ITEM_LABEL_FONT_SIZE_RATIO * displaySize);
		int titleFontSize = (int) (WeatherDesktop.FORECAST_CONTAINER_TITLE_FONT_SIZE_RATIO * displaySize);

		// Title style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.FORECAST_TITLE_LABEL));
		style.setFont(semiBoldFont.getFont(titleFontSize));
		style.setColor(Colors.WHITE);

		// ChartImageLegend style
		style = stylesheet.getSelectorStyle(new TypeSelector(ChartImageLegend.class));
		style.setExtraInt(ChartImageLegend.Y_OFFSET_STYLE, CHART_IMAGE_LEGEND_Y_OFFSET);

		int xItemOffset = (int) (FORECAST_ITEM_X_OFFSET_RATIO * displaySize);
		// Day Forecast item style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DAY_FORECAST_ITEM));
		style.setDimension(new FixedDimension(ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT));
		style.setPadding(new FlexibleOutline(0, HORIZONTAL_ITEM_PADDING, 0, HORIZONTAL_ITEM_PADDING));
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setVerticalAlignment(Alignment.VCENTER);
		style.setExtraInt(DayForecastItemWidget.X_OFFSET_STYLE, xItemOffset);
		style.setExtraObject(DayForecastItemWidget.SEMI_BOLD_FONT_STYLE, semiBoldFont);
		style.setExtraObject(DayForecastItemWidget.REGULAR_FONT_STYLE, regularFont);
		style.setExtraObject(DayForecastItemWidget.TITLE_TEXT_SIZE_STYLE, itemTitleFontSize);
		style.setExtraInt(DayForecastItemWidget.VALUE_TEXT_SIZE_STYLE, forecastTemperatureFontSize);
		style.setExtraInt(DayForecastItemWidget.LABEL_TEXT_SIZE_STYLE, labelFontSize);
		style.setColor(Colors.WHITE);

		// Hour Forecast item style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.HOUR_FORECAST_ITEM));
		style.setDimension(new FixedDimension(ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT));
		style.setPadding(new FlexibleOutline(0, HORIZONTAL_ITEM_PADDING, 0, HORIZONTAL_ITEM_PADDING));
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setVerticalAlignment(Alignment.VCENTER);
		style.setExtraInt(DayForecastItemWidget.X_OFFSET_STYLE, xItemOffset);
		style.setExtraObject(HourForecastItemWidget.SEMI_BOLD_FONT_STYLE, semiBoldFont);
		style.setExtraInt(HourForecastItemWidget.TEMPERATURE_TEXT_SIZE_STYLE, forecastTemperatureFontSize);
		style.setExtraObject(HourForecastItemWidget.RAIN_FONT_STYLE, semiBoldFont);
		style.setExtraInt(HourForecastItemWidget.RAIN_TEXT_SIZE_STYLE, rainFontSize);
		style.setExtraInt(HourForecastItemWidget.BACKGROUND_COLOR_STYLE, ITEM_ROUNDED_BACKGROUND_COLOR);
		style.setExtraObject(HourForecastItemWidget.TITLE_TEXT_SIZE_STYLE, itemTitleFontSize);
		style.setColor(Colors.WHITE);

		int firstOrLastXItemOffset = (int) (FORECAST_FIRST_OR_LAST_ITEM_X_OFFSET_RATIO * displaySize);
		int firstOrLastPadding = (int) (FIRST_LAST_ITEM_PADDING_RATIO * displaySize);

		// Day Forecast first and last item style
		style = stylesheet.getSelectorStyle(new AndCombinator(new ClassSelector(ClassIdentifiers.DAY_FORECAST_ITEM),
				FirstChildSelector.FIRST_CHILD_SELECTOR));
		style.setMargin(new FlexibleOutline(0, 0, 0, firstOrLastPadding));
		style.setExtraInt(DayForecastItemWidget.X_OFFSET_STYLE, firstOrLastXItemOffset);

		style = stylesheet.getSelectorStyle(new AndCombinator(new ClassSelector(ClassIdentifiers.DAY_FORECAST_ITEM),
				LastChildSelector.LAST_CHILD_SELECTOR));
		style.setMargin(new FlexibleOutline(0, firstOrLastPadding, 0, 0));
		style.setExtraInt(DayForecastItemWidget.X_OFFSET_STYLE, firstOrLastXItemOffset);

		// Hour Forecast first and last item style
		style = stylesheet.getSelectorStyle(new AndCombinator(new ClassSelector(ClassIdentifiers.HOUR_FORECAST_ITEM),
				FirstChildSelector.FIRST_CHILD_SELECTOR));
		style.setMargin(new FlexibleOutline(0, 0, 0, firstOrLastPadding));
		style.setExtraInt(DayForecastItemWidget.X_OFFSET_STYLE, firstOrLastXItemOffset);

		style = stylesheet.getSelectorStyle(new AndCombinator(new ClassSelector(ClassIdentifiers.HOUR_FORECAST_ITEM),
				LastChildSelector.LAST_CHILD_SELECTOR));
		style.setMargin(new FlexibleOutline(0, firstOrLastPadding, 0, 0));
		style.setExtraInt(DayForecastItemWidget.X_OFFSET_STYLE, firstOrLastXItemOffset);

		return stylesheet;
	}

	private Widget createContentWidget() {
		SwipeContainer swipeContainer = new SwipeContainer();
		swipeContainer.addClassSelector(ClassIdentifiers.ROOT_WIDGET);
		DailyChartContainer dailyChartContainer = new DailyChartContainer(this.weather);
		dailyChartContainer.addClassSelector(ClassIdentifiers.ITEM_CAROUSEL);
		DailyForecastContainer dailyForecastContainer = new DailyForecastContainer(this.weather);
		dailyForecastContainer.addClassSelector(ClassIdentifiers.ITEM_CAROUSEL);
		HourlyForecastContainer hourlyForecastContainer = new HourlyForecastContainer(this.weather);
		hourlyForecastContainer.addClassSelector(ClassIdentifiers.ITEM_CAROUSEL);

		swipeContainer.addChildToContainer(dailyChartContainer);
		swipeContainer.addChildToContainer(dailyForecastContainer);
		swipeContainer.addChildToContainer(hourlyForecastContainer);

		return swipeContainer;
	}

	/**
	 * Loads and returns the semi bold vector font.
	 *
	 * @return A {@link VectorFont} instance representing the semi bold font.
	 */
	public static VectorFont getSemiBoldFont() {
		return KernelServiceProvider.getFontService().getSemiBoldFont();
	}

	/**
	 * Loads and returns the default vector font.
	 *
	 * @return A {@link VectorFont} instance loaded with the default font.
	 */
	public static VectorFont getRegularFont() {
		return KernelServiceProvider.getFontService().getRegularFont();
	}
}
