package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.hichain.prototype.concept.AroundDir;

public class Move extends RRChainSign {
	public static Move ROOT;

	private Move parent;			//親の手
	private List <Move> children;	//子どもの手

	private int moveValue;    //手の評価
	private int moveDepth;    //手の深さ

	private Move() {
		super();
		children = new ArrayList<>();
	}

	public Move(Move _source, AroundDir _dir) {
		super(_source, _dir);
		children = new ArrayList<>();
	}

	public Move(Move _source, AroundDir _dir, Player player, ChainSign _sign) {
		super(_source, _dir, player, _sign);
		children = new ArrayList<>();
	}

	public Move(Move _source, AroundDir _dir, Player player, ChainSign _sign, Move _parent) {
		this(_source, _dir, player, _sign);
		parent = _parent;
	}

	static {
		godMap = new HashMap<>();
		ROOT = new Move();
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
