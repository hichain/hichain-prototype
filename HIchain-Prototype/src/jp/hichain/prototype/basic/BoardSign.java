package jp.hichain.prototype.basic;

/**
 * 文字のchar、プレイヤー番号、文字の向き、絶対座標を保持する
 * 盤上の文字を表す基本的なクラス
 * BS = BoardSign
 * @author Tokiwa
 * @see CompleteBS
 */
public class BoardSign {
	private int player;   //プレイヤー番号
	private char ch;      //文字
	private int dir;      //文字の向き
	private int [] pos;   //座標

	/**
	 * 盤上の空のBS
	 * 座標のみ
	 * @param _x x座標
	 * @param _y y座標
	 */
	public BoardSign(int _x, int _y) {
		player = 0;
		ch = ' ';
		dir = 0;
		pos = new int [] {_x, _y};
	}

	/**
	 * 場のBS
	 * プレイヤー番号、文字、文字の向きのみ
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 */
	public BoardSign(int _player, char _ch, int _dir) {
		player = _player;
		ch = _ch;
		dir = _dir;
		pos = new int [] {_player, 0};
	}

	/**
	 * プレイヤー番号を返す
	 * @return プレイヤー番号
	 */
	public int getPlayer() {
		return player;
	}

	/**
	 * 座標を返す
	 * @return 座標
	 */
	public int [] getPos() {
		return pos;
	}

	/**
	 * X座標を返す
	 * @return x座標
	 */
	public int getX() {
		return pos[0];
	}

	/**
	 * Y座標を返す
	 * @return y座標
	 */
	public int getY() {
		return pos[1];
	}

	/**
	 * 文字を返す
	 * @return 文字
	 */
	public char getChar() {
		return ch;
	}

	/**
	 * 文字の向きを返す
	 * @return 文字の向き
	 */
	public int getDir() {
		return dir;
	}

	/**
	 * 文字を置く
	 * プレイヤー番号、文字、文字の向きを置き換える
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 */
	public void putSign(int _player, char _ch, int _dir) {
		player = _player;
		ch = _ch;
		dir = _dir;
	}

	/**
	 * 座標をオーバーライドする
	 * @param _pos 座標
	 */
	public void setPos(int [] _pos) {
		pos = _pos;
	}

	/**
	 * 文字の向きをオーバーライドする
	 * @param _dir 文字の向き
	 */
	public void setDir(int _dir) {
		dir = _dir;
	}

	/**
	 * プレイヤー番号が等しいか返す
	 * @param _player プレイヤー番号
	 * @return true/false
	 */
	public boolean equalsPl(int _player) {
		return (player == _player);
	}

	/**
	 * 文字と文字の向きが等しいか
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 * @return true/false
	 */
	public boolean equalsChDir(char _ch, int _dir) {
		return ( (ch == _ch) && (dir == _dir) );
	}

	/**
	 * 文字と文字の向きが等しいか (プレイヤー番号込み)
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 * @return true/false
	 */
	public boolean equalsChDir(int _player, char _ch, int _dir) {
		return ( (player == _player) && (ch == _ch) && (dir == _dir) );
	}

	/**
	 * 座標が等しいか
	 * @param _x x座標
	 * @param _y y座標
	 * @return true/false
	 */
	public boolean equalsPos(int _x, int _y) {
		return ( (pos[0] == _x) && (pos[1] == _y) );
	}

	/**
	 * 座標が等しいか (プレイヤー番号込み)
	 * @param _player プレイヤー番号
	 * @param _x x座標
	 * @param _y y座標
	 * @return true/false
	 */
	public boolean equalsPos(int _player, int _x, int _y) {
		return ( (player == _player) && (pos[0] == _x) && (pos[1] == _y) );
	}

	/**
	 * 全ての値が等しいか (プレイヤー番号抜き)
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 * @param _x x座標
	 * @param _y y座標
	 * @return true/false
	 */
	public boolean equals(char _ch, int _dir, int _x, int _y) {
		return ( (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
	}

	/**
	 * 全ての値が等しいか
	 * @param _player プレイヤー番号
	 * @param _ch 文字
	 * @param _dir 文字の向き
	 * @param _x x座標
	 * @param _y y座標
	 * @return true/false
	 */
	public boolean equals(int _player, char _ch, int _dir, int _x, int _y) {
		return ( (player == _player) && (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
	}

	/**
	 * プレイヤー番号が等しいか (BoardSignクラスから)
	 * @param _bs BS
	 * @return true/false
	 */
	public boolean equalsPl(BoardSign _bs) {
		return equalsPl(_bs.getPlayer());
	}

	/**
	 * 文字と文字の向きが等しいか (BoardSignクラスから)
	 * @param _bs BS
	 * @return true/false
	 */
	public boolean equalsChDir(BoardSign _bs) {
		return equalsChDir(_bs.getChar(), _bs.getDir());
	}

	/**
	 * 座標が等しいか (BoardSignクラスから)
	 * @param _bs BS
	 * @return true/false
	 */
	public boolean equalsPos(BoardSign _bs) {
		return equalsPos(_bs.getChar(), _bs.getX(), _bs.getY());
	}

	/**
	 * 全ての値が等しいか (BoardSignクラスから)
	 * @param _bs BS
	 * @return true/false
	 */
	public boolean equals(BoardSign _bs) {
		return equals(_bs.getPlayer(), _bs.getChar(), _bs.getDir(), _bs.getX(), _bs.getY());
	}

	/**
	 * 棋譜を返す
	 * @return 棋譜
	 */
	String getRecode() {
		return ((getPlayer() == 1) ? "B" : "R") + getX() + "," + getY() + " " + getChar() + getDir();
	}
}
