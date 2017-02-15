package jp.hichain.prototype.concept;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.basic.DirComp;
import jp.hichain.prototype.concept.Direction.Relative;

/**
 * 点・辺データ (Points/Sides)
 * 16方向
 * @author NT
 *
 */
public enum PS {
	NORTH(1, 0, 0, 0),
	NORTH_NORTHEAST(2, 1, 0, 0),
	NORTHEAST(1, 1, 0, 0),
	EAST_NORTHEAST(1, 2, 0, 0),
	EAST(0, 1, 0, 0),
	EAST_SOUTHEAST(0, 2, 1, 0),
	SOUTHEAST(0, 1, 1, 0),
	SOUTH_SOUTHEAST(0, 1, 2, 0),
	SOUTH(0, 0, 1, 0),
	SOUTH_SOUTHWEST(0, 0, 2, 1),
	SOUTHWEST(0, 0, 1, 1),
	WEST_SOUTHWEST(0, 0, 1, 2),
	WEST(0, 0, 0, 1),
	WEST_NORTHWEST(1, 0, 0, 2),
	NORTHWEST(1, 0, 0, 1),
	NORTH_NORTHWEST(2, 0, 0, 1);

	private DirComp components;
	private Type type;
	private PS left, right, opposite;

	private PS(int north, int east, int south, int west) {
		components = new DirComp(north, east, south, west);
		setType();
		setRelative();
	}

	/**
	 * PSの種類を返す
	 * @return PSの種類
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 方角の成分を返す
	 * @return DirComp
	 */
	public DirComp getComp() {
		return components;
	}

	/**
	 * 相対方角を返す
	 * @param relative 相対方角
	 * @return AroundDir
	 */
	public PS getRelative(Direction.Relative dir) {
		switch (dir) {
		case LEFT:
			return left;
		case RIGHT:
			return right;
		case OPPOSITE:
			return opposite;
		}
		return null;
	}

	/**
	 * 文字列からそれに該当するenumを返す
	 * @param str 文字列
	 * @return PS
	 */
	public static PS getEnum(String str) {
		PS [] pss = PS.values();
		for (PS ps : pss) {
			if (str.equals(ps.name())) {
				return ps;
			}
		}
		return null;
	}

	private void setType() {
		switch (components.getDenominator()) {
			case 4:
				type = Type.POINT;
			case 8:
				type = Type.CORNER;
			case 16:
				type = Type.SIDE;
		}
	}

	private void setRelative() {
		left = getByComp( components.getRelative(Relative.LEFT) );
		right = getByComp( components.getRelative(Relative.RIGHT) );
		opposite = getOpposite(this);
	}

	private PS getOpposite(PS ps) {
		DirComp comp = ps.getComp();

		if (comp.getDenominator() != 16) {
			return getByComp( comp.getRelative(Relative.OPPOSITE) );
		}

		Direction staticDir = null;
		for (Map.Entry<Direction, Integer> entry : comp.getMap().entrySet()) {
			if (entry.getValue() == 1) {
				staticDir = entry.getKey();
			}
		}

		Map <Direction, Integer> newMap = new HashMap<Direction, Integer>();
		for (Map.Entry<Direction, Integer> entry : comp.getMap().entrySet()) {
			Direction key = entry.getKey().getRelative(Relative.OPPOSITE);
			int value = entry.getValue();
			if (key == staticDir) {
				value = 0;
			}
			if (value == 1) {
				staticDir = key;
				key = entry.getKey();
			}
			newMap.put(key, value);
		}

		return getByComp( new DirComp(newMap) );
	}

	private static PS getByComp(DirComp comp) {
		for (PS dir : PS.values()) {
			if (comp == dir.getComp()) {
				return dir;
			}
		}
		return null;
	}

	/**
	 * PSの種類 (点か辺か)
	 * @author NT
	 *
	 */
	public enum Type {
		POINT,
		SIDE,
		CORNER;
	}

	/**
	 * 接点の種類
	 * @author NT
	 *
	 */
	public enum Contact {
		NONE(false),			//接点なし
		POINT_POINT(true),	//点と点で接する
		SIDE_SIDE(false);		//辺と辺で接する

		private boolean available;

		private Contact(boolean _available) {
			available = _available;
		}

		/**
		 * 有効な接点か返す
		 * @return true/false
		 */
		public boolean isAvailable() {
			return available;
		}
	}
}
