/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.navigation.effect;

import com.microej.wear.components.Renderable;

import ej.bon.Util;
import ej.microui.MicroUI;
import ej.microui.display.Display;
import ej.microui.display.Displayable;
import ej.microui.display.GraphicsContext;
import ej.motion.Function;
import ej.motion.Motion;
import ej.mwt.animation.Animation;
import ej.mwt.animation.Animator;

/**
 * {@link Displayable} which shows a transition with a slide effect from the current display content to a given
 * {@link Renderable}.
 */
public class SlideTransitionDisplayable extends Displayable {

	private final Renderable renderable;
	private final TransitionEffect effect;
	private final Animator animator;
	private final Motion motion;

	private int currentPosition;

	/**
	 * Creates a slide transition displayable.
	 *
	 * @param renderable
	 *            the renderable to transition to.
	 * @param effect
	 *            the effect to apply.
	 * @param function
	 *            the motion function of the animation.
	 * @param duration
	 *            the duration of the animation.
	 */
	public SlideTransitionDisplayable(Renderable renderable, TransitionEffect effect, Function function,
			long duration) {
		this.renderable = renderable;
		this.effect = effect;
		this.animator = new Animator();
		this.motion = createMotion(effect, function, duration);
	}

	@Override
	protected void onShown() {
		this.currentPosition = this.motion.getStartValue();

		final long startTime = Util.platformTimeMillis();

		this.animator.startAnimation(new Animation() {
			@Override
			public boolean tick(long platformTimeMillis) {
				return SlideTransitionDisplayable.this.tick(platformTimeMillis - startTime);
			}
		});
	}

	private boolean tick(long elapsedTime) {
		Motion motion = this.motion;
		if (elapsedTime < motion.getDuration()) {
			this.currentPosition = motion.getValue(elapsedTime);
			requestRender();
			return true;
		} else {
			// execute show in separate event to avoid physical display switch, which would hide this displayable
			// immediately, hence stopping the animations and causing Animator to throw an IllegalStateException
			MicroUI.callSerially(new Runnable() {
				@Override
				public void run() {
					SlideTransitionDisplayable.this.renderable.showOnDisplay();
				}
			});
			return false;
		}
	}

	@Override
	protected void onHidden() {
		this.animator.stopAllAnimations();
	}

	@Override
	public boolean handleEvent(int event) {
		return false;
	}

	@Override
	protected void render(GraphicsContext gc) {
		Display display = Display.getDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		int currentPosition = this.currentPosition;
		TransitionEffect effect = this.effect;

		switch (effect) {
		case SLIDE_LEFT:
			renderRenderable(gc, currentPosition, 0, currentPosition, 0, width - currentPosition, height);
			break;
		case SLIDE_RIGHT:
			renderRenderable(gc, currentPosition, 0, 0, 0, currentPosition + width, height);
			break;
		case SLIDE_UP:
			renderRenderable(gc, 0, currentPosition, 0, currentPosition, width, height - currentPosition);
			break;
		case SLIDE_DOWN:
			renderRenderable(gc, 0, currentPosition, 0, 0, width, currentPosition + height);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void renderRenderable(GraphicsContext gc, int x, int y, int clipX, int clipY, int clipWidth,
			int clipHeight) {
		gc.setClip(clipX, clipY, clipWidth, clipHeight);
		gc.setTranslation(x, y);
		this.renderable.render(gc);
	}

	private static Motion createMotion(TransitionEffect effect, Function function, long duration) {
		Display display = Display.getDisplay();
		switch (effect) {
		case SLIDE_LEFT:
			return new Motion(function, display.getWidth(), 0, duration);
		case SLIDE_RIGHT:
			return new Motion(function, -display.getWidth(), 0, duration);
		case SLIDE_UP:
			return new Motion(function, display.getHeight(), 0, duration);
		case SLIDE_DOWN:
			return new Motion(function, -display.getHeight(), 0, duration);
		default:
			throw new IllegalArgumentException();
		}
	}
}