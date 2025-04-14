/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.fragment;

import com.microej.example.wear.training.activity.TrainingDesktop;
import com.microej.example.wear.training.activity.style.ClassIdentifiers;
import com.microej.example.wear.training.activity.widget.CircleArc;
import com.microej.example.wear.training.activity.widget.VectorLabel;
import com.microej.example.wear.training.activity.widget.VectorRadialProgressWidget;
import com.microej.example.wear.training.model.Training;
import com.microej.wear.KernelServiceProvider;

import ej.bon.Util;
import ej.drawing.ShapePainter;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.microvg.VectorFont;
import ej.motion.Motion;
import ej.motion.linear.LinearFunction;
import ej.mwt.animation.Animator;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.util.Alignment;
import ej.widget.container.Canvas;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * The CountDownFragment class extends Canvas and is responsible for building the UI components related to countdown
 * before training starts.
 */
public class CountDownFragment extends Canvas {

	/* Layout constants */
	private static final float COUNTDOWN_PROGRESS_RATIO = 0.913f;
	private static final float FONT_SIZE_RATIO = 0.5f;

	/* Radial progress constants */
	private static final float ARC_THICKNESS_RATIO = 0.455f;
	private static final int ARC_BACKGROUND_COLOR = 0x222222;
	private static final int[] RADIAL_PROGRESS_COLORS = new int[] { 0xff00ff56, 0xffc8ff00 };
	private static final float[] RADIAL_GRADIENT_STOPS = new float[] { 0.04f, 1 };
	private static final CircleArc.GradientStyle RADIAL_PROGRESS_GRADIENT = new CircleArc.GradientStyle(
			CountDownFragment.RADIAL_PROGRESS_COLORS, CountDownFragment.RADIAL_GRADIENT_STOPS, -60);

	/* Countdown constants */
	private static final int STARTING_CYCLE_VALUE = 0;
	private static final int CYCLE_DURATION = 1_000;
	private static final int COUNTDOWN_STARTING_VALUE = 3;
	private static final int COUNTDOWN_ENDING_VALUE = 1;
	private static final float COUNTDOWN_LABEL_Y_RATIO = 0.3f;
	private static final String COUNTDOWN_PATTERN_STRING = "10";

	/* Countdown fields */
	private VectorLabel countDownLabel;
	private VectorRadialProgressWidget radialProgressWidget;
	private int countDownValue;
	private boolean countdownEnded;
	private final Training training;
	private MotionAnimation countDownAnimation;

	/**
	 * Constructs a CountDownFragment with a given training instance. Initializes the UI components upon creation.
	 *
	 * @param training
	 *            The training instance used to track training duration.
	 */
	public CountDownFragment(Training training) {
		this.training = training;
		this.countdownEnded = false;
		this.countDownValue = CountDownFragment.COUNTDOWN_STARTING_VALUE;
		buildUI(this);
	}

	/**
	 * Builds and initializes the UI components on the given Canvas.
	 *
	 * @param canvas
	 *            The canvas on which the UI elements are drawn.
	 */
	private void buildUI(Canvas canvas) {

		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);
		int centerX = displayWidth / 2;
		int centerY = displayHeight / 2;

		canvas.setEnabled(true);
		canvas.addClassSelector(ClassIdentifiers.ROOT_WIDGET);

		VectorFont font = TrainingDesktop.getFont();
		float fontSize = CountDownFragment.FONT_SIZE_RATIO * displaySize;
		int fontHeight = (int) font.getHeight(fontSize);
		int labelWidth = (int) font.measureStringWidth(CountDownFragment.COUNTDOWN_PATTERN_STRING, fontSize);

		this.radialProgressWidget = CountDownFragment.addRadialProgressWidget(canvas, displaySize,
				CountDownFragment.COUNTDOWN_PROGRESS_RATIO, centerX, centerY);
		this.radialProgressWidget.addClassSelector(ClassIdentifiers.RADIAL_PROGRESS);

		// countdown value
		VectorLabel countdownValueLabel = new VectorLabel(String.valueOf(this.countDownValue));
		countdownValueLabel.addClassSelector(ClassIdentifiers.COUNTDOWN_VALUE);
		int countDownY = (int) (CountDownFragment.COUNTDOWN_LABEL_Y_RATIO * displaySize);
		int countDownX = Alignment.computeLeftX(labelWidth, 0, displaySize, Alignment.HCENTER);

