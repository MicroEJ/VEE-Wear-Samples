/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.breath.activity;

import com.microej.example.wear.breath.activity.pages.BreathingBackgroundPage;
import com.microej.example.wear.breath.activity.pages.BreathingPage;
import com.microej.example.wear.breath.activity.pages.HomeBackgroundPage;
import com.microej.example.wear.breath.activity.pages.HomePage;
import com.microej.example.wear.breath.activity.style.ClassIdentifiers;
import com.microej.example.wear.breath.activity.style.Theme;
import com.microej.example.wear.breath.activity.widget.CircleArc;
import com.microej.example.wear.breath.activity.widget.TimerScrollWidget;
import com.microej.example.wear.breath.activity.widget.VectorCircularProgressBar;
import com.microej.example.wear.breath.activity.widget.transition.AnimatedFadeEffect;
import com.microej.example.wear.breath.activity.widget.transition.TransitionContainer;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.util.renderable.RenderableDesktop;

import ej.drawing.ShapePainter;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.dimension.FixedDimension;
import ej.mwt.style.outline.border.RoundedBorder;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.util.Alignment;

/**
 * {@link RenderableDesktop} that renders a breath start menu.
 */
public final class BreathDesktop extends RenderableDesktop {

	private static final int FORWARD_ANIMATION_START_TIMESTAMP = 0;
	private static final int BACKWARD_ANIMATION_START_TIMESTAMP = 11000;
	private static final int TIMER_DEFAULT_INDEX = 3;
	private final TransitionContainer transitionContainer;
	private int currentTimerIndex;

	/**
	 * Creates a desktop that holds the pages of breath.
	 */
	public BreathDesktop() {
		TransitionContainer transition = new TransitionContainer(
				new AnimatedFadeEffect(FORWARD_ANIMATION_START_TIMESTAMP));
		transition.setBackgroundChild(new HomeBackgroundPage());
		transition.setMainChild(new HomePage(TIMER_DEFAULT_INDEX));
		this.transitionContainer = transition;
		this.currentTimerIndex = TIMER_DEFAULT_INDEX;

		setStylesheet(createStylesheet());
		setWidget(this.transitionContainer);
	}

	/**
	 * Changes the transition container main widget to the breathing page.
	 *
	 * @param timeSeconds
	 *            the target time of the breathing progress timer.
	 */
	public void switchToBreathingPage(int timeSeconds) {
		TransitionContainer transition = this.transitionContainer;
		transition.setEffect(new AnimatedFadeEffect(FORWARD_ANIMATION_START_TIMESTAMP));
		transition.setBackgroundChild(new BreathingBackgroundPage());
		transition.setMainChild(new BreathingPage(timeSeconds));
	}

	/**
	 * Changes the transition container main widget to the home page.
	 */
	public void switchToStartPage() {
		TransitionContainer transition = this.transitionContainer;
		transition.setEffect(new AnimatedFadeEffect(BACKWARD_ANIMATION_START_TIMESTAMP));
		transition.setBackgroundChild(new HomeBackgroundPage());
		transition.setMainChild(new HomePage(this.currentTimerIndex));
	}

	/**
	 * Sets the last selected index in the timer scroll.
	 *
	 * @param index
	 *            the last selected index.
	 */
	public void setCurrentTimerIndex(int index) {
		this.currentTimerIndex = index;
	}

	private Stylesheet createStylesheet() {
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();

		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setColor(Colors.WHITE);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DESKTOP_ROOT));
		style.setBackground(NoBackground.NO_BACKGROUND);

		style = stylesheet.getSelectorStyle(new TypeSelector(TransitionContainer.class));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		// Control
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DESKTOP_CONTROLS));
		style.setBorder(new RoundedBorder(Theme.CONTROL_BUTTON_BORDER_COLOR, 90, Theme.CONTROL_BUTTON_BORDER_WIDTH));

		// Timer progress style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TIMER_CIRCULAR_PROGRESS));
		CircleArc.CircleArcBuilder circleArcBuilder = new CircleArc.CircleArcBuilder(
				Theme.TIMER_CIRCULAR_ARC_PROGRESS_START_COLOR, Theme.TIMER_CIRCULAR_ARC_THICKNESS,
				ShapePainter.Cap.PERPENDICULAR);
		style.setExtraObject(VectorCircularProgressBar.CIRCLE_ARC_STYLE, circleArcBuilder);
		circleArcBuilder = new CircleArc.CircleArcBuilder(Theme.TIMER_CIRCULAR_ARC_BACKGROUND_COLOR,
				Theme.TIMER_CIRCULAR_ARC_THICKNESS, ShapePainter.Cap.PERPENDICULAR);
		style.setExtraObject(VectorCircularProgressBar.BACKGROUND_CIRCLE_ARC_STYLE, circleArcBuilder);

		// Timer scroll
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TIMER_LIST));
		style.setExtraInt(TimerScrollWidget.LINEAR_GRADIANT_COLOR_FIELD, Theme.TIMER_LIST_LINEAR_GRADIENT_COLOR);

		// Timer Label
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TIMER_LABEL));
		style.setColor(Theme.TIMER_LABEL_COLOR);
		VectorFont boldItalicFont = KernelServiceProvider.getFontService().getBoldItalicFont();
		style.setFont(boldItalicFont.getFont((int) (Theme.TIMER_LABEL_FONT_SIZE * displayHeight)));
		style.setDimension(new FixedDimension((int) (Theme.TIMER_LABEL_WIDTH * displayWidth),
				(int) (Theme.TIMER_LABEL_HEIGHT * displayHeight)));

		return stylesheet;
	}
}
