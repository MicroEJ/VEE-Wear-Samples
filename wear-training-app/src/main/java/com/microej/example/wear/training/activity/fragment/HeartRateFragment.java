/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.fragment;

import com.microej.example.wear.training.activity.TrainingDesktop;
import com.microej.example.wear.training.activity.style.ClassIdentifiers;
import com.microej.example.wear.training.activity.widget.HeartRateZone;
import com.microej.example.wear.training.activity.widget.TimeFormatter;
import com.microej.example.wear.training.activity.widget.VectorDurationLabel;
import com.microej.example.wear.training.activity.widget.VectorImageWidget;
import com.microej.example.wear.training.activity.widget.VectorLabel;
import com.microej.example.wear.training.model.Training;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.HealthService;

import ej.annotation.Nullable;
import ej.bon.TimerTask;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.util.Alignment;
import ej.widget.container.Canvas;

/**
 * The HeartRateFragment class extends Canvas and is responsible for building the UI components related to Heart rate
 * metrics .
 */
public class HeartRateFragment extends Canvas {
	private static final int HR_ZONE_1_THRESHOLD = 48;
	private static final int HR_ZONE_2_THRESHOLD = 96;
	private static final int HR_ZONE_3_THRESHOLD = 144;
	private static final int HR_ZONE_4_THRESHOLD = 192;
	private static final int HR_ZONE_5_THRESHOLD = 240;
	private static final int X_HEART_RATE_ICON_OFFSET = 120;
	private static final long UPDATE_PERIOD = 1000;
	private static final long UPDATE_IMAGE_PERIOD = 50;
	private static final int HUNDRED = 100;
	private static final int ZONE_ONE = 1;
	private static final int ZONE_TWO = 2;
	private static final int ZONE_THREE = 3;
	private static final int ZONE_FOUR = 4;
	private static final int ZONE_FIVE = 5;
	private static final String BPM = " BPM";

	/* Layout constants */
	private static final float TIME_LABEL_Y_RATIO = 0.09f;
	private static final float HR_ZONES_RATIO = 0.94f;
	private static final float WIDGET_Y_RATIO = 0.22f;
	private static final float VALUE_FONT_SIZE_RATIO = 0.15f;
	private static final float BOLD_VALUE_FONT_SIZE_RATIO = 0.12f;
	private static final float TIME_VALUE_FONT_SIZE_RATIO = 0.08f;
	private static final float LABEL_FONT_SIZE_RATIO = 0.05f;
	private static final String HEART_RATE_VALUE_PATTERN = "000 BPM";
	private static final int TEXT_GAP = 5;
	private static final String DURATION_LABEL = "Duration";
	private static final String DURATION_PATTERN = "00:00:00";
	private static final int WIDGET_VERTICAL_INTER_MARGIN = 12;
	private static final int STARTING_SCALE_VALUE = 20;
	private static final int ENDING_SCALE_VALUE = 30;
	private static final int HEART_RATE_MARGIN = 10;
	private static final String TIME_PATTERN = "00:00";

	// Widgets
	private VectorLabel heartRateWidget;
	private VectorImageWidget vectorHeartImage;
	private VectorDurationLabel durationWidget;
	private HeartRateZone heartRateZones;
	private VectorLabel timeWidget;
	private final Training training;
	@Nullable
	private TimerTask updateTask;
	@Nullable
	private TimerTask updateImageTask;
	private int hrImageScale;
	private boolean increment;

	/**
	 * Constructs a HeartRateFragment with a given training instance. Initializes the UI components upon creation.
	 *
	 * @param training
	 *            The training instance used to track training duration.
	 */
	public HeartRateFragment(Training training) {
		this.training = training;
		this.increment = true;
		this.hrImageScale = HeartRateFragment.STARTING_SCALE_VALUE;
		buildUI(this);
	}

	@Override
	protected void onShown() {
		super.onShown();
		startUpdateTask();
		startUpdateImageTask();
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

		TimerTask updateTask = new TimerTask() {
			@Override
			public void run() {
				updateHeartRateZone();
				updateValues();
				updateDuration();
			}
		};

		this.updateTask = updateTask;

		KernelServiceProvider.getTimer().schedule(updateTask, HeartRateFragment.UPDATE_PERIOD,
				HeartRateFragment.UPDATE_PERIOD);
	}

	/**
	 * Triggers a re-render of the duration widget.
	 */
	public void updateDuration() {
		HeartRateFragment.this.durationWidget.requestRender();
	}

