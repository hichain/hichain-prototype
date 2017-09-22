package jp.hichain.prototype.basic;

import jp.hichain.prototype.algorithm.Judge;
import jp.hichain.prototype.concept.PS;

import java.util.ArrayList;
import java.util.List;

public class Move {
	private static Move firstMove;
	private static Move lastMove;

	private final Move parent;	//親の手
	private final Square me;	//自分の手
	private List <Move> children;	//子どもの手

	private List <ChainSign> fieldSigns;    //場にある文字

	private int moveValue;    //手の評価
	private int moveDepth;    //手の深さ

	protected Move(Square thisSquare) {
		this(thisSquare, lastMove);
		lastMove = this;
	}

	private Move(Square thisSquare, Move parentMove) {
		children = new ArrayList<>();
		me = thisSquare;
		parent = parentMove;
		fieldSigns = new ArrayList<>( parent.getFieldSigns() );
		fieldSigns.remove( me.getSign() );
	}

	public static Move getFirstMove() {
		return firstMove;
	}

	public static Move getLastMove() {
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

	public List <ChainSign> getFieldSigns() {
		return fieldSigns;
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
	public void addChild(Move _parent) {
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

	/**
	 * 子どもの手を生成する
	 */
	public void createChildren() {
		Player player = me.getPlayer();
		for (ChainSign chainSign : getFieldSigns()) {
			for (Position position : Position.getAll()) {
				Square square = position.getSquare();

				PS.Contact contact = Judge.getContact(player, chainSign, square);
				if (contact == PS.Contact.POINT_POINT) {
					Move childMove = new Move(square, lastMove);
					addChild(childMove);
				}
			}
		}
	}
}
