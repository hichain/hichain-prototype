package jp.hichain.prototype.concept;

import java.util.EnumMap;

/**
 * 方角 (16方向)
 * 全ての列挙型共通の概念
 * @author NT
 *
 */
public enum Direction {
	NORTH,
	EAST,
	SOUTH,
	WEST,
	NORTHEAST,
	NORTHWEST,
	SOUTHEAST,
	SOUTHWEST,
	NORTH_NORTHEAST,
	NORTH_NORTHWEST,
	EAST_NORTHEAST,
	EAST_SOUTHEAST,
	SOUTH_SOUTHEAST,
	SOUTH_SOUTHWEST,
	WEST_NORTHWEST,
	WEST_SOUTHWEST;

	public enum Relation {
		LEFT,
		RIGHT,
		OPPOSITE;
	}

	private EnumMap<Relation, Direction> relations;

	private Direction() {
		relations = new EnumMap<>(Relation.class);
	}

	static {
		set( new Direction [] {
			NORTH, NORTH_NORTHEAST, NORTHEAST, EAST_NORTHEAST,
			EAST, EAST_SOUTHEAST, SOUTHEAST, SOUTH_SOUTHEAST,
			SOUTH, SOUTH_SOUTHWEST, SOUTHWEST, WEST_SOUTHWEST,
			WEST, WEST_NORTHWEST, NORTHWEST, NORTH_NORTHWEST
		});
	}

	public Direction get(Relation relation) {
		return relations.get(relation);
	}

	public Direction get(Relation relation, int times) {
		Direction dir = this;
		for (int i = 0; i < times; i++) {
			dir = dir.get(relation);
		}
		return dir;
	}

	private static void set(Direction [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			dirs[i].relations.put( Relation.LEFT, dirs[l] );
			dirs[i].relations.put( Relation.RIGHT, dirs[r] );
		}
		for (int i = 0; i < dirs.length; i++) {
			int o = (i+8) % dirs.length;
			dirs[i].relations.put( Relation.OPPOSITE, dirs[o] );
		}
	}
}