	/**
	 * Starts a periodic task to update the heart image animation.
	 */
	public void startUpdateImageTask() {
		stopUpdateImageTask();
		TimerTask updateImageTask = new TimerTask() {
			@Override
			public void run() {
				updateHeartImageAnimation();
			}
		};
		this.updateImageTask = updateImageTask;
		KernelServiceProvider.getTimer().schedule(updateImageTask, HeartRateFragment.UPDATE_IMAGE_PERIOD,
				HeartRateFragment.UPDATE_IMAGE_PERIOD);
	}

	/**
	 * Stops the currently running periodic update task, if any.
	 */
	public void stopUpdateTask() {
		TimerTask updateTask = this.updateTask;
		if (updateTask != null) {
			updateTask.cancel();
			this.updateTask = null;
		}
	}

	/**
	 * Stops the currently running periodic heart image animation, if any.
	 */
	public void stopUpdateImageTask() {
		TimerTask updateImageTask = this.updateImageTask;
		if (updateImageTask != null) {
			updateImageTask.cancel();
			this.updateImageTask = null;
		}
	}

	/**
	 * Builds and initializes the UI components on the given Canvas.
	 *
	 * @param canvas
	 *            The canvas on which the UI elements are drawn.
	 */
	private void buildUI(Canvas canvas) {
		canvas.setEnabled(true);
		canvas.addClassSelector(ClassIdentifiers.HR_ROOT_WIDGET);

		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int displaySize = Math.min(displayWidth, displayHeight);
		HealthService healthService = KernelServiceProvider.getHealthService();
		String heartRateTextValue = String.valueOf(healthService.getHeartRate());
		addHeartRate(canvas, displaySize);
		addHeartRateWidget(heartRateTextValue, canvas, displaySize);
		addDurationWidget(canvas, displaySize);
		addTimeWidget(canvas, displaySize);
		addHeartRateZones(canvas, displaySize);
		updateHeartRateZone();
	}

	private void addHeartRate(Canvas canvas, int displaySize) {
		VectorImageWidget heartImage = new VectorImageWidget("/images/heart_icon.svg",
				HeartRateFragment.STARTING_SCALE_VALUE);
		int valueFontHeight = getBoldFontHeight(displaySize);

		int imageWidth = HeartRateFragment.ENDING_SCALE_VALUE + HeartRateFragment.HEART_RATE_MARGIN;
		int imageHeight = HeartRateFragment.ENDING_SCALE_VALUE + HeartRateFragment.HEART_RATE_MARGIN;

		int heartImageY = (int) (HeartRateFragment.WIDGET_Y_RATIO * displaySize) + 20;
		int y = Alignment.computeTopY(imageHeight, heartImageY, valueFontHeight, Alignment.VCENTER);
		int heartImageX = Alignment.computeLeftX(imageWidth, 0, displaySize, Alignment.HCENTER)
				- HeartRateFragment.X_HEART_RATE_ICON_OFFSET;
		canvas.addChild(heartImage, heartImageX, y, imageWidth, imageHeight);
		this.vectorHeartImage = heartImage;
	}

	private void addHeartRateWidget(String initialValue, Canvas canvas, int displaySize) {
		// Heart rate widget.
		int valueFontHeight = getBoldFontHeight(displaySize);

		int valueWidth = getBoldFontHeartRateWidth(displaySize) + HeartRateFragment.TEXT_GAP;
		VectorLabel heartRateLabeledValue = new VectorLabel(initialValue + HeartRateFragment.BPM);
		heartRateLabeledValue.addClassSelector(ClassIdentifiers.HR_LABEL_WIDGET);

		int labelY = (int) (HeartRateFragment.WIDGET_Y_RATIO * displaySize) + 20;
		int labelX = Alignment.computeLeftX(valueWidth, 0, displaySize, Alignment.HCENTER);
		canvas.addChild(heartRateLabeledValue, labelX, labelY, valueWidth,
				valueFontHeight + HeartRateFragment.TEXT_GAP);
		this.heartRateWidget = heartRateLabeledValue;
	}

	private void addHeartRateZones(Canvas canvas, int displaySize) {
		// Heart rate zones widget.
		HeartRateZone hrZones = new HeartRateZone(1);
		Display display = Display.getDisplay();
		int hrZonesWidth = (int) (display.getWidth() * HeartRateFragment.HR_ZONES_RATIO);
		int zonesY = (int) (HeartRateFragment.WIDGET_Y_RATIO * displaySize + 110);
		int zonesX = Alignment.computeLeftX(hrZonesWidth, 0, display.getWidth(), Alignment.HCENTER);
		canvas.addChild(hrZones, zonesX, zonesY, hrZonesWidth, 80);
		this.heartRateZones = hrZones;
	}

