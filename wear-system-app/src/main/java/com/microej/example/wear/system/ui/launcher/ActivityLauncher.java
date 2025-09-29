/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.system.ui.launcher;

import com.microej.example.wear.system.util.TimeUtils;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.Activity;

import ej.bon.Util;
import ej.bon.XMath;
import ej.drawing.ShapePainter;
import ej.microui.MicroUI;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.microvg.BlendMode;
import ej.microvg.Matrix;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorGraphicsPainter.Direction;
import ej.motion.Motion;
import ej.motion.quad.QuadEaseOutFunction;
import ej.motion.quart.QuartEaseOutFunction;
import ej.mwt.Widget;
import ej.mwt.animation.Animation;
import ej.mwt.style.Style;
import ej.mwt.util.Size;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * {@link Widget} that renders a list of activities and allows to open one.
 */
public class ActivityLauncher extends Widget {

	/**
	 * Provides callbacks related to activity launcher events.
	 */
	public interface Listener {

		/**
		 * Called when an activity is launched.
		 *
		 * @param activity
		 *            the activity.
		 */
		void onActivityLaunched(Activity activity);
	}

	/** The constant value for the default font size. */
	public static final float DEFAULT_FONT_SIZE = 20.0f;

	/** The extra field ID for the font. */
	public static final int FONT_STYLE = 0;

	/** The extra field ID for the font size. */
	public static final int FONT_SIZE_STYLE = 1;

	private static final float SPACING_RATIO = 0.6f; // entry spacing
	private static final int OFFSET_THRESHOLD = 250; // don't draw entry if further

	private static final int SHOWUP_DURATION = 200;
	private static final int INITIAL_GOTO_ANIM = 2;

	private static final int GOTO_ANIM_STEPS = 1000;
	private static final int GOTO_ANIM_DURATION = 200;
	private static final float GOTO_ANIM_SPEED = 0.5f;

	private static final long RELEASE_WITH_NO_MOVE_DELAY = 80;

	private static final long SELECTED_ANIM_DURATION = 200;

	private static final float DOT_CENTER_ANGLE = 90.0f * (float) Math.PI / 180.0f;
	private static final float DOT_SPACING_ANGLE = 8.0f * (float) Math.PI / 180.0f;
	private static final float PI = 3.14f;
	private static final int DOT_SIZE = 18;
	private static final int DOT_COLOR = 0x999999;
	private static final int SELECTED_DOT_COLOR = 0xFFFFFF;

	private final ActivityLauncherEntry[] entries;
	private final int entryWidth;
	private final int entryHeight;
	private final Listener listener;

	private final Animation repaintAnimation;
	private long showTime;
	private boolean animationStarted;

	private long lastDragTime;
	private long lastPressTime;
	private int lastPressX;
	private int currentDrag;
	private int startDragX;
	private int endDragX;
	private boolean noDrag;

	private final Motion gotoAnimMotion;
	private long gotoAnimStartTime;
	private int gotoAnimDistance;
	private int gotoAnimStep;

	private int selectedEntry;
	private int unselectedEntry;
	private long selectedTime;

	private String timeString;

	private float launchAppSizeRatio;
	private boolean launchAnimationFinished;
	private static final int SELECTED_ANIM_STOP_VALUE = 10500;
	private static final int SIZE_RATIO_HUNDRED_PERCENT = 10000;

	/**
	 * Creates an activity launcher.
	 *
	 * @param entries
	 *            the entries.
	 * @param entryWidth
	 *            the width of every entry.
	 * @param entryHeight
	 *            the height of every entry.
	 * @param listener
	 *            the listener that will receive activity launcher events.
	 */
	public ActivityLauncher(ActivityLauncherEntry[] entries, int entryWidth, int entryHeight, Listener listener) {
		this.entries = entries.clone();
		this.entryWidth = entryWidth;
		this.entryHeight = entryHeight;
		this.listener = listener;

		// launch activity animation parameters
		this.launchAppSizeRatio = 0.0f;
		this.launchAnimationFinished = false;

		// init drag
		this.currentDrag = INITIAL_GOTO_ANIM * this.entryWidth;
		this.startDragX = 0;
		this.endDragX = 0;
		this.noDrag = false;

		// init goto anim
		this.gotoAnimMotion = new Motion(QuadEaseOutFunction.INSTANCE, 0, GOTO_ANIM_STEPS, GOTO_ANIM_DURATION);
		this.gotoAnimDistance = 0;
		this.gotoAnimStep = 0;

		// init selected entry
		this.selectedEntry = -1;
		this.unselectedEntry = -1;
		this.selectedTime = -SELECTED_ANIM_DURATION - 1;

		// init repaint task
		this.repaintAnimation = new Animation() {
			@Override
			public boolean tick(long currentTimeMillis) {
				return repaintTick(currentTimeMillis);
			}
		};
		this.animationStarted = false;

		// init time string
		this.timeString = "";

		// enable widget
		setEnabled(true);
	}

