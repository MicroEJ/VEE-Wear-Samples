/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.flower.watchface;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.TimeService;
import com.microej.wear.util.renderable.RenderableDisplayable;

import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.microvg.LinearGradient;
import ej.microvg.Matrix;
import ej.microvg.Path;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorGraphicsPainter.Direction;
import ej.microvg.VectorImage;
import ej.mwt.animation.Animation;
import ej.mwt.animation.Animator;
import ej.mwt.util.Alignment;
import ej.widget.render.ImagePainter;

/**
 * Renders the Flower watchface.
 * <p>
 * This watchface displays a background image, 3 vector hands and a gradient.
 */
public class FlowerDisplayable extends RenderableDisplayable {

	private static final float FULL_ANGLE = 360.0f;
	private static final float DATE_FONT_SIZE_RATIO = 0.045f;
	private static final float DATE_CIRCLE_RADIUS_RATIO = 0.95f;
	private static final int TEXT_ANGLE = 165;
	private static final String SEPARATOR = "  ";
	private static final String IMAGES_PATH = "/images/";
	private static final int[] RADAR_SWEEP_GRADIENT_COLORS = new int[] { 0xFF000000, 0xCC000000, 0x00FF007D,
			0x00000000 };
	private static final float[] RADAR_SWEEP_GRADIENT_POSITIONS = new float[] { 0, 0.15f, 0.83f, 1f };
	private static final float RADAR_SWEEP_GRADIENT_ANGLE = 105;
	private static final long MILLISECONDS_IN_SECOND = 1000L;
	private static final int NANOS_IN_MILLISECOND = 1_000_000;
	private static final int MILLIS_IN_MINUTE = 60_000;
	private static final int MILLIS_IN_HOUR = 3600000;
	private static final int MILLIS_IN_HALFDAY = 43200000;
	private static final long MILLIS_IN_SECOND = 1000L;

	private final Animator animator;
	private final Animation animation;
	private long currentLocalTime;
	private final Image backgroundImage;
	private final Image centerImage;
	private final VectorImage hourHand;
	private final VectorImage minuteHand;
	private final VectorImage secondHand;
	private final Path radarSweepPath;
	private final LinearGradient radarSweepGradient;
	private final VectorFont font;

	/**
	 * Creates a Flower displayable.
	 */
	public FlowerDisplayable() {
		this.backgroundImage = Image.getImage(IMAGES_PATH + "flower_background.png");
		this.centerImage = Image.getImage(IMAGES_PATH + "flower_center.png");
		this.hourHand = VectorImage.getImage(IMAGES_PATH + "flower_hour.xml");
		this.minuteHand = VectorImage.getImage(IMAGES_PATH + "flower_minute.xml");
		this.secondHand = VectorImage.getImage(IMAGES_PATH + "flower_second.xml");
		int radius = Display.getDisplay().getWidth() / 2;
		this.radarSweepPath = createRadarSweepPath(radius);
		this.radarSweepGradient = createRadarSweepGradient(radius);
		this.font = KernelServiceProvider.getFontService().getRegularFont();
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
	public void render(GraphicsContext gc) {
		int width = gc.getWidth();
		int height = gc.getHeight();

		// render background
		Painter.drawImage(gc, this.backgroundImage, 0, 0);

		// render hands
		renderHourHand(gc);
		renderMinuteHand(gc);
		renderRadarSweep(gc);
		renderSecondHand(gc);

		// render center image
		ImagePainter.drawImageInArea(gc, this.centerImage, 0, 0, width, height, Alignment.HCENTER, Alignment.VCENTER);

		// render date
		renderDate(gc, this.currentLocalTime);
	}

	@Override
	protected void onShown() {
		this.animator.startAnimation(this.animation);
		updateCurrentTime();
	}

	@Override
	protected void onHidden() {
		this.animator.stopAnimation(this.animation);
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

	private void renderRadarSweep(GraphicsContext gc) {
		float angle = FULL_ANGLE * (this.currentLocalTime % MILLIS_IN_MINUTE) / MILLIS_IN_MINUTE;
		Matrix matrix = new Matrix();
		matrix.setRotate(angle);
		matrix.postTranslate(gc.getWidth() / 2f, gc.getHeight() / 2f);
		VectorGraphicsPainter.fillGradientPath(gc, this.radarSweepPath, matrix, this.radarSweepGradient);
	}

	private void renderDate(GraphicsContext gc, long currentLocalTime) {
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(currentLocalTime / MILLISECONDS_IN_SECOND,
				(int) ((currentLocalTime % MILLISECONDS_IN_SECOND) * NANOS_IN_MILLISECOND), ZoneOffset.UTC);
		String text = formatDateTime(dateTime);
		int width = gc.getWidth();
		float fontSize = DATE_FONT_SIZE_RATIO * width;
		Matrix matrix = new Matrix();
		float translateX = width / 2f;
		matrix.setRotate(TEXT_ANGLE);
		matrix.postTranslate(translateX, gc.getHeight() / 2f);
		gc.setColor(Colors.WHITE);
		VectorGraphicsPainter.drawStringOnCircle(gc, text, this.font, fontSize, matrix,
				DATE_CIRCLE_RADIUS_RATIO * translateX, Direction.COUNTER_CLOCKWISE);
	}

	private static Path createRadarSweepPath(float radius) {
		Path path = new Path();
		path.moveTo(0, -radius);
		path.lineTo(-radius, -radius);
		path.lineTo(-radius, radius);
		path.lineTo(0, radius);
		return path;
	}

	private static LinearGradient createRadarSweepGradient(float radius) {
		LinearGradient gradient = new LinearGradient(0f, 0f, radius, 0f, RADAR_SWEEP_GRADIENT_COLORS,
				RADAR_SWEEP_GRADIENT_POSITIONS);
		Matrix matrix = gradient.getMatrix();
		matrix.setRotate(RADAR_SWEEP_GRADIENT_ANGLE);
		matrix.postTranslate(0, -radius);
		return gradient;
	}

	private static String formatDateTime(LocalDateTime dateTime) {
		StringBuilder builder = new StringBuilder();
		int monthValue = dateTime.getMonthValue();
		int dayValue = dateTime.getDayOfMonth();
		int nanoValue = dateTime.getNano() / 10_000_000;
		builder.append(dateTime.getYear()).append(monthValue < 10 ? "-0" : "-").append(monthValue)
				.append(dayValue < 10 ? "-0" : "-").append(dayValue);
		int hourValue = dateTime.getHour();
		int minuteValue = dateTime.getMinute();
		int secondValue = dateTime.getSecond();
		builder.append(SEPARATOR).append(hourValue < 10 ? "0" : "").append(hourValue)
				.append(minuteValue < 10 ? ":0" : ":").append(minuteValue).append(secondValue < 10 ? ":0" : ":")
				.append(secondValue).append(nanoValue < 10 ? ".0" : ".").append(nanoValue);
		return builder.toString();
	}
}
