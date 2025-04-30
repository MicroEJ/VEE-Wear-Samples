/*
 * Copyright 2013-2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget.scroll;

import ej.annotation.Nullable;
import ej.bon.XMath;
import ej.microui.MicroUI;
import ej.motion.Function;
import ej.motion.quart.QuartEaseOutFunction;
import ej.mwt.Container;
import ej.mwt.Widget;
import ej.mwt.animation.Animator;
import ej.mwt.util.Size;
import ej.widget.swipe.SwipeListener;
import ej.widget.swipe.SwipeEventHandler;
import ej.widget.swipe.Swipeable;

/**
 * Allows to scroll a widget horizontally or vertically.
 */
public class Scroll extends Container {

	private static final int SWIPE_ANIMATION_DURATION = 500;
	private static final Function MOTION_FUNCTION = QuartEaseOutFunction.INSTANCE;

	private @Nullable Widget child;
	private @Nullable Scrollable scrollableChild;
	private final boolean horizontal;

	// Swipe management.
	private @Nullable SwipeEventHandler swipeEventHandler;
	private final ScrollAssistant assistant;
	private int value;
	private final boolean allowExcess;

	/**
	 * Creates a scroll container with a specified orientation and associated listeners.
	 *
	 * @param horizontal
	 *            <code>true</code> to scroll horizontally, <code>false</code> to scroll vertically.
	 * @param swipeListener
	 *            The listener to handle swipe events.
	 * @param scrollListener
	 *            The listener to handle scroll position changes.
	 */
	public Scroll(boolean horizontal, SwipeListener swipeListener, ScrollListener scrollListener) {
		super(true);
		this.horizontal = horizontal;
		this.assistant = new ScrollAssistant(swipeListener, scrollListener);
		this.allowExcess = true;
	}

	@Override
	protected void setShownChildren() {
		Widget child = this.child;
		if (child != null) {
			setShownChild(child);
		}
	}

