/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity;

import com.microej.example.wear.training.activity.fragment.CarouselFragment;
import com.microej.example.wear.training.activity.fragment.CountDownFragment;
import com.microej.example.wear.training.activity.fragment.HeartRateFragment;
import com.microej.example.wear.training.activity.fragment.MetricsFragment;
import com.microej.example.wear.training.model.Training;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.util.renderable.RenderableDesktop;

import ej.microvg.VectorFont;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;

/**
 * A desktop that shows the countdown for the training app.
 */
public final class TrainingDesktop extends RenderableDesktop {
	private static final String DEFAULT_FONT = "/fonts/BarlowCondensed-Monospace.ttf";
	private static final String SEMI_BOLD_FONT = "/fonts/BarlowCondensed-SemiBold.ttf";
	private final Training training;

	/**
	 * Creates the desktop for the training app.
	 *
	 * @param training
	 *            The data manager to use to fetch data from the model.
	 */
	public TrainingDesktop(Training training) {
		this.training = training;
		CascadingStylesheet stylesheet = new CascadingStylesheet();
		CountDownFragment.appendStyles(stylesheet);
		MetricsFragment.appendStyles(stylesheet);
		HeartRateFragment.appendStyles(stylesheet);
		CarouselFragment.appendStyles(stylesheet);
		setStylesheet(stylesheet);
		showCountDownFragment();
	}

	/**
	 * Creates and displays a new instance of the CountDownFragment. The fragment is initialized with the current
	 * training instance and set as the active widget.
	 */
	public void showCountDownFragment() {
		CountDownFragment currentFragment = new CountDownFragment(TrainingDesktop.this.training);
		setWidget(currentFragment);
	}

	/**
	 * Creates and displays a new instance of the CarouselFragment. The fragment is initialized with the current
	 * training instance and set as the active widget.
	 */
	public void showCarousel() {
		setWidget(new CarouselFragment(TrainingDesktop.this.training));
		requestLayOut();
	}

	/**
	 * Loads and returns the default vector font.
	 *
	 * @return A {@link VectorFont} instance loaded with the default font.
	 */
	public static VectorFont getFont() {
		return VectorFont.loadFont(TrainingDesktop.DEFAULT_FONT);
	}

	/**
	 * Retrieves and returns the bold italic font from the font service.
	 *
	 * @return A {@link VectorFont} instance representing the bold italic font.
	 */
	public static VectorFont getBoldFont() {
		return KernelServiceProvider.getFontService().getBoldItalicFont();
	}

	/**
	 * Loads and returns the semi bold vector font.
	 *
	 * @return A {@link VectorFont} instance representing the semi bold font.
	 */
	public static VectorFont getSemiBoldFont() {
		return VectorFont.loadFont(TrainingDesktop.SEMI_BOLD_FONT);
	}

}
