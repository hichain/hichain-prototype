package jp.hichain.prototype.basic;

/**
 * 手を表すBS
 * AIにとって評価の基準となるデータが入る
 * AdBSにゲーム進行における前の手、次に取り得る手を持つ
 * 手の評価や深さ(=何ターン目か)を持つ
 * @author Tokiwa
 */
public class Move extends AdBoardSign {
	private Move parent;      //親の手
	private Move [] children; //子どもの手

	private int value;    //手の評価
	private int depth;    //手の深さ

	/**
	 * 盤上の空のBS
	 * @see AdBoardSign#constructor(Move, int)
	 */
	public Move(Move _sourceBS, int _source) {
		super(_sourceBS, _source);
	}

	/**
	 * 盤上の空のBS (ルート座標限定)
	 * @see AdBoardSign#constructor(int, int)
	 */
	public Move(int _x, int _y) {
		super(_x, _y);
	}

	/**
	 * 親の手を返す
	 * @return 親の手
	 */
	public Move getParent() {
		return parent;
	}

	/**
	 * 子どもの手を返す
	 * @return 子どもの手
	 */
	public Move [] getChildren() {
		return children;
	}

	/**
	 * 手の評価を返す
	 * @return 手の評価
	 */
	public int getValue() {
		return value;
	}

	/**
	 * 手の深さを返す
	 * @return 手の深さ
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * 親子の手をセットする
	 * @param _parent 親の手
	 * @param _children 子どもの手
	 */
	public void setFamily(Move _parent, Move [] _children) {
		parent = _parent;
		children = _children;
	}

	/**
	 * 手の評価をセットする
	 * @param _value 手の評価
	 * @param _depth 手の深さ
	 */
	public void setValue(int _value, int _depth) {
		value = _value;
		depth = _depth;
	}
}
