package jp.hichain.prototype.concept;

import java.util.EnumMap;

/**
 * 周囲マス方向 (8方向)
 * @author NT
 *
 */
public enum AroundDir {
	NORTH,
	NORTHEAST,
	EAST,
	SOUTHEAST,
	SOUTH,
	SOUTHWEST,
	WEST,
	NORTHWEST;

	private EnumMap<Direction.Relation, AroundDir> relations;

	private AroundDir() {
		relations = new EnumMap<>(Direction.Relation.class);
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
