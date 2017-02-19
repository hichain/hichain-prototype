package jp.hichain.prototype.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.concept.CardColor;

public class SIPanel extends JPanel {

	private ChainSign sign;

	public SIPanel(ChainSign _sign) {
		sign = _sign;
	}

	@Override
	public void paintComponent(Graphics g) {
		BufferedImage image = sign.getSI(CardColor.BLACK).get();
		AffineTransform af = sign.getSI(CardColor.BLACK).getFilter();

		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(image, af, this);
	}

}
