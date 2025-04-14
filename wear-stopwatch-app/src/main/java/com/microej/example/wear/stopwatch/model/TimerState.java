/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.stopwatch.model;

/**
 * The possible states a timer can be in.
 */
public enum TimerState {

	/**
	 * At zero, is not counting. Default state.
	 */
	STOPPED,

	/**
	 * Is counting.
	 */
	RUNNING,

	/**
	 * Is not counting, but the run is still ongoing.
	 */
	PAUSED
}
