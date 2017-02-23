package jp.hichain.prototype.concept;

import java.util.EnumMap;
import java.util.EnumSet;

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
	NORTHEAST(NORTH, EAST),
	NORTHWEST(NORTH, WEST),
	SOUTHEAST(SOUTH, EAST),
	SOUTHWEST(SOUTH, WEST),
	NORTH_NORTHEAST(NORTH),
	NORTH_NORTHWEST(NORTH),
	EAST_NORTHEAST(EAST),
	EAST_SOUTHEAST(EAST),
	SOUTH_SOUTHEAST(SOUTH),
	SOUTH_SOUTHWEST(SOUTH),
	WEST_NORTHWEST(WEST),
	WEST_SOUTHWEST(WEST);

	public enum Relation {
		LEFT,
		RIGHT,
		OPPOSITE;
	}

	private EnumMap<Relation, Direction> relations;
	private EnumSet<Direction> squareSides;

	private Direction() {
		relations = new EnumMap<>(Relation.class);
		squareSides = EnumSet.of(this);
	}

	private Direction(Direction squareSide) {
		relations = new EnumMap<>(Relation.class);
		squareSides = EnumSet.of(squareSide);
	}

	private Direction(Direction squareSide1, Direction squareSide2) {
		relations = new EnumMap<>(Relation.class);
		squareSides = EnumSet.of(squareSide1, squareSide2);
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

	public EnumSet<Direction> getSquareSides() {
		return squareSides;
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
