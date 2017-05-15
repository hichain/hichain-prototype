package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import jp.hichain.prototype.algorithm.ChainSearcher;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.AroundDir.Axis;
import jp.hichain.prototype.concept.Direction.Relation;

public final class Position {
	private static List <Position> posDB;

	private final Square thisSquare;
	private final EnumMap<Axis, Integer> position;
	private EnumMap <AroundDir, Position> arounds;

	/**
	 * ルート座標
	 * @param _root ルートSquare
	 * @param _v V座標
	 * @param _h H座標
	 */
	public Position(Square _root, int _v, int _h) {
		init();
		thisSquare = _root;
		position = new EnumMap<Axis, Integer>(Axis.class) {{
			put(Axis.VERTICAL, _v);
			put(Axis.HORIZONTAL, _h);
		}};
		Collections.unmodifiableMap(position);
		posDB.add(this);
	}

	/**
	 * 通常の座標
	 * @param _thisSq 座標を設定するマス
	 * @param _source _centerのアクセス元のマス
	 * @param _dir _sourceからみた_centerの方向
	 */
	public Position(Square _thisSq, Position _source, AroundDir _dir) {
		init();
		thisSquare = _thisSq;
		position = new EnumMap<>(Axis.class);
		for (Axis axis : Axis.values()) {
			position.put(axis, _source.getPosition(axis) + _dir.getComp(axis));
		}
		Collections.unmodifiableMap(position);
		posDB.add(this);
	}

	static {
		posDB = new ArrayList<>();
	}

	public static Position get(int _v, int _h) {
		EnumMap<Axis, Integer> targetPos = new EnumMap<Axis, Integer>(Axis.class) {{
			put(Axis.VERTICAL, _v);
			put(Axis.HORIZONTAL, _h);
		}};

		for (Position pos : posDB) {
			if (pos.position.equals(targetPos)) {
				return pos;
			}
		}
		return null;
	}

	public static List<Position> getAll() {
		return posDB;
	}

	/**
	 * この座標にあるマスを取得する
	 * @return Square
	 */
	public Square getSquare() {
		return thisSquare;
	}

	/**
	 * 絶対座標を取得する
	 * @param axis 軸
	 * @return 座標
	 */
	public int getPosition(Axis axis) {
		return position.get(axis);
	}

	/**
	 * 指定の方角にある座標を返す
	 * @param _dir 自身(this)からみたAroundDir
	 * @return Position
	 */
	public Position getAround(AroundDir _dir) {
		return arounds.get(_dir);
	}

	/**
	 * 指定の方角にマスがあるか返す
	 * @param _dir 自身(this)からみたAroundDir
	 * @return boolean
	 */
	public boolean hasAround(AroundDir _dir) {
		return getAround(_dir) != null;
	}

	/**
	 * 周囲座標を全て返す
	 * @return 周囲座標Map
	 */
	public EnumMap <AroundDir, Position> getAroundAll() {
		return arounds;
	}


	/**
	 * 周囲座標を追加
	 * @param _dir この座標(this)からみた方向
	 * @param _square 追加する座標
	 */
	public void addAround(AroundDir _dir, Position _position) {
		arounds.put(_dir, _position);
	}

	/**
	 * 周囲に空マスを作成する
	 */
	public void createAroundsAll() {
		for (AroundDir dir : AroundDir.values()) {
			if (hasAround(dir)) continue;
			new Square(thisSquare, dir);
		}
	}

	/**
	 * 周囲座標を更新する
	 * 自分から周囲へのアクセス、周囲から自分へのアクセスの両方を更新
	 */
	public void updateAroundsAll() {
		for (AroundDir dir : AroundDir.values()) {
			if (hasAround(dir)) continue;
			int v = getPosition(Axis.VERTICAL) + dir.getComp(Axis.VERTICAL);
			int h = getPosition(Axis.HORIZONTAL) + dir.getComp(Axis.HORIZONTAL);
			Position target = Position.get(v, h);
			if (target == null) continue;
			addAround(dir, target);
			target.addAround(dir.get(Relation.OPPOSITE), this);
		}
	}

	/**
	 * 周囲との連鎖を全て探索する
	 */
	public void searchChainsAll() {
		for (AroundDir dir : AroundDir.values()) {
			if (hasAround(dir)) {
				ChainSearcher.search(thisSquare, dir);
				ChainSearcher.search(getAround(dir).getSquare(), dir.get(Relation.OPPOSITE));
			}
		}
	}

	private void init() {
		arounds = new EnumMap<>(AroundDir.class);
		for (AroundDir dir : AroundDir.values()) {
			arounds.put(dir, null);
		}
	}

	@Override
	public String toString() {
		return "(" + getPosition(Axis.VERTICAL) + ", " + getPosition(Axis.HORIZONTAL) + ")";
	}

	/**
	 * 絶対座標が同じなら等しいとみなす
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Position)) {
			return false;
		}
		Position pos = (Position)obj;
		return position.equals(pos.position);
	}

    @Override
    public int hashCode() {
    	return Objects.hash(position);
    }
}
