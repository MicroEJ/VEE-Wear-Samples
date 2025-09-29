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
import ej.bon.Util;
import ej.mwt.animation.Animation;
import ej.mwt.animation.Animator;
import ej.widget.basic.Label;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.SimpleDock;

/**
 * A {@link SimpleDock} with time measurement logic.
 */
public class TimerWidget extends SimpleDock implements StopWatchEventListener {

	private static final short REFRESH_INTERVAL = 30;

	private static final char FORMAT_MAIN_SEPARATOR = ':';
	private static final char FORMAT_MILLIS_SEPARATOR = '.';

	private final Animator animator;
	private final Animation count;
	private final Stopwatch stopwatch;
	private final Label hoursLabel;
	private final Label centralLabel;
	private final Label millisLabel;
	private boolean isRunning;
	private long lastMeasuredTime;

	/**
	 * Creates a timer.
	 *
	 * @param animator
	 *            the animator to use for animations.
	 * @param stopwatch
	 *            The data manager to use to fetch data from the model.
	 */
	public TimerWidget(Animator animator, Stopwatch stopwatch) {
		super(LayoutOrientation.HORIZONTAL);
		stopwatch.addListener(this);

		this.isRunning = false;
		this.animator = animator;
		this.stopwatch = stopwatch;
		this.lastMeasuredTime = 0;

		// layout
		this.hoursLabel = new Label();
		this.hoursLabel.addClassSelector(ClassIdentifiers.TIMER_SIDE);
		setFirstChild(this.hoursLabel);
		this.centralLabel = new Label();
		this.centralLabel.addClassSelector(ClassIdentifiers.TIMER_CENTRAL);
		setCenterChild(this.centralLabel);
		this.millisLabel = new Label();
		this.millisLabel.addClassSelector(ClassIdentifiers.TIMER_SIDE);
		setLastChild(this.millisLabel);
		// set up the counting animation
		this.count = new Animation() {
			@Override
			public boolean tick(long platformTimeMillis) {
				if (!TimerWidget.this.isRunning) {
					return false;
				}
				if (platformTimeMillis - TimerWidget.this.lastMeasuredTime < REFRESH_INTERVAL) {
					return true;
				}

				long elapsed = measureTimeElapsed(platformTimeMillis);
				setTime(elapsed);

				requestLayOut();
				return true;
			}
		};
		// show count at 0
		setTime(stopwatch.getElapsedTotal());
	}

	private void setTime(long time) {
		String hours = TimeFormatter.format(time, TimeFormatter.FORMAT_HOURS);
		String central = TimeFormatter.format(time, TimeFormatter.FORMAT_MINUTES | TimeFormatter.FORMAT_SECONDS);
		String millis = TimeFormatter.format(time, TimeFormatter.FORMAT_MILLIS);

		this.hoursLabel.setText(hours + FORMAT_MAIN_SEPARATOR);
		this.centralLabel.setText(central);
		this.millisLabel.setText(FORMAT_MILLIS_SEPARATOR + millis);
	}

	private long measureTimeElapsed(long currentTime) {
		long elapsedTotal = this.stopwatch.getElapsedTotal();
		long startTime = this.stopwatch.getStartTime();

		if (startTime == Stopwatch.NULL_TIME) {
			throw new IllegalStateException("A measure of time was attempted when start time was null (current state: "
					+ this.stopwatch.getCurrentState() + ").");
		}

		this.lastMeasuredTime = currentTime;

		return elapsedTotal + (currentTime - startTime);
	}

	private long measureTimeElapsed() {
		long currentTime = Util.platformTimeMillis();
		return measureTimeElapsed(currentTime);
	}

	/**
	 * Starts the timer.
	 */
	public void start() {
		this.stopwatch.setStartTime(Util.platformTimeMillis());
		resume();
	}

	/**
	 * Resumes the timer.
	 */
	public void resume() {
		this.isRunning = true;

		this.animator.startAnimation(this.count);
	}

	/**
	 * Pauses the timer.
	 */
	public void pause() {
		this.isRunning = false;

		// save elapsed time and reset the clock
		long newElapsedTotal = measureTimeElapsed();
		this.stopwatch.setElapsedTotal(newElapsedTotal);
		this.stopwatch.setStartTime(Stopwatch.NULL_TIME);

		setTime(newElapsedTotal);
	}

	/**
	 * Stops the timer.
	 */
	public void stop() {
		this.isRunning = false;

		this.stopwatch.setStartTime(Stopwatch.NULL_TIME);
		this.stopwatch.setElapsedTotal(Stopwatch.NULL_TIME);

		setTime(Stopwatch.NULL_TIME);
	}

	@Override
	protected void onShown() {
		super.onShown();

		// synchronize on the current state
		if (this.stopwatch.getCurrentState() == TimerState.RUNNING) {
			resume();
		}
	}

	@Override
	protected void onHidden() {
		super.onHidden();

		this.animator.stopAnimation(this.count);
	}

	@Override
	protected void onDetached() {
		super.onDetached();
		this.stopwatch.removeListener(this);
	}

	private void chooseRunningState(TimerState state) {
		switch (state) {
		case RUNNING:
			// resumes if a run was already started
			if (this.stopwatch.getStartTime() == Stopwatch.NULL_TIME) {
				start();
			} else {
				resume();
			}
			break;
		case PAUSED:
			pause();
			break;
		case STOPPED:
			stop();
			break;
		default:
			return;
		}
		requestLayOut();
	}

	@Override
	public void onLapAdded(Lap lap) {
		/* Does nothing */ }

	@Override
	public void onStateChanged(TimerState state) {
		chooseRunningState(state);
	}
}
