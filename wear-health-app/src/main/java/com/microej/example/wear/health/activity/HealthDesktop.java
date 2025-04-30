/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.health.activity;

import com.microej.example.wear.health.activity.widget.HealthIndicator;
import com.microej.example.wear.health.activity.widget.ProgressBar;
import com.microej.example.wear.health.activity.widget.Scroll;
import com.microej.example.wear.health.activity.widget.ScrollableList;
import com.microej.example.wear.health.activity.widget.VectorLabel;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.FontService;
import com.microej.wear.services.HealthService;
import com.microej.wear.util.renderable.RenderableDesktop;
import ej.annotation.Nullable;
import ej.bon.TimerTask;
import ej.microui.display.Colors;
import ej.microvg.VectorFont;
import ej.mwt.Desktop;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RoundedBackground;
import ej.mwt.style.dimension.FixedDimension;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.stylesheet.selector.combinator.AndCombinator;
import ej.mwt.util.Alignment;
import ej.widget.basic.ImageWidget;
import ej.widget.container.Dock;
import ej.widget.container.LayoutOrientation;

/**
 * {@link Desktop} which renders health information.
 */
public class HealthDesktop extends RenderableDesktop {

	private static final int UPDATE_PERIOD = 2000;
	private static final int STEPS_GOAL = 10000;
	private static final int CALORIES_GOAL = 2000;

	private static final int TITLE = 2001;
	private static final int BUTTON_ITEM = 2002;
	private static final int BUTTON_ITEM_NAME = 2003;
	private static final int BUTTON_ITEM_ICON = 2004;
	private static final int BUTTON_ITEM_VALUE = 2005;
	private static final int BUTTON_ITEM_INDICATOR = 2006;
	private static final int BUTTON_ITEM_EXTRA = 2007;

	private final VectorLabel stepsLabel;
	private final ProgressBar stepsProgressBar;
	private final VectorLabel hrLabel;
	private final VectorLabel caloriesLabel;
	private final HealthIndicator caloriesIndicator;
	private final VectorLabel sleepLabel;
	private final HealthIndicator sleepIndicator;
	private final VectorLabel spo2Label;
	private final HealthIndicator spo2Indicator;

	private @Nullable TimerTask task;

	/**
	 * Creates a health desktop.
	 */
	public HealthDesktop() {
		setStylesheet(createStylesheet());

		Scroll scroll = new Scroll(LayoutOrientation.VERTICAL);
		scroll.showScrollbar(false);
		ScrollableList scrollableList = new ScrollableList(LayoutOrientation.VERTICAL, false);
		scroll.setChild(scrollableList);

		VectorLabel title = new VectorLabel("Health");
		title.addClassSelector(TITLE);
		scrollableList.addChild(title);

		// create steps
		HealthService healthService = KernelServiceProvider.getHealthService();
		VectorLabel todayLabel = new VectorLabel("today");
		ProgressBar progressBar = new ProgressBar();
		int stepValue = healthService.getSteps();
		progressBar.setProgress((float) stepValue / STEPS_GOAL);
		this.stepsLabel = new VectorLabel(String.valueOf(stepValue));
		Widget stepItem = createHealthItem("/images/ic_step.png", "Steps", this.stepsLabel, todayLabel, progressBar);
		this.stepsProgressBar = progressBar;
		scrollableList.addChild(stepItem);

		// create HR
		ImageWidget arrow = new ImageWidget("/images/ic_arrow-right-circle.png");
		int hearRateValue = healthService.getHeartRate();
		this.hrLabel = new VectorLabel(String.valueOf(hearRateValue));
		Widget hrItem = createHealthItem("/images/ic_hr.png", "Heart Rate", this.hrLabel, arrow, null);
		scrollableList.addChild(hrItem);

		// create calories
		int caloriesPercent = Math.round(100.0f * healthService.getCalories() / CALORIES_GOAL);
		HealthIndicator caloriesIndicator = new HealthIndicator(computeLevel(caloriesPercent));
		this.caloriesLabel = new VectorLabel(caloriesPercent + "%");
		Widget caloriesItem = createHealthItem("/images/ic_calorie.png", "Calories", this.caloriesLabel,
				caloriesIndicator, null);
		scrollableList.addChild(caloriesItem);
		this.caloriesIndicator = caloriesIndicator;

		// create sleep
		int sleepDuration = getTotalSleepDuration();
		String sleepText = (sleepDuration / 60) + "h " + (sleepDuration % 60) + "m";
		HealthIndicator sleepIndicator = new HealthIndicator(computeSleepLevel(sleepDuration));
		this.sleepLabel = new VectorLabel(sleepText);
		Widget sleepItem = createHealthItem("/images/ic_sleep.png", "Sleep", this.sleepLabel, sleepIndicator, null);
		scrollableList.addChild(sleepItem);
		this.sleepIndicator = sleepIndicator;

		// create SpO2
		int spo2Percent = healthService.getOxygenSaturation();
		HealthIndicator spo2Indicator = new HealthIndicator(computeLevel(spo2Percent));
		this.spo2Label = new VectorLabel(spo2Percent + "%");
		Widget spo2Item = createHealthItem("/images/ic_spo2.png", "SpO2", this.spo2Label, spo2Indicator, null);
		scrollableList.addChild(spo2Item);
		this.spo2Indicator = spo2Indicator;

		setWidget(scroll);
	}

