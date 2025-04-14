/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.fitness.activity;

import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;

/**
 * {@link Activity} which shows a fitness app.
 */
public class FitnessActivity implements Activity {

	private static final String ICON_IMAGE_135 = "/images/ic_fitness_135px.png";
	private static final String ICON_IMAGE_314 = "/images/ic_fitness_314px.png";

	private final Image iconImage135;
	private final Image iconImage314;

	/**
	 * Creates the activity of the Fitness app.
	 */
	public FitnessActivity() {
		this.iconImage135 = Image.getImage(ICON_IMAGE_135);
		this.iconImage314 = Image.getImage(ICON_IMAGE_314);
	}

	@Override
	public String getName() {
		return "Fitness";
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
		return new FitnessDesktop();
	}
}
