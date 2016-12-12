package jp.hichain.prototype.basic;

import java.awt.image.BufferedImage;

/**
 * BoardSignにSN, PS, SIを追加したクラス
 * @author Tokiwa
 * @see BoardSign
 * @see AdBoardSign
 */
public class CompleteBS extends BoardSign {
	private int [] nums;  //SN
	private int ps;       //PS
	private BufferedImage image; //SI

	/**
	 * 盤上の空のBS
	 * @param _x x座標
	 * @param _y y座標
	 * @see BoardSign#constructor(int, int)
	 */
	public CompleteBS (int _x, int _y) {
		super(_x, _y);
	}

	/**
	 * 場のBS
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir
	 */
	public CompleteBS (int _player, char _ch, int _dir) {
		super(_player, _ch, _dir);
	}

	/**
	 * SNを返す
	 * @param _dir 文字の向き
	 * @return SN (16bit)
	 */
	public int getNum(int _dir) {
		return nums[_dir];
	}

	/**
	 * SNを全て返す
	 * @return 4方向全てのSN
	 */
	public int [] getNums() {
		return nums;
	}

	/**
	 * PSを返す
	 * @return PS
	 */
	public int getPS() {
		return ps;
	}

	/**
	 * SIを返す
	 * @return SI
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * 文字を置く
	 * プレイヤー番号、文字、文字の向きを代入し、それらよりSN・PS・SIを代入 (座標は置換しない)
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 */
	@Override
	public void putSign(int _player, char _ch, int _dir) {
		super.putSign(_player, _ch, _dir);
		nums = SignData.getNum(_ch, _dir);
		ps = SignData.getPS(_ch, _dir);
		image = SignData.getImage(_player, _ch, _dir);
	}

	/**
	 * 文字を置く (BS指定)
	 * @param _bs BS
	 */
	void putSign(BoardSign _bs) {
		putSign( _bs.getPlayer(), _bs.getChar(), _bs.getDir());
	}
}