	@Override
	protected void onShown() {
		super.onShown();
		startUpdateTask();
	}

	@Override
	protected void onHidden() {
		stopUpdateTask();
		super.onHidden();
	}

	private void updateValues() {
		HealthService healthService = KernelServiceProvider.getHealthService();

		// update steps
		int steps = healthService.getSteps();
		updateLabel(this.stepsLabel, String.valueOf(steps));
		ProgressBar progressBar = this.stepsProgressBar;
		progressBar.setProgress((float) steps / STEPS_GOAL);
		progressBar.requestRender();

		// update HR
		updateLabel(this.hrLabel, String.valueOf(healthService.getHeartRate()));

		// update calories
		int caloriesPercent = Math.round(100.0f * healthService.getCalories() / CALORIES_GOAL);
		updateLabel(this.caloriesLabel, caloriesPercent + "%");
		int calorieLevel = computeLevel(caloriesPercent);
		updateIndicator(this.caloriesIndicator, calorieLevel);

		// update sleep
		int sleepDuration = getTotalSleepDuration();
		String sleepText = (sleepDuration / 60) + "h " + (sleepDuration % 60) + "m";
		updateLabel(this.sleepLabel, sleepText);
		int sleepLevel = computeSleepLevel(sleepDuration);
		updateIndicator(this.sleepIndicator, sleepLevel);

		// update SpO2
		int spo2Percent = healthService.getOxygenSaturation();
		updateLabel(this.spo2Label, spo2Percent + "%");
		int spo2Level = computeLevel(spo2Percent);
		updateIndicator(this.spo2Indicator, spo2Level);
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

	private Widget createHealthItem(String imagePath, String itemName, VectorLabel itemValue, Widget indicator,
			@Nullable Widget extra) {
		Dock dock = new Dock();
		dock.addClassSelector(BUTTON_ITEM);

		ImageWidget icon = new ImageWidget(imagePath);
		icon.addClassSelector(BUTTON_ITEM_ICON);
		dock.addChildOnLeft(icon);

		if (extra != null) {
			dock.addChildOnBottom(extra);
			extra.addClassSelector(BUTTON_ITEM_EXTRA);
		}

		VectorLabel name = new VectorLabel(itemName);
		name.addClassSelector(BUTTON_ITEM_NAME);
		dock.addChildOnLeft(name);

		indicator.addClassSelector(BUTTON_ITEM_INDICATOR);
		dock.addChildOnRight(indicator);

		itemValue.addClassSelector(BUTTON_ITEM_VALUE);
		dock.setCenterChild(itemValue);

		return dock;
	}

	private Stylesheet createStylesheet() {
		FontService fontService = KernelServiceProvider.getFontService();
		VectorFont font = fontService.getRegularFont();
		VectorFont boldItalicFont = fontService.getBoldItalicFont();
		VectorFont lightFont = fontService.getLightFont();

		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setColor(Colors.WHITE);

		style = stylesheet.getSelectorStyle(new TypeSelector(Scroll.class));
		style.setBackground(new RoundedBackground(Colors.BLACK, 0, 0));

		style = stylesheet.getSelectorStyle(new TypeSelector(ScrollableList.class));
		style.setPadding(new FlexibleOutline(0, 8, 84, 8));

		// App title
		style = stylesheet.getSelectorStyle(new ClassSelector(TITLE));
		style.setExtraInt(VectorLabel.TEXT_SIZE_STYLE, 38);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setExtraObject(VectorLabel.FONT_STYLE, lightFont);
		style.setPadding(new FlexibleOutline(34, 0, 17, 0));

		style = stylesheet.getSelectorStyle(new ClassSelector(BUTTON_ITEM));
		style.setBackground(new RoundedBackground(0x262a2c, 50, 0));
		style.setDimension(new FixedDimension(Widget.NO_CONSTRAINT, 84));
		style.setPadding(new FlexibleOutline(0, 8, 0, 8));
		style.setMargin(new FlexibleOutline(4, 0, 4, 0));

		style = stylesheet.getSelectorStyle(new ClassSelector(BUTTON_ITEM_ICON));
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(BUTTON_ITEM_NAME));
		style.setPadding(new FlexibleOutline(0, 0, 0, 8));
		style.setExtraInt(VectorLabel.TEXT_SIZE_STYLE, 32);
		style.setExtraObject(VectorLabel.FONT_STYLE, lightFont);
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(BUTTON_ITEM_VALUE));
		style.setHorizontalAlignment(Alignment.RIGHT);
		style.setVerticalAlignment(Alignment.VCENTER);
		style.setExtraObject(VectorLabel.FONT_STYLE, boldItalicFont);
		style.setExtraInt(VectorLabel.TEXT_SIZE_STYLE, 37);

