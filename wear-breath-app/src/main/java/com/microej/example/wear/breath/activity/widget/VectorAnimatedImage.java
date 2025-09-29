/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget;

import ej.annotation.Nullable;
import ej.bon.Util;
import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.animation.Animation;
import ej.mwt.util.Size;

/**
 * A widget that animates the vector image.
 */
public class VectorAnimatedImage extends Widget {

	private final int startTimestampMilliseconds;
	private final int endTimestampMilliseconds;
	private long startTime;
	private long currentTime;

	private final VectorImage animatedImage;

	@Nullable
	private Animation animation;

	/**
	 * Creates a timer scroll widget.
	 *
	 * @param animatedImagePath
	 *            the animated image path
	 * @param startTimestampMilliseconds
	 *            the start timestamp int the animated image
	 * @param endTimestampMilliseconds
	 *            the timestamp before the end of the animated image.
	 */
	public VectorAnimatedImage(String animatedImagePath, int startTimestampMilliseconds, int endTimestampMilliseconds) {
		this.animatedImage = VectorImage.getImage(animatedImagePath);
		this.startTimestampMilliseconds = startTimestampMilliseconds;
		this.endTimestampMilliseconds = endTimestampMilliseconds;
	}

	/**
	 * Returns the width of the image.
	 *
	 * @return The image width.
	 */
	public int getOriginalImageWidth() {
		return (int) this.animatedImage.getWidth();
	}

	/**
	 * Returns the height of the image.
	 *
	 * @return The image height.
	 */
	public int getOriginalImageHeight() {
		return (int) this.animatedImage.getHeight();
	}

	private void startAnimation() {
		stopAnimation();

		Animation animation = new Animation() {

			@Override
			public boolean tick(long currentTimeMillis) {
				VectorAnimatedImage.this.currentTime = currentTimeMillis;
				requestRender();
				return true;
			}
		};
		this.animation = animation;
		this.currentTime = Util.platformTimeMillis();
		this.startTime = Util.platformTimeMillis();
		getDesktop().getAnimator().startAnimation(animation);
	}

	private void stopAnimation() {
		Animation animation = this.animation;
		if (animation != null) {
			getDesktop().getAnimator().stopAnimation(animation);
		}
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Matrix matrix = new Matrix();
		VectorImage image = this.animatedImage;
		long elapsed = (this.currentTime - this.startTime) + this.startTimestampMilliseconds;
		matrix.setScale(contentWidth / image.getWidth(), contentHeight / image.getHeight());
		VectorGraphicsPainter.drawAnimatedImage(g, image, matrix, elapsed);

		if (elapsed > this.endTimestampMilliseconds) {
			this.startTime = this.currentTime;
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		VectorImage image = this.animatedImage;
		size.setSize((int) image.getWidth(), (int) image.getHeight());
	}

	@Override
	protected void onShown() {
		super.onShown();
		startAnimation();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		stopAnimation();
	}
}
