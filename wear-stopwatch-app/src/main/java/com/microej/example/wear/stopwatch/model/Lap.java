/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.stopwatch.model;

/**
 * Represents a lap: the total time taken to complete it, the time of the previous lap and the difference between both.
 */
public class Lap {

	private final long time;
	private final long previous;

	/**
	 * Creates a lap.
	 *
	 * @param time
	 *            The total time of the run when the lap was created.
	 * @param previous
	 *            The time of the previous lap.
	 */
	public Lap(long time, long previous) {
		this.time = time;
		this.previous = previous;
	}

	/**
	 * Returns the total run time when the lap was created.
	 *
	 * @return The total run time.
	 */
	public long getTime() {
		return this.time;
	}

	/**
	 * Returns the difference between the current lap time and the previous lap time.
	 *
	 * @return The time difference in milliseconds.
	 */
	public long getDelta() {
		return this.time - this.previous;
	}
}
