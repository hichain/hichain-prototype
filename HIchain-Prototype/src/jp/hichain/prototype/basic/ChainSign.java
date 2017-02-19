package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.Direction;

public class ChainSign {
	private char character;
	private Direction direction;
	private SignPS ps;
	private SignNum number;
	private SignImage image;

	/**
	 * コンストラクタ
	 * @param _ch Sign Character (SC)
	 * @param _dir Sign Dir (SD)
	 * @param _ps Sign PS (SPS)
	 * @param _num Sign Number (SN)
	 * @param _image Sign Image (SI)
	 */
	public ChainSign(char _ch, Direction _dir, SignPS _ps, SignNum _num, SignImage _image) {
		character = _ch;
		direction = _dir;
		ps = _ps;
		number = _num;
		image = _image;
	}

	public void rotate(Direction.Relation _dir) {
		direction = direction.getRelation(_dir);
		ps.rotate(_dir);
		number.rotate(_dir);
		image.rotate(_dir);
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
	public Direction getSD() {
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
	public SignImage getSI() {
		return image;
	}
}