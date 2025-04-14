/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.fitness.activity;

import com.microej.example.wear.fitness.activity.widget.CircleArc;
import com.microej.example.wear.fitness.activity.widget.VectorCircularProgressBar;
import com.microej.example.wear.fitness.activity.widget.VectorLabel;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.HealthService;
import com.microej.wear.util.renderable.RenderableDesktop;
import ej.annotation.Nullable;
import ej.bon.TimerTask;
import ej.drawing.ShapePainter;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.motion.Motion;
import ej.motion.sine.SineEaseInOutFunction;
import ej.mwt.animation.Animator;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.util.Alignment;
import ej.widget.container.Canvas;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * A desktop that shows the fitness information.
 */
public final class FitnessDesktop extends RenderableDesktop {

	/* Class selectors */
	private static final int ROOT_WIDGET = 0;
	private static final int STEPS_PROGRESS = 1;
	private static final int CALORIES_PROGRESS = 2;
	private static final int TITLE = 3;
	private static final int STEPS_VALUE = 4;
	private static final int CALORIES_VALUE = 5;

	/* Layout constants */
	private static final float TIME_PROGRESS_RATIO = 0.913f;
	private static final float CALORIES_PROGRESS_RATIO = 0.792f;
	private static final float LABEL_X_RATIO = 0.34f;
	private static final float TITLE_STEPS_Y_RATIO = 0.25f;
	private static final float TITLE_CALORIES_Y_RATIO = 0.5f;
	private static final float FONT_SIZE_RATIO = 0.09f;
	private static final float LABEL_SIZE_RATIO = 0.435f;

	private static final float PROGRESS_BAR_START_ANGLE = 52;
	private static final float PROGRESS_BAR_MAX_ANGLE = -284;
	private static final float ARC_THICKNESS = 10f;
	private static final int ARC_BACKGROUND_COLOR = 0x222222;
	private static final int STEPS_GOAL = 10000;
	private static final int CALORIES_GOAL = 2000;
	private static final long ANIMATION_DURATION = 750;
	private static final long UPDATE_PERIOD = 1000;
	private static final String STEPS = "Steps";
	private static final String CALORIES = "Calories";

	private final VectorCircularProgressBar stepsProgressBar;
	private final VectorCircularProgressBar caloriesProgressBar;
	private final VectorLabel stepLabel;
	private final VectorLabel caloriesLabel;
	@Nullable
	private TimerTask task;

	/**
	 * Creates the desktop for the fitness app.
	 */
	public FitnessDesktop() {
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);
		int centerX = displayWidth / 2;
		int centerY = displayHeight / 2;

		Canvas canvas = new Canvas();
		canvas.addClassSelector(ROOT_WIDGET);

		VectorFont font = getFont();
		float fontSize = FONT_SIZE_RATIO * displaySize;
		int fontHeight = (int) font.getHeight(fontSize);
		int labelWidth = (int) (LABEL_SIZE_RATIO * displaySize);
		HealthService healthService = KernelServiceProvider.getHealthService();

		this.stepsProgressBar = addProgressBar(canvas, displaySize, TIME_PROGRESS_RATIO, centerX, centerY);
		this.stepsProgressBar.addClassSelector(STEPS_PROGRESS);

		this.caloriesProgressBar = addProgressBar(canvas, displaySize, CALORIES_PROGRESS_RATIO, centerX, centerY);
		this.caloriesProgressBar.addClassSelector(CALORIES_PROGRESS);

		// steps title
		VectorLabel stepsTitleLabel = new VectorLabel(STEPS);
		stepsTitleLabel.addClassSelector(TITLE);
		int labelX = (int) (LABEL_X_RATIO * displaySize);
		int labelY = (int) (TITLE_STEPS_Y_RATIO * displaySize);
		canvas.addChild(stepsTitleLabel, labelX, labelY, labelWidth, fontHeight);

		// steps value
		String stepsValue = getStepsText(healthService);
		VectorLabel stepsValueLabel = new VectorLabel(stepsValue);
		stepsValueLabel.addClassSelector(STEPS_VALUE);
		labelY += fontHeight;
		canvas.addChild(stepsValueLabel, labelX, labelY, labelWidth, fontHeight);
		this.stepLabel = stepsValueLabel;

		// calories title
		VectorLabel caloriesTitleLabel = new VectorLabel(CALORIES);
		caloriesTitleLabel.addClassSelector(TITLE);
		labelY = (int) (TITLE_CALORIES_Y_RATIO * displaySize);
		canvas.addChild(caloriesTitleLabel, labelX, labelY, labelWidth, fontHeight);

		// calories value
		String caloriesValue = getCaloriesText(healthService);
		VectorLabel caloriesValueLabel = new VectorLabel(caloriesValue);
		caloriesValueLabel.addClassSelector(CALORIES_VALUE);
		labelY += fontHeight;
		canvas.addChild(caloriesValueLabel, labelX, labelY, labelWidth, fontHeight);
		this.caloriesLabel = caloriesValueLabel;

