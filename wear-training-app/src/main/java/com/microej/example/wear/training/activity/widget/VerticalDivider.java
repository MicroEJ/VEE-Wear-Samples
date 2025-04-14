/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget;

import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.mwt.Widget;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A simple vertical divider widget that renders a thin vertical line.
 */
public class VerticalDivider extends Widget {
	private static final int THICKNESS = 3;

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setWidth(VerticalDivider.THICKNESS);
		size.setHeight(getHeight());
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		g.setColor(Colors.WHITE);
		int x = Alignment.computeLeftX(VerticalDivider.THICKNESS, 0, contentWidth, Alignment.HCENTER);
		Painter.fillRectangle(g, x, 0, VerticalDivider.THICKNESS, contentHeight);
	}

}
