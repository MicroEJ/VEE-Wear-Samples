/*
 * Java
 *
 * Copyright 2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget.transition;

import ej.annotation.Nullable;
import ej.microui.display.BufferedImage;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.mwt.Container;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Layouts a main widget and can replace it. Makes the new widget appear using transition effect.
 */
public class TransitionContainer extends Container {

	@Nullable
	private Widget mainChild;
	@Nullable
	private Widget backgroundChild;

	@Nullable
	private BufferedImage newMainChildScreenshot;

	@Nullable
	private BufferedImage previousMainChildScreenshot;

	private boolean animating;

	private TransitionEffect effect;

	/**
	 * Creates a bands transition container.
	 *
	 * @param effect
	 *            the transition effect to apply
	 */
	public TransitionContainer(TransitionEffect effect) {
		this.effect = effect;
	}

	/**
	 * Changes the effect.
	 *
	 * @param effect
	 *            the transition effect to apply
	 */
	public void setEffect(TransitionEffect effect) {
		this.effect = effect;
	}

	/**
	 * Changes the background child of this container.
	 *
	 * @param child
	 *            the child
	 */
	public void setBackgroundChild(Widget child) {
		Widget currentBackgroundChild = this.backgroundChild;
		this.backgroundChild = child;
		if (currentBackgroundChild == null) {
			// First background child, no need to replace.
			insertChild(child, 0);
		} else {
			replaceChild(0, child);
		}
	}

	/**
	 * Sets the main child of this container.
	 * <p>
	 * If there is already a child shown, an animation is performed.
	 *
	 * @param child
	 *            the child
	 */
	public void setMainChild(Widget child) {
		Widget currentMainChild = this.mainChild;
		this.mainChild = child;
		if (currentMainChild == null) {
			// First child, no animation to run.
			addChild(child);
		} else {
			// There is already a child, start an animation.

			// Here could be used buffered image pool
			// Get an image of the current child and prepare it.
			final int contentWidth = getContentWidth();
			int contentHeight = getContentHeight();
			BufferedImage previousMainChildScreenshot = new BufferedImage(contentWidth, contentHeight);
			this.previousMainChildScreenshot = previousMainChildScreenshot;
			GraphicsContext g = previousMainChildScreenshot.getGraphicsContext();
			g.reset();

			g.setColor(Colors.BLACK);
			Painter.fillRectangle(g, 0, 0, contentWidth, contentHeight);
			renderChild(currentMainChild, g);

			// Remove the current child and its children. If it (or one of its children) owns an image from
			// the pool, it releases it (see Histogram.onHidden()). And the transition container can then
			// acquire it to do the animation.
			removeChild(currentMainChild);
			addChild(child);

			// Lay out the child hidden.
			computeChildOptimalSize(child, contentWidth, contentHeight);
			layOutChild(child, 0, 0, contentWidth, contentHeight);

			// Here could be used buffered image pool
			// Get an image of the new child and prepare it.
			BufferedImage newMainChildScreenshot = new BufferedImage(contentWidth, contentHeight);
			this.newMainChildScreenshot = newMainChildScreenshot;
			g = newMainChildScreenshot.getGraphicsContext();
			g.reset();

			g.setColor(Colors.BLACK);
			Painter.fillRectangle(g, 0, 0, contentWidth, contentHeight);
			renderChild(child, g);

			this.effect.start(getDesktop().getAnimator(), contentWidth, contentHeight, this);
			this.animating = true;
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int widthHint = size.getWidth();
		int heightHint = size.getHeight();

		Widget currentMainChild = this.mainChild;
		if (currentMainChild != null) {
			computeChildOptimalSize(currentMainChild, widthHint, heightHint);
		}

		Widget currentBackgroundChild = this.backgroundChild;
		if (currentBackgroundChild != null) {
			computeChildOptimalSize(currentBackgroundChild, widthHint, heightHint);
		}
	}

	@Override
	protected void layOutChildren(int contentWidth, int contentHeight) {
		Widget currentMainChild = this.mainChild;
		if (currentMainChild != null) {
			layOutChild(currentMainChild, 0, 0, contentWidth, contentHeight);
		}
		Widget currentBackgroundChild = this.backgroundChild;
		if (currentBackgroundChild != null) {
			layOutChild(currentBackgroundChild, 0, 0, contentWidth, contentHeight);
		}
	}

	@Override
	public void render(GraphicsContext g) {
		if (this.animating) {
			// An animation is running.
			// - Do not draw outlines (in particular the background) to keep the current state of the screen.
			g.translate(getContentX(), getContentY());
			int contentWidth = getContentWidth();
			int contentHeight = getContentHeight();
			g.intersectClip(0, 0, contentWidth, contentHeight);
			// - Draw the screenshot of the incoming widget.
			BufferedImage newMainChildScreenshot = this.newMainChildScreenshot;
			BufferedImage previousMainChildScreenshot = this.previousMainChildScreenshot;
			assert (newMainChildScreenshot != null);
			assert (previousMainChildScreenshot != null);
			this.effect.render(g, newMainChildScreenshot, previousMainChildScreenshot, contentWidth, contentHeight);
		} else {
			super.render(g);
		}
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		onAnimationStopped();
	}

	@Override
	@Nullable
	public Widget getWidgetAt(int x, int y) {
		// Ignore clicks during the animations.
		if (this.animating) {
			return null;
		}
		return super.getWidgetAt(x, y);
	}

	/**
	 * Stop animation.
	 *
	 */
	public void onAnimationStopped() {
		this.animating = false;
		BufferedImage bufferedScreenshot = this.newMainChildScreenshot;
		if (bufferedScreenshot != null) {
			bufferedScreenshot.close();
			this.newMainChildScreenshot = null;
		}
		bufferedScreenshot = this.previousMainChildScreenshot;
		if (bufferedScreenshot != null) {
			bufferedScreenshot.close();
			this.previousMainChildScreenshot = null;
		}

		Widget currentBackgroundChild = this.backgroundChild;
		if (currentBackgroundChild != null) {
			setShownChild(currentBackgroundChild);
		}

		Widget currentMainChild = this.mainChild;
		if (currentMainChild != null) {
			setShownChild(currentMainChild);
		}
	}
}
