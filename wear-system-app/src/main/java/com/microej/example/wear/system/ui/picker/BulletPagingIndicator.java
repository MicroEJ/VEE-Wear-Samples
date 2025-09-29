/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.ui.picker;

import ej.annotation.Nullable;
import ej.bon.Util;
import ej.drawing.ShapePainter;
import ej.microui.display.GraphicsContext;
import ej.motion.Motion;
import ej.motion.linear.LinearFunction;
import ej.mwt.animation.Animation;
import ej.mwt.style.Style;
import ej.mwt.util.Size;

/**
 * The bullet paging indicator is a widget that displays a set of dots following a line. A bigger dot indicates the
 * selected index.
 * <p>
 * The bullet paging indicator is hidden by sliding smoothly the dots outside the drawing area.
 */
public class BulletPagingIndicator extends PagingIndicator implements Animation {

	/** The constant value for the cursor size. */
	public static final int CURSOR_SIZE_STYLE = 0;

	/** The constant value for the selected color. */
	public static final int SELECTED_COLOR_STYLE = 1;

	private static final int DEFAULT_CURSOR_SIZE = 20;

	private static final int HIDE_DURATION = 300;

	private int bulletsPosition;
	@Nullable
	private Motion motion;
	private long startTime;

	/**
	 * Creates an horizontal bullet paging indicator.
	 */
	public BulletPagingIndicator() {
		super();
	}

	/**
	 * Creates a bullet paging indicator.
	 *
	 * @param horizontal
	 *            <code>true</code> if the paging indicator is horizontal, <code>false</code> otherwise.
	 */
	public BulletPagingIndicator(boolean horizontal) {
		super(horizontal);
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		cancelHide();
	}

	@Override
	public void show() {
		cancelHide();
		this.bulletsPosition = 0;
	}

	private void cancelHide() {
		getDesktop().getAnimator().stopAnimation(this);
	}

	@Override
	public void hide() {
		int bulletSize;
		if (isHorizontal()) {
			bulletSize = getHeight();
		} else {
			bulletSize = getWidth();
		}
		this.motion = new Motion(LinearFunction.INSTANCE, this.bulletsPosition, bulletSize, HIDE_DURATION);
		this.startTime = Util.platformTimeMillis();
		getDesktop().getAnimator().startAnimation(this);
	}

	@Override
	public boolean tick(long currentTimeMillis) {
		// Cursors showing/hiding tick.
		Motion motion = this.motion;
		if (motion == null) {
			return false;
		}
		long elapsedTime = currentTimeMillis - this.startTime;
		this.bulletsPosition = motion.getValue(elapsedTime);
		requestRender();
		return (elapsedTime < motion.getDuration());
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		int color = style.getColor();
		int selectedColor = style.getExtraInt(SELECTED_COLOR_STYLE, color);

		boolean horizontal = isHorizontal();
		int itemsCount = getItemsCount();
		if (itemsCount == 0) {
			// Nothing to draw.
			return;
		}

		int currentItemIndex = getSelectedItem();
		int nextItemIndex = (currentItemIndex + 1) % itemsCount;
		float percent = getPercent();
		int selectedIndex = (percent > 0.5f ? currentItemIndex : currentItemIndex + 1);

		// Compute sizes and initial position.
		int bigSize;
		int x;
		int y;
		if (horizontal) {
			bigSize = contentHeight - 1; // Minus 1 for antialiasing.
			x = (contentWidth - bigSize * itemsCount) / 2;
			y = contentHeight / 2 + this.bulletsPosition;
		} else {
			bigSize = contentWidth - 1; // Minus 1 for antialiasing.
			x = contentWidth / 2 + this.bulletsPosition;
			y = (contentHeight - bigSize * itemsCount) / 2;
		}
		int smallSize = bigSize / 2;
		int percentSize = (int) (smallSize * getPercent());
		int nextSize = bigSize - percentSize;
		int currentSize = smallSize + percentSize;
		// Draw dots.
		for (int i = -1; ++i < itemsCount;) {
			int pointSize;
			if (i == currentItemIndex) {
				pointSize = currentSize;
			} else if (i == nextItemIndex) {
				pointSize = nextSize;
			} else {
				pointSize = smallSize;
			}

			g.setColor(i == selectedIndex ? selectedColor : color);

			if (horizontal) {
				ShapePainter.drawThickFadedPoint(g, x + bigSize / 2, y, pointSize, 1);
				x += bigSize;
			} else {
				ShapePainter.drawThickFadedPoint(g, x, y + bigSize / 2, pointSize, 1);
				y += bigSize;
			}
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();

		int baseSize = style.getExtraInt(CURSOR_SIZE_STYLE, DEFAULT_CURSOR_SIZE);
		if ((baseSize & 0x1) == 1) {
			// Make this size event to make sure the half is exactly the half!
			baseSize++;
		}
		// Add 1 for antialiasing for every dimension.
		int referenceSize = baseSize * getItemsCount() + 1;
		if (isHorizontal()) {
			size.setSize(referenceSize, baseSize + 1);
		} else {
			size.setSize(baseSize + 1, referenceSize);
		}
	}

}
