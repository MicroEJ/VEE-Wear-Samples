/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.fragment;

import com.microej.example.wear.training.activity.TrainingDesktop;
import com.microej.example.wear.training.activity.style.ClassIdentifiers;
import com.microej.example.wear.training.activity.widget.CircleArc;
import com.microej.example.wear.training.activity.widget.TimeFormatter;
import com.microej.example.wear.training.activity.widget.VectorCircularProgressBar;
import com.microej.example.wear.training.activity.widget.VectorDurationLabel;
import com.microej.example.wear.training.activity.widget.VectorLabeledValue;
import com.microej.example.wear.training.activity.widget.VerticalDivider;
import com.microej.example.wear.training.model.Training;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.HealthService;

import ej.annotation.Nullable;
import ej.bon.TimerTask;
import ej.bon.Util;
import ej.drawing.ShapePainter;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.util.Alignment;
import ej.widget.basic.Label;
import ej.widget.container.Canvas;

/**
 * The MetricsFragment class extends Canvas and is responsible for building the UI components related to training
 * metrics such as heart rate, speed, distance, duration, and time.
 */
public class MetricsFragment extends Canvas {

	/* Layout constants */
	private static final float HEART_RATE_PROGRESS_RATIO = 0.913f;
	private static final float WIDGET_Y_RATIO = 0.22f;
	private static final float VALUE_FONT_SIZE_RATIO = 0.15f;
	private static final float TIME_LABEL_Y_RATIO = 0.09f;
	private static final float TIME_VALUE_FONT_SIZE_RATIO = 0.08f;
	private static final float LABEL_FONT_SIZE_RATIO = 0.05f;
	private static final int TEXT_GAP = 5;
	private static final int HEART_RATE_WIDGET_PADDING_TOP = 20;
	private static final int WIDGET_VERTICAL_INTER_MARGIN = 12;
	private static final int WIDGET_HORIZONTAL_MARGIN = 80;
	private static final int TIME_COLOR = 0xb3b3b3;
	private static final float PROGRESS_BAR_START_ANGLE = -140;
	private static final float PROGRESS_BAR_MAX_ANGLE = -260;
	private static final float ARC_THICKNESS = 10f;
	private static final int ARC_BACKGROUND_COLOR = 0x222222;
	private static final long UPDATE_PERIOD = 1000;
	private static final int HEART_RATE_GOAL = 240;
	private static final String HEART_RATE_LABEL = "Heart rate (bpm)";
	private static final String HEART_RATE_VALUE_PATTERN = "000";
	private static final String SPEED_LABEL = "Avg Speed (km/h)";
	private static final String SPEED_DEFAULT_VALUE = "0.0";
	private static final String SPEED_PATTERN = "00.00";
	private static final int DIVIDER_LAYOUT_WIDTH = 20;
	private static final int DIVIDER_MARGIN_TOP = 15;
	private static final String DISTANCE_LABEL = "Distance (km)";
	private static final String DISTANCE_PATTERN = "00.00";
	private static final String DURATION_LABEL = "Duration";
	private static final String DURATION_PATTERN = "00:00:00";
	private static final String TIME_PATTERN = "00:00";
	private static final float STEPS_LENGTH = 0.726f;
	private final Training training;
	// Widgets
	private final VectorLabeledValue heartRateWidget;
	private final VectorLabeledValue speedWidget;
	private final VectorLabeledValue distanceWidget;
	private final VectorDurationLabel durationWidget;
	private final Label timeWidget;
	private VectorCircularProgressBar heartRateProgressBar;
	@Nullable
	private TimerTask updateTask;
	private float latestAvgSpeed;
	private int refreshSpeedCounter;

	/**
	 * Constructs a MetricsFragment with a given training instance. Initializes the UI components upon creation.
	 *
	 * @param training
	 *            The training instance used to track training duration.
	 */
	public MetricsFragment(Training training) {
		this.training = training;
		this.latestAvgSpeed = 0.0f;
		this.refreshSpeedCounter = 0;

		this.heartRateWidget = new VectorLabeledValue(MetricsFragment.HEART_RATE_VALUE_PATTERN,
				String.valueOf(KernelServiceProvider.getHealthService().getHeartRate()),
				MetricsFragment.HEART_RATE_LABEL);

		this.speedWidget = new VectorLabeledValue(MetricsFragment.SPEED_PATTERN, MetricsFragment.SPEED_DEFAULT_VALUE,
				MetricsFragment.SPEED_LABEL);

		this.distanceWidget = new VectorLabeledValue(MetricsFragment.SPEED_PATTERN, MetricsFragment.SPEED_DEFAULT_VALUE,
				MetricsFragment.DISTANCE_LABEL);

		this.durationWidget = new VectorDurationLabel(this.training.getStartTime());
		this.durationWidget.addClassSelector(ClassIdentifiers.DURATION_WIDGET);

		this.timeWidget = new Label(TimeFormatter.getCurrentTimeFormatted());
		this.timeWidget.addClassSelector(ClassIdentifiers.TIME_WIDGET);

		this.heartRateProgressBar = new VectorCircularProgressBar(
				(float) KernelServiceProvider.getHealthService().getHeartRate() / MetricsFragment.HEART_RATE_GOAL,
				MetricsFragment.PROGRESS_BAR_START_ANGLE, MetricsFragment.PROGRESS_BAR_MAX_ANGLE);

		buildUI(this);
	}

