/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.sport.watchface;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.components.ComplicationDataSource;
import com.microej.wear.services.ResourceService;
import com.microej.wear.services.TimeService;
import com.microej.wear.util.renderable.RenderableDisplayable;

import ej.annotation.Nullable;
import ej.basictool.ArrayTools;
import ej.bon.XMath;
import ej.drawing.ShapePainter;
import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.microvg.Matrix;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.animation.Animation;
import ej.mwt.animation.Animator;
import ej.mwt.util.Rectangle;

/**
 * Renders the Sport watchface.
 * <p>
 * This watchface displays a background image, 3 vector hands and 4 complications.
 */
public class SportDisplayable extends RenderableDisplayable {

	private static final float FULL_ANGLE = 360.0f;
	private static final int MILLIS_IN_MINUTE = 60_000;
	private static final int MILLIS_IN_HOUR = 3600000;
	private static final int MILLIS_IN_HALFDAY = 43200000;
	private static final long MILLIS_IN_SECOND = 1000L;

	private static class Complication {

		private final Rectangle bounds;
		private @Nullable ComplicationDataSource source;

		private Complication(Rectangle bounds) {
			this.bounds = bounds;
		}
	}

	/**
	 * The size of complications, from the center of the face, expressed as a ratio of the display width.
	 */
	private static final float COMPLICATION_SIZE_RATIO = 0.25f;
	/**
	 * The shift of complications, from the center of the face, expressed as a ratio of the display width.
	 */
	private static final float COMPLICATION_SHIFT_RATIO = 0.19f;

	private static final int ACCENT_COLOR = 0x00dcf8;
	private static final int BACKGROUND_COLOR = 0x303838;
	private static final float THICKNESS_RATIO = 0.074f;
	private static final int START_ANGLE = 90;
	private static final int FADE = 1;
	private static final float TEXT_SIZE_RATIO = 0.24f;

	// the offsets in complication bounds array of each complication
	private static final int TOP = 0;
	private static final int BOTTOM = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;

	private final Animator animator;
	private final Animation animation;
	private final VectorImage hourHand;
	private final VectorImage minuteHand;
	private final VectorImage secondHand;
	private final VectorFont font;
	private final Rectangle batteryComplicationBounds;
	private final Complication[] complications;
	private long currentLocalTime;
	@Nullable
	private ResourceImage backgroundImage;
	@Nullable
	private ResourceImage complicationBackgroundImage;
	@Nullable
	private ResourceImage powerImage;

	/**
	 * Creates a Sport displayable.
	 */
	public SportDisplayable() {
		this.hourHand = VectorImage.getImage("/images/sport_hour.xml");
		this.minuteHand = VectorImage.getImage("/images/sport_minute.xml");
		this.secondHand = VectorImage.getImage("/images/sport_second.xml");
		this.font = KernelServiceProvider.getFontService().getRegularFont();

		Rectangle[] complicationBounds = computeComplicationBounds();
		Rectangle batteryComplicationBounds = complicationBounds[RIGHT];
		assert (batteryComplicationBounds != null);
		this.batteryComplicationBounds = batteryComplicationBounds;

		ComplicationDataSource[] sources = KernelServiceProvider.getComponentService().getComplicationDataSources();
		Complication[] complications = new Complication[3];
		for (int i = 0; i < complications.length; i++) {
			Rectangle bounds = complicationBounds[i];
			assert (bounds != null);
			complications[i] = new Complication(bounds);
			if (i < sources.length) {
				complications[i].source = sources[i];
			}
		}
		this.complications = complications;
		this.animator = new Animator();
		this.animation = new Animation() {
			@Override
			public boolean tick(long currentTimeMillis) {
				updateCurrentTime();
				requestRender();
				return true;
			}
		};

		updateCurrentTime();
	}

	@Override
	public void onAttached() {
		super.onAttached();

		ResourceService resourceService = KernelServiceProvider.getResourceService();
		this.backgroundImage = ResourceImage.loadImage(resourceService.getImagePath("/images/sport_background.png"));
		this.complicationBackgroundImage = ResourceImage
				.loadImage(resourceService.getImagePath("/images/sport_complication_bkg.png"));
		this.powerImage = ResourceImage.loadImage(resourceService.getImagePath("/images/ic_power.png"));

		for (Complication complication : this.complications) {
			ComplicationDataSource source = complication.source;
			if (source != null && source.hasIcon()) {
				source.onIconAttached();
			}
		}
	}

