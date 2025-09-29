/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.stopwatch.activity.widget;

import com.microej.example.wear.stopwatch.activity.style.ClassIdentifiers;
import com.microej.example.wear.stopwatch.model.Lap;
import com.microej.example.wear.stopwatch.model.StopWatchEventListener;
import com.microej.example.wear.stopwatch.model.Stopwatch;
import com.microej.example.wear.stopwatch.model.TimerState;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ResourceService;

import ej.mwt.Widget;
import ej.widget.basic.ImageButton;
import ej.widget.basic.OnClickListener;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.List;
import ej.widget.container.SimpleDock;

/**
 * Contains buttons to control a {@link TimerWidget}.
 */
public class TimerControlWidget extends SimpleDock implements StopWatchEventListener {

	private static final String START_ICON = "/images/icon_start.png";
	private static final String PAUSE_ICON = "/images/icon_pause.png";
	private static final String STOP_ICON = "/images/icon_stop.png";
	private static final String STOP_DISABLED_ICON = "/images/icon_stop_disabled.png";
	private static final String LAP_ICON = "/images/icon_lap.png";

	private final Widget stoppedControl;
	private final Widget runningControl;
	private final Widget pausedControl;
	private final Stopwatch stopwatch;

	/**
	 * Creates a control.
	 *
	 * @param stopwatch
	 *            The data manager to use to fetch data from the model.
	 */
	public TimerControlWidget(Stopwatch stopwatch) {
		super(LayoutOrientation.VERTICAL);
		stopwatch.addListener(this);

		this.stopwatch = stopwatch;
		this.stoppedControl = createStoppedControl();
		this.runningControl = createRunningControl();
		this.pausedControl = createPausedControl();

		TimerState currentState = stopwatch.getCurrentState();
		if (!chooseCenterChild(currentState)) {
			throw new IllegalStateException(
					"The current Timer state: " + currentState + ", is not supported by the Timer controls.");
		}
	}

	private Widget createStoppedControl() {
		List control = new List(LayoutOrientation.HORIZONTAL);

		// start button
		OnClickListener onStartClick = new OnClickListener() {
			@Override
			public void onClick() {
				TimerControlWidget.this.stopwatch.setCurrentState(TimerState.RUNNING);
			}
		};
		Widget start = createButton(START_ICON, onStartClick, ClassIdentifiers.CONTROL_BUTTON);

		// greyed out stop button
		OnClickListener onStopDisabledClick = new OnClickListener() {
			@Override
			public void onClick() {
				// do nothing
			}
		};
		Widget stopDisabled = createButton(STOP_DISABLED_ICON, onStopDisabledClick,
				ClassIdentifiers.DISABLED_CONTROL_BUTTON);

		control.addChild(start);
		control.addChild(createMargin());
		control.addChild(stopDisabled);

		return control;
	}

	private Widget createRunningControl() {
		List control = new List(LayoutOrientation.HORIZONTAL);

		// lap button
		OnClickListener onLapClick = new OnClickListener() {
			@Override
			public void onClick() {
				TimerControlWidget.this.stopwatch.addLap();
			}
		};
		Widget lap = createButton(LAP_ICON, onLapClick, ClassIdentifiers.CONTROL_BUTTON);

		// pause button
		OnClickListener onPauseClick = new OnClickListener() {
			@Override
			public void onClick() {
				TimerControlWidget.this.stopwatch.setCurrentState(TimerState.PAUSED);
			}
		};
		Widget pause = createButton(PAUSE_ICON, onPauseClick, ClassIdentifiers.CONTROL_BUTTON);

		control.addChild(pause);
		control.addChild(createMargin());
		control.addChild(lap);

		return control;
	}

	private Widget createPausedControl() {
		List control = new List(LayoutOrientation.HORIZONTAL);

		// stop button
		OnClickListener onStopClick = new OnClickListener() {
			@Override
			public void onClick() {
				TimerControlWidget.this.stopwatch.setCurrentState(TimerState.STOPPED);
			}
		};
		Widget stop = createButton(STOP_ICON, onStopClick, ClassIdentifiers.CONTROL_BUTTON);

		// resume button
		OnClickListener onResumeClick = new OnClickListener() {
			@Override
			public void onClick() {
				TimerControlWidget.this.stopwatch.setCurrentState(TimerState.RUNNING);
			}
		};
		Widget resume = createButton(START_ICON, onResumeClick, ClassIdentifiers.CONTROL_BUTTON);

		control.addChild(resume);
		control.addChild(createMargin());
		control.addChild(stop);

		return control;
	}

	private Widget createMargin() {
		SpacerWidget margin = new SpacerWidget(Widget.NO_CONSTRAINT, LayoutOrientation.HORIZONTAL);
		margin.addClassSelector(ClassIdentifiers.CONTROL_INNER_MARGIN);

		return margin;
	}

	private Widget createButton(String imagePath, OnClickListener onClick, int... selectors) {
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		ImageButton button = new ImageButton(resourceService.getImagePath(imagePath));
		for (int selector : selectors) {
			button.addClassSelector(selector);
		}
		button.setOnClickListener(onClick);

		return button;
	}

	private boolean chooseCenterChild(TimerState state) {
		boolean res = true;
		switch (state) {
		case STOPPED:
			setCenterChild(this.stoppedControl);
			break;
		case RUNNING:
			setCenterChild(this.runningControl);
			break;
		case PAUSED:
			setCenterChild(this.pausedControl);
			break;
		default:
			res = false;
		}
		return res;
	}

	@Override
	public void onLapAdded(Lap lap) {
		/* Does nothing */ }

	@Override
	public void onStateChanged(TimerState state) {
		if (chooseCenterChild(state)) {
			requestLayOut();
		}
	}
}
