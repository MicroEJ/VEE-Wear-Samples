/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Size;

/**
 * A widget that displays a legend of weather icons for a chart.
 * <p>
 * Each icon in the legend is scaled and positioned based on the provided spacing and start offset. This widget uses
 * vector images to represent different weather conditions.
 */
public class ChartImageLegend extends Widget {

	/** The extra field ID for the y offset. */
	public static final int Y_OFFSET_STYLE = 0;
	private static final float FORECAST_IMAGE_SCALE = 25f;
	private static final int CHART_IMAGE_LEGEND_Y_DEFAULT_OFFSET = 25;
	private static final float INITIAL_SCALE = 3f;
	private final VectorImage[] forecastImages;
	private final float scale;
	private final int legendStartX;
	private final float legendSpacing;

	/**
	 * Constructs the widget with the specified icons and layout settings.
	 *
	 * @param iconPaths
	 *            an array of string paths for the weather icons to be displayed in the legend.
	 * @param legendStartX
	 *            the horizontal starting position (in pixels) of the legend on the chart.
	 * @param legendSpacing
	 *            the spacing (in pixels) between each icon in the legend.
	 */
	public ChartImageLegend(String[] iconPaths, int legendStartX, float legendSpacing) {
		this.legendStartX = legendStartX;
		this.legendSpacing = legendSpacing;
		this.forecastImages = new VectorImage[iconPaths.length];
		for (int i = 0; i < iconPaths.length; i++) {
			String iconPath = iconPaths[i];
			assert (iconPath != null);
			this.forecastImages[i] = VectorImage.getImage(iconPath);
		}
		this.scale = ChartImageLegend.INITIAL_SCALE;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setHeight((int) ChartImageLegend.FORECAST_IMAGE_SCALE);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		g.setColor(style.getColor());
		int baseY = getHeight() - style.getExtraInt(Y_OFFSET_STYLE, CHART_IMAGE_LEGEND_Y_DEFAULT_OFFSET);
		int index = 0;
		for (VectorImage image : this.forecastImages) {
			assert (image != null);
			int x = computeForecastImageX(index);
			Matrix matrix = getForecastImageMatrix(image, x, baseY);
			VectorGraphicsPainter.drawImage(g, image, matrix);
			index++;
		}
	}

	private int computeForecastImageX(int index) {
		int xCenter = getWidth() / 2;
		return (int) (xCenter * (1 - this.scale) + this.legendStartX * this.scale
				+ index * (this.legendSpacing * this.scale) - this.legendSpacing / 2f);
	}

	private Matrix getForecastImageMatrix(VectorImage image, int x, int y) {
		Matrix matrix = new Matrix();
		matrix.setTranslate(x, y);
		matrix.preScale(ChartImageLegend.FORECAST_IMAGE_SCALE / image.getWidth(),
				ChartImageLegend.FORECAST_IMAGE_SCALE / image.getHeight());
		return matrix;
	}
}