		style = stylesheet.getSelectorStyle(new ClassSelector(BUTTON_ITEM_INDICATOR));
		style.setVerticalAlignment(Alignment.VCENTER);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setPadding(new FlexibleOutline(0, 17, 0, 17));

		style = stylesheet.getSelectorStyle(
				new AndCombinator(new ClassSelector(BUTTON_ITEM_INDICATOR), new TypeSelector(VectorLabel.class)));
		style.setExtraInt(VectorLabel.TEXT_SIZE_STYLE, 22);
		style.setExtraObject(VectorLabel.FONT_STYLE, font);

		style = stylesheet.getSelectorStyle(new ClassSelector(BUTTON_ITEM_EXTRA));
		style.setColor(0x4B5357);
		style.setPadding(new FlexibleOutline(0, 59, 12, 4));

		style = stylesheet.getSelectorStyle(new TypeSelector(ProgressBar.class));
		style.setExtraInt(ProgressBar.EXTRA_FIELD_PROGRESS_COLOR, 0x6CC24A);
		style.setExtraInt(ProgressBar.EXTRA_FIELD_THICKNESS, 10);

		return stylesheet;
	}

	private static void updateLabel(VectorLabel label, String text) {
		label.setText(text);
		label.requestRender();
	}

	private static void updateIndicator(HealthIndicator indicator, int level) {
		indicator.setLevel(level);
		indicator.requestRender();
	}

	private static int computeLevel(int value) {
		if (value < 50) {
			return HealthIndicator.BAD;
		} else if (value < 95) {
			return HealthIndicator.AVERAGE;
		} else {
			return HealthIndicator.GOOD;
		}
	}

	private static int computeSleepLevel(int value) {
		if (value < 6 * 60) {
			return HealthIndicator.BAD;
		} else if (value < 8 * 60) {
			return HealthIndicator.AVERAGE;
		} else {
			return HealthIndicator.GOOD;
		}
	}

	private static int getTotalSleepDuration() {
		HealthService healthService = KernelServiceProvider.getHealthService();
		return healthService.getAwakeSleepDuration() + healthService.getRemSleepDuration()
				+ healthService.getLightSleepDuration() + healthService.getDeepSleepDuration();
	}
}
