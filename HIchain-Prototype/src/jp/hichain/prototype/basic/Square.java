package jp.hichain.prototype.basic;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;

/**
 * 相対マス
 * @author NT
 *
 */
public class Square {
	Square source;
	Map <AroundDir, Square> around;

	/**
	 * コンストラクタ
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(Square _source, AroundDir _dir) {
		source = _source;
		around = new HashMap<AroundDir, Square>();
		addAround(_source, _dir);
	}

	/**
	 * 周囲Squareを追加
	 * @param _square Square
	 * @param _dir _squareから見た自身(this)のAroundDir
	 */
	public void addAround(Square _square, AroundDir _dir) {
		around.put(_dir.getOpposite(), _square);
	}

	/**
	 * 指定の方角のSquareを返す
	 * @param _dir 自身(this)からみたAroudDir
	 * @return 周囲Square
	 */
	public Square getAround(AroundDir _dir) {
		return around.get(_dir);
	}

	/**
	 * 周囲Squareを全て返す
	 * @return 周囲Square Map
	 */
	public Map <AroundDir, Square> getAroundAll() {
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
