package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.List;

public class Move {
	private static Move ROOT;
	private static Move finalMove;

	private BoardSign thisBS;
	private Move parent;			//親の手
	private List <Move> children;	//子どものとりうる手

	private int moveValue;    //手の評価
	private int moveDepth;    //手の深さ

	public Move(BoardSign _thisBS) {
		if (ROOT == null) {
			ROOT = this;
		} else {
			parent = finalMove.getParent();
		}
		thisBS = _thisBS;
		children = new ArrayList<>();
		finalMove = this;
	}

	/**
	 * 最初の手を返す
	 * @return Move
	 */
	public static Move getRoot() {
		return ROOT;
	}

	/**
	 * 最後の手を返す
	 * @return Move
	 */
	public static Move getFinalMove() {
		return finalMove;
	}

	/**
	 * このSquareのBSを返す
	 * @return BoardSign
	 */
	public BoardSign getBS() {
		return thisBS;
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
	public List <Move> getChildren() {
		return children;
	}

	/**
	 * 手の評価を返す
	 * @return 手の評価
	 */
	public int getMoveValue() {
		return moveValue;
	}

	/**
	 * 手の深さを返す
	 * @return 手の深さ
	 */
	public int getMoveDepth() {
		return moveDepth;
	}

	/**
	 * 子の手を追加する
	 * @param _parent 子の手
	 */
	public void addChildMove(Move _parent) {
		children.add(_parent);
	}

	/**
	 * 親の手をセットする
	 * @param _parent 親の手
	 */
	public void setParentMove(Move _parent) {
		parent = _parent;
	}

	/**
	 * 手の評価をセットする
	 * @param _value 手の評価
	 * @param _depth 手の深さ
	 */
	public void setValue(int _value, int _depth) {
		moveValue = _value;
		moveDepth = _depth;
	}
}
