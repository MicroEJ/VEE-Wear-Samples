/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.breath.activity.widget;

import ej.drawing.ShapePainter.Cap;
import ej.microvg.Path;

/**
 * A utility class that offers convenient methods for advanced operations on vector paths.
 */
public class PathHelper {

	private static final int FULL_ANGLE = 360;

	private static final int MIN_ANGLE = 0;

	private static final float CAP_TAN = 1.33f;

	private PathHelper() {
		// prevents instantiation
	}

	/**
	 * Computes the vector path representing the ellipse arc with the given diameter and thickness, starting at
	 * specified start angle.
	 *
	 * <p>
	 * Angles are interpreted such that 0 degrees is at the 3 o'clock position. A positive value indicates a
	 * counter-clockwise rotation while a negative value indicates a clockwise rotation.
	 *
	 * @param horizontalDiameter
	 *            the horizontal diameter of the ellipse.
	 * @param verticalDiameter
	 *            the vertical diameter of the ellipse.
	 * @param thickness
	 *            the thickness of the path.
	 * @param startAngle
	 *            the beginning angle of the arc.
	 * @param arcAngle
	 *            the angular extent of the arc from <code>startAngle</code>.
	 * @param cap
	 *            the cap to use for the start and end of shape.
	 * @return the path of the ellipse arc.
	 */
	public static Path computeThickShapeEllipseArc(float horizontalDiameter, float verticalDiameter, float thickness,
			float startAngle, float arcAngle, Cap cap) {

		if (arcAngle < MIN_ANGLE) {
			startAngle = startAngle + arcAngle;
			arcAngle = -arcAngle;
		}
		startAngle %= FULL_ANGLE;

		float horizontalOuterRadius = (horizontalDiameter + thickness) / 2;
		float verticalOuterRadius = (verticalDiameter + thickness) / 2;
		float horizontalInnerRadius = (horizontalDiameter - thickness) / 2;
		float verticalInnerRadius = (verticalDiameter - thickness) / 2;

		return approximateEllipseArc(horizontalOuterRadius, verticalOuterRadius, horizontalInnerRadius,
				verticalInnerRadius, (float) Math.toRadians(startAngle), (float) Math.toRadians(arcAngle), cap);
	}

	/*
	 * CircleArc approximation from Bezier curves is acceptable if the arc angle is lower than than 90 degrees. If the
	 * caller requires an arc angle greater than 90 degrees, the curve should be splitted into sections with arc angles
	 * lower than 90 degrees.
	 */
	private static Path approximateEllipseArc(float horizontalOuterRadius, float verticalOuterRadius,
			float horizontalInnerRadius, float verticalInnerRadius, float startAngle, float arcAngle, Cap cap) {

		Path pathBuffer = new Path();

		float cosStartAngle = (float) Math.cos(startAngle);
		float sinStartAngle = (float) Math.sin(startAngle);
		float cosEndAngle = (float) Math.cos(startAngle + arcAngle);
		float sinEndAngle = (float) Math.sin(startAngle + arcAngle);

		// Number of sections per curve.
		int nbSections = 1 + (int) (arcAngle / (Math.PI / 2));

		// Angle of each section
		float sectionAngle = arcAngle / nbSections;
		float tangent = 4 * (float) Math.tan(sectionAngle / 4) / 3;

		// Compute outer start point
		float outerStartX = horizontalOuterRadius * cosStartAngle;
		float outerStartY = -verticalOuterRadius * sinStartAngle;

		// Compute inner start point
		float inStartX = horizontalInnerRadius * cosStartAngle;
		float inStartY = -verticalInnerRadius * sinStartAngle;

		// Compute outer end point
		float outerEndX = horizontalOuterRadius * cosEndAngle;
		float outerEndY = -verticalOuterRadius * sinEndAngle;

		// Compute inner end point
		float innerEndX = horizontalInnerRadius * cosEndAngle;
		float innerEndY = -verticalInnerRadius * sinEndAngle;

		// Move to start point
		pathBuffer.moveTo(outerStartX, outerStartY);

		// Compute first arc (forward)
		approximateEllipseSubArc(pathBuffer, nbSections, sectionAngle, horizontalOuterRadius, verticalOuterRadius,
				startAngle, tangent, false);

		float capRadius = (horizontalOuterRadius - horizontalInnerRadius) / 2;

		// Compute end cap
		if (cap == Cap.PERPENDICULAR) {
			pathBuffer.lineTo(innerEndX, innerEndY);
		} else {
			approximateRoundedCap(pathBuffer, (innerEndX + outerEndX) / 2, (innerEndY + outerEndY) / 2, capRadius,
					startAngle + arcAngle, startAngle + arcAngle + (float) Math.PI);
		}

		// Compute second arc (backward)
		approximateEllipseSubArc(pathBuffer, nbSections, sectionAngle, horizontalInnerRadius, verticalInnerRadius,
				startAngle, tangent, true);

		// Compute start cap
		if (cap == Cap.PERPENDICULAR) {
			pathBuffer.lineTo(inStartX, inStartY);
		} else {
			approximateRoundedCap(pathBuffer, (inStartX + outerStartX) / 2, (inStartY + outerStartY) / 2, capRadius,
					startAngle + (float) Math.PI, startAngle);
		}

		return pathBuffer;
	}

