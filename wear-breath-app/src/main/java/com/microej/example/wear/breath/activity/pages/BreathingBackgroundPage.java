/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.pages;

import com.microej.example.wear.breath.activity.style.ClassIdentifiers;
import com.microej.example.wear.breath.activity.widget.VectorAnimatedImage;

import ej.microui.display.Display;
import ej.mwt.util.Alignment;
import ej.widget.container.Canvas;

/**
 * The breathing background page contains breath animation.
 */
public class BreathingBackgroundPage extends Canvas {

	/** Breath animation constants. */
	private static final String BREATH_ANIMATED_IMAGE_PATH = "/images/breath_animation.xml"; //$NON-NLS-1$
	private static final int ANIMATION_START_TIMESTAMP = 1000;
	private static final int ANIMATION_END_TIMESTAMP = 11000;

	/**
	 * Creates breathing background page with breath animation.
	 */
	public BreathingBackgroundPage() {
		// create the background content
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();

		addClassSelector(ClassIdentifiers.DESKTOP_ROOT);

		VectorAnimatedImage breathAnimatedImage = new VectorAnimatedImage(BREATH_ANIMATED_IMAGE_PATH,
				ANIMATION_START_TIMESTAMP, ANIMATION_END_TIMESTAMP);
		int animatedImageWidth = breathAnimatedImage.getOriginalImageWidth();
		int animatedImageHeight = breathAnimatedImage.getOriginalImageHeight();
		int x = Alignment.computeLeftX(animatedImageWidth, 0, displayWidth, Alignment.HCENTER);
		int y = Alignment.computeTopY(animatedImageHeight, 0, displayHeight, Alignment.VCENTER);
		addChild(breathAnimatedImage, x, y, animatedImageWidth, animatedImageHeight);
	}
}
