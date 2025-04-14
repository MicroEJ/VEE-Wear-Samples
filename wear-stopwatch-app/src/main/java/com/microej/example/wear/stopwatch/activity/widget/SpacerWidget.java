/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.stopwatch.activity.widget;

import ej.microui.display.GraphicsContext;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * An empty Widget adding space in the vertical or horizontal axis.
 */
public class SpacerWidget extends Widget {

	private final int space;
	private final boolean orientation;

	/**
	 * Creates a spacer.
	 *
	 * @param space
	 *            The space size in pixel.
	 * @param orientation
	 *            The axis.
	 */
	public SpacerWidget(int space, boolean orientation) {
		super();

		this.space = space;
		this.orientation = orientation;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		if (this.orientation) {
			size.setSize(this.space, size.getHeight());
		} else {
			size.setSize(size.getWidth(), this.space);
		}
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		/* Empty widget */
	}
}
