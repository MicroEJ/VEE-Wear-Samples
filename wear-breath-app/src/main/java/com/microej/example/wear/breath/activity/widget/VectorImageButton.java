/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget;

import ej.annotation.Nullable;
import ej.mwt.stylesheet.selector.StateSelector;
import ej.widget.basic.OnClickListener;
import ej.widget.event.ClickEventHandler;
import ej.widget.event.Clickable;

/**
 * A vector image button is a widget that displays a vector image and reacts to click events.
 */
public class VectorImageButton extends VectorImageWidget implements Clickable {

	private final ClickEventHandler eventHandler;
	private boolean pressed;

	/**
	 * Creates a vector image button with the resource path of the vector image to display.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 */
	public VectorImageButton(String imagePath) {
		super(imagePath, true);

		this.eventHandler = new ClickEventHandler(this, this);
		this.pressed = false;
	}

	/**
	 * Creates a vector image button with the resource path of the image to display.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 * @param scale
	 *            the scaling factor for the image.
	 */
	public VectorImageButton(String imagePath, float scale) {
		super(imagePath, true);
		this.setScale(scale);

		this.eventHandler = new ClickEventHandler(this, this);
		this.pressed = false;
	}

	/**
	 * Sets the listener on the click events of this button.
	 *
	 * @param listener
	 *            the listener to set.
	 */
	public void setOnClickListener(@Nullable OnClickListener listener) {
		this.eventHandler.setOnClickListener(listener);
	}

	@Override
	public boolean isInState(int state) {
		return (state == StateSelector.ACTIVE && this.pressed) || super.isInState(state);
	}

	@Override
	public boolean handleEvent(int event) {
		return this.eventHandler.handleEvent(event);
	}

	@Override
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
		updateStyle();
		requestRender();
	}
}
