/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity;

import com.microej.example.wear.weather.activity.widget.ScaledImageWidget;
import com.microej.example.wear.weather.activity.widget.Split;
import com.microej.example.wear.weather.activity.widget.VectorLabel;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;
import com.microej.wear.services.TimeService;
import com.microej.wear.util.renderable.RenderableDesktop;
import ej.drawing.TransformPainter;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microvg.VectorFont;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.ImageBackground;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.dimension.RelativeDimension;
import ej.mwt.style.outline.UniformOutline;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.util.Alignment;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.List;
import ej.widget.container.SimpleDock;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * {@link Activity} which shows the weather forecast.
 */
public class WeatherActivity implements Activity {

	/* Style constants */
	private static final int ROOT_WIDGET = 0;
	private static final int FORECAST_LIST = 1;
	private static final int FORECAST_IMAGE = 2;

	private static final String ICON_IMAGE_135 = "/images/ic_weather_135px.png";
	private static final String ICON_IMAGE_314 = "/images/ic_weather_314px.png";

	private static final int DAYS_FORECAST = 4;
	private static final String[] DAY_NAMES = new String[] { "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" };
	private static final long MILLIS_IN_SECOND = 1000;

	private final Image iconImage135;
	private final Image iconImage314;
	private final Random random;

	/* package */ enum WeatherType {
		CLOUDY, FEW_CLOUDS, RAINY, SUNNY;

		public String getIconPath() {
			return "/images/ic_" + name().toLowerCase() + ".png";
		}
	}

	/**
	 * Creates the activity of the Weather app.
	 */
	public WeatherActivity() {
		this.iconImage135 = Image.getImage(ICON_IMAGE_135);
		this.iconImage314 = Image.getImage(ICON_IMAGE_314);
		this.random = new Random();
	}

	@Override
	public String getName() {
		return "Weather";
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int size) {
		Image image = (size > 135 ? this.iconImage314 : this.iconImage135);
		float scale = (float) size / image.getWidth();
		int imageScaledHeight = (int) (scale * image.getHeight());
		TransformPainter.drawScaledImageBilinear(g, image, x, y + (size - imageScaledHeight) / 2, scale, scale);
	}

	@Override
	public Renderable createRenderable() {
		RenderableDesktop desktop = new RenderableDesktop();
		desktop.setStylesheet(createStylesheet());

		Split rootContainer = new Split(LayoutOrientation.VERTICAL);
		rootContainer.addClassSelector(ROOT_WIDGET);

		Random random = this.random;
		WeatherType[] weatherTypes = WeatherType.values();
		WeatherType todayWeather = weatherTypes[random.nextInt(weatherTypes.length)];
		rootContainer.setFirstChild(new ScaledImageWidget(todayWeather.getIconPath(), false));

		List forecast = new List(LayoutOrientation.HORIZONTAL);
		forecast.addClassSelector(FORECAST_LIST);

		LocalDateTime dateTime = getCurrentLocalDateTime();
		int currentDayIndex = dateTime.getDayOfWeek().getValue() - 1;
		int nextDay = currentDayIndex + 1;
		for (int i = nextDay; i < nextDay + DAYS_FORECAST; i++) {
			String day = DAY_NAMES[i % 7];
			WeatherType weather = weatherTypes[random.nextInt(weatherTypes.length)];
			forecast.addChild(createForecastItem(day, weather));
		}
		rootContainer.setLastChild(forecast);

		desktop.setWidget(rootContainer);
		return desktop;
	}

	private static Widget createForecastItem(String day, WeatherType weather) {
		SimpleDock dock = new SimpleDock(LayoutOrientation.VERTICAL);
		VectorLabel label = new VectorLabel(day);
		dock.setFirstChild(label);
		ScaledImageWidget image = new ScaledImageWidget(weather.getIconPath(), false);
		image.addClassSelector(FORECAST_IMAGE);
		dock.setCenterChild(image);
		return dock;
	}

	private static Stylesheet createStylesheet() {
		CascadingStylesheet stylesheet = new CascadingStylesheet();
		VectorFont font = KernelServiceProvider.getFontService().getRegularFont();

		EditableStyle style = stylesheet.getDefaultStyle();
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setVerticalAlignment(Alignment.VCENTER);
		style.setBackground(NoBackground.NO_BACKGROUND);

		style = stylesheet.getSelectorStyle(new ClassSelector(ROOT_WIDGET));
		style.setBackground(new ImageBackground(Image.getImage("/images/background_night.png"), Alignment.HCENTER,
				Alignment.VCENTER));

		style = stylesheet.getSelectorStyle(new ClassSelector(FORECAST_LIST));
		style.setDimension(new RelativeDimension(0.75f, 1f));

		style = stylesheet.getSelectorStyle(new ClassSelector(FORECAST_IMAGE));
		style.setVerticalAlignment(Alignment.TOP);
		style.setPadding(new UniformOutline(5));

		style = stylesheet.getSelectorStyle(new TypeSelector(VectorLabel.class));
		style.setColor(Colors.WHITE);
		style.setExtraObject(VectorLabel.FONT_STYLE, font);

		style = stylesheet.getSelectorStyle(new TypeSelector(Split.class));
		style.setExtraFloat(Split.RATIO_FIELD, 0.6f);

		return stylesheet;
	}

	/**
	 * Returns the date-time for the current time zone.
	 *
	 * <p>
	 * The local date-time is adjusted for the current time-zone and DST, in other words, the time that would be
	 * displayed to the user.
	 *
	 * @return the current local date-time
	 */
	private static LocalDateTime getCurrentLocalDateTime() {
		TimeService timeService = KernelServiceProvider.getTimeService();
		long currentLocalTime = timeService.getCurrentTime() + timeService.getTimeZoneOffset() * MILLIS_IN_SECOND;
		Instant now = Instant.ofEpochMilli(currentLocalTime);
		// using UTC because the instant is already adjusted for the current time-zone
		return LocalDateTime.ofInstant(now, ZoneOffset.UTC);
	}
}
