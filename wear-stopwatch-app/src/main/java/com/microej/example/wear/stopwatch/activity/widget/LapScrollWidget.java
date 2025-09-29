/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.stopwatch.activity.widget;

import com.microej.example.wear.stopwatch.activity.StopwatchActivity;
import com.microej.example.wear.stopwatch.activity.style.ClassIdentifiers;
import com.microej.example.wear.stopwatch.model.Lap;
import com.microej.example.wear.stopwatch.model.StopWatchEventListener;
import com.microej.example.wear.stopwatch.model.Stopwatch;
import com.microej.example.wear.stopwatch.model.TimerState;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ResourceService;

import ej.annotation.Nullable;
import ej.microui.MicroUI;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.mwt.Widget;
import ej.widget.basic.ImageWidget;
import ej.widget.basic.Label;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.SimpleDock;

/**
 * A Widget containing a scrollable list of laps. Is anchored to the bottom and laps are added from top to bottom.
 */
public class LapScrollWidget extends Scroll implements StopWatchEventListener {

	private static final String PLUS_ICON = "/images/icon_lap_plus.png";
	private static final String MINUS_ICON = "/images/icon_lap_minus.png";
	private static final String TOP_GRADIENT = "/images/top_gradient.png";
	private static final String BOTTOM_GRADIENT = "/images/bottom_gradient.png";
	private static final int PADDING_HEIGHT = 100;
	private static final short MAX_LAPS = 5;
	private static final int SWIPE_DURATION = 150;

	private final Stopwatch stopwatch;
	private final ScrollableList list;
	private final SpacerWidget padding;
	@Nullable
	private ResourceImage topGradient;
	@Nullable
	private ResourceImage bottomGradient;

	/**
	 * Creates a lap list widget.
	 *
	 * @param stopwatch
	 *            The data manager to use to fetch data from the model.
	 */
	public LapScrollWidget(Stopwatch stopwatch) {
		super(LayoutOrientation.VERTICAL);

		stopwatch.addListener(this);

		this.stopwatch = stopwatch;
		this.list = new ScrollableList(LayoutOrientation.VERTICAL, false);
		this.padding = new SpacerWidget(PADDING_HEIGHT, LayoutOrientation.VERTICAL);

		showScrollbar(false);
		setChild(this.list);

		showLaps();
	}

	private void showLaps() {
		Lap[] laps = this.stopwatch.getLaps();

		this.list.removeAllChildren();

		// add padding to the top
		this.list.addChild(this.padding);

		// add laps to show
		for (int i = laps.length > MAX_LAPS ? laps.length - MAX_LAPS : 0; i < laps.length; i++) {
			Lap lap = laps[i];
			assert (lap != null);
			this.list.addChild(createLap(lap, i + 1));
		}

		requestLayOut();
		scrollToLast();
	}

	/**
	 * Add a lap to the list.
	 *
	 * @param lap
	 *            The lap to display.
	 */
	private void showLap(Lap lap) {
		int nLaps = this.stopwatch.getLaps().length;

		// remove the oldest lap
		if (nLaps > MAX_LAPS) {
			this.list.removeChild(this.list.getChild(1)); // first child of the list is the padding
		}

		// add the new lap
		this.list.addChild(createLap(lap, nLaps));

		requestLayOut();
		scrollToLast();
	}

	private void scrollToLast() {
		MicroUI.callSerially(new Runnable() {
			@Override
			public void run() {
				// move the list down by the height of a lap and scroll to the last child
				// this will cause a newly added lap to pop up from the bottom and appear smoothly in the view.
				ScrollableList list = LapScrollWidget.this.list;
				int offset = getHeight() - (list.getChildrenCount() > 2 ? list.getChild(1).getHeight() : 0);
				scrollToNoLimit(offset, false);
				scrollToIndex(list.getChildrenCount() - 1, true, SWIPE_DURATION);
			}
		});
	}

	private Widget createLap(Lap lap, int id) {
		SimpleDock lapWidget = new SimpleDock(LayoutOrientation.HORIZONTAL);
		lapWidget.addClassSelector(ClassIdentifiers.LAP);

		String idString = Integer.toString(id);
		Label idLabel = new Label(idString);
		idLabel.addClassSelector(ClassIdentifiers.LAP_ID);
		lapWidget.setFirstChild(idLabel);

		boolean isImprovement = id == 1 || lap.getDelta() < 0;
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		ImageWidget diffIcon = new ImageWidget(resourceService.getImagePath(isImprovement ? PLUS_ICON : MINUS_ICON));
		diffIcon.addClassSelector(ClassIdentifiers.LAP_DIFF);
		lapWidget.setCenterChild(diffIcon);

		String timeString = TimeFormatter.format(lap.getTime());
		Label time = new Label(timeString);
		time.addClassSelector(ClassIdentifiers.LAP_TIME);
		lapWidget.setLastChild(time);

		return lapWidget;
	}

	@Override
	protected void onAttached() {
		super.onAttached();

		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.topGradient = ResourceImage.loadImage(resourceService.getImagePath(TOP_GRADIENT));
		this.bottomGradient = ResourceImage.loadImage(resourceService.getImagePath(BOTTOM_GRADIENT));
	}

	@Override
	protected void onDetached() {
		super.onDetached();

		ResourceImage image = this.topGradient;
		if (image != null) {
			image.close();
			this.topGradient = null;
		}
		image = this.bottomGradient;
		if (image != null) {
			image.close();
			this.bottomGradient = null;
		}

		this.stopwatch.removeListener(this);
	}

	@Override
	public void onStateChanged(TimerState state) {
		if (state == TimerState.STOPPED) {
			this.stopwatch.removeLaps();

			removeAllLaps();
			requestLayOut();
		}
	}

	private void removeAllLaps() {
		this.list.removeAllChildren();
		this.list.addChild(this.padding);
	}

	/**
	 * Assumes the {@link Stopwatch} subscribes first (likely in {@link StopwatchActivity StopwatchActivity})
	 */
	@Override
	public void onLapAdded(Lap lap) {
		showLap(lap);
	}

	@Override
	protected void onShown() {
		super.onShown();
		scrollToLast();
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		int translateX = g.getTranslationX();
		int translateY = g.getTranslationY();
		int x = g.getClipX();
		int y = g.getClipY();
		int width = g.getClipWidth();
		int height = g.getClipHeight();

		super.renderContent(g, contentWidth, contentHeight);

		// reset graphics context's state after parent render (Container.renderContent)
		g.setTranslation(translateX, translateY);
		g.setClip(x, y, width, height);

		// draw gradient on top (repeated to spare space)
		g.setColor(Colors.BLACK);
		ResourceImage image = this.topGradient;
		assert (image != null);
		for (int i = 0; i * image.getWidth() < width; i++) {
			Painter.drawImage(g, image, i * image.getWidth(), 0);
		}

		// draw gradient on the bottom (repeated to spare space)
		image = this.bottomGradient;
		assert (image != null);
		int bgY = contentHeight - image.getHeight();
		for (int i = 0; i * image.getWidth() < width; i++) {
			Painter.drawImage(g, image, i * image.getWidth(), bgY);
		}
	}
}
