/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.pages;

import com.microej.example.wear.breath.activity.style.ClassIdentifiers;
import com.microej.example.wear.breath.activity.style.Theme;
import com.microej.example.wear.breath.activity.widget.VectorImageWidget;

import ej.microui.display.Display;
import ej.mwt.util.Alignment;
import ej.widget.container.Canvas;

/**
 * The home background page contains microej logo.
 */
public class HomeBackgroundPage extends Canvas {

	/** MicroEJ Logo constants. */
	private static final String MICROEJ_LOGO_PATH = "/images/breath_animation.xml"; //$NON-NLS-1$

	/**
	 * Creates home background page contains microej logo
	 */
	public HomeBackgroundPage() {
		// create the background content
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();

		addClassSelector(ClassIdentifiers.DESKTOP_ROOT);

		// draw the logo
		VectorImageWidget microejLogo = new VectorImageWidget(MICROEJ_LOGO_PATH, Theme.BREATH_ANIMATION_SCALE);
		int imageWidth = microejLogo.getOriginalImageWidth();
		int imageHeight = microejLogo.getOriginalImageHeight();
		int x = Alignment.computeLeftX(imageWidth, 0, displayWidth, Alignment.HCENTER);
		int y = Alignment.computeTopY(imageHeight, 0, displayHeight, Alignment.VCENTER);
		addChild(microejLogo, x, y, imageWidth, imageHeight);
	}
}
