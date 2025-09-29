/*
 * Java
 *
 * Copyright 2021-2025 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.training.activity.widget;

import ej.annotation.Nullable;
import ej.basictool.ArrayTools;
import ej.bon.Util;
import ej.microui.MicroUI;
import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.microvg.BufferedVectorImage;
import ej.microvg.VectorGraphicsPainter;
import ej.motion.Motion;
import ej.motion.quart.QuartEaseOutFunction;
import ej.mwt.Container;
import ej.mwt.Widget;
import ej.mwt.event.DesktopEventGenerator;
import ej.mwt.event.PointerEventDispatcher;
import ej.mwt.util.Size;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * A swipe container holds several widgets.
 *
 * <p>
 * It is possible to change the visible widget by swiping left or right.
 */
public class SwipeContainer extends Container {

	private static final int SCREEN_RATIO = 6;
	private static final float SPEED_THRESHOLD = 0.1f;

	private static final int TRANSITION_DURATION = 200;

	private static final int CURRENT = 0;
	private static final int OTHER = 1;

	@Nullable
	private MotionAnimation motionAnimation;

	private final BulletPagingIndicator indicator;

	private Widget[] children;
	private int currentChildIndex;
	private int otherChildIndex;
	private int targetChildIndex;

	// Drag management.
	private boolean pressed;
	private int pressedX;
	private long pressedTime;
	private boolean moving;
	private int previousX;
	private int previousY;

	// Transition snapshots.
	@Nullable
	private BufferedVectorImage currentSnapshot;
	@Nullable
	private BufferedVectorImage otherSnapshot;

	/**
	 * Creates a slide container.
	 */
	public SwipeContainer(BulletPagingIndicator indicator) {
		super(true);
		this.indicator = indicator;
		this.currentChildIndex = -1;
		this.children = new Widget[0];
	}

