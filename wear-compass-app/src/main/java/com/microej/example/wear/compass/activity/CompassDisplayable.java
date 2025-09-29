/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.compass.activity;

import java.util.Random;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ResourceService;
import com.microej.wear.util.renderable.RenderableDisplayable;

import ej.annotation.Nullable;
import ej.drawing.TransformPainter;
import ej.microui.MicroUI;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.microvg.Matrix;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.motion.Motion;
import ej.motion.sine.SineEaseInOutFunction;
import ej.mwt.animation.Animator;
import ej.mwt.util.Alignment;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * Renders the Compass.
 */
public class CompassDisplayable extends RenderableDisplayable implements MotionAnimationListener {

	private static final int NORTH_ANGLE_LOW = 23;
	private static final int NORTH_ANGLE_HIGH = 337;
	private static final int NORTH_EAST_ANGLE = 68;
	private static final int EAST_ANGLE = 115;
	private static final int SOUTH_EAST_ANGLE = 158;
	private static final int SOUTH_ANGLE = 203;
	private static final int SOUTH_WEST_ANGLE = 248;
	private static final int WEST_ANGLE = 291;
	private static final int MULTIPLIER = 100;
	private static final int ANIMATION_DURATION = 5_000;
	private static final float TEXT_HEIGHT_RATIO = 0.15f;
	private static final float ORIENTATION_X_RATIO = 0.47f;
	private static final float ANGLE_X_RATIO = 0.51f;
	private static final float TEXT_BASELINE_Y_RATIO = 0.055f;
	private static final float WHITE_MARKER_SIZE_RATIO = 0.133f;
	private static final float ORANGE_MARKER_SIZE_RATIO = 0.287f;

	private final VectorImage whiteMarker;
	private final VectorImage orangeMarker;
	private final VectorFont font;
	private final int width;
	private final int height;
	private float currentAngle;
	private final Animator animator;
	private final Random random;
	@Nullable
	private MotionAnimation animation;
	@Nullable
	private ResourceImage centerImage;
	@Nullable
	private ResourceImage backgroundImage;

	/**
	 * Creates a Compass displayable.
	 *
	 * @param font
	 *            the font to use
	 * @param animator
	 *            the animator to use
	 */
	public CompassDisplayable(VectorFont font, Animator animator) {
		this.font = font;
		this.orangeMarker = VectorImage.getImage("/images/compass_marker_orange.svg");
		this.whiteMarker = VectorImage.getImage("/images/compass_marker_white.svg");
		Display display = Display.getDisplay();
		this.width = display.getWidth();
		this.height = display.getHeight();
		this.currentAngle = 0f;
		this.animator = animator;
		this.random = new Random();
	}

	@Override
	public void tick(int value, boolean finished) {
		this.currentAngle = (float) value / MULTIPLIER;
		requestRender();
		if (finished) {
			MicroUI.callSerially(new Runnable() {
				@Override
				public void run() {
					startNewAnimation();
				}
			});
		}
	}

	@Override
	public void render(GraphicsContext g) {
		int centerX = this.width / 2;
		int centerY = this.height / 2;
		g.setColor(Colors.BLACK);
		Painter.fillRectangle(g, 0, 0, this.width, this.height);

		ResourceImage tempBackgroundImage = this.backgroundImage;
		assert (tempBackgroundImage != null);
		TransformPainter.drawRotatedImageBilinear(g, tempBackgroundImage, 0, 0, centerX, centerY, this.currentAngle);
		drawCenterImage(g, centerX, centerY);
		drawText(g, centerY);
		drawMarkers(g, centerX, centerY);
	}

	@Override
	public void onAttached() {
		super.onAttached();
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.backgroundImage = ResourceImage.loadImage(resourceService.getImagePath("/images/circle_rotate_black.png"));
		this.centerImage = ResourceImage.loadImage(resourceService.getImagePath("/images/compass-center.png"));
	}

	@Override
	public void onDetached() {
		super.onDetached();

		ResourceImage image = this.backgroundImage;
		if (image != null) {
			image.close();
			this.backgroundImage = null;
		}
		image = this.centerImage;
		if (image != null) {
			image.close();
			this.centerImage = null;
		}
	}

