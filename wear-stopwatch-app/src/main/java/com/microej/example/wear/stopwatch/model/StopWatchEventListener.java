/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.stopwatch.model;

/**
 * Defines an object that listens to events related to the stopwatch.
 *
 * @see Stopwatch Stopwatch
 */
public interface StopWatchEventListener {

	/**
	 * Handles the addition of a {@link Lap} to the model.
	 *
	 * @param lap
	 *            The lap to handle.
	 */
	void onLapAdded(Lap lap);

	/**
	 * Handles the change of the {@link TimerState} in the model.
	 *
	 * @param state
	 *            The new state of the timer.
	 */
	void onStateChanged(TimerState state);
}