	@Override
	public void onDetached() {
		super.onDetached();

		ResourceImage image = this.backgroundImage;
		if (image != null) {
			image.close();
			this.backgroundImage = null;
		}
		image = this.complicationBackgroundImage;
		if (image != null) {
			image.close();
			this.complicationBackgroundImage = null;
		}
		image = this.powerImage;
		if (image != null) {
			image.close();
			this.powerImage = null;
		}

		for (Complication complication : this.complications) {
			ComplicationDataSource source = complication.source;
			if (source != null && source.hasIcon()) {
				source.onIconDetached();
			}
		}
	}

	@Override
	protected void onShown() {
		super.onShown();
		this.animator.startAnimation(this.animation);
		updateCurrentTime();
	}

	@Override
	protected void onHidden() {
		this.animator.stopAnimation(this.animation);
	}

	@Override
	public void render(GraphicsContext g) {
		ResourceImage image = this.backgroundImage;
		assert (image != null);
		// render the background
		Painter.drawImage(g, image, 0, 0);

		// render the dynamic complications
		renderDynamicComplications(g);

		// render the battery complication
		renderBatteryComplication(g);

		// render the hands
		renderHourHand(g);
		renderMinuteHand(g);
		renderSecondHand(g);
	}

	/**
	 * Renders the hour hand, using the specified vector image.
	 *
	 * @param g
	 *            the graphics context to use
	 */
	private void renderHourHand(GraphicsContext g) {
		float angle = FULL_ANGLE * (this.currentLocalTime % MILLIS_IN_HALFDAY) / MILLIS_IN_HALFDAY;
		renderHand(g, this.hourHand, angle);
	}

	/**
	 * Renders the minute hand, using the specified vector image.
	 *
	 * @param g
	 *            the graphics context to use
	 */
	private void renderMinuteHand(GraphicsContext g) {
		float angle = FULL_ANGLE * (this.currentLocalTime % MILLIS_IN_HOUR) / MILLIS_IN_HOUR;
		renderHand(g, this.minuteHand, angle);
	}

	/**
	 * Renders the second hand, using the specified vector image.
	 *
	 * @param g
	 *            the graphics context to use
	 */
	private void renderSecondHand(GraphicsContext g) {
		float angle = FULL_ANGLE * (this.currentLocalTime % MILLIS_IN_MINUTE) / MILLIS_IN_MINUTE;
		renderHand(g, this.secondHand, angle);
	}

	private static void renderHand(GraphicsContext g, VectorImage image, float angle) {
		int width = g.getWidth();
		int height = g.getHeight();
		float imageWidth = image.getWidth();
		float scale = width / imageWidth;
		Matrix matrix = new Matrix();
		matrix.setRotate(angle);
		matrix.preScale(scale, scale);
		matrix.preTranslate(-imageWidth / 2f, -image.getHeight() / 2f);
		matrix.postTranslate(width / 2f, height / 2f);
		VectorGraphicsPainter.drawImage(g, image, matrix);
	}

	private void updateCurrentTime() {
		TimeService timeService = KernelServiceProvider.getTimeService();
		long currentTime = timeService.getCurrentTime();
		int currentZoneOffset = timeService.getTimeZoneOffset();
		this.currentLocalTime = currentTime + currentZoneOffset * MILLIS_IN_SECOND;
	}

	private void renderBatteryComplication(GraphicsContext g) {
		Rectangle bounds = this.batteryComplicationBounds;
		float progress = KernelServiceProvider.getDeviceService().getBatteryLevel() / 100.0f;
		ResourceImage complicationBackgroundImageTemp = this.complicationBackgroundImage;
		ResourceImage powerImageTemp = this.powerImage;
		assert (complicationBackgroundImageTemp != null && powerImageTemp != null);
		DottedProgressComplication.render(g, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(),
				progress, complicationBackgroundImageTemp, powerImageTemp, this.font, Colors.WHITE);
	}

	private void renderDynamicComplications(GraphicsContext g) {
		for (Complication complication : this.complications) {
			assert (complication != null);
			renderDynamicComplication(g, complication);
		}
	}

