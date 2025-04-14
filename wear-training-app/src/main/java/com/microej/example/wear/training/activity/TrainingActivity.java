/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity;

import com.microej.example.wear.training.model.Training;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;

import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;

/**
 * {@link Activity} which shows a training app.
 */
public class TrainingActivity implements Activity {

	private static final String ICON_IMAGE_135 = "/images/ic_training_135px.png";
	private static final String ICON_IMAGE_314 = "/images/ic_training_314px.png";

	private final Image iconImage135;
	private final Image iconImage314;
	private final Training training;

	/**
	 * Creates the activity of the Training app.
	 */
	public TrainingActivity() {
		this.iconImage135 = Image.getImage(TrainingActivity.ICON_IMAGE_135);
		this.iconImage314 = Image.getImage(TrainingActivity.ICON_IMAGE_314);
		this.training = new Training();
	}

	@Override
	public String getName() {
		return "Training";
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
		return new TrainingDesktop(this.training);
	}
}
