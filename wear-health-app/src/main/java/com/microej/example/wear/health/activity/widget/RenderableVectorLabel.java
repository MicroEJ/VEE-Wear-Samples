/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.health.activity.widget;

import ej.annotation.Nullable;
import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.microvg.BufferedVectorImage;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;
import ej.widget.render.StringPainter;

/**
 * A renderable vector label is a widget that displays a text.
 * <p>
 * It is slightly faster than {@link ej.widget.basic.Label} as it uses {@link ej.microvg.BufferedVectorImage} to cache
 * information about the text displayed.
 */
public class RenderableVectorLabel extends Widget {

	@Nullable
	private BufferedVectorImage vectorImage;

	private String text;

	/**
	 * Creates a RenderableVectorLabel with the given text to display.
	 *
	 * @param text
	 *            the text to display.
	 */
	public RenderableVectorLabel(String text) {
		this.text = text;
	}

	/**
	 * Sets the text of the label and caches the drawing of the text if it has changed.
	 *
	 * @param text
	 *            the text to display on this label.
	 */
	public void setText(String text) {
		if (!this.text.equals(text)) {
			this.text = text;

			BufferedVectorImage image = this.vectorImage;
			if (image != null) {
				image.close();
			}

			this.vectorImage = createBufferedImage();
		}
	}

	@Override
	public void onDetached() {
		super.onDetached();
		BufferedVectorImage image = this.vectorImage;
		if (image != null) {
			image.close();
		}
		this.vectorImage = null;
	}

	@Override
	protected void onLaidOut() {
		super.onLaidOut();
		if (this.vectorImage == null) {
			this.vectorImage = createBufferedImage();
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Font font = getStyle().getFont();
		int width = stringWidthVgFont(this.text, font);
		int height = font.getHeight();
		size.setSize(width, height);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		Font font = style.getFont();
		int width = stringWidthVgFont(this.text, font);
		int height = font.getHeight();

		int stringX = Alignment.computeLeftX(width, 0, contentWidth, style.getHorizontalAlignment());
		int stringY = Alignment.computeTopY(height, 0, contentHeight, style.getVerticalAlignment());

		BufferedVectorImage image = this.vectorImage;
		assert (image != null);
		VectorGraphicsPainter.drawImage(g, image, stringX, stringY);
	}

	/**
	 * Creates a BufferedVectorImage containing the current text of the label drawn.
	 *
	 * @return a BufferedVectorImage with the current text displayed.
	 */
	private BufferedVectorImage createBufferedImage() {
		String text = this.text;
		Style style = getStyle();
		Font font = style.getFont();
		int width = stringWidthVgFont(text, font);
		int height = font.getHeight();

		BufferedVectorImage image = new BufferedVectorImage(width, height);
		GraphicsContext g = image.getGraphicsContext();

		g.setColor(style.getColor());

		/**
		 * ⚠️ Note: please refer to stringWidthVgFont method
		 */
		width = font.stringWidth(text);

		StringPainter.drawStringInArea(g, text, style.getFont(), 0, 0, width, height, style.getHorizontalAlignment(),
				style.getVerticalAlignment());

		return image;
	}

	/**
	 * This function is a temporary patch introduced to compensate for a missing rounding operation in the existing
	 * stringWidth function. It serves as a workaround to correct measurement caused by the current implementation of
	 * Font.stringWidth.
	 *
	 * ⚠️ Note: This patch is not a permanent solution. It must be reverted as soon as the underlying issue with
	 * stringWidth is properly fixed at its source
	 *
	 * @param font
	 *            the font of the string
	 * @param string
	 *            the string to calculate width
	 *
	 * @return the width of the string plus one
	 */
	private int stringWidthVgFont(String string, Font font) {
		return font.stringWidth(string) + 1;
	}
}
