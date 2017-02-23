package jp.hichain.prototype.basic;

import java.util.Map;

import jp.hichain.prototype.concept.CardColor;
import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.SignDir;

public class ChainSign {
	private char character;
	private SignDir direction;
	private SignPS ps;
	private SignNum number;
	private Map<CardColor, SignImage> images;

	/**
	 * コンストラクタ
	 * @param _ch Sign Character (SC)
	 * @param _dir Sign Dir (SD)
	 * @param _ps Sign PS (SPS)
	 * @param _num Sign Number (SN)
	 * @param _image Sign Image (SI)
	 */
	public ChainSign(char _ch, SignPS _ps, SignNum _num, Map<CardColor, SignImage> _images) {
		character = _ch;
		direction = SignDir.NORTH;
		ps = _ps;
		number = _num;
		images = _images;
	}

	public void rotate(Direction.Relation _dir) {
		direction = direction.get(_dir);
		ps.rotate(_dir);
		number.rotate(_dir);
		for (SignImage signImage : images.values()) {
			signImage.rotate(_dir);
		}
	}

	public void resize(double scale) {
		for (SignImage signImage : images.values()) {
			signImage.setToScale(scale);
		}
	}

	/**
	 * SCを返す
	 * @return SC
	 */
	public char getSC() {
		return character;
	}

	/**
	 * SDを返す
	 * @return SD
	 */
	public SignDir getSD() {
		return direction;
	}

	/**
	 * SPSを返す
	 * @return MPS
	 */
	public SignPS getSPS() {
		return ps;
	}

	/**
	 * SNを返す
	 * @return SN
	 */
	public SignNum getSN() {
		return number;
	}

	/**
	 * SIを返す
	 * @return SI
	 */
	public SignImage getSI(CardColor color) {
		return images.get(color);
	}
}
