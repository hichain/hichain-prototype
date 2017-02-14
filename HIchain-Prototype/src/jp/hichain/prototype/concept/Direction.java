package jp.hichain.prototype.concept;

/**
 * 方角(4方向)
 * 全ての列挙型共通の概念
 * @author NT
 *
 */
public enum Direction {
	NORTH,
	EAST,
	SOUTH,
	WEST;

	public enum Relative {
		LEFT,
		RIGHT,
		OPPOSITE;
	}

	static {
		NORTH.setRelative(WEST, EAST, SOUTH);
		EAST.setRelative(NORTH, SOUTH, WEST);
		SOUTH.setRelative(EAST, WEST, NORTH);
		WEST.setRelative(SOUTH, NORTH, EAST);
	}

	private Direction left, right, opposite;

	private void setRelative(Direction left, Direction right, Direction opposite) {
		this.left = left;
		this.right = right;
		this.opposite = opposite;
	}

	public Direction getRelative(Relative relative) {
		switch (relative) {
			case LEFT:
				return left;
			case RIGHT:
				return right;
			case OPPOSITE:
				return opposite;
		}
		return null;
	}

}
