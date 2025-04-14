/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.compass.activity;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;
import ej.drawing.TransformPainter;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microvg.VectorFont;
import ej.mwt.animation.Animator;

/**
 * {@link Activity} that shows a compass.
 */
public class CompassActivity implements Activity {

	private static final String ICON_IMAGE_135 = "/images/ic_compass_135px.png";
	private static final String ICON_IMAGE_314 = "/images/ic_compass_314px.png";

	private final Image iconImage135;
	private final Image iconImage314;

	/**
	 * Creates the activity of the Compass app.
	 */
	public CompassActivity() {
		this.iconImage135 = Image.getImage(ICON_IMAGE_135);
		this.iconImage314 = Image.getImage(ICON_IMAGE_314);
	}

	@Override
	public String getName() {
		return "Compass";
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
		Animator animator = new Animator();
		VectorFont font = KernelServiceProvider.getFontService().getBoldItalicFont();
		return new CompassDisplayable(font, animator);
	}
}
