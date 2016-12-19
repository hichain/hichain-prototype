package jp.hichain.prototype.basic;

import java.awt.geom.AffineTransform;
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
	private AffineTransform filter; //SIのフィルター

	/**
	 * 盤上の空のBS
	 * @param _x x座標
	 * @param _y y座標
	 * @see BoardSign#constructor(int, int)
	 */
	public CompleteBS (int _x, int _y) {
		super(_x, _y);
		filter = new AffineTransform();
	}

	/**
	 * 場のBS
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir
	 */
	public CompleteBS (int _player, char _ch, int _dir) {
		super(_player, _ch, _dir);
		filter = new AffineTransform();
	}

	public CompleteBS(int _player, char _ch, int _dir, int [] _nums, int _ps, BufferedImage _image) {
		super(_player, _ch, _dir);
		filter = new AffineTransform();
		putSign(_player, _ch, _dir, _nums, _ps, _image);
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
	 * SIのフィルターを返す
	 * @return SIのフィルター
	 */
	public AffineTransform getFilter() {
		return filter;
	}

	/**
	 * 文字を置く
	 * プレイヤー番号、文字、文字の向き、SN、PS、SIを代入 (座標は置換しない)
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 * @param _nums 4方向のSN
	 * @param _ps PS
	 * @param _image SI
	 */
	public void putSign(int _player, char _ch, int _dir, int [] _nums, int _ps, BufferedImage _image) {
		putSign(_player, _ch, _dir);
		nums = _nums;
		ps = _ps;
		image = _image;
		rotate(_dir);
	}

	/**
	 * 文字を置く (BS指定)
	 * @param _bs CompleteBS
	 */
	public void putSign(CompleteBS _bs) {
		putSign( _bs.getPlayer(), _bs.getChar(), _bs.getDir(), _bs.getNums(), _bs.getPS(), _bs.getImage() );
	}

	/**
	 * 文字を回転させる
	 * @param _dir 回転数(反時計回り)
	 */
	public void rotate(int _dir) {
		_dir %= 4;
		rotatePS(_dir);
		rotateSN(_dir);
		rotateSI(_dir);
		setDir( (getDir() + _dir) % 4 );
	}

	/**
	 * 反時計回りに90度文字を回転させる
	 */
	public void rotate() {
		rotate(1);
	}

	/**
	 * SIをリサイズする
	 * @param _r 一辺の長さ
	 */
	public void resize(double _r) {
		filter.setToScale( _r/image.getWidth(),_r/image.getHeight() );
		rotateSI(getDir());	//画像の中心が移動するため再度フィルターに回転を加える
	}

	/**
	 * SNを回転する
	 * @param _dir 回転数(反時計回り)
	 */
	private void rotateSN(int _dir) {
		for (int i = 0; i < _dir; ++i) {
			int tmp = nums[3];
			for (int j = nums.length-1; j > 0; --j) {
				nums[j] = nums[j-1];
			}
			nums[0] = tmp;
		}
	}

	/**
	 * PSを回転する
	 * @param _dir 回転数(反時計回り)
	 */
	private void rotatePS(int _dir) {
		if (_dir <= 0) return;

		int newPs = 0;

		for (int i = 0; i < 4; i++) {
			newPs ^= (ps & (0x800 >>> 2*i)) << 4+i;    //0000 1000 0000 0000
			newPs ^= (ps & (0x400 >>> 2*i)) >>> 7-i;   //0000 0100 0000 0000
			newPs ^= (ps & (0x8000 >>> i)) >>> 11-3*i; //1000 0000 0000 0000
			newPs ^= (ps & (0x8 >>> i)) << 2+3*i;      //0000 0000 0000 1000
		}

		rotatePS(--_dir);
	}

	/**
	 * SIのフィルターに回転を加える
	 * @param _dir 回転数(反時計回り)
	 */
	private void rotateSI(int _dir) {
		filter.rotate(-_dir*Math.PI/2, image.getWidth()/2, image.getHeight()/2);
	}
}
