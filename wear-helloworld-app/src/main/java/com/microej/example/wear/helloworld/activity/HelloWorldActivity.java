/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.helloworld.activity;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Renderable;
import com.microej.wear.services.ResourceService;
import com.microej.wear.util.renderable.RenderableDisplayable;

import ej.annotation.Nullable;
import ej.drawing.TransformPainter;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;

/**
 * {@link Activity} that shows a simple Hello World.
 */
public class HelloWorldActivity implements Activity {

	private static final String ICON_IMAGE_135 = "/images/icon_empty_135px.png";
	private static final String ICON_IMAGE_314 = "/images/icon_empty_314px.png";
	private static final String HELLO_WORLD = "Hello World";

	private @Nullable ResourceImage iconImage135;
	private @Nullable ResourceImage iconImage314;

	@Override
	public void onIconAttached() {
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.iconImage135 = ResourceImage.loadImage(resourceService.getImagePath(ICON_IMAGE_135));
		this.iconImage314 = ResourceImage.loadImage(resourceService.getImagePath(ICON_IMAGE_314));
	}

	@Override
	public String getName() {
		return HELLO_WORLD;
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
		return new RenderableDisplayable() {
			@Override
			public void render(GraphicsContext g) {
				int width = g.getWidth();
				int height = g.getHeight();
				g.setColor(Colors.BLACK);
				Painter.fillRectangle(g, 0, 0, width, height);

				VectorFont font = KernelServiceProvider.getFontService().getRegularFont();
				float fontSize = height / 5f;
				float x = (width - font.measureStringWidth(HELLO_WORLD, fontSize)) / 2f;
				float y = (height - font.getHeight(fontSize)) / 2f;
				g.setColor(Colors.WHITE);
				VectorGraphicsPainter.drawString(g, HELLO_WORLD, font, fontSize, x, y);
			}
		};
	}
}
