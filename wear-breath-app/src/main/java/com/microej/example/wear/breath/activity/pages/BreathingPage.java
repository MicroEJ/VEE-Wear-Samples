/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.pages;

import com.microej.example.wear.breath.activity.BreathDesktop;
import com.microej.example.wear.breath.activity.style.ClassIdentifiers;
import com.microej.example.wear.breath.activity.style.Theme;
import com.microej.example.wear.breath.activity.widget.VectorCircularProgressBar;

import ej.annotation.Nullable;
import ej.bon.Util;
import ej.microui.MicroUI;
import ej.microui.display.Display;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.mwt.animation.Animation;
import ej.mwt.animation.Animator;
import ej.mwt.util.Alignment;
import ej.widget.color.GradientHelper;
import ej.widget.container.Canvas;

/**
 * The breathing page contains timer circular progress bar.
 */
public class BreathingPage extends Canvas {
	/** Timer circular progress constants. */
	private static final float TIMER_PROGRESS_INITIAL = 0.0f;
	private static final float TIMER_MIDDLE_PROGRESS = 0.5f;
	private static final float TIMER_FULL_PROGRESS = 1.0f;
	private static final long TIMER_UNIT_MILLISECONDS = 1000;

	private final long targetTimerMilliseconds;
	private final VectorCircularProgressBar timerProgressBar;

	@Nullable
	private Animation animation;

	/**
	 * Creates breathing page with timer circular progress bar.
	 *
	 * @param timeSeconds
	 *            the target time of the breathing progress timer.
	 */
	public BreathingPage(int timeSeconds) {
		// set enabled to get pointer events
		setEnabled(true);

		this.targetTimerMilliseconds = timeSeconds * TIMER_UNIT_MILLISECONDS;

		addClassSelector(ClassIdentifiers.DESKTOP_ROOT);

		// create the page content
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);

		// Add circular progress bar
		int centerX = displayWidth / 2;
		int centerY = displayHeight / 2;

		int size = (int) (displaySize * Theme.TIMER_CIRCULAR_TIME_PROGRESS_RATIO);
		int x = Alignment.computeLeftX(size, centerX, Alignment.HCENTER);
		int y = Alignment.computeTopY(size, centerY, Alignment.VCENTER);

		this.timerProgressBar = new VectorCircularProgressBar(TIMER_PROGRESS_INITIAL,
				Theme.TIMER_CIRCULAR_PROGRESS_BAR_START_ANGLE, Theme.TIMER_CIRCULAR_PROGRESS_BAR_MAX_ANGLE);
		this.timerProgressBar.addClassSelector(ClassIdentifiers.TIMER_CIRCULAR_PROGRESS);
		addChild(this.timerProgressBar, x, y, size, size);
	}

	private void startAnimation() {
		stopAnimation();

		Animator animator = getDesktop().getAnimator();
		final long startTime = Util.platformTimeMillis();

		Animation animation = new Animation() {
			@Override
			public boolean tick(long platformTimeMillis) {
				long targetTime = BreathingPage.this.targetTimerMilliseconds;
				long elapsedTime = platformTimeMillis - startTime;

				if (elapsedTime < targetTime) {
					float progress = (float) elapsedTime / targetTime;
					int progressColor;
					if (progress < TIMER_MIDDLE_PROGRESS) {
						progressColor = GradientHelper.blendColors(Theme.TIMER_CIRCULAR_ARC_PROGRESS_START_COLOR,
								Theme.TIMER_CIRCULAR_ARC_PROGRESS_MIDDLE_COLOR, progress * 2);
					} else {
						progressColor = GradientHelper.blendColors(Theme.TIMER_CIRCULAR_ARC_PROGRESS_MIDDLE_COLOR,
								Theme.TIMER_CIRCULAR_ARC_PROGRESS_END_COLOR, (progress - TIMER_MIDDLE_PROGRESS) * 2);
					}
					timerProgressBar.setProgress(progress);
					timerProgressBar.updateProgressColor(progressColor);
					timerProgressBar.requestRender();
					return true;
				} else {
					timerProgressBar.setProgress(TIMER_FULL_PROGRESS);
					timerProgressBar.requestRender();

					MicroUI.callSerially(new Runnable() {
						@Override
						public void run() {
							BreathDesktop desktop = (BreathDesktop) getDesktop();
							desktop.switchToStartPage();
						}
					});
					return false;
				}
			}
		};
		this.animation = animation;
		animator.startAnimation(animation);
	}

	private void stopAnimation() {
		Animation animation = this.animation;
		if (animation != null) {
			Animator animator = getDesktop().getAnimator();
			animator.stopAnimation(animation);
			this.animation = null;
		}
	}

	@Override
	protected void onShown() {
		startAnimation();
		super.onShown();
	}

	@Override
	protected void onHidden() {
		stopAnimation();
		super.onHidden();
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Pointer.EVENT_TYPE && Buttons.getAction(event) == Buttons.RELEASED) {
			BreathDesktop desktop = (BreathDesktop) getDesktop();
			desktop.switchToStartPage();
			return true;
		}

		return super.handleEvent(event);
	}
}