	private void addDurationWidget(Canvas canvas, int displaySize) {
		// Duration widget.
		VectorFont valueFont = TrainingDesktop.getFont();
		VectorFont labelFont = HeartRateFragment.getLabelFont();
		float valueFontSize = HeartRateFragment.VALUE_FONT_SIZE_RATIO * displaySize;
		int valueFontHeight = (int) valueFont.getHeight(valueFontSize);
		float labelFontSize = HeartRateFragment.LABEL_FONT_SIZE_RATIO * displaySize;
		int labelFontHeight = (int) labelFont.getHeight(labelFontSize);
		int labelWidth = (int) labelFont.measureStringWidth(HeartRateFragment.DURATION_LABEL, labelFontSize)
				+ HeartRateFragment.TEXT_GAP;
		int valueWidth = (int) valueFont.measureStringWidth(HeartRateFragment.DURATION_PATTERN, valueFontSize)
				+ HeartRateFragment.TEXT_GAP;
		VectorDurationLabel durationLabeledValue = new VectorDurationLabel(this.training.getStartTime());
		durationLabeledValue.addClassSelector(ClassIdentifiers.DURATION_WIDGET);
		int labelY = (int) (HeartRateFragment.WIDGET_Y_RATIO * displaySize) * 3
				+ HeartRateFragment.WIDGET_VERTICAL_INTER_MARGIN;
		int widgetWidth = Math.max(labelWidth, valueWidth);
		int labelX = Alignment.computeLeftX(widgetWidth, 0, displaySize, Alignment.HCENTER);
		canvas.addChild(durationLabeledValue, labelX, labelY, widgetWidth,
				valueFontHeight + labelFontHeight + HeartRateFragment.TEXT_GAP);
		this.durationWidget = durationLabeledValue;
	}

	private void addTimeWidget(Canvas canvas, int displaySize) {
		// Time widget.
		VectorFont timeFont = TrainingDesktop.getSemiBoldFont();
		float timeFontSize = HeartRateFragment.TIME_VALUE_FONT_SIZE_RATIO * displaySize;
		int widgetWidth = (int) timeFont.measureStringWidth(HeartRateFragment.TIME_PATTERN, timeFontSize);
		int widgetHeight = (int) timeFont.getHeight(timeFontSize);
		VectorLabel timeLabeledValue = new VectorLabel(TimeFormatter.getCurrentTimeFormatted());
		timeLabeledValue.addClassSelector(ClassIdentifiers.TIME_WIDGET);
		int labelY = (int) (HeartRateFragment.TIME_LABEL_Y_RATIO * displaySize);

		int labelX = Alignment.computeLeftX(widgetWidth, 0, displaySize, Alignment.HCENTER);
		canvas.addChild(timeLabeledValue, labelX, labelY, widgetWidth, widgetHeight);
		this.timeWidget = timeLabeledValue;
	}

	private void updateHeartImageAnimation() {
		this.vectorHeartImage.setScale(this.hrImageScale);

		if (this.hrImageScale > HeartRateFragment.ENDING_SCALE_VALUE) {
			this.increment = false;
		}

		if (this.hrImageScale < HeartRateFragment.STARTING_SCALE_VALUE) {
			this.increment = true;
		}

		if (this.increment) {
			this.hrImageScale++;
		} else {
			this.hrImageScale--;
		}

		this.vectorHeartImage.requestRender();
	}

	/**
	 * HeartRate fragment styles.
	 *
	 * @param stylesheet
	 *            the main style sheet instance.
	 */
	public static void appendStyles(CascadingStylesheet stylesheet) {
		Display display = Display.getDisplay();
		int displaySize = Math.min(display.getWidth(), display.getHeight());
		int boldValueFontSize = (int) (HeartRateFragment.BOLD_VALUE_FONT_SIZE_RATIO * displaySize);
		VectorFont boldValueFont = TrainingDesktop.getBoldFont();

		// default style
		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);

