package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.EnumSet;

import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.PS;
import jp.hichain.prototype.concept.PS.Type;

/**
 * 文字のPS
 * 16個のPSの集合
 * @author NT
 *
 */
public class SignPS {
	private EnumMap<PS.Type, Part> map;

	public SignPS() {
		map = new EnumMap<PS.Type, Part>(PS.Type.class) {{
			put(PS.Type.POINT, new Part());
			put(PS.Type.SIDE, new Part());
			put(PS.Type.CORNER, new Part());
		}};
	}

	public boolean exist(PS.Type type, Direction dir) {
		return map.get(type).get(dir);
	}

	public EnumSet<Direction> get(PS.Type type) {
		return map.get(type).map;
	}

	public void add(Direction dir) {
		EnumSet<PS.Type> set = PS.getType(dir);
		for (PS.Type type : set) {
			map.get(type).add(dir);
		}
	}

	public void rotate(Direction.Relation rel) {
		for (PS.Type type : map.keySet()) {
			int times;
			if (type == Type.POINT || type == Type.CORNER) {
				times = 1;
			} else {
				times = 2;
			}

			Part part = new Part();
			for (Direction dir : part.map) {
				part.add( dir.getRelation(rel, times) );
			}

			map.put(type, part);
		}
	}

	private class Part {
		private EnumSet<Direction> map;

		public Part() {
			map = EnumSet.noneOf(Direction.class);
		}

		public boolean get(Direction dir) {
			return map.contains(dir);
		}

		public void add(Direction dir) {
			map.add(dir);
		}
	}
}
