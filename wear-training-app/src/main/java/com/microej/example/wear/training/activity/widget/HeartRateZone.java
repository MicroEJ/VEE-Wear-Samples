/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget;

import com.microej.wear.KernelServiceProvider;

import ej.microui.display.Colors;
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
 * Represents a heart rate zone widget. This widget displays different heart rate zones with visual cues, allowing
 * selection and cursor progress tracking.
 */
public class HeartRateZone extends Widget {
	/**
	 * The constant value for the default label text size.
	 */
	public static final int DEFAULT_TEXT_SIZE = 30;

	/**
	 * The constant value for the default widget height.
	 */
	public static final int DEFAULT_WIDGET_HEIGHT = 50;
	/**
	 * The extra field ID for the font.
	 */
	public static final int FONT_STYLE = 0;

	/**
	 * The extra field ID for the text size.
	 */
	public static final int TEXT_SIZE_STYLE = 1;
	/**
	 * The extra field ID for the widget height.
	 */
	public static final int WIDGET_HEIGHT = 2;
	private static final String ZONE = "ZONE ";
	private static final String CURSOR_ICON_PATH = "/images/cursor.svg";
	private static final String SELECTED_ZONE_PATH = "/images/selected_zone.svg";
	private static final String UNSELECTED_ZONE_PATH = "/images/unselected_zone.svg";
	private static final int CURSOR_HEIGHT = 20;
	private static final int AREA_HEIGHT = 70;
	private static final int CURSOR_LEFT_OFFSET = 10;
	private static final int SCALE_CURSOR = 20;
	private static final int X_OFFSET = 10;
	private static final int INTER_PADDING = 2;
	private static final int ZONE_COUNT = 7;
	private static final int HORIZONTAL_MARGIN = 20;
	private static final int SELECTED_ZONE_FACTOR = 3;
	private static final int HUNDRED = 100;