	@Override
	protected void onAttached() {
		super.onAttached();

		this.showTime = -1;
		this.timeString = getTimeText();

		for (ActivityLauncherEntry entry : this.entries) {
			entry.getActivity().onIconAttached();
		}
	}

	@Override
	protected void onDetached() {
		super.onDetached();

		for (ActivityLauncherEntry entry : this.entries) {
			entry.getActivity().onIconDetached();
		}
	}

	@Override
	protected void onShown() {
		super.onShown();

		this.showTime = Util.platformTimeMillis();
		startGotoAnim(INITIAL_GOTO_ANIM * this.entryWidth);
	}

	@Override
	protected void onHidden() {
		super.onHidden();

		stopAnimation();
	}

	private void startAnimation() {
		if (!this.animationStarted) {
			getDesktop().getAnimator().startAnimation(this.repaintAnimation);
			this.animationStarted = true;
		}
	}

	private void stopAnimation() {
		if (this.animationStarted) {
			getDesktop().getAnimator().stopAnimation(this.repaintAnimation);
			this.animationStarted = false;
		}
	}

	private boolean repaintTick(long currentTime) {
		// request repaint
		requestRender();

		// check if the carousel is now stopped
		return !(this.gotoAnimDistance == 0 && currentTime - this.lastDragTime >= RELEASE_WITH_NO_MOVE_DELAY
				&& this.launchAnimationFinished);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(this.entryWidth, this.entryHeight);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		// get current time
		long currentTime = Util.platformTimeMillis();

		// get text style
		Style style = getStyle();
		VectorFont font = getFont(style);
		float fontSize = getFontSize(style);

		// update goto anim step
		if (this.gotoAnimDistance != 0) {
			this.gotoAnimStep = this.gotoAnimMotion.getValue(currentTime - this.gotoAnimStartTime);
			if (this.gotoAnimStep >= GOTO_ANIM_STEPS) {
				stopGotoAnim();
			}
		}

		// calculate drag
		int totalDrag = getTotalDrag();

		// get top entry
		int topEntry = getEntryAtDrag(totalDrag);

		// draw dots
		long showTime = this.showTime;
		int showupElapsed = (showTime == -1 ? 0 : (int) (currentTime - this.showTime));
		boolean showingUp = (showupElapsed < SHOWUP_DURATION);
		float showupRatio = (float) showupElapsed / SHOWUP_DURATION;
		float firstDotAngle = DOT_CENTER_ANGLE + (this.entries.length - 1) / 2.0f * DOT_SPACING_ANGLE;
		for (int e = 0; e < this.entries.length; e++) {
			int offsetX = e * this.entryWidth + totalDrag;
			float sizeRatio = getEntrySizeRatio(offsetX, contentWidth);
			float transitionRatio = ActivityLauncherEntry.computeTransitionRatio(sizeRatio);
			float dotRatio = (1.0f - transitionRatio) / 2.0f + 0.5f;
			if (showingUp) {
				dotRatio *= showupRatio;
			}

			int dotRadius = contentWidth / 2 - DOT_SIZE * 3 / 2;
			float dotAngle = firstDotAngle - e * DOT_SPACING_ANGLE;
			int dotX = contentWidth / 2 + (int) (dotRadius * Math.cos(dotAngle));
			int dotY = contentHeight / 2 + (int) (dotRadius * Math.sin(dotAngle));

			g.setColor(e == topEntry ? SELECTED_DOT_COLOR : DOT_COLOR);
			int dotThickness = (int) (DOT_SIZE * dotRatio);
			if (dotThickness > 0) {
				ShapePainter.drawThickFadedPoint(g, dotX, dotY, dotThickness, 1);
			}
		}

		// draw time
		float timeFontSize = fontSize * 1.5f;
		float timeRadius = contentWidth / 2.0f - 50;
		Direction timeDirection = Direction.CLOCKWISE;
		int showupAlpha = GraphicsContext.OPAQUE * showupElapsed / SHOWUP_DURATION;

		// calculates the angle corresponding to half of the text width to center the text
		float arcLength = font.measureStringWidth(this.timeString, timeFontSize) / 2;
		float perimeter = (contentWidth - (timeFontSize * 2)) * PI;
		float angle = 360 * arcLength / perimeter;

		Matrix matrix = new Matrix();
		matrix.setRotate(-90.0f - angle);
		matrix.postTranslate(contentWidth / 2.0f, contentHeight / 2.0f);
		g.setColor(Colors.WHITE);
		if (showingUp) {
			VectorGraphicsPainter.drawStringOnCircle(g, this.timeString, font, timeFontSize, matrix, timeRadius,
					timeDirection, showupAlpha, BlendMode.SRC_OVER, 0.0f);
		} else {
			VectorGraphicsPainter.drawStringOnCircle(g, this.timeString, font, timeFontSize, matrix, timeRadius,
					timeDirection);
		}

		// draw entries
		g.setColor(style.getColor());
		int minEntry = Math.max(topEntry - 2, 0);
		int maxEntry = Math.min(topEntry + 2, this.entries.length - 1);
		for (int e = minEntry; e < topEntry; e++) {
			drawEntry(g, contentWidth, contentHeight, font, fontSize, e, totalDrag);
		}
		for (int e = maxEntry; e > topEntry; e--) {
			drawEntry(g, contentWidth, contentHeight, font, fontSize, e, totalDrag);
		}
		drawEntry(g, contentWidth, contentHeight, font, fontSize, topEntry, totalDrag);
	}

