package jp.hichain.prototype.basic;

import java.util.EnumMap;

import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.Direction.Relation;

/**
 * 相対マス
 * @author NT
 *
 */
public class Square {
	private Square source;
	private EnumMap <Direction, Square> around;

	/**
	 * コンストラクタ
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(Square _source, Direction _dir) {
		source = _source;
		around = new EnumMap<Direction, Square>(Direction.class);
		addAround(_source, _dir);
	}

	/**
	 * 周囲Squareを追加
	 * @param _square Square
	 * @param _dir _squareから見た自身(this)のAroundDir
	 */
	public void addAround(Square _square, Direction _dir) {
		around.put(_dir.getRelation(Relation.LEFT, 2), _square);
	}

	/**
	 * 指定の方角のSquareを返す
	 * @param _dir 自身(this)からみたAroudDir
	 * @return 周囲Square
	 */
	public Square getAround(Direction _dir) {
		return around.get(_dir);
	}

	/**
	 * 周囲Squareを全て返す
	 * @return 周囲Square Map
	 */
	public EnumMap <Direction, Square> getAroundAll() {
		return around;
	}

	/**
	 * ソースSquareを返す
	 * @return Square
	 */
	public Square getSource() {
		return source;
	}
}
