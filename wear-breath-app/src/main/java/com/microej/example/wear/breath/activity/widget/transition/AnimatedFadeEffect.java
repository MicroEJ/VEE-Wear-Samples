/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget.transition;

import ej.microui.display.BufferedImage;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.motion.Motion;
import ej.motion.linear.LinearFunction;
import ej.mwt.animation.Animator;
import ej.mwt.util.Alignment;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * Make the new widget appear with a fade in and an animated image.
 */
public class AnimatedFadeEffect implements TransitionEffect {

	private static final String ANIMATION_PATH = "/images/breath_animation.xml"; //$NON-NLS-1$
	private static final int TRANSITION_DURATION_DEFAULT = 1000;

	private final long startTimestampMilliseconds;
	private int alpha;
	private int elapsed;
	private final VectorImage animatedImage;

	/**
	 * Creates a fade effect on a transition with animation on foreground.
	 *
	 * @param startTimestampMilliseconds
	 *            the start timer in the animation.
	 */
	public AnimatedFadeEffect(int startTimestampMilliseconds) {
		this.startTimestampMilliseconds = startTimestampMilliseconds;
		this.animatedImage = VectorImage.getImage(ANIMATION_PATH);
		this.elapsed = 0;
	}

	@Override
	public void start(Animator animator, int contentWidth, int contentHeight, final TransitionContainer container) {
		final Motion motion = new Motion(LinearFunction.INSTANCE, 0, TRANSITION_DURATION_DEFAULT,
				TRANSITION_DURATION_DEFAULT);
		new MotionAnimation(animator, motion, new MotionAnimationListener() {
			@Override
			public void tick(int value, boolean finished) {
				AnimatedFadeEffect.this.alpha = (value * GraphicsContext.OPAQUE) / TRANSITION_DURATION_DEFAULT;
				AnimatedFadeEffect.this.elapsed = value;

				if (!finished) {
					container.requestRender();
				} else {
					container.onAnimationStopped();
					container.requestLayOut();
				}
			}
		}).start();
	}

	@Override
	public void render(GraphicsContext g, BufferedImage foregroundScreenshot, BufferedImage backgroundScreenshot,
			int contentWidth, int contentHeight) {
		// Force to restore the whole display.
		Painter.writePixel(g, 0, 0);
		Painter.drawImage(g, backgroundScreenshot, 0, 0, GraphicsContext.OPAQUE);
		Painter.drawImage(g, foregroundScreenshot, 0, 0, this.alpha);

		// draw the animated image
		Matrix sizeMatrix = new Matrix();
		int x = Alignment.computeLeftX((int) this.animatedImage.getWidth(), 0, contentWidth, Alignment.HCENTER);
		int y = Alignment.computeTopY((int) this.animatedImage.getHeight(), 0, contentHeight, Alignment.VCENTER);
		sizeMatrix.setTranslate(x, y);
		VectorGraphicsPainter.drawAnimatedImage(g, this.animatedImage, sizeMatrix,
				this.startTimestampMilliseconds + this.elapsed);
	}
}
