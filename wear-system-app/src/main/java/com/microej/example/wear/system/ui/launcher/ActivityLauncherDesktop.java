/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.ui.launcher;

import com.microej.example.wear.system.navigation.SystemNavigator;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;
import com.microej.wear.util.renderable.RenderableDesktop;

import ej.annotation.Nullable;
import ej.microui.display.BufferedImage;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.microvg.VectorFont;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.ImageBackground;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.util.Alignment;
import ej.widget.basic.Label;

/**
 * {@link ej.mwt.Desktop} which renders the activity launcher.
 */
public class ActivityLauncherDesktop extends RenderableDesktop {

	private static final String ENTRY_IMAGE = "/images/activity_launcher_entry_background.xml";
	private static final String PLACEHOLDER_TEXT = "No Activity";
	private static final int PLACEHOLDER_FONT_SIZE = 30;

	private @Nullable ResourceImage backgroundImage;

	/**
	 * Creates an activity launcher desktop.
	 *
	 * @param activities
	 *            the activities.
	 */
	public ActivityLauncherDesktop(Activity[] activities) {
		Widget widget;
		if (activities.length == 0) {
			widget = new Label(PLACEHOLDER_TEXT);
		} else {
			widget = createLauncher(activities);
		}
		setWidget(widget);
	}

	@Override
	public void setAttached() {
		if (!isAttached()) {
			ResourceImage backgroundImage = takeScreenshot();
			this.backgroundImage = backgroundImage;
			setStylesheet(createStylesheet(backgroundImage));
		}

		super.setAttached();
	}

	@Override
	public void setDetached() {
		super.setDetached();

		ResourceImage backgroundImage = this.backgroundImage;
		if (backgroundImage != null) {
			backgroundImage.close();
			this.backgroundImage = null;
		}
	}

	private static Widget createLauncher(Activity[] activities) {
		VectorImage entryImage = VectorImage.getImage(ENTRY_IMAGE);

		int entryWidth = (int) entryImage.getWidth();
		int entryHeight = (int) entryImage.getHeight();

		ActivityLauncherEntry[] launcherEntries = new ActivityLauncherEntry[activities.length];
		for (int i = 0; i < activities.length; i++) {
			Activity activity = activities[i];
			assert (activity != null);
			launcherEntries[i] = new ActivityLauncherEntry(entryImage, activity);
		}

		ActivityLauncher.Listener listener = new ActivityLauncher.Listener() {
			@Override
			public void onActivityLaunched(Activity activity) {
				SystemNavigator.getInstance().handleActivityLaunched(activity);
			}
		};

		return new ActivityLauncher(launcherEntries, entryWidth, entryHeight, listener);
	}

	private static Stylesheet createStylesheet(Image backgroundImage) {
		VectorFont regularFont = KernelServiceProvider.getFontService().getRegularFont();

		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getSelectorStyle(new TypeSelector(ActivityLauncher.class));
		style.setExtraObject(ActivityLauncher.FONT_STYLE, regularFont);
		style.setExtraFloat(ActivityLauncher.FONT_SIZE_STYLE, 26.0f);
		style.setBackground(new ImageBackground(backgroundImage));
		style = stylesheet.getSelectorStyle(new TypeSelector(Label.class));
		style.setFont(regularFont.getFont(PLACEHOLDER_FONT_SIZE));
		style.setBackground(new ImageBackground(backgroundImage));
		style.setColor(Colors.WHITE);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setVerticalAlignment(Alignment.VCENTER);

		return stylesheet;
	}

	private static BufferedImage takeScreenshot() {
		Display display = Display.getDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		BufferedImage screenshot = new BufferedImage(width, height);
		GraphicsContext g = screenshot.getGraphicsContext();
		g.setColor(Colors.BLACK);
		Painter.fillRectangle(g, 0, 0, width, height);
		Painter.drawDisplayRegion(g, 0, 0, width, height, 0, 0, 64);
		return screenshot;
	}

	@Override
	public boolean handleEvent(int event) {
		Widget widget = getWidget();
		if (widget instanceof ActivityLauncher && widget.handleEvent(event)) {
			return true;
		}
		return super.handleEvent(event);
	}
}