		// root widget style
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.HR_ROOT_WIDGET));
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setMargin(new FlexibleOutline(0, 0, 0, 0));

		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.HR_LABEL_WIDGET));
		style.setColor(Colors.WHITE);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setExtraObject(VectorLabel.FONT_STYLE, boldValueFont);
		style.setExtraInt(VectorLabel.TEXT_SIZE_STYLE, boldValueFontSize);

	}

	private static VectorFont getLabelFont() {
		return KernelServiceProvider.getFontService().getRegularFont();
	}

	private void updateHeartRateZone() {
		HealthService healthService = KernelServiceProvider.getHealthService();
		int hr = healthService.getHeartRate();
		int progress;

		if (hr < HeartRateFragment.HR_ZONE_1_THRESHOLD) {
			progress = (hr * HeartRateFragment.HUNDRED) / HeartRateFragment.HR_ZONE_1_THRESHOLD; // Maps 0-48 to 0-100
			this.heartRateZones.selectZone(HeartRateFragment.ZONE_ONE);
		} else if (hr > HeartRateFragment.HR_ZONE_1_THRESHOLD && hr <= HeartRateFragment.HR_ZONE_2_THRESHOLD) {
			progress = ((hr - HeartRateFragment.HR_ZONE_1_THRESHOLD) * HeartRateFragment.HUNDRED)
					/ (HeartRateFragment.HR_ZONE_2_THRESHOLD - HeartRateFragment.HR_ZONE_1_THRESHOLD);
			this.heartRateZones.selectZone(HeartRateFragment.ZONE_TWO);// Maps 48-96 to 0-100
		} else if (hr > HeartRateFragment.HR_ZONE_2_THRESHOLD && hr <= HeartRateFragment.HR_ZONE_3_THRESHOLD) {
			progress = ((hr - HeartRateFragment.HR_ZONE_2_THRESHOLD) * HeartRateFragment.HUNDRED)
					/ (HeartRateFragment.HR_ZONE_3_THRESHOLD - HeartRateFragment.HR_ZONE_2_THRESHOLD);// Maps
			// 96-144 to
			// 0-100
			this.heartRateZones.selectZone(HeartRateFragment.ZONE_THREE);
		} else if (hr > HeartRateFragment.HR_ZONE_3_THRESHOLD && hr <= HeartRateFragment.HR_ZONE_4_THRESHOLD) {
			progress = ((hr - HeartRateFragment.HR_ZONE_3_THRESHOLD) * HeartRateFragment.HUNDRED)
					/ (HeartRateFragment.HR_ZONE_4_THRESHOLD - HeartRateFragment.HR_ZONE_3_THRESHOLD); // Maps
			// 144-192
			// to
			// 0-100
			this.heartRateZones.selectZone(HeartRateFragment.ZONE_FOUR);
		} else if (hr > HeartRateFragment.HR_ZONE_4_THRESHOLD && hr <= HeartRateFragment.HR_ZONE_5_THRESHOLD) {
			progress = ((hr - HeartRateFragment.HR_ZONE_4_THRESHOLD) * HeartRateFragment.HUNDRED)
					/ (HeartRateFragment.HR_ZONE_5_THRESHOLD - HeartRateFragment.HR_ZONE_4_THRESHOLD); // Maps
			// 192-240
			// to
			// 0-100
			this.heartRateZones.selectZone(HeartRateFragment.ZONE_FIVE);
		} else {
			progress = HeartRateFragment.HUNDRED; // Cap at 100 for hr > 240
			this.heartRateZones.selectZone(HeartRateFragment.ZONE_FIVE);
		}
		this.heartRateZones.setCursorProgress(progress);
		this.heartRateZones.requestRender();
	}

	private void updateValues() {
		this.timeWidget.setText(TimeFormatter.getCurrentTimeFormatted());
		HealthService healthService = KernelServiceProvider.getHealthService();
		int hr = healthService.getHeartRate();
		this.heartRateWidget.setText(String.valueOf(hr) + HeartRateFragment.BPM);
		this.timeWidget.requestRender();
		this.heartRateWidget.requestRender();
	}

	private int getBoldFontHeight(int displaySize) {
		VectorFont valueFont = TrainingDesktop.getBoldFont();
		float boldValueFontSize = HeartRateFragment.BOLD_VALUE_FONT_SIZE_RATIO * displaySize;
		return (int) valueFont.getHeight(boldValueFontSize);
	}

	private int getBoldFontHeartRateWidth(int displaySize) {
		VectorFont valueFont = TrainingDesktop.getBoldFont();
		float boldValueFontSize = HeartRateFragment.BOLD_VALUE_FONT_SIZE_RATIO * displaySize;
		return (int) valueFont.measureStringWidth(HeartRateFragment.HEART_RATE_VALUE_PATTERN, boldValueFontSize);
	}
}