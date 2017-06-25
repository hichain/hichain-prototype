package jp.hichain.prototype.basic;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import jp.hichain.prototype.concept.Direction;

public class SignImage {
	BufferedImage signImage;
	AffineTransform filter;
	double imageScale;

	public SignImage(BufferedImage image) {
		signImage = image;
		filter = new AffineTransform();
	}

	public BufferedImage get() {
		return signImage;
	}

	public AffineTransform getFilter() {
		return filter;
	}

	public double getImageScale() {
		return imageScale;
	}

	public void setToScale(double scale) {
		filter.setToScale(scale, scale);
		imageScale = scale;
	}

	public void rotate(Direction.Relation dir) {
		int plusminus = 0;
		switch (dir) {
		case LEFT:
			plusminus = 1;
			break;
		case RIGHT:
			plusminus = -1;
			break;
		default:
			break;
		}

		filter.rotate(plusminus*Math.PI/2, signImage.getWidth()/2, signImage.getHeight()/2);
	}
}