	/**
	 * Adds a child to the view and updates the indicator. If it's the first child, adds it to the container and sets it
	 * as the current child.
	 *
	 * @param child
	 *            the child widget to add.
	 */
	public void addChildToContainer(Widget child) {
		this.children = ArrayTools.add(this.children, child);
		this.indicator.setItemsCount(this.children.length);
		if (this.currentChildIndex == -1) {
			addChild(child);
			this.currentChildIndex = 0;

		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int widthHint = size.getWidth();
		int heightHint = size.getHeight();

		computeChildOptimalSize(this.indicator, widthHint, heightHint);
		int childrenWidth;
		int childrenHeight;
		if (getChildrenCount() > 1) {
			Widget child = getChild(CURRENT);
			computeChildOptimalSize(child, widthHint, heightHint);
			childrenWidth = child.getWidth();
			childrenHeight = child.getHeight();
		} else {
			childrenWidth = 0;
			childrenHeight = 0;
		}

		size.setSize(childrenWidth, childrenHeight);
	}

	@Override
	protected void layOutChildren(int contentWidth, int contentHeight) {
		int dotsWidth = this.indicator.getWidth();
		layOutChild(this.indicator, contentWidth - dotsWidth, 0, dotsWidth, contentHeight);

		if (getChildrenCount() > 0) {
			Widget child = getChild(CURRENT);
			layOutChild(child, 0, 0, contentWidth, contentHeight);
		}
	}

	private void doAnimation(final Widget currentChild, final Widget otherChild, final int startX, int endX,
			final int targetIndex, final boolean removeOther) {
		final int shift = otherChild.getX() - currentChild.getX();
		long duration = TRANSITION_DURATION * Math.abs(endX - startX) / getContentWidth();
		Motion motion = new Motion(QuartEaseOutFunction.INSTANCE, startX, endX, duration);
		this.targetChildIndex = targetIndex;
		MotionAnimation animation = new MotionAnimation(getDesktop().getAnimator(), motion,
				new MotionAnimationListener() {
					@Override
					public void tick(int value, boolean finished) {
						currentChild.setPosition(value, 0);
						otherChild.setPosition(value + shift, 0);
						requestRender();
						if (finished) {
							restore(removeOther);
						}
					}
				});
		animation.start();
		this.motionAnimation = animation;
	}

	private void interruptAnimation() {
		MotionAnimation animation = this.motionAnimation;
		if (animation != null) {
			animation.stop();
			this.targetChildIndex = this.currentChildIndex;
			restore(true);
		}
	}

	private void restore(boolean removeOther) {
		this.moving = false;
		this.motionAnimation = null;
		int childrenCount = getChildrenCount();
		if (childrenCount >= 2) {
			this.currentChildIndex = this.targetChildIndex;
			this.indicator.setSelectedItem(this.targetChildIndex, 1.0f);

			Widget currentChild = getChild(CURRENT);
			Widget otherChild = getChild(OTHER);

			closeSnapshot(this.currentSnapshot);
			closeSnapshot(this.otherSnapshot);
			this.currentSnapshot = null;
			this.otherSnapshot = null;

			// Restart any animation/refresh on the newly visible child.
			Widget newlyVisibleChild;
			if (removeOther) {
				removeChild(otherChild);
				newlyVisibleChild = currentChild;
			} else {
				removeChild(currentChild);
				newlyVisibleChild = otherChild;
			}
			newlyVisibleChild.setPosition(0, 0);
			setShownChild(newlyVisibleChild);
			requestRender();
		}
	}

	private void closeSnapshot(@Nullable BufferedVectorImage snapshot) {
		if (snapshot != null && !snapshot.isClosed()) {
			snapshot.clear();
			snapshot.close();
		}
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		if (this.moving) {
			BufferedVectorImage currentSnapshot = this.currentSnapshot;
			if (currentSnapshot != null) {
				Widget currentChild = getChild(CURRENT);
				VectorGraphicsPainter.drawImage(g, currentSnapshot, currentChild.getX(), currentChild.getY());
			}
			BufferedVectorImage otherSnapshot = this.otherSnapshot;
			if (otherSnapshot != null) {
				Widget otherChild = getChild(OTHER);
				VectorGraphicsPainter.drawImage(g, otherSnapshot, otherChild.getX(), otherChild.getY());
			}
		} else {
			int translateX = g.getTranslationX();
			int translateY = g.getTranslationY();
			int x = g.getClipX();
			int y = g.getClipY();
			int width = g.getClipWidth();
			int height = g.getClipHeight();
			Widget child = getChild(CURRENT);
			renderChild(child, g);
			// Restore initial translation and clip
			g.setTranslation(translateX, translateY);
			g.setClip(x, y, width, height);
		}
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Pointer.EVENT_TYPE) {
			Pointer pointer = (Pointer) Event.getGenerator(event);
			int pointerX = pointer.getX();
			int pointerY = pointer.getY();
			int contentWidth = getContentWidth();
			int childrenCount = this.children.length;
			switch (Buttons.getAction(event)) {
			case Buttons.PRESSED:
				onPointerPressed(pointerX, pointerY, childrenCount);
				break;
			case Pointer.DRAGGED:
				if (onPointerDragged(contentWidth, childrenCount, pointerX, pointerY)) {
					return true;
				}
				break;
			case Buttons.RELEASED:
				if (onPointerReleased(pointerX, contentWidth)) {
					return true;
				}
				break;
			default:
				break;
			}
		} else if (Event.getType(event) == DesktopEventGenerator.EVENT_TYPE) {
			int action = DesktopEventGenerator.getAction(event);
			if (action == PointerEventDispatcher.EXITED) {
				this.pressed = false;
			}
		}
		return super.handleEvent(event);
	}

	private void onPointerPressed(int pointerX, int pointerY, int childrenCount) {
		if (this.moving || childrenCount < 2) {
			this.pressed = false;
		} else {
			interruptAnimation();
			this.pressed = true;
			this.pressedX = pointerX;
			this.pressedTime = Util.platformTimeMillis();
			this.previousX = pointerX;
			this.previousY = pointerY;
		}
	}

	private BufferedVectorImage createSnapshot(Widget child) {
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		BufferedVectorImage image = new BufferedVectorImage(displayWidth, displayHeight);

		GraphicsContext g = image.getGraphicsContext();
		g.reset();

		int contentWidth = getContentWidth();
		int contentHeight = getContentHeight();

		// Apply the background of this container in case the child is transparent.
		getStyle().getBackground().apply(g, contentWidth, contentHeight);

		g.translate(-child.getX(), -child.getY());
		renderChild(child, g);

		return image;
	}

