/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget;

import com.microej.wear.KernelServiceProvider;

import ej.bon.Util;
import ej.microui.display.GraphicsContext;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that displays a duration value on top (based on a start time) and a static label below.
 */
public class VectorDurationLabel extends Widget {

	/**
	 * The constant value for the default label text size.
	 */
	public static final int DEFAULT_LABEL_SIZE = 10;

	/**
	 * The constant value for the default value text size.
	 */
	public static final int DEFAULT_VALUE_SIZE = 16;

	/**
	 * The constant value for the default label color.
	 */
	public static final int DEFAULT_LABEL_COLOR = 0xd9d9d9;

	/**
	 * The extra field ID for the value font.
	 */
	public static final int VALUE_FONT_STYLE = 0;

	/**
	 * The extra field ID for the label font.
	 */
	public static final int LABEL_FONT_STYLE = 1;

	/**
	 * The extra field ID for the value size.
	 */
	public static final int VALUE_SIZE_STYLE = 2;

	/**
	 * The extra field ID for the label size.
	 */
	public static final int LABEL_SIZE_STYLE = 3;

	/**
	 * The extra field ID for the label color.
	 */
	public static final int LABEL_COLOR_STYLE = 5;

	private static final int VERTICAL_SPACING = 23;
	private static final String DURATION_PATTERN = "00:00:00";
	private static final String DURATION_LABEL = "Duration";
	private final long startTime;

	/**
	 * Constructs a VectorDurationLabel with the specified start time.
	 *
	 * @param startTime
	 *            the start time in milliseconds
	 */
	public VectorDurationLabel(long startTime) {
		this.startTime = startTime;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();

		// Compute value width and height.
		VectorFont valueFont = getValueFont(style);
		int valueFontSize = VectorDurationLabel.getValueFontSize(style);
		int valueWidth = (int) valueFont.measureStringWidth(VectorDurationLabel.DURATION_PATTERN, valueFontSize) + 1;
		int valueHeight = (int) valueFont.getHeight(valueFontSize);

		int labelWidth = 0;
		int labelHeight = 0;

		// Compute label width and height.
		VectorFont labelFont = getLabelFont(style);
		int labelFontSize = VectorDurationLabel.getLabelFontSize(style);
		labelWidth = (int) labelFont.measureStringWidth(VectorDurationLabel.DURATION_LABEL, labelFontSize) + 1;
		labelHeight = (int) labelFont.getHeight(labelFontSize);

		// Compute the widget width and height.
		int maxWidth = Math.max(labelWidth, valueWidth);
		int widgetHeight = labelHeight + valueHeight;

		// Set the widget width and height.
		size.setWidth(maxWidth);
		size.setHeight(widgetHeight);
	}

	private VectorFont getLabelFont(Style style) {
		return style.getExtraObject(VectorDurationLabel.LABEL_FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getLabelFontSize(Style style) {
		return style.getExtraInt(VectorDurationLabel.LABEL_SIZE_STYLE, VectorDurationLabel.DEFAULT_LABEL_SIZE);
	}

	private VectorFont getValueFont(Style style) {
		return style.getExtraObject(VectorDurationLabel.VALUE_FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getValueFontSize(Style style) {
		return style.getExtraInt(VectorDurationLabel.VALUE_SIZE_STYLE, VectorDurationLabel.DEFAULT_VALUE_SIZE);
	}

	private static int getLabelColor(Style style) {
		return style.getExtraInt(VectorDurationLabel.LABEL_COLOR_STYLE, VectorDurationLabel.DEFAULT_LABEL_COLOR);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();

		String valueString = getDuration();
		// Draw value.
		VectorFont valueFont = getValueFont(style);
		int valueFontSize = VectorDurationLabel.getValueFontSize(style);

		int valueWidth = (int) valueFont.measureStringWidth(valueString, valueFontSize);
		int valueHeight = (int) valueFont.getHeight(valueFontSize);
		int xValue = Alignment.computeLeftX(valueWidth, 0, contentWidth, Alignment.HCENTER);
		int yValue = 0;
		g.setColor(style.getColor());
		VectorGraphicsPainter.drawString(g, valueString, valueFont, valueFontSize, xValue, yValue);

		if (!VectorDurationLabel.DURATION_LABEL.isEmpty()) {
			// Draw label.
			VectorFont labelFont = getLabelFont(style);
			int labelFontSize = VectorDurationLabel.getLabelFontSize(style);
			int labelWidth = (int) labelFont.measureStringWidth(VectorDurationLabel.DURATION_LABEL, labelFontSize);
			int xLabel = Alignment.computeLeftX(labelWidth, 0, contentWidth, style.getHorizontalAlignment());
			int yLabel = yValue + valueHeight - VectorDurationLabel.VERTICAL_SPACING;
			g.setColor(VectorDurationLabel.getLabelColor(style));
			VectorGraphicsPainter.drawString(g, VectorDurationLabel.DURATION_LABEL, labelFont, labelFontSize, xLabel,
					yLabel);
		}
	}

	private String getDuration() {
		long platformTimeMillis = Util.platformTimeMillis();
		long elapsed = platformTimeMillis - this.startTime;
		return TimeFormatter.getFormatedDuration(elapsed);
	}

}
