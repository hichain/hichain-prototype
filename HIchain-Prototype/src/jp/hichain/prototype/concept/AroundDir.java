package jp.hichain.prototype.concept;

import java.util.EnumMap;

/**
 * 周囲マス方向 (8方向)
 * @author NT
 *
 */
public enum AroundDir {
	NORTH(Direction.NORTH),
	NORTHEAST(Direction.NORTHEAST),
	EAST(Direction.EAST),
	SOUTHEAST(Direction.SOUTHEAST),
	SOUTH(Direction.SOUTH),
	SOUTHWEST(Direction.SOUTHWEST),
	WEST(Direction.WEST),
	NORTHWEST(Direction.NORTHWEST);

	private Direction commonDir;
	private EnumMap<Direction.Relation, AroundDir> relations;

	private AroundDir(Direction direction) {
		commonDir = direction;
	}

	static {
		set( new AroundDir[] {
			NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST
		} );
	}

	public Direction getCommonDir() {
		return commonDir;
	}

	public AroundDir get(Direction.Relation relation) {
		return relations.get(relation);
	}

	private static void set(AroundDir [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			dirs[i].relations.put( Direction.Relation.LEFT, dirs[l] );
			dirs[i].relations.put( Direction.Relation.RIGHT, dirs[r] );
		}
		for (int i = 0; i < dirs.length; i++) {
			int o = (i+4) % dirs.length;
			dirs[i].relations.put( Direction.Relation.OPPOSITE, dirs[o] );
		}
	}
}