	private static VectorCircularProgressBar addProgressBar(float initialValue, Canvas canvas, int availableSize,
			int centerX, int centerY) {
		int size = (int) (availableSize * MetricsFragment.HEART_RATE_PROGRESS_RATIO);
		int x = Alignment.computeLeftX(size, centerX, Alignment.HCENTER);
		int y = Alignment.computeTopY(size, centerY, Alignment.VCENTER);
		VectorCircularProgressBar progressBar = new VectorCircularProgressBar(initialValue,
				MetricsFragment.PROGRESS_BAR_START_ANGLE, MetricsFragment.PROGRESS_BAR_MAX_ANGLE);
		canvas.addChild(progressBar, x, y, size, size);
		return progressBar;
	}

	private static VectorFont getLabelFont() {
		return KernelServiceProvider.getFontService().getRegularFont();
	}

	/**
	 * Training fragment styles.
	 *
	 * @param stylesheet
	 *            the main style sheet instance.
	 */
	public static void appendStyles(CascadingStylesheet stylesheet) {

		VectorFont valueFont = TrainingDesktop.getFont();
		VectorFont labelFont = MetricsFragment.getLabelFont();
		Display display = Display.getDisplay();
		int displaySize = Math.min(display.getWidth(), display.getHeight());
		int valueFontSize = (int) (MetricsFragment.VALUE_FONT_SIZE_RATIO * displaySize);
		int timeFontSize = (int) (MetricsFragment.TIME_VALUE_FONT_SIZE_RATIO * displaySize);
		int labelFontSize = (int) (MetricsFragment.LABEL_FONT_SIZE_RATIO * displaySize);

		// default style
		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);

		// root widget style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TRAINING_ROOT_WIDGET));
		style.setBackground(NoBackground.NO_BACKGROUND);

