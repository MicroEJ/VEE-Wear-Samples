/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget;

import com.microej.example.wear.breath.activity.style.ClassIdentifiers;
import com.microej.example.wear.breath.activity.widget.scroll.Scroll;
import com.microej.example.wear.breath.activity.widget.scroll.ScrollableList;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ResourceService;

import ej.annotation.Nullable;
import ej.drawing.TransformPainter;
import ej.drawing.TransformPainter.Flip;
import ej.microui.MicroUI;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.mwt.style.Style;
import ej.widget.basic.Label;
import ej.widget.container.LayoutOrientation;

/**
 * A Widget containing a scrollable list of timers.
 */
public class TimerScrollWidget extends Scroll {

	/** The extra field ID for the linear radial gradient color. */
	public static final int LINEAR_GRADIANT_COLOR_FIELD = 0;
	private static final int LINEAR_GRADIENT_DEFAULT_COLOR = Colors.BLACK;
	private static final String LINEAR_GRADIENT = "/images/linear_gradient.png";
	private static final short MAX_TIMERS = 5;

	private @Nullable ResourceImage linearRadialGradientImage;
	private final int defaultIndex;

	/**
	 * Creates a timer scroll widget.
	 *
	 * @param defaultIndex
	 *            the default index in the timer list
	 * @param elementHeight
	 *            the default height of an element from the list
	 * @param scrollbarMaximum
	 *            the maximum bound.
	 */
	public TimerScrollWidget(int defaultIndex, int elementHeight, int scrollbarMaximum) {
		super(LayoutOrientation.VERTICAL, defaultIndex * elementHeight, scrollbarMaximum);

		this.defaultIndex = defaultIndex;
		ScrollableList list = new ScrollableList(LayoutOrientation.VERTICAL, true);

		showScrollbar(false);
		setChild(list);

		// add timers to show
		for (int i = MAX_TIMERS; i > 0; i--) {
			Label label = new Label(i + " min");
			label.addClassSelector(ClassIdentifiers.TIMER_LABEL);
			list.addChild(label);
		}
		// add test timers to show
		Label label = new Label("10 sec");
		label.addClassSelector(ClassIdentifiers.TIMER_LABEL);
		list.addChild(label);
	}

	@Override
	protected void onAttached() {
		super.onAttached();

		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.linearRadialGradientImage = ResourceImage.loadImage(resourceService.getImagePath(LINEAR_GRADIENT));
	}

	@Override
	protected void onDetached() {
		super.onDetached();

		ResourceImage linearGradientImage = this.linearRadialGradientImage;
		if (linearGradientImage != null) {
			linearGradientImage.close();
			this.linearRadialGradientImage = null;
		}
	}

	@Override
	protected void onShown() {
		super.onShown();
		scrollToDefaultIndex();
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		int translateX = g.getTranslationX();
		int translateY = g.getTranslationY();
		int x = g.getClipX();
		int y = g.getClipY();
		int width = g.getClipWidth();
		int height = g.getClipHeight();
		Style style = getStyle();
		Image linearGradient = this.linearRadialGradientImage;

		super.renderContent(g, contentWidth, contentHeight);

		// reset graphics context's state after parent render (Container.renderContent)
		g.setTranslation(translateX, translateY);
		g.setClip(x, y, width, height);

		// draw gradient on top and on bottom (repeated to spare space)
		if (linearGradient != null) {
			int linearRadialGradientColor = style.getExtraInt(LINEAR_GRADIANT_COLOR_FIELD,
					LINEAR_GRADIENT_DEFAULT_COLOR);
			int sparedSpace = 0;
			int bottomY = contentHeight - linearGradient.getHeight();
			int linearGradientWidth = linearGradient.getWidth();

			g.setColor(linearRadialGradientColor);
			while (sparedSpace < width) {
				// draw top
				Painter.drawImage(g, linearGradient, sparedSpace, 0);
				// draw bottom
				TransformPainter.drawFlippedImage(g, linearGradient, sparedSpace, bottomY, Flip.FLIP_180);
				sparedSpace += linearGradientWidth;
			}
		}
	}

	private void scrollToDefaultIndex() {
		MicroUI.callSerially(new Runnable() {
			@Override
			public void run() {
				// move the list down and scroll without animation to the middle child
				scrollToIndex(TimerScrollWidget.this.defaultIndex, false, 0);
			}
		});
	}
}
