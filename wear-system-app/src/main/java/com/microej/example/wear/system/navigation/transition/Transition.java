/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.navigation.transition;

import com.microej.example.wear.system.navigation.NavigationState;
import com.microej.example.wear.system.navigation.effect.TransitionEffect;

/**
 * Represents a transition to a {@link NavigationState} with a {@link TransitionEffect}.
 */
public class Transition {

	private final NavigationState newState;
	private final TransitionEffect effect;

	/**
	 * Creates a transition.
	 *
	 * @param newState
	 *            the new state.
	 * @param effect
	 *            the effect.
	 */
	public Transition(NavigationState newState, TransitionEffect effect) {
		this.newState = newState;
		this.effect = effect;
	}

	/**
	 * Returns the new state of this transition.
	 *
	 * @return the new state.
	 */
	public NavigationState getNewState() {
		return this.newState;
	}

	/**
	 * Returns the effect of this transition.
	 *
	 * @return the effect.
	 */
	public TransitionEffect getEffect() {
		return this.effect;
	}
}
