/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.sport.watchface;

import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.microvg.Matrix;
import ej.microvg.Path;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.util.Alignment;
import ej.widget.color.GradientHelper;
import ej.widget.render.ImagePainter;

/**
 * A complication that displays some progress with dots.
 */
public class DottedProgressComplication {

	private static final int NB_LEVELS = 15;
	private static final float RADIUS_RATIO = 0.379f;
	private static final float FONT_SIZE_RATIO = 0.263f;
	private static final float DOT_RADIUS_RATIO = 0.0316f;
	private static final float BACK_DOT_SCALE = 1.4f;
	private static final float TANGENT = 0.5522847498307933f; // Magic number to approximate circle arcs from Bezier
	private static final int MIN_COLOR = 0xFF4060;
	private static final int MID_COLOR = 0xFFFF00;
	private static final int MAX_COLOR = 0x40FF60;
	private static final float START_ANGLE = 150.0f;
	private static final float ARC_ANGLE = 240.0f;
	private static final Path DOT_PATH = computeDotPath();

	private DottedProgressComplication() {
		// private constructor
	}

	/**
	 * Renders this complication.
	 * <p>
	 * The graphics context is translated and clipped according to the given bounds. As a consequence, the coordinates
	 * used in this method are relative to the origin of the complication (the top-left corner).
	 *
	 * @param g
	 *            the graphics context
	 * @param x
	 *            the x of the render area
	 * @param y
	 *            the y of the render area
	 * @param width
	 *            the width of the render area
	 * @param height
	 *            the height of the render area
	 * @param progress
	 *            the progress to display
	 * @param background
	 *            the background image
	 * @param icon
	 *            the icon image
	 * @param font
	 *            the font to use for the texts
	 * @param color
	 *            the color to use for the text and icon
	 */
	public static void render(GraphicsContext g, int x, int y, int width, int height, float progress, Image background,
			Image icon, VectorFont font, int color) {
		// render background
		Painter.drawImage(g, background, x, y);

		// render the dotted progress bar
		renderDots(g, progress, x, y, width, height);

		// render icon
		g.setColor(color);
		ImagePainter.drawImageInArea(g, icon, x, y, width, height, Alignment.HCENTER, Alignment.TOP);

		// render value
		float fontSize = width * FONT_SIZE_RATIO;
		String progressString = (int) (progress * 100) + "%";
		int progressWidth = (int) font.measureStringWidth(progressString, fontSize);
		int valueX = x + Alignment.computeLeftX(progressWidth, 0, width, Alignment.HCENTER);
		int valueY = y + Alignment.computeTopY((int) font.getHeight(fontSize), height / 2, Alignment.VCENTER);
		VectorGraphicsPainter.drawString(g, progressString, font, fontSize, valueX, valueY);
	}

	private static void renderDots(GraphicsContext g, float progress, int x, int y, int width, int height) {
		int currentDot = Math.round(progress * NB_LEVELS);
		int centerX = x + width / 2;
		int centerY = y + height / 2;
		int radius = (int) (width * RADIUS_RATIO);
		float scale = DOT_RADIUS_RATIO * width;

		for (int i = 0; i < NB_LEVELS; i++) {
			float angleDeg = START_ANGLE + i * ARC_ANGLE / (NB_LEVELS - 1);
			float angleRad = (float) (angleDeg * Math.PI / 180.0f);
			float cosAngle = (float) Math.cos(angleRad);
			float sinAngle = (float) Math.sin(angleRad);
			float dotX = centerX + cosAngle * radius;
			float dotY = centerY - sinAngle * radius;

			g.setColor(Colors.BLACK);
			renderDot(g, dotX, dotY, scale * BACK_DOT_SCALE);
			if (i < currentDot) {
				g.setColor(getDotColor(i));
				renderDot(g, dotX, dotY, scale);
			}
		}
	}

	private static void renderDot(GraphicsContext g, float x, float y, float scale) {
		Matrix matrix = new Matrix();
		matrix.setTranslate(x, y);
		matrix.preScale(scale, scale);
		VectorGraphicsPainter.fillPath(g, DOT_PATH, matrix);
	}

	private static int getDotColor(int level) {
		int midLevel = NB_LEVELS / 2;
		if (level > midLevel) {
			return GradientHelper.blendColors(MID_COLOR, MAX_COLOR, (float) (level - midLevel) / midLevel);
		} else {
			return GradientHelper.blendColors(MIN_COLOR, MID_COLOR, (float) level / midLevel);
		}
	}

	private static Path computeDotPath() {
		Path path = new Path();
		float radius = 1f;
		float tangent = TANGENT * radius;
		path.moveTo(0, -radius);
		path.cubicToRelative(tangent, 0, radius, radius - tangent, radius, radius);
		path.cubicToRelative(0, tangent, -radius + tangent, radius, -radius, radius);
		path.cubicToRelative(-tangent, 0, -radius, -radius + tangent, -radius, -radius);
		path.cubicToRelative(0, -tangent, radius - tangent, -radius, radius, -radius);
		return path;
	}
}
