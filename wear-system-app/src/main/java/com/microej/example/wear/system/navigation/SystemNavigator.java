/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.navigation;

import java.util.Objects;

import com.microej.example.wear.system.navigation.effect.SlideTransitionDisplayable;
import com.microej.example.wear.system.navigation.effect.TransitionEffect;
import com.microej.example.wear.system.navigation.transition.Transition;
import com.microej.example.wear.system.navigation.transition.TransitionManager;
import com.microej.example.wear.system.ui.launcher.ActivityLauncherDesktop;
import com.microej.example.wear.system.ui.picker.WatchfacePickerDesktop;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;
import com.microej.wear.components.Navigator;
import com.microej.wear.components.Renderable;
import com.microej.wear.components.Watchface;
import com.microej.wear.services.ComponentService;

import ej.annotation.Nullable;
import ej.basictool.ArrayTools;
import ej.bon.IllegalStateException;
import ej.microui.MicroUI;
import ej.microui.display.Display;
import ej.microui.display.Displayable;
import ej.motion.Function;
import ej.motion.quad.QuadEaseOutFunction;

/**
 * Implements the navigator.
 */
public class SystemNavigator implements Navigator {

	private static final NavigationState INITIAL_STATE = NavigationState.WATCHFACE;
	private static final Function SLIDE_FUNCTION = QuadEaseOutFunction.INSTANCE;
	private static final int SLIDE_DURATION = 250;

	private static final SystemNavigator INSTANCE = new SystemNavigator();

	private @Nullable Renderable currentRenderable;
	private @Nullable NavigationState currentState;
	private @Nullable Watchface lastPickedWatchface;

	private SystemNavigator() {
		this.currentRenderable = null;
		this.currentState = null;
		this.lastPickedWatchface = null;
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return the singleton instance.
	 */
	public static SystemNavigator getInstance() {
		return SystemNavigator.INSTANCE;
	}

	@Override
	public boolean handleGesture(Gesture gesture) {
		Objects.requireNonNull(gesture);

		NavigationState currentState = this.currentState;
		if (currentState != null) {
			Transition transition = TransitionManager.resolveTransition(currentState, gesture);
			if (transition != null) {
				NavigationState newState = transition.getNewState();
				showRenderable(createRenderable(newState), newState, transition.getEffect());
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles the display being ready.
	 */
	public void handleDisplayReady() {
		if (this.currentState != null) {
			throw new IllegalStateException();
		}

		showRenderable(createRenderable(INITIAL_STATE), INITIAL_STATE, TransitionEffect.NONE);
	}

	/**
	 * Handles the given activity being launched.
	 *
	 * @param activity
	 *            the launched activity.
	 */
	public void handleActivityLaunched(Activity activity) {
		showRenderable(activity.createRenderable(), NavigationState.ACTIVITY, TransitionEffect.NONE);
	}

	/**
	 * Handles the given watchface being picked.
	 *
	 * @param watchface
	 *            the picked watchface.
	 */
	public void handleWatchfacePicked(Watchface watchface) {
		this.lastPickedWatchface = watchface;
		showRenderable(watchface.createRenderable(), NavigationState.WATCHFACE, TransitionEffect.NONE);
	}

	private void showRenderable(final Renderable renderable, final NavigationState state,
			final TransitionEffect transitionEffect) {
		// first we want to hide the current displayable before detaching the associated renderable
		// then we will call proceedShowRenderable() to detach, attach and show

		Displayable displayable = Display.getDisplay().getDisplayable();
		if (displayable != null) {
			// hide current displayable
			Display.getDisplay().requestHide(displayable);

			// proceed once the hide event is executed
			MicroUI.callSerially(new Runnable() {
				@Override
				public void run() {
					proceedShowRenderable(renderable, state, transitionEffect);
				}
			});
		} else {
			// displayable may be null because the display pump is owned by another Module
			// trigger a display switch that hides any displayable shown by another Module
			MicroUI.callSerially(new Runnable() {
				@Override
				public void run() {
					// do nothing
				}
			});

			// can proceed immediately
			proceedShowRenderable(renderable, state, transitionEffect);
		}
	}

	private void proceedShowRenderable(final Renderable renderable, NavigationState state,
			TransitionEffect transitionEffect) {
		// now that the old displayable has been hidden, we can safely detach the current renderable
		Renderable currentRenderable = this.currentRenderable;
		if (currentRenderable != null) {
			currentRenderable.onDetached();
		}

		// attach new renderable
		renderable.onAttached();

		// update state
		this.currentRenderable = renderable;
		this.currentState = state;

		// show new displayable (immediately or after a transition)
		if (isSlideEffect(transitionEffect)) {
			Display.getDisplay().requestShow(
					new SlideTransitionDisplayable(renderable, transitionEffect, SLIDE_FUNCTION, SLIDE_DURATION));
		} else {
			renderable.showOnDisplay();
		}
	}

	private Watchface getSelectedWatchface() {
		Watchface watchface = this.lastPickedWatchface;
		Watchface[] watchfaces = KernelServiceProvider.getComponentService().getWatchfaces();
		if (watchface == null || !ArrayTools.contains(watchfaces, watchface)) {
			watchface = watchfaces[0];
			assert (watchface != null);
			this.lastPickedWatchface = watchface;
		}
		return watchface;
	}

	private Renderable createRenderable(NavigationState state) {
		ComponentService componentService = KernelServiceProvider.getComponentService();
		switch (state) {
		case WATCHFACE_PICKER:
			return new WatchfacePickerDesktop(componentService.getWatchfaces(), this.lastPickedWatchface);
		case ACTIVITY_LAUNCHER:
			return new ActivityLauncherDesktop(componentService.getActivities());
		case WATCHFACE:
			return getSelectedWatchface().createRenderable();
		default:
			throw new IllegalArgumentException();
		}
	}

	private static boolean isSlideEffect(TransitionEffect transitionEffect) {
		switch (transitionEffect) {
		case SLIDE_LEFT:
		case SLIDE_RIGHT:
		case SLIDE_UP:
		case SLIDE_DOWN:
			return true;
		default:
			return false;
		}
	}
}