	private static void approximateRoundedCap(Path pathBuffer, float x, float y, float radius, float startAngle,
			float endAngle) {
		approximateEllipseArcFragment(pathBuffer, x, y, radius, radius, startAngle, endAngle, CAP_TAN, false);
	}

	private static void approximateEllipseSubArc(Path pathBuffer, int nbSections, float sectionAngle,
			float horizontalRadius, float verticalRadius, float startAngle, float tangent, boolean revert) {
		for (int i = 0; i < nbSections; i++) {
			int factor = revert ? (nbSections - 1 - i) : i;
			float sectionStartAngle = startAngle + factor * sectionAngle;
			float sectionEndAngle = sectionStartAngle + sectionAngle;
			approximateEllipseArcFragment(pathBuffer, 0, 0, horizontalRadius, verticalRadius, sectionStartAngle,
					sectionEndAngle, tangent, revert);
		}
	}

	private static void approximateEllipseArcFragment(Path pathBuffer, float centerX, float centerY,
			float horizontalRadius, float verticalRadius, float startAngle, float endAngle, float tangent,
			boolean revert) {
		float tx;
		float ty;

		// Compute start point A1 & A2
		float aX = (float) (horizontalRadius * Math.cos(startAngle));
		float aY = (float) (verticalRadius * -Math.sin(startAngle)); // minus because y axis is inverted (0,0 is top
		// left)

		// Compute end point B1 & B2
		float bX = (float) (horizontalRadius * Math.cos(endAngle));
		float bY = (float) (verticalRadius * -Math.sin(endAngle));

		// Compute control point C
		tx = tangent * -(float) Math.sin(startAngle);
		ty = tangent * -(float) Math.cos(startAngle);

		float cX = aX + horizontalRadius * tx;
		float cY = aY + verticalRadius * ty;

		// Compute control point D
		tx = tangent * (float) Math.sin(endAngle);
		ty = tangent * (float) Math.cos(endAngle);

		float dX = bX + horizontalRadius * tx;
		float dY = bY + verticalRadius * ty;

		aX += centerX;
		aY += centerY;
		bX += centerX;
		bY += centerY;
		cX += centerX;
		cY += centerY;
		dX += centerX;
		dY += centerY;

		if (revert) {
			pathBuffer.cubicTo(dX, dY, cX, cY, aX, aY);
		} else {
			pathBuffer.cubicTo(cX, cY, dX, dY, bX, bY);
		}
	}

}
