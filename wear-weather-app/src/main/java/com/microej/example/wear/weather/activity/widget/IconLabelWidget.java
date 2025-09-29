/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget;

import com.microej.wear.KernelServiceProvider;

import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that displays a temperature indicator using vector fonts.
 */
public class IconLabelWidget extends Widget {

	/** The constant value for the default label text size. */
	public static final int DEFAULT_TEXT_SIZE = 20;

	/** The extra field ID for the font. */
	public static final int FONT_STYLE = 0;

	/** The extra field ID for the text size. */
	public static final int TEXT_SIZE_STYLE = 1;

	/** The extra field ID for the text spacing between icon and text. */
	public static final int TEXT_SPACING_STYLE = 2;

	private static final int CURSOR_HEIGHT = 20;
	private static final int ICON_MARGIN_LEFT = 5;
	private static final int ICON_MARGIN_TOP = 5;
	private static final int DEFAULT_TEXT_SPACING = 8;
	private String text;
	private final String imagePath;
	private final int iconScale;

	/**
	 * Creates a label with the given text to display.
	 *
	 * @param text
	 *            the text to display.
	 */
	public IconLabelWidget(String text, String imagePath, int iconScale) {
		this.text = text;
		this.imagePath = imagePath;
		this.iconScale = iconScale;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Sets the text to display for this label.
	 *
	 * @param text
	 *            the text to display.
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();
		VectorFont font = IconLabelWidget.getFont(style);
		int fontSize = IconLabelWidget.getFontSize(style);
		size.setWidth((int) font.measureStringWidth(this.text, fontSize) + this.iconScale);
		size.setHeight((int) font.getHeight(fontSize));
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		int textSpacing = style.getExtraInt(IconLabelWidget.TEXT_SPACING_STYLE, IconLabelWidget.DEFAULT_TEXT_SPACING);
		VectorFont font = IconLabelWidget.getFont(style);
		int fontSize = IconLabelWidget.getFontSize(style);
		int stringHeight = (int) font.getHeight(fontSize);
		int x = IconLabelWidget.ICON_MARGIN_LEFT + this.iconScale + textSpacing;
		int y = Alignment.computeTopY(stringHeight, 0, contentHeight, Alignment.VCENTER);
		g.setColor(style.getColor());
		VectorGraphicsPainter.drawString(g, this.text, font, fontSize, x, y);
		drawIcon(g, contentHeight);
	}

	private void drawIcon(GraphicsContext g, int contentHeight) {
		VectorImage imageIcon = VectorImage.getImage(this.imagePath);
		float[] color = new float[] { 0, 0, 0, 0, 77, // red
				0, 0, 0, 0, 77, // green
				0, 0, 0, 0, 77, // blue
				0, 0, 0, 1, 0 // alpha
		};
		Matrix matrix = getMatrix(contentHeight, imageIcon);
		VectorGraphicsPainter.drawFilteredImage(g, imageIcon, matrix, color);
	}

	private Matrix getMatrix(int contentHeight, VectorImage image) {
		Matrix matrix = new Matrix();
		int y = Alignment.computeTopY(IconLabelWidget.CURSOR_HEIGHT, 0, contentHeight, Alignment.VCENTER)
				+ IconLabelWidget.ICON_MARGIN_TOP;
		matrix.setTranslate(IconLabelWidget.ICON_MARGIN_LEFT, y);
		matrix.preScale(this.iconScale / image.getWidth(), this.iconScale / image.getHeight());

		return matrix;
	}

	private static VectorFont getFont(Style style) {
		return style.getExtraObject(IconLabelWidget.FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getFontSize(Style style) {
		return style.getExtraInt(IconLabelWidget.TEXT_SIZE_STYLE, IconLabelWidget.DEFAULT_TEXT_SIZE);
	}

}