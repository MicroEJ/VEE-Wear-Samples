/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.navigation.transition;

import com.microej.example.wear.system.navigation.NavigationState;
import com.microej.example.wear.system.navigation.effect.TransitionEffect;
import com.microej.wear.components.Navigator.Gesture;

import ej.annotation.Nullable;

/**
 * Provides the transitions between the different states considering performed gestures.
 */
public class TransitionManager {

	private TransitionManager() {
		// private constructor
	}

	/**
	 * Returns the transition to apply when performing the given gesture from the given current state.
	 *
	 * @param currentState
	 *            the current state.
	 * @param gesture
	 *            the gesture.
	 * @return the transition.
	 */
	public static @Nullable Transition resolveTransition(NavigationState currentState, Gesture gesture) {
		if (gesture == Gesture.TOP_BUTTON_PRESS || gesture == Gesture.MIDDLE_BUTTON_PRESS
				|| gesture == Gesture.BOTTOM_BUTTON_PRESS) {
			if (currentState == NavigationState.WATCHFACE) {
				return new Transition(NavigationState.ACTIVITY_LAUNCHER, TransitionEffect.NONE);
			} else {
				return new Transition(NavigationState.WATCHFACE, TransitionEffect.NONE);
			}
		}

		if (currentState == NavigationState.WATCHFACE && gesture == Gesture.POINTER_LONG_PRESS) {
			return new Transition(NavigationState.WATCHFACE_PICKER, TransitionEffect.NONE);
		}

		if (currentState == NavigationState.WATCHFACE && gesture == Gesture.SWIPE_LEFT) {
			return new Transition(NavigationState.ACTIVITY_LAUNCHER, TransitionEffect.NONE);
		}

		if (currentState == NavigationState.ACTIVITY_LAUNCHER && gesture == Gesture.SWIPE_RIGHT) {
			return new Transition(NavigationState.WATCHFACE, TransitionEffect.NONE);
		}

		return null;
	}
}
