package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.AroundDir.Axis;
import jp.hichain.prototype.concept.Direction.Relation;

/**
 * マス
 * @author NT
 *
 */
public class Square {
	public static Square ROOT;
	protected static Map<Position, Square> godMap;
	private Square source;
	private EnumMap <AroundDir, Square> around;
	private Position position;

	/**
	 * ルートマス
	 */
	public Square() {
		init();
		position = new Position(-1, -1);
		godMap.put(position, this);
		createAroundAll();
	}

	/**
	 * 通常のマス
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(Square _source, AroundDir _dir) {
		init();
		position = new Position(
			_source.getPosition(Axis.VERTICAL) + _dir.getComp(Axis.VERTICAL),
			_source.getPosition(Axis.HORIZONTAL) + _dir.getComp(Axis.HORIZONTAL)
		);
		godMap.put(position, this);
		source = _source;
		setArounds(_source, _dir);
	}

	static {
		godMap = new HashMap<>();
		ROOT = new Square();
	}

	public static Square get(int _v, int _h) {
		return godMap.get( new Position(_v, _h) );
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

	public int getPosition(Axis _axis) {
		return position.get(_axis);
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

	private void setArounds(Square _source, AroundDir _sourceDir) {
		addAround(_sourceDir.get(Relation.OPPOSITE), _source);
		for (AroundDir dir : AroundDir.values()) {
			int v = getPosition(Axis.VERTICAL) + dir.getComp(Axis.VERTICAL);
			int h = getPosition(Axis.HORIZONTAL) + dir.getComp(Axis.HORIZONTAL);
			addAround(dir, get(v, h));
		}
	}

	private void init() {
		around = new EnumMap<>(AroundDir.class);
		for (AroundDir dir : AroundDir.values()) {
			around.put(dir, null);
		}
	}
}
