/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.ui.launcher;

import com.microej.wear.components.Activity;

import ej.bon.XMath;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microvg.BlendMode;
import ej.microvg.Matrix;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.util.Alignment;

/**
 * Provides a method that renders an entry of the activity launcher.
 */
public class ActivityLauncherEntry {

	private static final int IMAGE_TEXT_MARGIN = 10;
	private static final int ICON_SIZE = 90;

	private static final float CONTENT_ANIM_START_RATIO = 0.8f; // size ratio at which the content anim starts
	private static final float CONTENT_ANIM_END_RATIO = 1.0f; // size ratio at which the content anim stops
	private static final float MAX_ICON_SIZE_RATIO = 2.0f;

	private final VectorImage backgroundImage;
	private final Activity activity;

	/**
	 * Creates an activity launcher entry.
	 *
	 * @param backgroundImage
	 *            the background image.
	 * @param activity
	 *            the activity.
	 */
	public ActivityLauncherEntry(VectorImage backgroundImage, Activity activity) {
		this.backgroundImage = backgroundImage;
		this.activity = activity;
	}

	/**
	 * Returns the activity of this entry.
	 *
	 * @return the activity.
	 */
	public Activity getActivity() {
		return this.activity;
	}

	/**
	 * Renders this activity launcher entry.
	 *
	 * @param g
	 *            the graphics context to render on.
	 * @param launcherWidth
	 *            the width of the launcher.
	 * @param launcherHeight
	 *            the height of the launcher.
	 * @param font
	 *            the font.
	 * @param fontSize
	 *            the size of the font.
	 * @param sizeRatio
	 *            the size ratio.
	 * @param offsetX
	 *            the x offset.
	 * @param selectedRatio
	 *            the selected animation ratio.
	 */
	public void render(GraphicsContext g, int launcherWidth, int launcherHeight, VectorFont font, float fontSize,
			float sizeRatio, int offsetX, float selectedRatio) {
		VectorImage background = this.backgroundImage;

		if (selectedRatio > 0.0f) {
			sizeRatio = 1.0f + (launcherWidth / background.getWidth() - 1.0f) * selectedRatio;
		}

		// compute transition ratio: 0-1 when size ratio is within start-end
		float transitionRatio = (CONTENT_ANIM_END_RATIO - sizeRatio)
				/ (CONTENT_ANIM_END_RATIO - CONTENT_ANIM_START_RATIO);
		transitionRatio = XMath.limit(transitionRatio, 0.0f, 1.0f);

		// draw background
		int backgroundWidth = (int) (background.getWidth() * sizeRatio);
		int backgroundHeight = (int) (background.getHeight() * sizeRatio);
		int backgroundX = Alignment.computeLeftX(backgroundWidth, offsetX, launcherWidth, Alignment.HCENTER);
		int backgroundY = (int) (launcherHeight * 0.35f * (1.0f - selectedRatio));
		Matrix matrix = new Matrix();
		matrix.setScale(sizeRatio, sizeRatio);
		matrix.postTranslate(backgroundX, backgroundY);
		VectorGraphicsPainter.drawImage(g, background, matrix);

		// draw name
		if (transitionRatio < 1.0f) {
			String name = this.activity.getName();
			int nameWidth = (int) font.measureStringWidth(name, fontSize);
			int nameX = Alignment.computeLeftX(nameWidth, backgroundX, backgroundWidth, Alignment.HCENTER);
			int nameY = backgroundY - IMAGE_TEXT_MARGIN - (int) font.getHeight(fontSize);
			int nameAlpha = (int) (GraphicsContext.OPAQUE * (1.0f - Math.max(transitionRatio, selectedRatio)));
			matrix.setTranslate(nameX, nameY);
			g.setColor(Colors.WHITE);
			VectorGraphicsPainter.drawString(g, name, font, fontSize, matrix, nameAlpha, BlendMode.SRC_OVER, 0.0f);
		}

		// draw icon
		float iconSizeRatio = 1.0f + (1.0f - transitionRatio) * 0.5f; // 1 to 1.5
		iconSizeRatio *= sizeRatio;
		iconSizeRatio = XMath.limit(iconSizeRatio, 0, MAX_ICON_SIZE_RATIO);
		int iconSize = (int) (ICON_SIZE * iconSizeRatio);
		int iconX = Alignment.computeLeftX(iconSize, backgroundX, backgroundWidth, Alignment.HCENTER);
		int iconY = Alignment.computeTopY(iconSize, backgroundY, backgroundHeight, Alignment.VCENTER);
		this.activity.renderIcon(g, iconX, iconY, iconSize);
	}

	/**
	 * Computes the transition ratio for the given size ratio.
	 *
	 * @param sizeRatio
	 *            the size ratio.
	 * @return the transition ratio.
	 */
	public static float computeTransitionRatio(float sizeRatio) {
		float transitionRatio = (CONTENT_ANIM_END_RATIO - sizeRatio)
				/ (CONTENT_ANIM_END_RATIO - CONTENT_ANIM_START_RATIO);
		return XMath.limit(transitionRatio, 0.0f, 1.0f);
	}
}
