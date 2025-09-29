/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

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
 * A widget that displays a text-based legend for a chart.
 */
public class ChartTextLegend extends Widget {

	/** The extra field ID for the font. */
	public static final int FONT_STYLE = 0;

	/** The extra field ID for the text size. */
	public static final int TEXT_SIZE_STYLE = 1;

	/** The extra field ID for the line y offset. */
	public static final int LEGEND_LINE_OFFSET_STYLE = 2;

	private static final String LEGEND_LINE_PATH = "/images/legendline.xml";
	private static final int DEFAULT_LEGEND_FONT_SIZE = 25;
	private static final int DEFAULT_LINE_Y_OFFSET = 7;
	private static final int LEGEND_LINE_EXTRA_SPACING = 2;
	private final String[] legends;
	private static final float INITIAL_SCALE = 3f;
	private final float scale;
	private final int legendStartX;
	private final float legendSpacing;
	private int selectedLegendIndex = 0;

	/**
	 * Constructs a new ChartTextLegend with the specified legend labels, starting X position, and spacing.
	 *
	 * @param legends
	 *            an array of strings representing the legend labels.
	 * @param legendStartX
	 *            the starting X coordinate for the legend.
	 * @param legendSpacing
	 *            the vertical spacing between legend entries.
	 */
	public ChartTextLegend(String[] legends, int legendStartX, float legendSpacing) {
		this.legendStartX = legendStartX;
		this.legendSpacing = legendSpacing;
		this.legends = legends;
		this.scale = ChartTextLegend.INITIAL_SCALE;
	}

	/**
	 * Sets the index of the selected legend.
	 *
	 * @param selectedLegendIndex
	 *            the index to select.
	 */
	public void setSelectedLegendIndex(int selectedLegendIndex) {
		this.selectedLegendIndex = selectedLegendIndex;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();
		int lineOffset = style.getExtraInt(LEGEND_LINE_OFFSET_STYLE, DEFAULT_LINE_Y_OFFSET);
		VectorFont font = ChartTextLegend.getFont(style);
		int fontSize = ChartTextLegend.getFontSize(style);
		size.setHeight((int) font.getHeight(fontSize) + lineOffset);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		drawLegend(g);
	}

	private void drawLegend(GraphicsContext g) {
		Style style = getStyle();
		int lineOffset = style.getExtraInt(LEGEND_LINE_OFFSET_STYLE, DEFAULT_LINE_Y_OFFSET);
		VectorFont font = ChartTextLegend.getFont(style);
		int fontSize = ChartTextLegend.getFontSize(style);
		VectorImage legendLine = VectorImage.getImage(ChartTextLegend.LEGEND_LINE_PATH);
		g.setColor(style.getColor());
		int index = 0;
		for (String legend : this.legends) {
			assert (legend != null);
			float x = computeLegendX(index, legend, font, fontSize);
			VectorGraphicsPainter.drawString(g, legend, font, fontSize, x, 0);
			if (index == this.selectedLegendIndex) {
				int y = fontSize + lineOffset;
				int lineWidth = (int) font.measureStringWidth(legend, fontSize) + LEGEND_LINE_EXTRA_SPACING;
				int areaX = (int) (x - ((float) LEGEND_LINE_EXTRA_SPACING / 2));
				int lineX = Alignment.computeLeftX(lineWidth, areaX, lineWidth, Alignment.HCENTER);
				Matrix matrix = createLineMatrix(legendLine, lineWidth, lineX, y);
				VectorGraphicsPainter.drawImage(g, legendLine, matrix);
			}
			index++;
		}
	}

	private Matrix createLineMatrix(VectorImage image, float scale, int x, int y) {
		Matrix matrix = new Matrix();
		float sx = scale / image.getWidth();
		float sy = image.getHeight() / 2;
		matrix.setTranslate(x, y);
		matrix.preScale(sx, sy);
		return matrix;
	}

	private int computeLegendX(int index, String text, VectorFont font, int fontSize) {
		float stringWidth = font.measureStringWidth(text, fontSize);
		int xCenter = getWidth() / 2;
		return (int) (xCenter * (1 - this.scale) + this.legendStartX * this.scale
				+ index * (this.legendSpacing * this.scale) - stringWidth / 2f);
	}

	private static VectorFont getFont(Style style) {
		return style.getExtraObject(ChartTextLegend.FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static int getFontSize(Style style) {
		return style.getExtraInt(ChartTextLegend.TEXT_SIZE_STYLE, ChartTextLegend.DEFAULT_LEGEND_FONT_SIZE);
	}
}
