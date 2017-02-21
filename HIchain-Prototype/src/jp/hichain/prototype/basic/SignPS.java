package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.EnumSet;

import jp.hichain.prototype.concept.PS;
import jp.hichain.prototype.concept.PS.Type;
import jp.hichain.prototype.concept.SignDir;

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

	public boolean exist(PS.Type type, PS dir) {
		return map.get(type).get(dir);
	}

	public EnumSet<PS> get(PS.Type type) {
		return map.get(type).map;
	}

	public void add(PS dir) {
		EnumSet<PS.Type> set = dir.getTypes();
		for (PS.Type type : set) {
			map.get(type).add(dir);
		}
	}

	public void rotate(SignDir.Rotation rel) {
		for (PS.Type type : map.keySet()) {
			Part part = new Part();
			for (PS dir : part.map) {
				part.add( dir.get(rel) );
			}

			map.put(type, part);
		}
	}

	@Override
	public String toString() {
		String string = "[POINTS] ";
		for (PS direction : get(Type.POINT)) {
			string += direction + " ";
		}
		string += "\n[SIDES] ";
		for (PS direction : get(Type.SIDE)) {
			string += direction + " ";
		}
		return string;
	}

	private class Part {
		private EnumSet<PS> map;

		public Part() {
			map = EnumSet.noneOf(PS.class);
		}

		public boolean get(PS dir) {
			return map.contains(dir);
		}

		public void add(PS dir) {
			map.add(dir);
		}
	}
}
