package com.microej.example.wear.training.activity.widget;

import ej.annotation.Nullable;
import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsException;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that displays a vector image from a specified resource path.
 */
public class VectorImageWidget extends Widget {

	private String imagePath;
	private int scale = 1;
	@Nullable
	private VectorImage image;

	/**
	 * Creates an image widget with the resource path of the image to display.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 * @param scale
	 *            the scaling factor for the image.
	 * @throws VectorGraphicsException
	 *             if the image cannot be loaded.
	 */
	public VectorImageWidget(String imagePath, int scale) {
		this.imagePath = imagePath;
		this.scale = scale;
		loadImage();
	}

	@Override
	public int getWidth() {
		VectorImage image = this.image;
		return (int) image.getWidth();
	}

	@Override
	public int getHeight() {
		VectorImage image = this.image;
		return (int) image.getHeight();
	}

	/**
	 * Creates an image widget with the resource path of the image to display and its enabled state.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 * @param enabled
	 *            <code>true</code> if this image widget is to be enabled, <code>false</code> otherwise.
	 */
	protected VectorImageWidget(String imagePath, boolean enabled) {
		super(enabled);
		this.imagePath = imagePath;
	}

	@Override
	protected void onDetached() {
		super.onDetached();
		closeImage();
	}

	private void loadImage() {
		this.image = VectorImage.getImage(this.imagePath);
	}

	private void closeImage() {
		VectorImage image = this.image;
		if (image != null) {
			this.image = null;
		}
	}

	/**
	 * Sets the image path.
	 *
	 * @param imagePath
	 *            the resource path of the image to display.
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
		if (isAttached()) {
			closeImage();
			loadImage();
		}
	}

	/**
	 * Sets the scaling factor for the image.
	 *
	 * @param scale
	 *            the scaling factor to apply.
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		VectorImage image = this.image;
		Style style = getStyle();
		g.setColor(style.getColor());
		Matrix sizeMatrix = new Matrix();
		float sx = this.scale / image.getWidth();
		float sy = this.scale / image.getHeight();
		int x = Alignment.computeLeftX(this.scale, 0, contentWidth, Alignment.HCENTER);
		int y = Alignment.computeTopY(this.scale, 0, contentHeight, Alignment.VCENTER);
		sizeMatrix.setTranslate(x, y);
		sizeMatrix.preScale(sx, sy);
		VectorGraphicsPainter.drawImage(g, image, sizeMatrix);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		VectorImage image = this.image;
		size.setSize((int) image.getWidth(), (int) image.getHeight());
	}

}
