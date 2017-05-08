package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.Objects;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.AroundDir.Axis;
import jp.hichain.prototype.concept.Direction.Relation;

public final class Position {
	private final EnumMap<Axis, Integer> position;
	private EnumMap <AroundDir, Square> arounds;

	public Position(Position _source, AroundDir _dir) {
		position = new EnumMap<>(Axis.class);
		for (Axis axis : Axis.values()) {
			position.put(axis, _source.get(axis) + _dir.getComp(axis));
		}
	}

	/**
	 * 絶対座標を取得する
	 * @param axis 軸
	 * @return 座標
	 */
	public int get(Axis axis) {
		return position.get(axis);
	}

	/**
	 * 指定の方角のSquareを返す
	 * @param _dir 自身(this)からみたAroudDir
	 * @return 周囲Square
	 */
	public Square getAround(AroundDir _dir) {
		return arounds.get(_dir);
	}

	/**
	 * 周囲Squareを全て返す
	 * @return 周囲Square Map
	 */
	public EnumMap <AroundDir, Square> getAroundAll() {
		return arounds;
	}

	/**
	 * 周囲Squareを追加
	 * @param _square Square
	 * @param _dir 自身(this)からみた_squareの方向
	 */
	public void addAround(AroundDir _dir, Square _square) {
		arounds.put(_dir, _square);
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
			int v = vertical + dir.getComp(Axis.VERTICAL);
			int h = horizontal + dir.getComp(Axis.HORIZONTAL);
			addAround(dir, get(v, h));
		}
	}

	private void init() {
		arounds = new EnumMap<>(AroundDir.class);
		for (AroundDir dir : AroundDir.values()) {
			arounds.put(dir, null);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Position)) {
			return false;
		}
		Position position = (Position)obj;
		return (vertical == position.vertical) && (horizontal == position.horizontal);
	}

    @Override
    public int hashCode() {
        return Objects.hash(vertical, horizontal);
    }
}
