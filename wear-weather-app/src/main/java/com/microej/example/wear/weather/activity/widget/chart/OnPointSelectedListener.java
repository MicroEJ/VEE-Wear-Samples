/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.weather.activity.widget.chart;

/**
 * Listener interface for handling point selection events on a chart.
 */
public interface OnPointSelectedListener {

	/**
	 * Called when a point on the graph is selected.
	 *
	 * @param index
	 *            The index of the selected point in the data list.
	 */
	void onPointSelected(int index);
}
