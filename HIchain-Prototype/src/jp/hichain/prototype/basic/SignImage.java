package jp.hichain.prototype.basic;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import jp.hichain.prototype.concept.SignDir;

public class SignImage {
	BufferedImage signImage;
	AffineTransform filter;
	double imageScale;

	public SignImage(BufferedImage image) {
		signImage = image;
	}

	public void setToScale(double scale) {
		filter.setToScale(scale, scale);
		imageScale = scale;
	}

	public void rotate(SignDir.ROTATION dir) {
		int plusminus;
		if (dir == SignDir.ROTATION.LEFT) {
			plusminus = 1;
		} else {
			plusminus = -1;
		}
		filter.rotate(plusminus*Math.PI/2, signImage.getWidth()/2, signImage.getHeight()/2);
	}

	public void setToRotation(SignDir dir) {
		int rotation;
		switch (dir) {
			case WEST:
				rotation = 1;
			break;
			case EAST:
				rotation = -1;
			break;
			case SOUTH:
				rotation = 2;
			break;
			default:
				rotation = 0;
			break;
		}
		filter.setToRotation(rotation*Math.PI/2, signImage.getWidth()/2, signImage.getHeight()/2);
		setToScale(imageScale);
	}
}