		canvas.addChild(countdownValueLabel, countDownX, countDownY, labelWidth, fontHeight);
		this.countDownLabel = countdownValueLabel;
	}

	private static VectorRadialProgressWidget addRadialProgressWidget(Canvas canvas, int availableSize,
			float widgetRatio, int centerX, int centerY) {
		int size = (int) (availableSize * widgetRatio);
		int x = Alignment.computeLeftX(size, centerX, Alignment.HCENTER);
		int y = Alignment.computeTopY(size, centerY, Alignment.VCENTER);

		VectorRadialProgressWidget progressBar = new VectorRadialProgressWidget(CountDownFragment.CYCLE_DURATION);
		canvas.addChild(progressBar, x, y, size, size);

		return progressBar;
	}

	@Override
	public void onShown() {
		startAnimation();
		this.countDownLabel.setText(String.valueOf(this.countDownValue));
		this.countDownLabel.requestRender();
	}

	@Override
	public void onHidden() {
		stopCountDownAnimation();
	}

	@Override
	public boolean handleEvent(int event) {
		int type = Event.getType(event);
		if (type == Pointer.EVENT_TYPE) {
			int action = Buttons.getAction(event);
			if (action == Buttons.RELEASED) {
				showNextPage();
				return true;
			}
		}
		return super.handleEvent(event);
	}

	private void showNextPage() {
		this.training.setStartTime(Util.platformTimeMillis());
		this.training.setStartSteps(KernelServiceProvider.getHealthService().getSteps());
		((TrainingDesktop) getDesktop()).showCarousel();
	}

	private void updateTime(int elapsed, boolean finished) {
		this.radialProgressWidget.updateValue(elapsed);
		this.radialProgressWidget.requestRender();
		if (finished) {
			this.countDownValue--;
			if (this.countDownValue < CountDownFragment.COUNTDOWN_ENDING_VALUE) {
				this.countdownEnded = true;
				showNextPage();
				return;
			}
			this.countDownLabel.setText(String.valueOf(this.countDownValue));
			startAnimation();
		}
	}

	private void stopCountDownAnimation() {
		if (!this.countdownEnded) {
			this.countDownAnimation.stop();
		}
	}

	private void startAnimation() {
		LinearFunction function = LinearFunction.INSTANCE;
		Motion timeMotion = new Motion(function, CountDownFragment.STARTING_CYCLE_VALUE,
				CountDownFragment.CYCLE_DURATION, CountDownFragment.CYCLE_DURATION);
		Animator animator = getDesktop().getAnimator();
		this.countDownAnimation = new MotionAnimation(animator, timeMotion, new MotionAnimationListener() {
			@Override
			public void tick(int value, boolean finished) {
				updateTime(value, finished);
				CountDownFragment.this.requestRender();
			}
		});
		this.countDownAnimation.start();
	}

	/**
	 * Countdown fragment styles.
	 *
	 * @param stylesheet
	 *            the main style sheet instance.
	 */
	public static void appendStyles(CascadingStylesheet stylesheet) {

		VectorFont font = TrainingDesktop.getFont();
		Display display = Display.getDisplay();
		int displaySize = Math.min(display.getWidth(), display.getHeight());
		int fontSize = (int) (CountDownFragment.FONT_SIZE_RATIO * displaySize);
		int arcThicknessSize = (int) (displaySize * CountDownFragment.ARC_THICKNESS_RATIO);

		// default style
		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);

		// root widget style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.ROOT_WIDGET));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		// Radial progress style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.RADIAL_PROGRESS));
		CircleArc.CircleArcBuilder circleArcBuilder = new CircleArc.CircleArcBuilder(
				CountDownFragment.RADIAL_PROGRESS_GRADIENT, arcThicknessSize, ShapePainter.Cap.PERPENDICULAR);
		style.setExtraObject(VectorRadialProgressWidget.CIRCLE_ARC_STYLE, circleArcBuilder);
		circleArcBuilder = new CircleArc.CircleArcBuilder(CountDownFragment.ARC_BACKGROUND_COLOR, arcThicknessSize,
				ShapePainter.Cap.PERPENDICULAR);
		style.setExtraObject(VectorRadialProgressWidget.BACKGROUND_CIRCLE_ARC_STYLE, circleArcBuilder);

		// Countdown label style

		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.COUNTDOWN_VALUE));
		style.setExtraObject(VectorLabel.FONT_STYLE, font);
		style.setExtraInt(VectorLabel.TEXT_SIZE_STYLE, fontSize);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setColor(Colors.WHITE);

	}

}
