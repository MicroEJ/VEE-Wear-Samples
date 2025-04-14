/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget.scroll;

/**
 * Interface for listening to scroll position changes.
 */
public interface ScrollListener {
	/**
	 * Called when the scroll position changes.
	 *
	 * @param position
	 *            The new position after the change.
	 */
	void onPositionChanged(int position);
}
