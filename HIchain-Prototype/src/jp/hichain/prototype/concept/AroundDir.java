package jp.hichain.prototype.concept;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * 周囲マス方向 (8方向)
 * @author NT
 *
 */
public enum AroundDir {
	NORTH(Type.NEXT, 0, -1),
	NORTHEAST(Type.CORNER, 1, -1),
	EAST(Type.NEXT, 1, 0),
	SOUTHEAST(Type.CORNER, 1, 1),
	SOUTH(Type.NEXT, 0, 1),
	SOUTHWEST(Type.CORNER, -1, 1),
	WEST(Type.NEXT, -1, 0),
	NORTHWEST(Type.CORNER, -1, -1);

	public enum Type {
		NEXT,
		CORNER;
	}

	public enum Axis {
		VERTICAL,
		HORIZONTAL;
	}

	private EnumMap<Direction.Relation, AroundDir> relations;
	private Type type;
	private EnumMap<Axis, Integer> comps;

	private AroundDir(Type type, int v, int h) {
		relations = new EnumMap<>(Direction.Relation.class);
		this.type = type;
		comps = new EnumMap<Axis, Integer>(Axis.class) {{
			put(Axis.VERTICAL, v);
			put(Axis.HORIZONTAL, h);
		}};
	}

	static {
		set( new AroundDir[] {
			NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST
		} );
	}

	public Direction getCommonDir() {
		return Direction.valueOf(this.name());
	}

	public AroundDir get(Direction.Relation relation) {
		return relations.get(relation);
	}

	public Type getType() {
		return type;
	}

	public int getComp(Axis axis) {
		return comps.get(axis);
	}

	public static AroundDir getByComp(int v, int h) {
		for (AroundDir dir : values()) {
			if (dir.getComp(Axis.VERTICAL) == v && dir.getComp(Axis.HORIZONTAL) == h) {
				return dir;
			}
		}
		return null;
	}

	public static EnumSet<AroundDir> values(Type type) {
		EnumSet<AroundDir> dirs = EnumSet.noneOf(AroundDir.class);
		for (AroundDir dir : AroundDir.values()) {
			if (dir.type == type) dirs.add(dir);
		}
		return dirs;
	}

	private static void set(AroundDir [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			int o = (i+4) % dirs.length;
			dirs[i].relations.put( Direction.Relation.LEFT, dirs[l] );
			dirs[i].relations.put( Direction.Relation.RIGHT, dirs[r] );
			dirs[i].relations.put( Direction.Relation.OPPOSITE, dirs[o] );
		}
	}
}
