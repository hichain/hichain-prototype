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
	private EnumSet<Direction> squareSides;

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
		NORTH.setSquareSides(NORTH);
		EAST.setSquareSides(EAST);
		SOUTH.setSquareSides(SOUTH);
		WEST.setSquareSides(WEST);
		NORTHEAST.setSquareSides(NORTH, EAST);
		NORTHWEST.setSquareSides(NORTH, WEST);
		SOUTHEAST.setSquareSides(SOUTH, EAST);
		SOUTHWEST.setSquareSides(SOUTH, WEST);
		NORTH_NORTHEAST.setSquareSides(NORTH);
		NORTH_NORTHWEST.setSquareSides(NORTH);
		EAST_NORTHEAST.setSquareSides(EAST);
		EAST_SOUTHEAST.setSquareSides(EAST);
		SOUTH_SOUTHEAST.setSquareSides(SOUTH);
		SOUTH_SOUTHWEST.setSquareSides(SOUTH);
		WEST_NORTHWEST.setSquareSides(WEST);
		WEST_SOUTHWEST.setSquareSides(WEST);
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

	private void setSquareSides(Direction dir) {
		squareSides = EnumSet.of(dir);
	}

	private void setSquareSides(Direction dir1, Direction dir2) {
		squareSides = EnumSet.of(dir1, dir2);
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
