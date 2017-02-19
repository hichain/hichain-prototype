package jp.hichain.prototype.concept;

import java.util.EnumSet;

/**
 * 方角(4方向)
 * 全ての列挙型共通の概念
 * @author NT
 *
 */
public enum Direction {
	NORTH(4),
	EAST(4),
	SOUTH(4),
	WEST(4),
	NORTHEAST(8, NORTH, Relation.RIGHT),
	NORTHWEST(8, NORTH, Relation.LEFT),
	SOUTHEAST(8, SOUTH, Relation.LEFT),
	SOUTHWEST(8, SOUTH, Relation.RIGHT),
	NORTH_NORTHEAST(16, NORTH, Relation.RIGHT),
	NORTH_NORTHWEST(16, NORTH, Relation.LEFT),
	EAST_NORTHEAST(16, EAST, Relation.LEFT),
	EAST_SOUTHEAST(16, EAST, Relation.RIGHT),
	SOUTH_SOUTHEAST(16, SOUTH, Relation.LEFT),
	SOUTH_SOUTHWEST(16, SOUTH, Relation.RIGHT),
	WEST_NORTHWEST(16, WEST, Relation.RIGHT),
	WEST_SOUTHWEST(16, WEST, Relation.LEFT);

	public enum Relation {
		LEFT,
		RIGHT;
	}

	private int denominator;
	private Direction left, right;
	private Direction squareSide;
	private Relation squareSidePos;

	private Direction(int denom) {
		denominator = denom;
	}

	private Direction(int denom, Direction dir, Relation rel) {
		this(denom);
		squareSide = dir;
		squareSidePos = rel;
	}

	static {
		setRelation(	new Direction [] {
			NORTH, EAST, SOUTH, WEST
		});
		setRelation( new Direction [] {
			NORTH_NORTHEAST, EAST_NORTHEAST, EAST_SOUTHEAST, SOUTH_SOUTHEAST, SOUTH_SOUTHWEST, WEST_SOUTHWEST, WEST_NORTHWEST, NORTH_NORTHWEST
		});
		setRelation( new Direction [] {
			NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST
		});
	}

	private static void setRelation(Direction [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			dirs[i].left = dirs[l];
			dirs[i].right = dirs[r];
		}
	}

	public Direction getRelation(Relation relation) {
		if (relation == Relation.LEFT) {
			return left;
		}
		return right;
	}

	public Direction getRelation(Relation relation, int times) {
		Direction dir = this;
		for (int i = 0; i < times; i++) {
			dir = dir.getRelation(relation);
		}
		return dir;
	}

	public Direction getSquareSide() {
		return squareSide;
	}

	public Relation getSquareSidePos() {
		return squareSidePos;
	}

	public int getDenominator() {
		return denominator;
	}

	public static Direction get(Direction direction, Relation relation, int denom) {
		for (Direction dir : Direction.values()) {
			if (dir.getSquareSide() == direction && dir.getDenominator() == denom && dir.getSquareSidePos() == relation) {
				return dir;
			}
		}
		return null;
	}

	public static EnumSet<Direction> getEnumSet(int denom) {
		EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
		for (Direction direction : Direction.values()) {
			if (direction.getDenominator() == denom) {
				set.add(direction);
			}
		}
		return set;
	}
}
