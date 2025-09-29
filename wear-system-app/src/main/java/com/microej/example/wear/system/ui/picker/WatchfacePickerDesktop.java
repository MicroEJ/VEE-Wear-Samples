/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.ui.picker;

import com.microej.example.wear.system.navigation.SystemNavigator;
import com.microej.wear.components.Watchface;
import com.microej.wear.util.renderable.RenderableDesktop;

import ej.annotation.Nullable;
import ej.basictool.ArrayTools;
import ej.microui.display.Colors;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.TypeSelector;

/**
 * {@link ej.mwt.Desktop} which renders the watchface picker.
 */
public class WatchfacePickerDesktop extends RenderableDesktop {

	/**
	 * Creates a watchface picker desktop.
	 *
	 * @param watchfaces
	 *            the watchfaces.
	 * @param selectedWatchface
	 *            the selected watchface.
	 */
	public WatchfacePickerDesktop(Watchface[] watchfaces, @Nullable Watchface selectedWatchface) {
		if (watchfaces.length == 0) {
			throw new IllegalArgumentException("No watchface");
		}

		WatchfacePickerEntry.Listener listener = new WatchfacePickerEntry.Listener() {
			@Override
			public void onWatchfaceOpened(Watchface watchface) {
				SystemNavigator.getInstance().handleWatchfacePicked(watchface);
			}
		};

		FillCarousel picker = new FillCarousel(true, false);

		for (Watchface watchface : watchfaces) {
			assert (watchface != null);
			picker.addChild(new WatchfacePickerEntry(watchface, listener));
		}

		if (selectedWatchface != null) {
			picker.setSelectedIndex(ArrayTools.getIndex(watchfaces, selectedWatchface));
		}
		BulletPagingIndicator cursor = new BulletPagingIndicator();
		picker.setCursor(cursor, false);

		setStylesheet(createStylesheet());
		setWidget(picker);
	}

	private Stylesheet createStylesheet() {
		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getSelectorStyle(new TypeSelector(FillCarousel.class));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		style = stylesheet.getSelectorStyle(new TypeSelector(BulletPagingIndicator.class));
		style.setBackground(new RectangularBackground(Colors.BLACK));
		style.setMargin(new FlexibleOutline(0, 0, 22, 0));
		style.setColor(0x999999);
		style.setExtraInt(BulletPagingIndicator.CURSOR_SIZE_STYLE, 18);
		style.setExtraInt(BulletPagingIndicator.SELECTED_COLOR_STYLE, 0xFFFFFF);

		style = stylesheet.getSelectorStyle(new TypeSelector(WatchfacePickerEntry.class));
		style.setBackground(new RectangularBackground(Colors.BLACK));
		style.setMargin(new FlexibleOutline(41, 0, 0, 0));

		return stylesheet;
	}
}
