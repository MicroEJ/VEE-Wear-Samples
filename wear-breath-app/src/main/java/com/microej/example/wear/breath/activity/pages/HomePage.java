/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.pages;

import com.microej.example.wear.breath.activity.BreathDesktop;
import com.microej.example.wear.breath.activity.style.ClassIdentifiers;
import com.microej.example.wear.breath.activity.style.Theme;
import com.microej.example.wear.breath.activity.widget.TimerScrollWidget;
import com.microej.example.wear.breath.activity.widget.VectorCircularProgressBar;
import com.microej.example.wear.breath.activity.widget.VectorImageButton;

import ej.microui.display.Display;
import ej.mwt.util.Alignment;
import ej.widget.basic.OnClickListener;
import ej.widget.container.Canvas;

/**
 * The home page contains timer list and a start button.
 */
public class HomePage extends Canvas {

	/** Timer circular progress constants. */
	private static final float TIMER_PROGRESS_INITIAL = 0.0f;

	/** Timer list constants. */
	private static final short TIMER_MAX_VAL = 6;
	private static final String START_ICON = "/images/icon_start.xml";
	private static final int TIMER_UNIT_SECONDS = 60;
	private static final int TIMER_10_SECONDS = 10;

	private final TimerScrollWidget timerScroll;

	/**
	 * Creates home page by the default time.
	 *
	 * @param index
	 *            the default index of the selected time.
	 */
	public HomePage(int index) {
		// create widgets
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);

		addClassSelector(ClassIdentifiers.DESKTOP_ROOT);

		// Add progress
		int centerX = displayWidth / 2;
		int centerY = displayHeight / 2;

		int size = (int) (displaySize * Theme.TIMER_CIRCULAR_TIME_PROGRESS_RATIO);
		int x = Alignment.computeLeftX(size, centerX, Alignment.HCENTER);
		int y = Alignment.computeTopY(size, centerY, Alignment.VCENTER);

		VectorCircularProgressBar timerProgressBar = new VectorCircularProgressBar(TIMER_PROGRESS_INITIAL,
				Theme.TIMER_CIRCULAR_PROGRESS_BAR_START_ANGLE, Theme.TIMER_CIRCULAR_PROGRESS_BAR_MAX_ANGLE);
		timerProgressBar.addClassSelector(ClassIdentifiers.TIMER_CIRCULAR_PROGRESS);
		addChild(timerProgressBar, x, y, size, size);

		// Add control
		VectorImageButton control = new VectorImageButton(START_ICON, Theme.CONTROL_BUTTON_ICON_SCALE);

		OnClickListener onControlClick = new OnClickListener() {
			@Override
			public void onClick() {
				int timerSelectedValueSeconds;
				int index = timerScroll.getCurrentIndex();
				if (index == (TIMER_MAX_VAL - 1)) {
					timerSelectedValueSeconds = TIMER_10_SECONDS;
				} else {
					timerSelectedValueSeconds = ((TIMER_MAX_VAL - 1) - index) * TIMER_UNIT_SECONDS;
				}

				BreathDesktop desktop = (BreathDesktop) getDesktop();
				desktop.setCurrentTimerIndex(index);
				desktop.switchToBreathingPage(timerSelectedValueSeconds);
			}
		};
		control.setOnClickListener(onControlClick);
		control.addClassSelector(ClassIdentifiers.DESKTOP_CONTROLS);

		// control width is: width of one icon, label and outer margin
		int controlWidth = (int) (Theme.CONTROL_BUTTON_SIDE * displayWidth);
		int controlHeight = (int) (Theme.CONTROL_BUTTON_SIDE * displayHeight);
		int controlX = Alignment.computeLeftX(controlWidth, centerX, Alignment.HCENTER);
		int controlY = (int) (Theme.CONTROL_BUTTON_POS_Y * (displayHeight - controlHeight));
		addChild(control, controlX, controlY, controlWidth, controlHeight);

		// add timers above the button
		int timerListWidth = (int) (Theme.TIMER_LIST_WIDTH * displayWidth);
		int timerListHeight = (int) (Theme.TIMER_LIST_HEIGHT * displayHeight);
		int timerListX = centerX - (timerListWidth / 2);
		int timerListY = (int) (Theme.TIMER_LIST_POS_Y * displayHeight);

		int timerLabelHeight = (int) (Theme.TIMER_LABEL_HEIGHT * displayHeight);
		this.timerScroll = new TimerScrollWidget(index, timerLabelHeight, timerLabelHeight * TIMER_MAX_VAL);
		this.timerScroll.setTopPadding((int) (Theme.TIMER_LIST_TOP_PADDING * displayHeight));
		this.timerScroll.addClassSelector(ClassIdentifiers.TIMER_LIST);
		addChild(this.timerScroll, timerListX, timerListY, timerListWidth, timerListHeight);
	}
}
