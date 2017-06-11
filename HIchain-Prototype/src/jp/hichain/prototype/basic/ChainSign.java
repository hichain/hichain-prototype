package jp.hichain.prototype.basic;

import java.util.Map;

import jp.hichain.prototype.concept.CardColor;
import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.SignDir;

public class ChainSign implements Cloneable {
	private SignChar character;
	private SignDir direction;
	private SignPS ps;
	private SignNum number;
	private Map<CardColor, SignImage> images;

	/**
	 * コンストラクタ
	 * @param _ch Sign Character (SC)
	 * @param _ps Sign PS (SPS)
	 * @param _num Sign Number (SN)
	 * @param _images Sign Image (SI)
	 */
	public ChainSign(SignChar _ch, SignPS _ps, SignNum _num, Map<CardColor, SignImage> _images) {
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
	public SignChar getSC() {
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

	@Override
	public ChainSign clone() throws CloneNotSupportedException {
		ChainSign sign = (ChainSign)super.clone();
		sign.character = character;
		sign.direction = direction;
		sign.ps = ps;
		sign.number = number;
		sign.images = images;

		return sign;
	}
}