		style = stylesheet.getSelectorStyle(new TypeSelector(VectorLabeledValue.class));
		style.setColor(Colors.WHITE);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setExtraObject(VectorLabeledValue.VALUE_FONT_STYLE, valueFont);
		style.setExtraInt(VectorLabeledValue.VALUE_SIZE_STYLE, valueFontSize);
		style.setExtraObject(VectorLabeledValue.LABEL_FONT_STYLE, labelFont);
		style.setExtraInt(VectorLabeledValue.LABEL_SIZE_STYLE, labelFontSize);
		style.setExtraInt(VectorLabeledValue.LABEL_COLOR_STYLE, VectorLabeledValue.DEFAULT_LABEL_COLOR);
		style.setMargin(new FlexibleOutline(0, 0, 20, 0));

		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DURATION_WIDGET));
		style.setColor(Colors.WHITE);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setExtraObject(VectorDurationLabel.VALUE_FONT_STYLE, valueFont);
		style.setExtraInt(VectorDurationLabel.VALUE_SIZE_STYLE, valueFontSize);
		style.setExtraObject(VectorDurationLabel.LABEL_FONT_STYLE, labelFont);
		style.setExtraInt(VectorDurationLabel.LABEL_SIZE_STYLE, labelFontSize);
		style.setExtraInt(VectorDurationLabel.LABEL_COLOR_STYLE, VectorDurationLabel.DEFAULT_LABEL_COLOR);

		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TIME_WIDGET));
		style.setColor(MetricsFragment.TIME_COLOR);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setFont(TrainingDesktop.getSemiBoldFont().getFont(timeFontSize));

		// steps progress style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.HEART_RATE_PROGRESS));
		CircleArc.GradientStyle gradientStyle = new CircleArc.GradientStyle(
				new int[] { 0xff66ff33, 0xffffff00, 0xffff3300 }, new float[] { 0.04f, 0.4f, 1 }, -160);
		CircleArc.CircleArcBuilder circleArcBuilder = new CircleArc.CircleArcBuilder(gradientStyle,
				MetricsFragment.ARC_THICKNESS, ShapePainter.Cap.ROUNDED);
		style.setExtraObject(VectorCircularProgressBar.CIRCLE_ARC_STYLE, circleArcBuilder);
		circleArcBuilder = new CircleArc.CircleArcBuilder(MetricsFragment.ARC_BACKGROUND_COLOR,
				MetricsFragment.ARC_THICKNESS, ShapePainter.Cap.ROUNDED);
		style.setExtraObject(VectorCircularProgressBar.BACKGROUND_CIRCLE_ARC_STYLE, circleArcBuilder);
	}

	@Override
	protected void onShown() {
		super.onShown();
		updateValues();
		startUpdateTask();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		stopUpdateTask();
	}

	/**
	 * Starts a periodic task to update label values and UI widgets.
	 */
	public void startUpdateTask() {
		stopUpdateTask();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				updateValues();
				updateDuration();
			}
		};
		this.updateTask = task;
		KernelServiceProvider.getTimer().schedule(task, MetricsFragment.UPDATE_PERIOD, MetricsFragment.UPDATE_PERIOD);
	}

	/**
	 * Triggers a re-render of the duration widget.
	 */
	public void updateDuration() {
		MetricsFragment.this.durationWidget.requestRender();
	}

	/**
	 * Stops the currently running periodic update task, if any.
	 */
	public void stopUpdateTask() {
		TimerTask task = this.updateTask;
		if (task != null) {
			task.cancel();
			this.updateTask = null;
		}
	}

	private long measureTimeElapsed(long currentTime) {
		return (currentTime - this.training.getStartTime());
	}

	private void updateAverageSpeed() {
		String currentSpeed = getSpeedInfo();
		this.speedWidget.setValue(currentSpeed);
		this.speedWidget.requestRender();
	}

	/**
	 * Builds and initializes the UI components on the given Canvas.
	 *
	 * @param canvas
	 *            The canvas on which the UI elements are drawn.
	 */
	private void buildUI(Canvas canvas) {

		canvas.setEnabled(true);
		canvas.addClassSelector(ClassIdentifiers.TRAINING_ROOT_WIDGET);

		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);

		HealthService healthService = KernelServiceProvider.getHealthService();

		float initialHeartRateProgress = (float) healthService.getHeartRate() / MetricsFragment.HEART_RATE_GOAL;

		addHeartRateProgress(initialHeartRateProgress, canvas, displaySize, displayWidth, displayHeight);
		addTimeWidget(canvas, displaySize);
		addHeartRateWidget(canvas, displaySize);
		addSpeedWidget(canvas, displaySize);
		addVerticalDivider(canvas, displaySize);
		addDistanceWidget(canvas, displaySize);
		addDurationWidget(canvas, displaySize);
	}

	private void addTimeWidget(Canvas canvas, int displaySize) {
		// Time widget.
		VectorFont timeFont = TrainingDesktop.getSemiBoldFont();
		float timeFontSize = MetricsFragment.TIME_VALUE_FONT_SIZE_RATIO * displaySize;
		int widgetWidth = (int) timeFont.measureStringWidth(MetricsFragment.TIME_PATTERN, timeFontSize);
		int widgetHeight = (int) timeFont.getHeight(timeFontSize);

		int labelY = (int) (MetricsFragment.TIME_LABEL_Y_RATIO * displaySize);

		int labelX = Alignment.computeLeftX(widgetWidth, 0, displaySize, Alignment.HCENTER);
		canvas.addChild(this.timeWidget, labelX, labelY, widgetWidth, widgetHeight);
	}

	private void addHeartRateWidget(Canvas canvas, int displaySize) {
		// Heart rate widget.
		VectorFont valueFont = TrainingDesktop.getFont();
		VectorFont labelFont = MetricsFragment.getLabelFont();
		float valueFontSize = MetricsFragment.VALUE_FONT_SIZE_RATIO * displaySize;
		int valueFontHeight = (int) valueFont.getHeight(valueFontSize);
		float labelFontSize = MetricsFragment.LABEL_FONT_SIZE_RATIO * displaySize;
		int labelFontHeight = (int) labelFont.getHeight(labelFontSize);
		int labelWidth = (int) labelFont.measureStringWidth(MetricsFragment.HEART_RATE_LABEL, labelFontSize)
				+ MetricsFragment.TEXT_GAP;
		int valueWidth = (int) valueFont.measureStringWidth(MetricsFragment.HEART_RATE_VALUE_PATTERN, valueFontSize)
				+ MetricsFragment.TEXT_GAP;
		int widgetWidth = Math.max(labelWidth, valueWidth);
		int labelY = (int) (MetricsFragment.WIDGET_Y_RATIO * displaySize)
				+ MetricsFragment.HEART_RATE_WIDGET_PADDING_TOP;
		int labelX = Alignment.computeLeftX(widgetWidth, 0, displaySize, Alignment.HCENTER);
		canvas.addChild(this.heartRateWidget, labelX, labelY, widgetWidth,
				valueFontHeight + labelFontHeight + MetricsFragment.TEXT_GAP);
	}

	private void addDurationWidget(Canvas canvas, int displaySize) {
		// Duration widget.
		VectorFont valueFont = TrainingDesktop.getFont();
		VectorFont labelFont = MetricsFragment.getLabelFont();
		float valueFontSize = MetricsFragment.VALUE_FONT_SIZE_RATIO * displaySize;
		int valueFontHeight = (int) valueFont.getHeight(valueFontSize);
		float labelFontSize = MetricsFragment.LABEL_FONT_SIZE_RATIO * displaySize;
		int labelFontHeight = (int) labelFont.getHeight(labelFontSize);
		int labelWidth = (int) labelFont.measureStringWidth(MetricsFragment.DURATION_LABEL, labelFontSize)
				+ MetricsFragment.TEXT_GAP;
		int valueWidth = (int) valueFont.measureStringWidth(MetricsFragment.DURATION_PATTERN, valueFontSize)
				+ MetricsFragment.TEXT_GAP;

		int labelY = (int) (MetricsFragment.WIDGET_Y_RATIO * displaySize) * 3
				+ MetricsFragment.WIDGET_VERTICAL_INTER_MARGIN;
		int widgetWidth = Math.max(labelWidth, valueWidth);
		int labelX = Alignment.computeLeftX(widgetWidth, 0, displaySize, Alignment.HCENTER);
		canvas.addChild(this.durationWidget, labelX, labelY, widgetWidth,
				valueFontHeight + labelFontHeight + MetricsFragment.TEXT_GAP);
	}

	private void addVerticalDivider(Canvas canvas, int displaySize) {
		// Vertical divider.
		VectorFont valueFont = TrainingDesktop.getFont();
		float valueFontSize = MetricsFragment.VALUE_FONT_SIZE_RATIO * displaySize;
		int dividerHeight = (int) valueFont.getHeight(valueFontSize) - 25;
		VerticalDivider verticalDivider = new VerticalDivider();
		int labelY = (int) (MetricsFragment.WIDGET_Y_RATIO * displaySize) * 2 + MetricsFragment.DIVIDER_MARGIN_TOP;
		int labelX = Alignment.computeLeftX(MetricsFragment.DIVIDER_LAYOUT_WIDTH, 0, displaySize, Alignment.HCENTER);
		canvas.addChild(verticalDivider, labelX, labelY, MetricsFragment.DIVIDER_LAYOUT_WIDTH, dividerHeight);
	}

	private void addSpeedWidget(Canvas canvas, int displaySize) {
		// Speed widget.
		VectorFont valueFont = TrainingDesktop.getFont();
		VectorFont labelFont = MetricsFragment.getLabelFont();
		float valueFontSize = MetricsFragment.VALUE_FONT_SIZE_RATIO * displaySize;
		int valueFontHeight = (int) valueFont.getHeight(valueFontSize);
		float labelFontSize = MetricsFragment.LABEL_FONT_SIZE_RATIO * displaySize;
		int labelFontHeight = (int) labelFont.getHeight(labelFontSize);
		int labelWidth = (int) labelFont.measureStringWidth(MetricsFragment.SPEED_LABEL, labelFontSize)
				+ MetricsFragment.TEXT_GAP;
		int valueWidth = (int) valueFont.measureStringWidth(MetricsFragment.SPEED_PATTERN, valueFontSize)
				+ MetricsFragment.TEXT_GAP;

		int labelY = (int) (MetricsFragment.WIDGET_Y_RATIO * displaySize) * 2
				+ MetricsFragment.WIDGET_VERTICAL_INTER_MARGIN;
		int widgetWidth = Math.max(labelWidth, valueWidth);
		int labelX = Alignment.computeLeftX(widgetWidth, 0, displaySize, Alignment.LEFT)
				+ MetricsFragment.WIDGET_HORIZONTAL_MARGIN;
		canvas.addChild(this.speedWidget, labelX, labelY, widgetWidth,
				valueFontHeight + labelFontHeight + MetricsFragment.TEXT_GAP);
	}

	private void addDistanceWidget(Canvas canvas, int displaySize) {
		// Distance widget.
		VectorFont valueFont = TrainingDesktop.getFont();
		VectorFont labelFont = MetricsFragment.getLabelFont();
		float valueFontSize = MetricsFragment.VALUE_FONT_SIZE_RATIO * displaySize;
		int valueFontHeight = (int) valueFont.getHeight(valueFontSize);
		float labelFontSize = MetricsFragment.LABEL_FONT_SIZE_RATIO * displaySize;
		int labelFontHeight = (int) labelFont.getHeight(labelFontSize);
		int labelWidth = (int) labelFont.measureStringWidth(MetricsFragment.DISTANCE_LABEL, labelFontSize)
				+ MetricsFragment.TEXT_GAP;
		int valueWidth = (int) valueFont.measureStringWidth(MetricsFragment.DISTANCE_PATTERN, valueFontSize)
				+ MetricsFragment.TEXT_GAP;

		int labelY = (int) (MetricsFragment.WIDGET_Y_RATIO * displaySize) * 2
				+ (MetricsFragment.WIDGET_VERTICAL_INTER_MARGIN);
		int widgetWidth = Math.max(labelWidth, valueWidth);
		int labelX = Alignment.computeLeftX(widgetWidth, 0, displaySize, Alignment.RIGHT)
				- MetricsFragment.WIDGET_HORIZONTAL_MARGIN;
		canvas.addChild(this.distanceWidget, labelX, labelY, widgetWidth,
				valueFontHeight + labelFontHeight + MetricsFragment.TEXT_GAP);
	}

	private void addHeartRateProgress(float initialValue, Canvas canvas, int displaySize, int displayWidth,
			int displayHeight) {
		int centerX = displayWidth / 2;
		int centerY = displayHeight / 2;
		this.heartRateProgressBar = MetricsFragment.addProgressBar(initialValue, canvas, displaySize, centerX, centerY);
		this.heartRateProgressBar.addClassSelector(ClassIdentifiers.HEART_RATE_PROGRESS);
	}

	private void updateValues() {
		HealthService healthService = KernelServiceProvider.getHealthService();
		this.heartRateProgressBar.setProgress((float) healthService.getHeartRate() / MetricsFragment.HEART_RATE_GOAL);
		this.heartRateWidget.setValue(String.valueOf(healthService.getHeartRate()));
		long currentSteps = healthService.getSteps() - this.training.getStartSteps();

		float distance = (currentSteps * MetricsFragment.STEPS_LENGTH) / 1000;
		if (distance < 0) {
			distance = 0.0f;
		}
		distance = ((int) (distance * 100)) / 100.0f; // Truncate to 2 decimal places
		String currentDistance = String.valueOf(distance);

		this.distanceWidget.setValue(currentDistance);
		this.timeWidget.setText(TimeFormatter.getCurrentTimeFormatted());

		this.heartRateWidget.requestRender();
		this.heartRateProgressBar.requestRender();
		this.distanceWidget.requestRender();
		this.timeWidget.requestRender();

		this.refreshSpeedCounter++;

		if (this.refreshSpeedCounter > 15) {
			this.refreshSpeedCounter = 0;
			updateAverageSpeed();
		}

	}

	private String getSpeedInfo() {
		// Convert steps to distance (km)
		long currentSteps = KernelServiceProvider.getHealthService().getSteps() - this.training.getStartSteps();
		float distance = (currentSteps * MetricsFragment.STEPS_LENGTH) / 1000;
		distance = ((int) (distance * 100)) / 100.0f; // Truncate to 2 decimals
		distance = Math.max(distance, 0); // Ensure non-negative

		// Get total time in minutes
		float elapsedMillis = measureTimeElapsed(Util.platformTimeMillis());
		float totalMinutes = elapsedMillis / 60000.0f; // Convert milliseconds to minutes
		totalMinutes = totalMinutes == 0.0f ? 1.0f : totalMinutes; // Avoid division by zero

		// Average Speed
		float avgSpeedKmPerMin = distance / totalMinutes; // Since it's total distance/time
		float avgSpeedKmPerHour = avgSpeedKmPerMin * 60;

		// Truncate values to 2 decimals
		avgSpeedKmPerHour = ((int) (avgSpeedKmPerHour * 100)) / 100.0f;

		if (avgSpeedKmPerHour > 60) {
			return String.valueOf(this.latestAvgSpeed);
		} else {
			this.latestAvgSpeed = avgSpeedKmPerHour;
			return String.valueOf(avgSpeedKmPerHour);
		}
	}
}