	private boolean onPointerDragged(int contentWidth, int childrenCount, int pointerX, int pointerY) {

		int shiftX = pointerX - this.previousX;
		if (this.pressed && shiftX != 0) {
			int shiftY = pointerY - this.previousY;
			Widget currentChild = getChild(CURRENT);
			boolean movingOutside = shiftX > 0 && this.currentChildIndex == 0
					|| shiftX < 0 && this.currentChildIndex == this.children.length - 1;
			if (!this.moving && Math.abs(shiftX) > Math.abs(shiftY)) {
				if (movingOutside) {
					this.previousX = pointerX;
					this.previousY = pointerY;
					return true;
				}
				this.moving = true;
				this.currentSnapshot = createSnapshot(currentChild);
				// Start to drag when moving vertically.
				setHiddenChild(currentChild);
				loadOtherChild(contentWidth, childrenCount, shiftX);
			}
			int previousChildX = currentChild.getX();
			int childX = previousChildX + shiftX;

			if (previousChildX > 0 && childX <= 0 || previousChildX < 0 && childX >= 0) {
				// Change way.
				this.pressedX = pointerX;
				this.pressedTime = Util.platformTimeMillis();
				if (getChildrenCount() > 1) {
					Widget otherChild = getChild(OTHER);
					removeChild(otherChild);
					closeSnapshot(this.otherSnapshot);
					this.otherSnapshot = null;
				}

				if (movingOutside) {
					this.moving = false;
					this.previousX = pointerX;
					this.previousY = pointerY;
					return true;
				}

				loadOtherChild(contentWidth, childrenCount, shiftX);
			}

			if (this.moving) {
				this.previousX = pointerX;
				this.previousY = pointerY;
				currentChild.setPosition(childX, 0);
				Widget otherChild = getChild(OTHER);
				otherChild.setPosition(otherChild.getX() + shiftX, 0);
				requestRender();
				return true;
			}
		}
		return false;
	}

	private void loadOtherChild(int contentWidth, int childrenCount, int shiftX) {
		int otherIndex;
		int otherShiftX;
		if (shiftX > 0) {
			// Load previous child.
			if (this.currentChildIndex == 0) {
				otherIndex = childrenCount - 1;
			} else {
				otherIndex = this.currentChildIndex - 1;
			}
			otherShiftX = -contentWidth;
		} else {
			// Load next child.
			if (this.currentChildIndex == childrenCount - 1) {
				otherIndex = 0;
			} else {
				otherIndex = this.currentChildIndex + 1;
			}
			otherShiftX = contentWidth;
		}
		this.otherChildIndex = otherIndex;
		Widget otherWidget = this.children[otherIndex];
		assert (otherWidget != null);
		super.addChild(otherWidget);
		int contentHeight = getContentHeight();
		computeChildOptimalSize(otherWidget, contentWidth, contentHeight);
		layOutChild(otherWidget, otherShiftX, 0, contentWidth, contentHeight);
		this.otherSnapshot = createSnapshot(otherWidget);
	}

	private boolean onPointerReleased(final int pointerX, final int contentWidth) {
		if (this.moving) {
			MicroUI.callSerially(new Runnable() {
				@Override
				public void run() {
					SwipeContainer.this.pressed = false;
					final Widget currentChild = getChild(CURRENT);
					int childX = currentChild.getX();
					Widget otherChild = getChild(OTHER);
					float speed = -(float) (pointerX - SwipeContainer.this.pressedX)
							/ (Util.platformTimeMillis() - SwipeContainer.this.pressedTime);
					if (childX < 0) {
						if (childX < -contentWidth / SCREEN_RATIO || speed > SPEED_THRESHOLD) {
							doAnimation(currentChild, otherChild, childX, -contentWidth,
									SwipeContainer.this.otherChildIndex, false);
						} else {
							doAnimation(currentChild, otherChild, 0, childX, SwipeContainer.this.currentChildIndex,
									true);
						}
					} else {
						if (childX > contentWidth / SCREEN_RATIO || speed < -SPEED_THRESHOLD) {
							doAnimation(currentChild, otherChild, childX, contentWidth,
									SwipeContainer.this.otherChildIndex, false);
						} else {
							doAnimation(currentChild, otherChild, childX, 0, SwipeContainer.this.currentChildIndex,
									true);
						}
					}
				}
			});
			return true;
		} else {
			closeSnapshot(this.currentSnapshot);
			this.currentSnapshot = null;
			this.pressed = false;
		}
		return false;
	}
}