	@Override
	protected void onShown() {
		super.onShown();
		startNewAnimation();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		stopAnimation();
	}

	private void drawCenterImage(GraphicsContext g, int centerX, int centerY) {
		Image image = this.centerImage;
		assert (image != null);
		int x = Alignment.computeLeftX(image.getWidth(), centerX, Alignment.HCENTER);
		int y = Alignment.computeTopY(image.getHeight(), centerY, Alignment.VCENTER);
		Painter.drawImage(g, image, x, y);
	}

	private void drawText(GraphicsContext g, int centerY) {
		int contentSize = Math.min(this.width, this.height);
		float fontSize = contentSize * TEXT_HEIGHT_RATIO;
		VectorFont vectorFont = this.font;
		int angle = (int) this.currentAngle;

		String orientationText = getOrientationText(angle);
		int orientationWidth = (int) vectorFont.measureStringWidth(orientationText, fontSize);
		int orientationX = Alignment.computeLeftX(orientationWidth, (int) (contentSize * ORIENTATION_X_RATIO),
				Alignment.RIGHT);
		float baselineY = centerY + contentSize * TEXT_BASELINE_Y_RATIO;
		float baselinePosition = vectorFont.getBaselinePosition(fontSize);
		float textY = baselineY - baselinePosition;
		g.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawString(g, orientationText, vectorFont, fontSize, orientationX, textY);

		float angleX = contentSize * ANGLE_X_RATIO;
		VectorGraphicsPainter.drawString(g, String.valueOf(angle), vectorFont, fontSize, angleX, textY);
	}

	private void drawMarkers(GraphicsContext g, int centerX, int centerY) {

		int contentSize = Math.min(this.width, this.height);
		Matrix matrix = new Matrix();
		VectorImage marker = this.orangeMarker;
		float scale = contentSize * ORANGE_MARKER_SIZE_RATIO / marker.getHeight();
		matrix.setScale(scale, scale);
		matrix.postTranslate(centerX - marker.getWidth() * scale / 2, 0);
		VectorGraphicsPainter.drawImage(g, marker, matrix);

		ResourceImage image = this.centerImage;
		assert (image != null);
		int radius = image.getWidth() / 2;
		for (int i = 45; i <= 315; i += 90) {
			drawArrow(g, this.whiteMarker, i, radius, contentSize, centerX, centerY);
		}
	}

	private void stopAnimation() {
		MotionAnimation motionAnimation = this.animation;
		if (motionAnimation != null) {
			motionAnimation.stop();
			this.animation = null;
		}
	}

	private void startNewAnimation() {
		stopAnimation();
		// use a multiplier to have a decimal angle with finer precision for smooth animation
		Motion motion = new Motion(SineEaseInOutFunction.INSTANCE, (int) this.currentAngle * MULTIPLIER,
				this.random.nextInt(360) * MULTIPLIER, ANIMATION_DURATION);
		MotionAnimation animation = new MotionAnimation(this.animator, motion, this);
		this.animation = animation;
		animation.start();
	}

	private static String getOrientationText(int angle) {
		String orientation;
		if (angle < NORTH_ANGLE_LOW || angle > NORTH_ANGLE_HIGH) {
			orientation = "N";
		} else if (angle < NORTH_EAST_ANGLE) {
			orientation = "NE";
		} else if (angle < EAST_ANGLE) {
			orientation = "E";
		} else if (angle < SOUTH_EAST_ANGLE) {
			orientation = "SE";
		} else if (angle < SOUTH_ANGLE) {
			orientation = "S";
		} else if (angle < SOUTH_WEST_ANGLE) {
			orientation = "SW";
		} else if (angle < WEST_ANGLE) {
			orientation = "W";
		} else {
			orientation = "NW";
		}
		return orientation;
	}

	private static void drawArrow(GraphicsContext g, VectorImage image, int angle, int radius, int contentSize,
			int centerX, int centerY) {
		float scale = contentSize * WHITE_MARKER_SIZE_RATIO / image.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(angle);
		matrix.preScale(scale, scale);
		matrix.preTranslate(0, -(radius + image.getHeight() * scale));
		matrix.postTranslate(centerX, centerY);
		VectorGraphicsPainter.drawImage(g, image, matrix);
	}
}