		setStylesheet(createStylesheet());
		setWidget(canvas);
	}

	@Override
	protected void onShown() {
		super.onShown();
		startAnimation();
		startUpdateTask();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		getAnimator().stopAllAnimations();
		stopUpdateTask();
	}

	private void startAnimation() {
		HealthService healthService = KernelServiceProvider.getHealthService();
		SineEaseInOutFunction function = SineEaseInOutFunction.INSTANCE;
		Motion stepsMotion = new Motion(function, 0, healthService.getSteps(), ANIMATION_DURATION);
		Motion caloriesMotion = new Motion(function, 0, healthService.getCalories(), ANIMATION_DURATION);
		Animator animator = getAnimator();
		MotionAnimation stepAnimation = new MotionAnimation(animator, stepsMotion, new MotionAnimationListener() {
			@Override
			public void tick(int value, boolean finished) {
				FitnessDesktop.this.stepsProgressBar.setProgress((float) value / STEPS_GOAL);
				// request render of the largest progress bar only, it will render the enclosing one too
				FitnessDesktop.this.stepsProgressBar.requestRender();
			}
		});
		MotionAnimation caloriesAnimation = new MotionAnimation(animator, caloriesMotion,
				new MotionAnimationListener() {
					@Override
					public void tick(int value, boolean finished) {
						FitnessDesktop.this.caloriesProgressBar.setProgress((float) value / CALORIES_GOAL);
					}
				});

		stepAnimation.start();
		caloriesAnimation.start();
	}

	private void startUpdateTask() {
		stopUpdateTask();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				updateValues();
			}
		};
		this.task = task;
		KernelServiceProvider.getTimer().schedule(task, UPDATE_PERIOD, UPDATE_PERIOD);
	}

	private void stopUpdateTask() {
		TimerTask task = this.task;
		if (task != null) {
			task.cancel();
			this.task = null;
		}
	}

	private void updateValues() {
		HealthService healthService = KernelServiceProvider.getHealthService();
		this.stepsProgressBar.setProgress((float) healthService.getSteps() / STEPS_GOAL);
		this.caloriesProgressBar.setProgress((float) healthService.getCalories() / CALORIES_GOAL);
		this.stepLabel.setText(getStepsText(healthService));
		this.caloriesLabel.setText(getCaloriesText(healthService));
		requestRender();
	}

	private static VectorCircularProgressBar addProgressBar(Canvas canvas, int availableSize, float widgetRatio,
			int centerX, int centerY) {
		int size = (int) (availableSize * widgetRatio);
		int x = Alignment.computeLeftX(size, centerX, Alignment.HCENTER);
		int y = Alignment.computeTopY(size, centerY, Alignment.VCENTER);

		VectorCircularProgressBar progressBar = new VectorCircularProgressBar(0, PROGRESS_BAR_START_ANGLE,
				PROGRESS_BAR_MAX_ANGLE);
		canvas.addChild(progressBar, x, y, size, size);
		return progressBar;
	}

	private static Stylesheet createStylesheet() {
		CascadingStylesheet stylesheet = new CascadingStylesheet();
		VectorFont font = getFont();
		Display display = Display.getDisplay();
		int displaySize = Math.min(display.getWidth(), display.getHeight());
		int fontSize = (int) (FONT_SIZE_RATIO * displaySize);

		// default style
		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);

		// root widget style
		style = stylesheet.getSelectorStyle(new ClassSelector(ROOT_WIDGET));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		// steps progress style
		style = stylesheet.getSelectorStyle(new ClassSelector(STEPS_PROGRESS));
		CircleArc.GradientStyle gradientStyle = new CircleArc.GradientStyle(new int[] { 0xffff0056, 0xffffc800 },
				new float[] { 0.04f, 1 }, -160);
		CircleArc.CircleArcBuilder circleArcBuilder = new CircleArc.CircleArcBuilder(gradientStyle, ARC_THICKNESS,
				ShapePainter.Cap.ROUNDED);
		style.setExtraObject(VectorCircularProgressBar.CIRCLE_ARC_STYLE, circleArcBuilder);
		circleArcBuilder = new CircleArc.CircleArcBuilder(ARC_BACKGROUND_COLOR, ARC_THICKNESS,
				ShapePainter.Cap.ROUNDED);
		style.setExtraObject(VectorCircularProgressBar.BACKGROUND_CIRCLE_ARC_STYLE, circleArcBuilder);

		// calories progress style
		style = stylesheet.getSelectorStyle(new ClassSelector(CALORIES_PROGRESS));
		gradientStyle = new CircleArc.GradientStyle(new int[] { 0xff29a1d8, 0xff29a1d8, 0xffb70079, 0xffff008a },
				new float[] { 0, 0.4f, 0.9f, 1 }, -15);
		circleArcBuilder = new CircleArc.CircleArcBuilder(gradientStyle, ARC_THICKNESS, ShapePainter.Cap.ROUNDED);
		style.setExtraObject(VectorCircularProgressBar.CIRCLE_ARC_STYLE, circleArcBuilder);
		circleArcBuilder = new CircleArc.CircleArcBuilder(ARC_BACKGROUND_COLOR, ARC_THICKNESS,
				ShapePainter.Cap.ROUNDED);
		style.setExtraObject(VectorCircularProgressBar.BACKGROUND_CIRCLE_ARC_STYLE, circleArcBuilder);

		style = stylesheet.getSelectorStyle(new TypeSelector(VectorLabel.class));
		style.setColor(Colors.WHITE);
		style.setExtraObject(VectorLabel.FONT_STYLE, font);
		style.setExtraInt(VectorLabel.TEXT_SIZE_STYLE, fontSize);

		style = stylesheet.getSelectorStyle(new ClassSelector(TITLE));
		style.setColor(Colors.WHITE);

		style = stylesheet.getSelectorStyle(new ClassSelector(STEPS_VALUE));
		style.setColor(0xffffc800);

		style = stylesheet.getSelectorStyle(new ClassSelector(CALORIES_VALUE));
		style.setColor(0xff29a1d8);

		return stylesheet;
	}

	private static String getCaloriesText(HealthService healthService) {
		return healthService.getCalories() + "/" + CALORIES_GOAL;
	}

	private static String getStepsText(HealthService healthService) {
		return healthService.getSteps() + "/" + STEPS_GOAL;
	}

	private static VectorFont getFont() {
		return KernelServiceProvider.getFontService().getBoldItalicFont();
	}
}
