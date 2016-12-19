package jp.hichain.prototype.basic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class SIPanel extends JPanel {

	private CompleteBS bs;

	public SIPanel(CompleteBS _bs) {
		bs = _bs;
	}

	@Override
	public void paintComponent(Graphics g) {
		BufferedImage image = bs.getImage();
		AffineTransform af = bs.getFilter();

		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(image, af, this);
	}

}
