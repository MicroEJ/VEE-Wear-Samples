/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.ui.picker;

import com.microej.wear.components.Watchface;

import ej.drawing.ShapePainter;
import ej.microui.display.GraphicsContext;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * {@link Widget} that renders an entry of the watchface picker.
 */
public class WatchfacePickerEntry extends Widget {

	private static final int BORDER_FADE = 1;
	private static final int BORDER_THICKNESS = 2;
	private static final int BORDER_COLOR = 0xC0C0C0;

	/**
	 * Provides callbacks related to watchface picker entry events.
	 */
	public interface Listener {

		/**
		 * Called when an watchface is opened.
		 *
		 * @param watchface
		 *            the watchface.
		 */
		void onWatchfaceOpened(Watchface watchface);
	}

	private final Watchface watchface;
	private final Listener listener;

	/**
	 * Creates a watchface picker entry.
	 *
	 * @param watchface
	 *            the watchface.
	 * @param listener
	 *            the listener that will receive watchface picker entry events.
	 */
	public WatchfacePickerEntry(Watchface watchface, Listener listener) {
		super(true);

		this.watchface = watchface;
		this.listener = listener;
	}

	@Override
	protected void onAttached() {
		super.onAttached();

		this.watchface.onPreviewAttached();
	}

	@Override
	protected void onDetached() {
		super.onDetached();

		this.watchface.onPreviewDetached();
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(0, 0);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		int size = contentHeight * 4 / 5;
		int x = (contentWidth - size) / 2;
		int y = (contentHeight - size) / 2;
		this.watchface.renderPreview(g, x, y, size);
		g.setColor(BORDER_COLOR);
		int backgroundColor = g.getBackgroundColor();
		g.removeBackgroundColor();
		ShapePainter.drawThickFadedCircle(g, x, y, size, BORDER_THICKNESS, BORDER_FADE);
		g.setBackgroundColor(backgroundColor);
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Pointer.EVENT_TYPE && Buttons.isReleased(event)) {
			this.listener.onWatchfaceOpened(this.watchface);
			return true;
		}

		return false;
	}
}
