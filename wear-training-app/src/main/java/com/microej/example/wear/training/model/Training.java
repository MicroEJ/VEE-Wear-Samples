/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.model;

/**
 * Manages and provides data to be shared between the app components, namely the UI.
 */
public class Training {

	/**
	 * Represents a time amount that is not set yet.
	 */
	public static final long NULL_TIME = 0;
	private long startTime;
	private long startSteps;

	/**
	 * Creates and initialises a {@code Training}.
	 */
	public Training() {
		this.startTime = Training.NULL_TIME;
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
	 * Saves the starting number of steps for the current run.
	 *
	 * @param startSteps
	 *            The initial step count to be stored.
	 */
	public void setStartSteps(long startSteps) {
		this.startSteps = startSteps;
	}

	/**
	 * Returns the starting number of steps recorded for the current run.
	 *
	 * @return The initial step count.
	 */
	public long getStartSteps() {
		return this.startSteps;
	}
}
