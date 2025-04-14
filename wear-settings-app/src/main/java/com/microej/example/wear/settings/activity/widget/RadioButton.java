/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.settings.activity.widget;

import com.microej.wear.KernelServiceProvider;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;
import ej.widget.render.ImagePainter;

/**
 * A radio button is a widget which displays a text and a box that can be checked or unchecked.
 */
public class RadioButton extends Widget {

	/** The extra field ID for the color of the radio button when it is checked. */
	public static final int CHECKED_COLOR_FIELD = 0;

	/** The constant value for the default label text size. */
	public static final int DEFAULT_TEXT_SIZE = 20;

	/** The extra field ID for the font. */
	public static final int FONT_STYLE = 0;

	/** The extra field ID for the text size. */
	public static final int TEXT_SIZE_STYLE = 1;

	private final String text;
	private final RadioButtonGroup group;

	private final Image radioImage;

	private final Image radioFillImage;

	/**
	 * Creates a radio button with the given text to display.
	 *
	 * @param text
	 *            the text to display.
	 * @param group
	 *            the group to which the radio button should belong.
	 */
	public RadioButton(String text, RadioButtonGroup group) {
		super(true);
		this.text = text;
		this.group = group;
		this.radioImage = Image.getImage("/images/ic_radio.png");
		this.radioFillImage = Image.getImage("/images/ic_radio_fill.png");
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		VectorFont font = getFont(style);
		int fontSize = style.getExtraInt(TEXT_SIZE_STYLE, DEFAULT_TEXT_SIZE);

		// draw text
		float textX = 0;
		float textY = Alignment.computeTopY((int) font.getHeight(fontSize), 0, contentHeight, Alignment.VCENTER);
		g.setColor(style.getColor());
		VectorGraphicsPainter.drawString(g, this.text, font, fontSize, textX, textY);

		int radioX = contentWidth - this.radioImage.getWidth() - 10;
		ImagePainter.drawImageInArea(g, this.radioImage, radioX, 0, this.radioImage.getWidth(), contentHeight,
				Alignment.HCENTER, Alignment.VCENTER);

		if (this.group.isChecked(this)) {
			ImagePainter.drawImageInArea(g, this.radioFillImage, radioX, 0, this.radioImage.getWidth(), contentHeight,
					Alignment.HCENTER, Alignment.VCENTER);
		}

	}

	private VectorFont getFont(Style style) {
		return style.getExtraObject(FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();
		VectorFont font = getFont(style);
		int fontSize = style.getExtraInt(TEXT_SIZE_STYLE, DEFAULT_TEXT_SIZE);
		int radioHeight = this.radioImage.getHeight();
		int height = Math.max(radioHeight, (int) font.getHeight(fontSize) + 1);
		size.setSize((int) font.measureStringWidth(this.text, fontSize), height);
	}

	@Override
	public boolean handleEvent(int event) {
		int type = Event.getType(event);
		if (type == Pointer.EVENT_TYPE) {
			int action = Buttons.getAction(event);
			if (action == Buttons.RELEASED) {
				this.group.setChecked(this);
				return true;
			}
		}

		return super.handleEvent(event);
	}

}