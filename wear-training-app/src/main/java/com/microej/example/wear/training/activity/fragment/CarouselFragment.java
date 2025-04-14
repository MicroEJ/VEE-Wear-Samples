/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.fragment;

import com.microej.example.wear.training.activity.style.ClassIdentifiers;
import com.microej.example.wear.training.activity.widget.BulletPagingIndicator;
import com.microej.example.wear.training.activity.widget.scroll.Scroll;
import com.microej.example.wear.training.activity.widget.scroll.ScrollListener;
import com.microej.example.wear.training.activity.widget.scroll.ScrollableList;
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
import ej.widget.container.LayoutOrientation;
import ej.widget.swipe.SwipeListener;

/**
 * A fragment that represents a scrollable horizontal carousel containing different items.
 */
public class CarouselFragment extends Canvas implements SwipeListener, ScrollListener {
	private static final int ITEM_HEART_RATE = 37100;
	private static final int ITEM_TRAINING = 37101;
	private static final int SCROLL = 37102;
	private static final int PAGINATION_COLOR = 0xcbd3d7;
	private static final int LIST_ITEM_MARGIN_SIDES = 0;
	private static final float WIDGET_WIDTH_RATIO = 0.9872f;
	private static final int INDICATOR_HEIGHT = 20;
	private final Training training;
	private BulletPagingIndicator indicator;
	private int selectedFragmentIndex;
	MetricsFragment metricsFragment;
	HeartRateFragment heartRateFragment;

	/**
	 * Constructs a CarouselFragment with a given training instance. Initializes the UI components upon creation.
	 *
	 * @param training
	 *            The training instance used to track training duration.
	 */
	public CarouselFragment(Training training) {
		this.training = training;
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
		canvas.addChild(getContentWidget(), 0, 0, displayWidth, displayHeight);
		addIndicator(canvas, displayWidth, displayHeight);
	}

	/**
	 * Creates and returns the main content widget.
	 *
	 * @return The root {@link Widget} containing the children widgets.
	 */
	private Widget getContentWidget() {
		ScrollableList snapList = new ScrollableList(LayoutOrientation.HORIZONTAL, true);
		this.metricsFragment = new MetricsFragment(this.training);
		this.metricsFragment.addClassSelector(CarouselFragment.ITEM_TRAINING);
		this.heartRateFragment = new HeartRateFragment(this.training);
		this.heartRateFragment.addClassSelector(CarouselFragment.ITEM_HEART_RATE);
		snapList.addChild(this.metricsFragment);
		snapList.addChild(this.heartRateFragment);
		Scroll scroll = new Scroll(LayoutOrientation.HORIZONTAL, this, this);
		scroll.addClassSelector(CarouselFragment.SCROLL);
		scroll.setChild(snapList);
		return scroll;
	}

	private void addIndicator(Canvas canvas, int displayWidth, int displayHeight) {
		this.selectedFragmentIndex = 0;
		this.indicator = new BulletPagingIndicator();
		this.indicator.setItemsCount(2);
		this.indicator.setSelectedItem(this.selectedFragmentIndex, 1.0f);
		this.indicator.addClassSelector(ClassIdentifiers.CAROUSEL_INDICATOR);
		int labelY = displayHeight - CarouselFragment.INDICATOR_HEIGHT * 2;
		int labelX = Alignment.computeLeftX(100, 0, displayWidth, Alignment.HCENTER);
		canvas.addChild(this.indicator, labelX, labelY, 100, CarouselFragment.INDICATOR_HEIGHT);
	}

	/**
	 * Caroussel fragment styles.
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

		style = stylesheet.getSelectorStyle(new ClassSelector(CarouselFragment.SCROLL));
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setPadding(new FlexibleOutline(0, 0, 0, 0));

		// Carousel cursor
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.CAROUSEL_INDICATOR));
		style.setColor(CarouselFragment.PAGINATION_COLOR);
		style.setExtraInt(BulletPagingIndicator.CURSOR_SIZE_STYLE, 7);
		style.setPadding(new FlexibleOutline(0, 0, 0, 0));
	}

	@Override
	public void onSwipeStarted() {
		this.heartRateFragment.stopUpdateImageTask();
		stopTasks();
	}

	@Override
	public void onSwipeStopped() {
		this.indicator.setSelectedItem(this.selectedFragmentIndex, 1.0f);
		this.indicator.requestRender();
		this.metricsFragment.updateDuration();
		this.heartRateFragment.updateDuration();
		startTasks();
	}

	@Override
	public void onPositionChanged(int position) {
		int displayWidth = Display.getDisplay().getWidth();
		this.selectedFragmentIndex = Math.round((float) position / (float) displayWidth);
		this.metricsFragment.updateDuration();
		this.heartRateFragment.updateDuration();
		stopTasks();
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
	protected void onDetached() {
		super.onDetached();
		stopTasks();
	}
}
