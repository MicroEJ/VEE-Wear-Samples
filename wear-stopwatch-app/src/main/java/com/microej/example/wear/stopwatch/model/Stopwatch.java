/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.stopwatch.model;

import ej.basictool.ArrayTools;
import ej.bon.Util;

/**
 * Manages and provides data to be shared between the app components, namely the UI.
 */
public class Stopwatch {

	/** Represents a time amount that is not set yet. */
	public static final long NULL_TIME = 0;

	private static final TimerState DEFAULT_STATE = TimerState.STOPPED;

	private TimerState currentState;
	private long startTime;
	private long elapsedTotal;
	private long cumulativeLaps;
	private Lap[] laps;
	private StopWatchEventListener[] listeners;

	/**
	 * Creates and initialises a {@code Stopwatch}.
	 */
	public Stopwatch() {
		this.currentState = DEFAULT_STATE;
		this.startTime = NULL_TIME;
		this.elapsedTotal = NULL_TIME;
		this.cumulativeLaps = NULL_TIME;
		this.laps = new Lap[0];
		this.listeners = new StopWatchEventListener[0];
	}

	/**
	 * Returns the current {@link TimerState} of the manager.
	 *
	 * @return The current state.
	 */
	public TimerState getCurrentState() {
		return this.currentState;
	}

	/**
	 * Sets the current {@link TimerState} of the manager.
	 *
	 * @param state
	 *            The current state.
	 */
	public void setCurrentState(TimerState state) {
		this.currentState = state;

		for (StopWatchEventListener listener : this.listeners) {
			listener.onStateChanged(state);
		}
	}

	/**
	 * The {@link Lap Laps} added to the manager.
	 *
	 * @return The added laps.
	 */
	public Lap[] getLaps() {
		return this.laps;
	}

	/**
	 * Adds a lap to the manager.
	 * <p>
	 * {@link Lap#getTime() Lap.time} is set to current elapsed time, {@code Lap.previous} is set to the last lap time
	 * added if exists, else null.
	 *
	 * @see Util#platformTimeMillis()
	 */
	public void addLap() {
		// add elapsed time to current time to accommodate for pauses in the stopwatch
		long currentTime = (Util.platformTimeMillis() - this.startTime) + this.elapsedTotal;

		// a lap time corresponds only to time elapsed since last lap
		long lapTime = currentTime - this.cumulativeLaps;

		// get previous lap if exists
		Lap previous = null;
		if (this.laps.length != 0) {
			previous = this.laps[this.laps.length - 1];
		}

		// add new lap
		Lap lap = new Lap(lapTime, (previous == null ? NULL_TIME : previous.getTime()));
		this.laps = ArrayTools.add(this.laps, lap);

		this.cumulativeLaps += lapTime;

		for (StopWatchEventListener listener : this.listeners) {
			listener.onLapAdded(lap);
		}
	}

	/**
	 * Removes all laps added to the manager.
	 */
	public void removeLaps() {
		this.laps = new Lap[] {};
		this.cumulativeLaps = NULL_TIME;
	}

	/**
	 * Returns the starting time of the current timer run.
	 *
	 * @return The run starting time.
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * Saves the starting time of the current run into the manager.
	 *
	 * @param startTime
	 *            The time to save.
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Returns the total time elapsed before the start of the current run.
	 * <p>
	 * Used to offset the measured time if the timer was once paused.
	 *
	 * @return The elapsed time.
	 */
	public long getElapsedTotal() {
		return this.elapsedTotal;
	}

	/**
	 * Saves the total time elapsed in the manager.
	 * <p>
	 * Used when paused, to be able to offset the measured time when the timer will resume and
	 * {@link #setStartTime(long) a new starting time will be set}.
	 *
	 * @param elapsedTotal
	 *            The new elapsed time.
	 */
	public void setElapsedTotal(long elapsedTotal) {
		this.elapsedTotal = elapsedTotal;
	}

	/**
	 * Adds a listener to be notified when an event happens.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(StopWatchEventListener listener) {
		this.listeners = ArrayTools.add(this.listeners, listener);
	}

	/**
	 * Removes the given listener from the list.
	 *
	 * @param listener
	 *            The listener to remove
	 * @see ArrayTools#remove(Object[], Object)
	 */
	public void removeListener(StopWatchEventListener listener) {
		this.listeners = ArrayTools.remove(this.listeners, listener);
	}
}