	private void renderDynamicComplication(GraphicsContext g, Complication complication) {

		Rectangle bounds = complication.bounds;
		int x = bounds.getX();
		int y = bounds.getY();
		int width = bounds.getWidth();
		int height = bounds.getHeight();

		ResourceImage complicationBackgroundImageTemp = this.complicationBackgroundImage;

		assert (complicationBackgroundImageTemp != null);
		// render background image
		Painter.drawImage(g, complicationBackgroundImageTemp, x, y);

		// get source
		ComplicationDataSource source = complication.source;
		if (source == null) {
			return;
		}

		// render progress
		if (source.hasProgress()) {
			g.setColor(BACKGROUND_COLOR);
			int thickness = (int) (THICKNESS_RATIO * width);
			int origin = thickness / 2;
			int diameter = width - 2 * origin + 1;
			ShapePainter.drawThickFadedCircle(g, x + origin, y + origin, diameter, thickness, FADE);

			g.setColor(ACCENT_COLOR);
			float progress = XMath.limit(source.getProgress(), 0.0f, 1.0f);
			float arcAngle = -progress * 360;
			ShapePainter.drawThickFadedCircleArc(g, x + origin, y + origin, diameter, START_ANGLE, arcAngle,
					thickness - 2, FADE, ShapePainter.Cap.ROUNDED, ShapePainter.Cap.ROUNDED);
		}

		// render icon
		if (source.hasIcon()) {
			g.setColor(Colors.WHITE);
			int iconWidth = width / 3;
			int iconHeight = height / 3;
			int iconX = (width - iconWidth) / 2;
			int iconY = height / 3 - iconHeight / 2;
			source.renderIcon(g, x + iconX, y + iconY, iconWidth, iconHeight);
		}

		// render text
		if (source.hasText()) {
			VectorFont font = this.font;
			String text = source.getText();
			float textFontSize = TEXT_SIZE_RATIO * width;
			int textWidth = (int) font.measureStringWidth(text, textFontSize);
			int fontHeight = (int) font.getHeight(textFontSize);
			int textX = x + (width - textWidth) / 2;
			int textY = y + height * 2 / 3 - fontHeight / 2;
			g.setColor(Colors.WHITE);
			VectorGraphicsPainter.drawString(g, text, font, textFontSize, textX, textY);
		}
	}

	@Override
	public boolean handleEvent(int event) {
		if (super.handleEvent(event)) {
			return true;
		}

		if (Event.getType(event) == Pointer.EVENT_TYPE && Buttons.isReleased(event)) {
			Pointer pointer = (Pointer) Event.getGenerator(event);
			int pointerX = pointer.getX();
			int pointerY = pointer.getY();

			for (Complication complication : this.complications) {
				Rectangle bounds = complication.bounds;
				int boundsX = bounds.getX();
				int boundsY = bounds.getY();
				if (pointerX >= boundsX && pointerX < boundsX + bounds.getWidth() //
						&& pointerY >= boundsY && pointerY < boundsY + bounds.getHeight()) {
					handleComplicationTap(complication);
					return true;
				}
			}
		}

		return false;
	}

	private void handleComplicationTap(Complication complication) {
		ComplicationDataSource[] sources = KernelServiceProvider.getComponentService().getComplicationDataSources();

		ComplicationDataSource oldSource = complication.source;
		int oldIndex = -1;
		if (oldSource != null) {
			oldIndex = ArrayTools.getIndex(sources, oldSource);
		}

		ComplicationDataSource newSource = null;
		for (int i = oldIndex + 1; i < sources.length; i++) {
			ComplicationDataSource source = sources[i];
			assert (source != null);
			if (!isComplicationDataSourceUsed(source)) {
				newSource = source;
				break;
			}
		}

		if (oldSource != null && oldSource.hasIcon()) {
			oldSource.onIconDetached();
		}
		if (newSource != null && newSource.hasIcon()) {
			newSource.onIconAttached();
		}
		complication.source = newSource;
	}

	private boolean isComplicationDataSourceUsed(ComplicationDataSource source) {
		for (Complication complication : this.complications) {
			if (complication.source == source) {
				return true;
			}
		}
		return false;
	}

	private static Rectangle[] computeComplicationBounds() {
		Display display = Display.getDisplay();
		int displayWidth = display.getWidth();
		int size = (int) (displayWidth * COMPLICATION_SIZE_RATIO);
		int shift = (int) (displayWidth * COMPLICATION_SHIFT_RATIO);
		int x = (displayWidth - size) / 2;
		int y = (display.getHeight() - size) / 2;

		Rectangle[] bounds = new Rectangle[4];
		bounds[TOP] = new Rectangle(x, y - shift, size, size);
		bounds[BOTTOM] = new Rectangle(x, y + shift, size, size);
		bounds[LEFT] = new Rectangle(x - shift, y, size, size);
		bounds[RIGHT] = new Rectangle(x + shift, y, size, size);
		return bounds;
	}
}