	private void drawEntry(GraphicsContext g, int contentWidth, int contentHeight, VectorFont font, float fontSize,
			int entryIndex, int totalDrag) {
		// calculate position and size
		int offsetX = entryIndex * this.entryWidth + totalDrag;
		float sizeRatio = getEntrySizeRatio(offsetX, contentWidth);

		// recalculate position
		offsetX = (int) ((SPACING_RATIO + sizeRatio) / 2.0f * offsetX);

		// draw if close enough
		if (Math.abs(offsetX) < OFFSET_THRESHOLD) {
			// draw entry
			ActivityLauncherEntry entry = this.entries[entryIndex];
			float selectedRatio = 0.0f;
			if (isEntrySelected() && (entryIndex == this.selectedEntry || entryIndex == this.unselectedEntry)) {
				selectedRatio = this.launchAppSizeRatio / SIZE_RATIO_HUNDRED_PERCENT;
			}
			entry.render(g, contentWidth, contentHeight, font, fontSize, sizeRatio, offsetX, selectedRatio);
		}
	}

	private float getEntrySizeRatio(int offsetX, int contentWidth) {
		float factor = Math.abs((float) offsetX / contentWidth);
		return 1.0f - Math.min(factor, 1.0f) / 2.0f;
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Pointer.EVENT_TYPE) {
			Pointer pointer = (Pointer) Event.getGenerator(event);
			int pointerX = pointer.getX() - getAbsoluteX();
			int action = Buttons.getAction(event);

			int topEntry = getEntryAtDrag(getTotalDrag());

			// update last press/drag vars
			if (action == Buttons.PRESSED || action == Pointer.DRAGGED) {
				long currentTime = Util.platformTimeMillis();
				if (action == Buttons.PRESSED || currentTime - this.lastDragTime >= RELEASE_WITH_NO_MOVE_DELAY) {
					this.lastPressTime = currentTime;
					this.lastPressX = pointerX;
					startAnimation();
				}
				this.lastDragTime = currentTime;
			}

			// handle drag
			handlePointer(action, pointerX);

			// don't capture when dragging over first activity so that navigation can handle swipe
			return (topEntry != 0 || action == Buttons.RELEASED);
		}
		return false;
	}

	private void handlePointer(int action, int pointerX) {
		if (isEntrySelected()) {
			if (action == Buttons.RELEASED) {
				unselectEntry();
			}
			return;
		}

		if (action == Buttons.PRESSED) {
			// stop goto animation
			stopGotoAnim();

			// start drag
			this.startDragX = pointerX;
			this.endDragX = pointerX;
			this.noDrag = true;
		} else if (action == Pointer.DRAGGED) {
			// update drag
			this.endDragX = pointerX;
			this.noDrag = false;
		} else if (action == Buttons.RELEASED) {
			handleRelease(pointerX);
		}
	}

	private void handleRelease(int pointerX) {
		if (this.noDrag) {
			// this is just a click
			int halfWidth = getWidth() / 2;
			if (this.lastPressX > halfWidth - this.entryWidth / 2
					&& this.lastPressX < halfWidth + this.entryWidth / 2) {
				// clicked on top entry: notify its click listeners
				int totalDrag = getTotalDrag();
				int topEntry = getEntryAtDrag(totalDrag);
				selectEntry(topEntry);
			} else {
				// clicked on side entry: go to the target entry
				int distance = getWidth() / 2 - pointerX;
				startGotoAnim(distance);
			}
		} else {
			// end drag
			this.currentDrag += this.endDragX - this.startDragX;
			long currentTime = Util.platformTimeMillis();
			if (currentTime - this.lastDragTime < RELEASE_WITH_NO_MOVE_DELAY) {
				// throw the carousel!
				float speed = (float) (pointerX - this.lastPressX) / (currentTime - this.lastPressTime);
				int distance = (int) (speed * GOTO_ANIM_DURATION * GOTO_ANIM_SPEED);
				startGotoAnim(distance);
			} else {
				// just stop the carousel
				startGotoAnim(0);
			}
		}

		// reset drag
		this.startDragX = 0;
		this.endDragX = 0;
		this.noDrag = false;
	}

	private MotionAnimation createLaunchAnimation() {
		this.launchAnimationFinished = false;
		Motion motion = new Motion(QuartEaseOutFunction.INSTANCE, 0, SELECTED_ANIM_STOP_VALUE, SELECTED_ANIM_DURATION);
		return new MotionAnimation(getDesktop().getAnimator(), motion, new MotionAnimationListener() {
			@Override
			public void tick(int value, boolean finished) {
				ActivityLauncher.this.launchAppSizeRatio = value;
				if (finished) {
					final Activity activity = ActivityLauncher.this.entries[ActivityLauncher.this.selectedEntry].getActivity();
					MicroUI.callSerially(new Runnable() { // avoid Animator.stopAllAnimations() being called in a tick()
						@Override
						public void run() {
							ActivityLauncher.this.listener.onActivityLaunched(activity);
						}
					});
				}
			}
		});
	}

	private boolean isEntrySelected() {
		return (this.selectedEntry != -1 || Util.platformTimeMillis() - this.selectedTime < SELECTED_ANIM_DURATION);
	}

	private void selectEntry(int entryIndex) {
		if (this.selectedEntry == -1) {
			this.selectedEntry = entryIndex;
			this.unselectedEntry = -1;
			this.selectedTime = Util.platformTimeMillis();
			createLaunchAnimation().start();
			startAnimation();
		}
	}

	private void unselectEntry() {
		if (this.selectedEntry != -1) {
			this.unselectedEntry = this.selectedEntry;
			this.selectedEntry = -1;
			this.selectedTime = Util.platformTimeMillis();
			startAnimation();
		}
	}

	private void startGotoAnim(int distance) {
		// start the goto animation with the given distance
		int targetEntry = getEntryAtDrag(this.currentDrag + distance);
		this.gotoAnimDistance = getDragOfEntry(targetEntry) - this.currentDrag;
		this.gotoAnimStep = 0;

		if (this.gotoAnimDistance != 0) {
			this.gotoAnimStartTime = Util.platformTimeMillis();
			startAnimation();
		}
	}

	private void stopGotoAnim() {
		this.currentDrag += this.gotoAnimDistance * this.gotoAnimStep / GOTO_ANIM_STEPS;
		this.gotoAnimDistance = 0;
	}

	private int getTotalDrag() {
		int totalDrag = this.currentDrag + (this.endDragX - this.startDragX);
		totalDrag += this.gotoAnimDistance * this.gotoAnimStep / GOTO_ANIM_STEPS;
		return totalDrag;
	}

	private int getDragOfEntry(int entryIndex) {
		return -entryIndex * this.entryWidth;
	}

	private int getEntryAtDrag(int drag) {
		int closest = Math.round((float) -drag / this.entryWidth);
		closest = XMath.limit(closest, 0, this.entries.length - 1);
		return closest;
	}

	private static VectorFont getFont(Style style) {
		return style.getExtraObject(FONT_STYLE, VectorFont.class,
				KernelServiceProvider.getFontService().getRegularFont());
	}

	private static float getFontSize(Style style) {
		return style.getExtraFloat(FONT_SIZE_STYLE, DEFAULT_FONT_SIZE);
	}

	private static String getTimeText() {
		long currentLocalTime = TimeUtils.getCurrentLocalTime();
		int hour = (int) ((currentLocalTime / TimeUtils.MILLIS_IN_HOUR) % TimeUtils.HOURS_IN_DAY);
		int minute = (int) ((currentLocalTime / TimeUtils.MILLIS_IN_MINUTE) % TimeUtils.MINUTES_IN_HOUR);

		StringBuilder stringBuilder = new StringBuilder();
		if (hour < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(hour);
		stringBuilder.append(':');
		if (minute < 10) {
			stringBuilder.append('0');
		}
		stringBuilder.append(minute);
		return stringBuilder.toString();
	}
}
