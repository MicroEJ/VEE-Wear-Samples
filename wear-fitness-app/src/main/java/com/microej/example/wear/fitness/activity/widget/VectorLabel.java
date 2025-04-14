/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.fitness.activity.widget;

import com.microej.wear.KernelServiceProvider;
import ej.microui.display.GraphicsContext;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 *
 */
public class VectorLabel extends Widget {

	/** The constant value for the default label text size. */
	public static final int DEFAULT_TEXT_SIZE = 20;

	/** The extra field ID for the font. */
	public static final int FONT_STYLE = 0;

	/** The extra field ID for the text size. */
	public static final int TEXT_SIZE_STYLE = 1;

	private String text;

	/**
	 * Creates a label with an empty text.
	 */
	public VectorLabel() {
		this("");
	}

	/**
	 * Creates a label with the given text to display.
	 *
	 * @param text
	 *            the text to display.
	 */
	public VectorLabel(String text) {
		this.text = text;
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
		VectorFont font = getFont(style);
		int fontSize = getFontSize(style);
		size.setWidth((int) font.measureStringWidth(this.text, fontSize) + 1);
		size.setHeight((int) font.getHeight(fontSize));
	}

	private VectorFont getFont(Style style) {
		return style.getExtraObject(FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getFontSize(Style style) {
		return style.getExtraInt(TEXT_SIZE_STYLE, DEFAULT_TEXT_SIZE);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		VectorFont font = getFont(style);
		int fontSize = getFontSize(style);
		int stringWidth = (int) font.measureStringWidth(this.text, fontSize);
		int stringHeight = (int) font.getHeight(fontSize);
		int x = Alignment.computeLeftX(stringWidth, 0, contentWidth, style.getHorizontalAlignment());
		int y = Alignment.computeTopY(stringHeight, 0, contentHeight, style.getVerticalAlignment());
		g.setColor(style.getColor());
		VectorGraphicsPainter.drawString(g, this.text, font, fontSize, x, y);
	}

}
