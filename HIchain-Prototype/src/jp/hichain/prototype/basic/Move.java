package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.List;

public class Move {
	private static Square firstMove;
	private static Square lastMove;

	private final Square parent;	//親の手
	private final Square me;		//自分の手
	private List <Square> children;	//子どもの手

	private int moveValue;    //手の評価
	private int moveDepth;    //手の深さ

	private Move(Square _thisSq) {
		children = new ArrayList<>();
		me = _thisSq;
		parent = lastMove.getMove().getParent();
	}

	public static Square getFirstMove() {
		return firstMove;
	}

	public static Square getLastMove() {
		return lastMove;
	}

	/**
	 * この手を打ったマスを返す
	 * @return Square
	 */
	public Square getSquare() {
		return me;
	}

	/**
	 * 親の手を返す
	 * @return 親の手
	 */
	public Square getParent() {
		return parent;
	}

	/**
	 * 子どもの手を返す
	 * @return 子どもの手
	 */
	public List <Square> getChildren() {
		return children;
	}

	/**
	 * 手の評価を返す
	 * @return 手の評価
	 */
	public int getValue() {
		return moveValue;
	}

	/**
	 * 手の深さを返す
	 * @return 手の深さ
	 */
	public int getDepth() {
		return moveDepth;
	}

	/**
	 * 子の手を追加する
	 * @param _parent 子の手
	 */
	public void addChild(Square _parent) {
		children.add(_parent);
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
