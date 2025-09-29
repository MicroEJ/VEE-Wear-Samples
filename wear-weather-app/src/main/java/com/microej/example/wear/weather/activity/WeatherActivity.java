/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity;

import com.microej.example.wear.weather.activity.model.Weather;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;
import com.microej.wear.services.ResourceService;

import ej.annotation.Nullable;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.ResourceImage;

/**
 * {@link Activity} which shows the weather forecast.
 */
public class WeatherActivity implements Activity {

	/** Number of days used in the forecast. */
	public static final int DAYS_FORECAST = 7;

	/** Number of hours used in the hourly forecast. */
	public static final int HOURS_FORECAST = 24;

	/* Style constants */
	private static final String ICON_IMAGE_135 = "/images/ic_weather_135px.png";
	private static final String ICON_IMAGE_314 = "/images/ic_weather_314px.png";

	private @Nullable ResourceImage iconImage135;
	private @Nullable ResourceImage iconImage314;
	private final Weather weather;

	/**
	 * Creates the activity of the Weather app.
	 */
	public WeatherActivity() {
		this.weather = new Weather();
	}

	@Override
	public String getName() {
		return "Weather";
	}

	@Override
	public void onIconAttached() {
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.iconImage135 = ResourceImage.loadImage(resourceService.getImagePath(ICON_IMAGE_135));
		this.iconImage314 = ResourceImage.loadImage(resourceService.getImagePath(ICON_IMAGE_314));
	}

	@Override
	public void onIconDetached() {
		ResourceImage iconImage135 = this.iconImage135;
		if (iconImage135 != null) {
			iconImage135.close();
			this.iconImage135 = null;
		}

		ResourceImage iconImage314 = this.iconImage314;
		if (iconImage314 != null) {
			iconImage314.close();
			this.iconImage314 = null;
		}
	}

	@Override
	public void renderIcon(GraphicsContext g, int x, int y, int size) {
		Image image = (size > 135 ? this.iconImage314 : this.iconImage135);
		if (image != null) {
			float scale = (float) size / image.getWidth();
			int imageScaledHeight = (int) (scale * image.getHeight());
			TransformPainter.drawScaledImageBilinear(g, image, x, y + (size - imageScaledHeight) / 2, scale, scale);
		}
	}

	@Override
	public Renderable createRenderable() {
		return new WeatherDesktop(this.weather);
	}

}
