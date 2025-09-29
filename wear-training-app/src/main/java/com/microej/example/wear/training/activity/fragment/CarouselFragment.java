/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.fragment;

import com.microej.example.wear.training.activity.style.ClassIdentifiers;
import com.microej.example.wear.training.activity.widget.BulletPagingIndicator;
import com.microej.example.wear.training.activity.widget.SwipeContainer;
import com.microej.example.wear.training.model.Training;

import ej.microui.display.Display;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.dimension.FixedDimension;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.util.Alignment;
import ej.widget.container.Canvas;

/**
 * A fragment that represents a swipeable horizontal carousel containing different items.
 */
public class CarouselFragment extends Canvas {
	private static final int ITEM_HEART_RATE = 37100;
	private static final int ITEM_TRAINING = 37101;
	private static final int SWIPE = 37102;
	private static final int PAGINATION_COLOR = 0xcbd3d7;
	private static final int LIST_ITEM_MARGIN_SIDES = 0;
	private static final float WIDGET_WIDTH_RATIO = 0.9872f;
	private static final int INDICATOR_HEIGHT = 20;
	MetricsFragment metricsFragment;
	HeartRateFragment heartRateFragment;

	/**
	 * Constructs a CarouselFragment with a given training instance. Initializes the UI components upon creation.
	 *
	 * @param training
	 *            The training instance used to track training duration.
	 */
	public CarouselFragment(Training training) {
		this.metricsFragment = new MetricsFragment(training);
		this.metricsFragment.addClassSelector(CarouselFragment.ITEM_TRAINING);
		this.heartRateFragment = new HeartRateFragment(training);
		this.heartRateFragment.addClassSelector(CarouselFragment.ITEM_HEART_RATE);
		buildUI(this);
	}

	/**
	 * Builds and initializes the UI components on the given Canvas.
	 *
	 * @param canvas
	 *            The canvas on which the UI elements are drawn.
	 */
	private void buildUI(Canvas canvas) {
		canvas.setEnabled(true);
		canvas.addClassSelector(ClassIdentifiers.ROOT_WIDGET);
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int indicatorY = displayHeight - CarouselFragment.INDICATOR_HEIGHT * 2;
		int indicatorX = Alignment.computeLeftX(100, 0, displayWidth, Alignment.HCENTER);
		BulletPagingIndicator indicator = getPagingIndicator();
		SwipeContainer swipeContainer = getSwipeContainer(indicator);
		canvas.addChild(swipeContainer, 0, 0, displayWidth, displayHeight);
		canvas.addChild(indicator, indicatorX, indicatorY, 100, CarouselFragment.INDICATOR_HEIGHT);
	}

	/**
	 * Creates and returns a SwipeContainer containing children widgets.
	 *
	 * @return The root {@link SwipeContainer} containing the children widgets.
	 */
	private SwipeContainer getSwipeContainer(BulletPagingIndicator indicator) {
		SwipeContainer swipeContainer = new SwipeContainer(indicator);
		swipeContainer.addChildToContainer(this.metricsFragment);
		swipeContainer.addChildToContainer(this.heartRateFragment);
		swipeContainer.addClassSelector(CarouselFragment.SWIPE);
		return swipeContainer;
	}

	/**
	 * Creates and returns a BulletPagingIndicator.
	 *
	 * @return A new {@link BulletPagingIndicator} counting two items.
	 */
	private BulletPagingIndicator getPagingIndicator() {
		BulletPagingIndicator indicator = new BulletPagingIndicator();
		indicator.setItemsCount(2);
		indicator.setSelectedItem(0, 1.0f);
		indicator.addClassSelector(ClassIdentifiers.CAROUSEL_INDICATOR);
		return indicator;
	}

	/**
	 * Carousel fragment styles.
	 *
	 * @param stylesheet
	 *            the main style sheet instance.
	 */
	public static void appendStyles(CascadingStylesheet stylesheet) {
		Display display = Display.getDisplay();
		int displaySize = Math.min(display.getWidth(), display.getHeight());
		int widgetWidth = (int) (displaySize * CarouselFragment.WIDGET_WIDTH_RATIO);

		EditableStyle style = stylesheet.getSelectorStyle(new ClassSelector(CarouselFragment.ITEM_TRAINING));
		style.setDimension(new FixedDimension(widgetWidth, Widget.NO_CONSTRAINT));
		style.setPadding(new FlexibleOutline(0, CarouselFragment.LIST_ITEM_MARGIN_SIDES, 0, 0));
		style.setHorizontalAlignment(Alignment.HCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(CarouselFragment.ITEM_HEART_RATE));
		style.setDimension(new FixedDimension(widgetWidth, Widget.NO_CONSTRAINT));
		style.setPadding(new FlexibleOutline(0, CarouselFragment.LIST_ITEM_MARGIN_SIDES, 0, 0));
		style.setHorizontalAlignment(Alignment.HCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(CarouselFragment.SWIPE));
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setPadding(new FlexibleOutline(0, 0, 0, 0));

		// Carousel cursor
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.CAROUSEL_INDICATOR));
		style.setColor(CarouselFragment.PAGINATION_COLOR);
		style.setExtraInt(BulletPagingIndicator.CURSOR_SIZE_STYLE, 7);
		style.setPadding(new FlexibleOutline(0, 0, 0, 0));
	}

	private void stopTasks() {
		this.heartRateFragment.stopUpdateImageTask();
		this.heartRateFragment.stopUpdateTask();
		this.metricsFragment.stopUpdateTask();
	}

	private void startTasks() {
		this.heartRateFragment.startUpdateTask();
		this.metricsFragment.startUpdateTask();
		this.heartRateFragment.startUpdateImageTask();
	}

	@Override
	protected void onAttached() {
		super.onAttached();
		startTasks();
	}

	@Override
	protected void onDetached() {
		super.onDetached();
		stopTasks();
	}
}
