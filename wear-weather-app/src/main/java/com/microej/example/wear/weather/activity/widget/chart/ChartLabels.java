/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

import com.microej.wear.KernelServiceProvider;

import ej.microui.display.GraphicsContext;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that displays two text labels used for annotating charts.
 */
public class ChartLabels extends Widget {

	/** The extra field ID for the font. */
	public static final int FONT_STYLE = 0;

	/** The extra field ID for the text size. */
	public static final int TEXT_SIZE_STYLE = 1;

	private static final int DEFAULT_LABEL_FONT_SIZE = 25;
	private final String topValue;
	private final String bottomValue;

	/**
	 * Constructs the widget.
	 *
	 * @param topValue
	 *            the text to display at the top of the chart label
	 * @param bottomValue
	 *            the text to display at the bottom of the chart label
	 */
	public ChartLabels(String topValue, String bottomValue) {
		this.topValue = topValue;
		this.bottomValue = bottomValue;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();
		VectorFont font = ChartLabels.getFont(style);
		int fontSize = ChartLabels.getFontSize(style);
		int widgetWidth = (int) font.measureStringWidth("XX°C", fontSize);
		size.setWidth(widgetWidth);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		drawLeftLabels(g, contentWidth, contentHeight);
	}

	private void drawLeftLabels(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		g.setColor(style.getColor());
		VectorFont font = ChartLabels.getFont(style);
		int fontSize = ChartLabels.getFontSize(style);
		float fontHeight = font.getHeight(fontSize);

		int topValueWidth = (int) font.measureStringWidth(this.topValue, fontSize);
		float xTopValue = Alignment.computeLeftX(topValueWidth, 0, contentWidth, Alignment.HCENTER);
		float yTopValue = Alignment.computeTopY((int) fontHeight, 0, contentHeight, Alignment.TOP);

		int bottomValueWidth = (int) font.measureStringWidth(this.bottomValue, fontSize);
		float xBottomValue = Alignment.computeLeftX(bottomValueWidth, 0, contentWidth, Alignment.HCENTER);
		float yBottomValue = Alignment.computeTopY((int) fontHeight, 0, contentHeight, Alignment.BOTTOM);

		VectorGraphicsPainter.drawString(g, this.topValue, font, fontSize, xTopValue, yTopValue);
		VectorGraphicsPainter.drawString(g, this.bottomValue, font, fontSize, xBottomValue, yBottomValue);
	}

	private static VectorFont getFont(Style style) {
		return style.getExtraObject(ChartLabels.FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getFontSize(Style style) {
		return style.getExtraInt(ChartLabels.TEXT_SIZE_STYLE, ChartLabels.DEFAULT_LABEL_FONT_SIZE);
	}
}
