package jp.hichain.prototype.concept;

/**
 * 文字の向き
 * 4方向
 * @author NT
 *
 */
public enum SignDir {
	NORTH,
	EAST,
	SOUTH,
	WEST;

	public enum RELATIVE {
		LEFT,
		RIGHT,
		OPPOSITE;
	}

	private SignDir left, right, opposite;

	static {
		NORTH.left = WEST;
		NORTH.right = EAST;
		NORTH.opposite = SOUTH;
		EAST.left = NORTH;
		EAST.right = SOUTH;
		EAST.opposite = WEST;
		SOUTH.left = EAST;
		SOUTH.right = WEST;
		SOUTH.opposite = NORTH;
		WEST.left = SOUTH;
		WEST.right = NORTH;
		WEST.opposite = EAST;
	}

	public SignDir getRelative(SignDir.RELATIVE dir) {
		switch (dir) {
		case LEFT:
			return left;
		case RIGHT:
			return right;
		case OPPOSITE:
			return opposite;
		}
		return null;
	}

	public static SignDir getEnum(String str) {
		SignDir [] dirs = SignDir.values();
		for (SignDir signDir : dirs) {
			if (str.equals(signDir.name())) {
				return signDir;
			}
		}
		return null;
	}
}