	/**
	 * Sets the child to scroll.
	 * <p>
	 * The given widget can implement {@link Scrollable} and be notified about when the visible area changes (for
	 * example for optimization purpose).
	 * <p>
	 * Should be called in the UI thread to avoid concurrency issues.
	 *
	 * @param child
	 *            the child to scroll.
	 * @see MicroUI#isUIThread()
	 */
	public void setChild(Widget child) {
		Widget oldChild = this.child;
		if (child != oldChild) {
			if (oldChild != null) {
				// replace old child by new child
				replaceChild(getChildIndex(oldChild), child);
			} else {
				// insert new child before scrollbar
				insertChild(child, 0);
			}

			// update fields
			this.child = child;
			if (child instanceof Scrollable) {
				this.scrollableChild = (Scrollable) child;
			} else {
				this.scrollableChild = null;
			}
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int width = 0;
		int height = 0;

		Widget child = this.child;
		if (child != null) {
			int widthHint = size.getWidth();
			int heightHint = size.getHeight();

			if (this.horizontal) {
				computeChildOptimalSize(child, Widget.NO_CONSTRAINT, heightHint);
			} else {
				computeChildOptimalSize(child, widthHint, Widget.NO_CONSTRAINT);
			}

			width = child.getWidth();
			height = child.getHeight();
		}

		// Set container optimal size.
		size.setSize(width, height);
	}

	@Override
	protected void layOutChildren(int contentWidth, int contentHeight) {
		Scrollable scrollableChild = this.scrollableChild;
		if (scrollableChild != null) {
			scrollableChild.initializeViewport(contentWidth, contentHeight);
		}

		layoutOnScroll(contentWidth, contentHeight);

	}

	private void layoutOnScroll(int contentWidth, int contentHeight) {
		Widget child = this.child;
		int childOptimalWidth;
		int childOptimalHeight;
		if (child != null) {
			childOptimalWidth = child.getWidth();
			childOptimalHeight = child.getHeight();
		} else {
			childOptimalWidth = 0;
			childOptimalHeight = 0;
		}

		int excess = treatExcess(child, contentWidth, contentHeight, childOptimalWidth, childOptimalHeight);
		if (excess > 0) {
			SwipeEventHandler swipeEventHandler = this.swipeEventHandler;
			if (swipeEventHandler != null) {
				swipeEventHandler.stop();
			}

			Animator animator = getDesktop().getAnimator();
			swipeEventHandler = createSwipeEventHandler(excess, this.horizontal, this.assistant, animator);
			swipeEventHandler.setSwipeListener(this.assistant);
			swipeEventHandler.moveTo(this.value);
			this.swipeEventHandler = swipeEventHandler;
			this.swipeEventHandler.setDuration(SWIPE_ANIMATION_DURATION);
			this.swipeEventHandler.setMotionFunction(MOTION_FUNCTION);
		}
	}

	private SwipeEventHandler createSwipeEventHandler(int size, boolean horizontal, Swipeable assistant,
			Animator animator) {
		Scrollable scrollable = this.scrollableChild;
		int[] itemSizes;
		if (scrollable != null && scrollable.snapToItems()) {
			itemSizes = scrollable.getItemSizes();
		} else {
			itemSizes = null;
		}
		if (itemSizes != null) {
			return new SwipeEventHandler(this, itemSizes, false, true, horizontal, assistant, animator);
		} else {
			return new SwipeEventHandler(this, size, false, horizontal, assistant, animator);
		}
	}

	private int treatExcess(@Nullable Widget child, int contentWidth, int contentHeight, int childOptimalWidth,
			int childOptimalHeight) {
		int excess = computeExcess(contentWidth, contentHeight, childOptimalWidth, childOptimalHeight);

		layOutChildUsingOrientation(child, contentWidth, contentHeight, childOptimalWidth, childOptimalHeight);
		return excess;
	}

	private int computeExcess(int contentWidth, int contentHeight, int childOptimalWidth, int childOptimalHeight) {
		return this.horizontal ? childOptimalWidth - contentWidth : childOptimalHeight - contentHeight;
	}

	private void layOutChildUsingOrientation(Widget child, int contentWidth, int contentHeight, int childOptimalWidth,
			int childOptimalHeight) {
		if (child == null) {
			return;
		}
		int childX;
		int childY;
		int childWidth;
		int childHeight;
		if (this.horizontal) {
			childX = 0;
			childY = 0;
			childWidth = childOptimalWidth;
			childHeight = contentHeight;
		} else {
			childX = 0;
			childY = 0;
			childHeight = childOptimalHeight;
			childWidth = contentWidth;
		}
		layOutChild(child, childX, childY, childWidth, childHeight);
	}

	@Override
	protected void onHidden() {
		super.onHidden();

		SwipeEventHandler swipeEventHandler = this.swipeEventHandler;
		if (swipeEventHandler != null) {
			swipeEventHandler.stop();
			swipeEventHandler.moveTo(limit(this.value));
		}
	}

	private int limit(int position) {
		int max;
		Widget child = this.child;
		if (child != null) {
			if (this.horizontal) {
				max = child.getWidth() - getContentWidth();
			} else {
				max = child.getHeight() - getContentHeight();
			}
			max = Math.max(0, max);
		} else {
			max = 0;
		}
		return XMath.limit(position, 0, max);
	}

	@Override
	public boolean handleEvent(int event) {
		SwipeEventHandler swipeEventHandler = this.swipeEventHandler;
		if (swipeEventHandler != null && swipeEventHandler.handleEvent(event)) {
			return true;
		}
		return super.handleEvent(event);
	}

	private void updateViewport(int x, int y) {
		Scrollable scrollableChild = this.scrollableChild;
		if (scrollableChild != null) {
			scrollableChild.updateViewport(x, y);
		}
		Widget child = this.child;
		if (child != null) {
			child.setPosition(x, y);
		}
	}

	private void updateViewport(int childCoordinate) {
		Widget child = this.child;
		if (child != null) {
			if (this.horizontal) {
				updateViewport(childCoordinate, child.getY());
			} else {
				updateViewport(child.getX(), childCoordinate);
			}
		}
	}

	private void shift() {
		if (isShown()) {
			int value = this.value;

			int childCoordinate = computeChildCoordinate(value);
			updateViewport(childCoordinate);
		}
	}

	private int computeChildCoordinate(int value) {
		Scrollable scrollable = this.scrollableChild;
		if (scrollable != null && scrollable.snapToItems()) {
			return -value;
		} else {
			return -(value) / 2;
		}
	}

	class ScrollAssistant implements Swipeable, SwipeListener {
		SwipeListener swipeListener;
		ScrollListener scrollListener;

		public ScrollAssistant(SwipeListener swipeListener, ScrollListener scrollListener) {
			this.swipeListener = swipeListener;
			this.scrollListener = scrollListener;
		}

		@Override
		public void onSwipeStarted() {
			this.swipeListener.onSwipeStarted();
		}

		@Override
		public void onSwipeStopped() {
			this.swipeListener.onSwipeStopped();
		}

		@Override
		public void onMove(int position) {
			Scroll scroll = Scroll.this;
			if (scroll.value != position) {
				scroll.value = scroll.allowExcess ? position : limit(position);
				scroll.shift();
				requestRender();
				this.scrollListener.onPositionChanged(position);
			}
		}

	}

}
