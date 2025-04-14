/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget;

import com.microej.wear.KernelServiceProvider;

import ej.microui.display.GraphicsContext;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * Represents a labeled value with a specific pattern and an initial value.
 */
public class VectorLabeledValue extends Widget {

	/**
	 * The constant value for the default label text size.
	 */
	public static final int DEFAULT_LABEL_SIZE = 10;

	/**
	 * The constant value for the default label text size.
	 */
	public static final int USE_PATTERN_FOR_HORIZONTAL_ALIGNMENT = 0;
	/**
	 * The constant value for the default label text size.
	 */
	public static final int USE_VALUE_FOR_HORIZONTAL_ALIGNMENT = 1;

	/**
	 * The constant value for the default value text size.
	 */
	public static final int DEFAULT_VALUE_SIZE = 16;

	/**
	 * The constant value for the default label color.
	 */
	public static final int DEFAULT_LABEL_COLOR = 0xd9d9d9;

	/**
	 * The extra field ID for the font.
	 */
	public static final int VALUE_FONT_STYLE = 0;

	/**
	 * The extra field ID for the font.
	 */
	public static final int LABEL_FONT_STYLE = 1;

	/**
	 * The extra field ID for the text size.
	 */
	public static final int VALUE_SIZE_STYLE = 2;

	/**
	 * The extra field ID for the text size.
	 */
	public static final int LABEL_SIZE_STYLE = 3;

	/**
	 * The extra field ID for the text size.
	 */
	public static final int HORIZONTAL_ALIGNMENT_STYLE = 4;

	/**
	 * The extra field ID for the label color.
	 */
	public static final int LABEL_COLOR_STYLE = 5;

	private static final int VERTICAL_SPACING = 23;

	private String label;
	private String value;
	private final String valuePattern;

	/**
	 * Creates a labeled value with a specified pattern and initial value.
	 *
	 * @param valuePattern
	 *            The pattern defining the format of the value.
	 * @param initialValue
	 *            The initial value to be displayed.
	 * @param label
	 *            The text label associated with the value.
	 */
	public VectorLabeledValue(String valuePattern, String initialValue, String label) {
		this.label = label;
		this.value = initialValue;
		this.valuePattern = valuePattern;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Sets the text to display for this value.
	 *
	 * @param text
	 *            the value to display.
	 */
	public void setValue(String text) {
		this.value = text;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the text to display for this label.
	 *
	 * @param text
	 *            the label to display.
	 */
	public void setLabel(String text) {
		this.label = text;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();

		// Compute value width and height.
		VectorFont valueFont = getValueFont(style);
		int valueFontSize = VectorLabeledValue.getValueFontSize(style);
		int valueWidth = (int) valueFont.measureStringWidth(this.valuePattern, valueFontSize) + 1;
		int valueHeight = (int) valueFont.getHeight(valueFontSize);

		int labelWidth = 0;
		int labelHeight = 0;
		if (!this.label.isEmpty()) {
			// Compute label width and height.
			VectorFont labelFont = getLabelFont(style);
			int labelFontSize = VectorLabeledValue.getLabelFontSize(style);
			labelWidth = (int) labelFont.measureStringWidth(this.label, labelFontSize) + 1;
			labelHeight = (int) labelFont.getHeight(labelFontSize);
		}

		// Compute the widget width and height.
		int maxWidth = Math.max(labelWidth, valueWidth);
		int widgetHeight = labelHeight + valueHeight;

		// Set the widget width and height.
		size.setWidth(maxWidth);
		size.setHeight(widgetHeight);
	}

	private VectorFont getLabelFont(Style style) {
		return style.getExtraObject(VectorLabeledValue.LABEL_FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getLabelFontSize(Style style) {
		return style.getExtraInt(VectorLabeledValue.LABEL_SIZE_STYLE, VectorLabeledValue.DEFAULT_LABEL_SIZE);
	}

	private VectorFont getValueFont(Style style) {
		return style.getExtraObject(VectorLabeledValue.VALUE_FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getValueFontSize(Style style) {
		return style.getExtraInt(VectorLabeledValue.VALUE_SIZE_STYLE, VectorLabeledValue.DEFAULT_VALUE_SIZE);
	}

	private static int getLabelColor(Style style) {
		return style.getExtraInt(VectorLabeledValue.LABEL_COLOR_STYLE, VectorLabeledValue.DEFAULT_LABEL_COLOR);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		int alignmentStyle = style.getExtraInt(VectorLabeledValue.HORIZONTAL_ALIGNMENT_STYLE,
				VectorLabeledValue.USE_VALUE_FOR_HORIZONTAL_ALIGNMENT);

		// Draw value.
		VectorFont valueFont = getValueFont(style);
		int valueFontSize = VectorLabeledValue.getValueFontSize(style);
		String valueString = this.valuePattern;
		if (alignmentStyle == VectorLabeledValue.USE_VALUE_FOR_HORIZONTAL_ALIGNMENT) {
			valueString = this.value;
		}
		int valueWidth = (int) valueFont.measureStringWidth(valueString, valueFontSize);
		int valueHeight = (int) valueFont.getHeight(valueFontSize);
		int xValue = Alignment.computeLeftX(valueWidth, 0, contentWidth, Alignment.HCENTER);
		int yValue = 0;
		g.setColor(style.getColor());
		VectorGraphicsPainter.drawString(g, this.value, valueFont, valueFontSize, xValue, yValue);

		if (!this.label.isEmpty()) {
			// Draw label.
			VectorFont labelFont = getLabelFont(style);
			int labelFontSize = VectorLabeledValue.getLabelFontSize(style);
			int labelWidth = (int) labelFont.measureStringWidth(this.label, labelFontSize);
			int xLabel = Alignment.computeLeftX(labelWidth, 0, contentWidth, style.getHorizontalAlignment());
			int yLabel = yValue + valueHeight - VectorLabeledValue.VERTICAL_SPACING;
			g.setColor(VectorLabeledValue.getLabelColor(style));
			VectorGraphicsPainter.drawString(g, this.label, labelFont, labelFontSize, xLabel, yLabel);
		}
	}

}