	private static final float[] ZONE_1_COLOR_UNSELECTED_MATRIX = new float[] { 0.086f, 0, 0, 0, 0, // Red
			0, 0.235f, 0, 0, 0, // Green
			0, 0, 0.361f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_2_COLOR_UNSELECTED_MATRIX = new float[] { 0.090f, 0, 0, 0, 0, // Red
			0, 0.337f, 0, 0, 0, // Green
			0, 0, 0.318f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_3_COLOR_UNSELECTED_MATRIX = new float[] { 0.275f, 0, 0, 0, 0, // Red
			0, 0.369f, 0, 0, 0, // Green
			0, 0, 0.0f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_4_COLOR_UNSELECTED_MATRIX = new float[] { 0.369f, 0, 0, 0, 0, // Red
			0, 0.188f, 0, 0, 0, // Green
			0, 0, 0.012f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_5_COLOR_UNSELECTED_MATRIX = new float[] { 0.369f, 0, 0, 0, 0, // Red
			0, 0.020f, 0, 0, 0, // Green
			0, 0, 0.157f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_1_COLOR_SELECTED_MATRIX = new float[] { 0.231f, 0, 0, 0, 0, // Red
			0, 0.643f, 0, 0, 0, // Green
			0, 0, 0.969f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_2_COLOR_SELECTED_MATRIX = new float[] { 0.263f, 0, 0, 0, 0, // Red
			0, 0.941f, 0, 0, 0, // Green
			0, 0, 0.878f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_3_COLOR_SELECTED_MATRIX = new float[] { 0.718f, 0, 0, 0, 0, // Red
			0, 0.984f, 0, 0, 0, // Green
			0, 0, 0.016f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_4_COLOR_SELECTED_MATRIX = new float[] { 1.0f, 0, 0, 0, 0, // Red
			0, 0.475f, 0, 0, 0, // Green
			0, 0, 0.008f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final float[] ZONE_5_COLOR_SELECTED_MATRIX = new float[] { 0.996f, 0, 0, 0, 0, // Red
			0, 0.231f, 0, 0, 0, // Green
			0, 0, 0.196f, 0, 0, // Blue
			0, 0, 0, 1, 0 // Alpha
	};

	private static final int MAX_PROGRESS = 85;
	private int selectedZone = 1;
	private int cursorProgress = 0;

	/**
	 * Constructs a HeartRateZone widget with a specified zone.
	 *
	 * @param zone
	 *            The initial selected heart rate zone.
	 */
	public HeartRateZone(int zone) {
		this.selectedZone = zone;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int allZoneWidth = getWidth() - HeartRateZone.HORIZONTAL_MARGIN;
		size.setWidth(allZoneWidth);
		size.setHeight(HeartRateZone.getWidgetHeight(getStyle()));
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		renderZones(g, this.selectedZone, contentWidth);
	}

	/**
	 * Sets the selected zone.
	 *
	 * @param zone
	 *            The zone to be selected.
	 */
	public void selectZone(int zone) {
		this.selectedZone = zone;
	}

	/**
	 * Sets the cursor progress value, ensuring it stays within valid bounds. The progress value is clamped between 0
	 * and {@code MAX_PROGRESS}.
	 *
	 * @param progress
	 *            The desired progress value.
	 */
	public void setCursorProgress(int progress) {
		progress = Math.max(progress, 0);
		progress = Math.min(progress, HeartRateZone.MAX_PROGRESS);
		this.cursorProgress = progress;
	}

	private void renderZones(GraphicsContext g, int selectedZone, int contentWidth) {
		int allZoneWidth = contentWidth - HeartRateZone.HORIZONTAL_MARGIN;
		int largeWidth = (allZoneWidth / HeartRateZone.ZONE_COUNT) * HeartRateZone.SELECTED_ZONE_FACTOR;
		int smallWidth = (allZoneWidth / HeartRateZone.ZONE_COUNT);

		// Array to store zone positions
		int[] zoneX = new int[HeartRateZone.ZONE_COUNT];
		zoneX[0] = HeartRateZone.X_OFFSET;

		// Compute zone positions
		for (int i = 1; i < HeartRateZone.ZONE_COUNT; i++) {
			int prevWidth = (i == selectedZone) ? largeWidth : smallWidth;
			zoneX[i] = zoneX[i - 1] + prevWidth + HeartRateZone.INTER_PADDING;
		}

		// Draw zones
		for (int i = 0; i < HeartRateZone.ZONE_COUNT - 2; i++) {
			if (i == selectedZone - 1) {
				drawSelectedZone(g, zoneX[i], 0, allZoneWidth, getZoneMatrixColor(i + 1, true));
			} else {
				drawUnselectedZone(g, zoneX[i], 0, getZoneMatrixColor(i + 1, false));
			}
		}
	}

	private float[] getZoneMatrixColor(int zoneNumber, boolean selected) {
		switch (zoneNumber) {
		case 1:
			return selected ? HeartRateZone.ZONE_1_COLOR_SELECTED_MATRIX : HeartRateZone.ZONE_1_COLOR_UNSELECTED_MATRIX;
		case 2:
			return selected ? HeartRateZone.ZONE_2_COLOR_SELECTED_MATRIX : HeartRateZone.ZONE_2_COLOR_UNSELECTED_MATRIX;
		case 3:
			return selected ? HeartRateZone.ZONE_3_COLOR_SELECTED_MATRIX : HeartRateZone.ZONE_3_COLOR_UNSELECTED_MATRIX;
		case 4:
			return selected ? HeartRateZone.ZONE_4_COLOR_SELECTED_MATRIX : HeartRateZone.ZONE_4_COLOR_UNSELECTED_MATRIX;
		default:
		case 5:
			return selected ? HeartRateZone.ZONE_5_COLOR_SELECTED_MATRIX : HeartRateZone.ZONE_5_COLOR_UNSELECTED_MATRIX;
		}
	}

	private void drawSelectedZone(GraphicsContext g, int x, int y, int contentWidth, float[] color) {
		Style style = getStyle();
		VectorFont font = getFont(style);
		int fontSize = HeartRateZone.getFontSize(style);
		int width = (contentWidth / HeartRateZone.ZONE_COUNT) * 3;

		VectorImage imageSelectedZone = VectorImage.getImage(HeartRateZone.SELECTED_ZONE_PATH);
		Matrix sizeMatrixSelectedZone = new Matrix();
		sizeMatrixSelectedZone.setTranslate(x, y);
		VectorGraphicsPainter.drawFilteredImage(g, imageSelectedZone, sizeMatrixSelectedZone, color);

		int textWidth = (int) font.measureStringWidth(HeartRateZone.ZONE + this.selectedZone, fontSize);

		int textHeight = (int) font.getHeight(fontSize);
		int textX = Alignment.computeLeftX(textWidth, 0, width, Alignment.HCENTER) + x;
		int textY = Alignment.computeTopY(textHeight, 0, HeartRateZone.getWidgetHeight(style), Alignment.VCENTER);
		g.setColor(Colors.BLACK);
		VectorGraphicsPainter.drawString(g, HeartRateZone.ZONE + this.selectedZone, font, fontSize, textX, textY);

		g.setColor(Colors.BLACK);

		VectorImage imageCursor = VectorImage.getImage(HeartRateZone.CURSOR_ICON_PATH);

		g.setColor(Colors.BLACK);
		// Derive a new VectorImage
		float[] colorMatrixCursor = new float[] { 1f, 1f, 1f, 1f, 0, // red
				1f, 1f, 1f, 1f, 0, // green
				1f, 1f, 1f, 1f, 0, // blue
				0, 0, 0, 1f, 0, // alpha
		};

		Matrix sizeMatrixCursor = new Matrix();

		int iconCursorY = Alignment.computeTopY(HeartRateZone.CURSOR_HEIGHT, 0, HeartRateZone.AREA_HEIGHT,
				Alignment.BOTTOM) - HeartRateZone.CURSOR_LEFT_OFFSET;

		int cursorXposition = x + ((this.cursorProgress * width) / HeartRateZone.HUNDRED);
		sizeMatrixCursor.setTranslate(cursorXposition, iconCursorY);
		sizeMatrixCursor.preScale(HeartRateZone.SCALE_CURSOR / imageCursor.getWidth(),
				HeartRateZone.SCALE_CURSOR / imageCursor.getHeight());
		VectorGraphicsPainter.drawFilteredImage(g, imageCursor, sizeMatrixCursor, colorMatrixCursor);
	}

	private void drawUnselectedZone(GraphicsContext g, int x, int y, float[] color) {
		VectorImage imageUnSelectedZone = VectorImage.getImage(HeartRateZone.UNSELECTED_ZONE_PATH);
		Matrix sizeMatrixUnSelectedZone = new Matrix();
		sizeMatrixUnSelectedZone.setTranslate(x, y);
		VectorGraphicsPainter.drawFilteredImage(g, imageUnSelectedZone, sizeMatrixUnSelectedZone, color);
	}

	private VectorFont getFont(Style style) {
		return style.getExtraObject(HeartRateZone.FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getBoldItalicFont());
	}

	private static int getFontSize(Style style) {
		return style.getExtraInt(HeartRateZone.TEXT_SIZE_STYLE, HeartRateZone.DEFAULT_TEXT_SIZE);
	}

	private static int getWidgetHeight(Style style) {
		return style.getExtraInt(HeartRateZone.WIDGET_HEIGHT, HeartRateZone.DEFAULT_WIDGET_HEIGHT);
	}
}
