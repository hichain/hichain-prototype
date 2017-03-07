package jp.hichain.prototype.basic;

import java.util.EnumMap;

import jp.hichain.prototype.algorithm.AroundSearcher;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.Direction;

/**
 * 相対マス
 * @author NT
 *
 */
public class Square {
	public static Square ROOT;
	private Square source;
	private EnumMap <AroundDir, Square> around;

	/**
	 * ルートマス
	 */
	protected Square() {
		System.out.println("Root Square: " + this);
		initAround();
		createAroundAll();
	}

	/**
	 * 通常のマス
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(Square _source, AroundDir _dir) {
		System.out.println("New Square: " + this + " (" + _source + ": " + _dir + ")");
		source = _source;
		initAround();
		_source.addAround(_dir, this);
		addAround(_dir.get(Direction.Relation.OPPOSITE), _source);
		AroundSearcher.search(this);
	}

	public static void createRoot() {
		ROOT = new Square();
	}

	/**
	 * ソースSquareを返す
	 * @return Square
	 */
	public Square getSource() {
		return source;
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
	public EnumMap <AroundDir, Square> getAroundAll() {
		return around;
	}

	/**
	 * 周囲Squareを追加
	 * @param _square Square
	 * @param _dir 自身(this)からみた_squareの方向
	 */
	public void addAround(AroundDir _dir, Square _square) {
		around.put(_dir, _square);
	}

	protected void createAroundAll() {
		for (AroundDir dir : AroundDir.values()) {
			Square square = getAround(dir);
			if (square == null) {
				addAround(dir, new Square(this, dir));
			}
		}
	}

	private void initAround() {
		around = new EnumMap<>(AroundDir.class);
		for (AroundDir dir : AroundDir.values()) {
			around.put(dir, null);
		}
	}
}
