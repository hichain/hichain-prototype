package jp.hichain.prototype.concept;

/**
 * 点・辺データ (Points/Sides)
 * 16方向
 * @author NT
 *
 */
public enum PS {
	NORTH(TYPE.POINT) {
		@Override
		public PS getOpposite() {
			return SOUTH;
		}
	},
	NORTH_NORTHEAST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return SOUTH_SOUTHEAST;
		}
	},
	NORTHEAST(TYPE.CORNER) {
		@Override
		public PS getOpposite() {
			return SOUTHWEST;
		}
	},
	EAST_NORTHEAST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return WEST_NORTHWEST;
		}
	},
	EAST(TYPE.POINT) {
		@Override
		public PS getOpposite() {
			return WEST;
		}
	},
	EAST_SOUTHEAST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return WEST_SOUTHWEST;
		}
	},
	SOUTHEAST(TYPE.CORNER) {
		@Override
		public PS getOpposite() {
			return NORTHWEST;
		}
	},
	SOUTH_SOUTHEAST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return NORTH_NORTHEAST;
		}
	},
	SOUTH(TYPE.POINT) {
		@Override
		public PS getOpposite() {
			return NORTH;
		}
	},
	SOUTH_SOUTHWEST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return NORTH_NORTHWEST;
		}
	},
	SOUTHWEST(TYPE.CORNER) {
		@Override
		public PS getOpposite() {
			return NORTHEAST;
		}
	},
	WEST_SOUTHWEST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return EAST_SOUTHEAST;
		}
	},
	WEST(TYPE.POINT) {
		@Override
		public PS getOpposite() {
			return EAST;
		}
	},
	WEST_NORTHWEST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return EAST_NORTHEAST;
		}
	},
	NORTHWEST(TYPE.CORNER) {
		@Override
		public PS getOpposite() {
			return SOUTHEAST;
		}
	},
	NORTH_NORTHWEST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return SOUTH_SOUTHWEST;
		}
	};

	/**
	 * PSの種類 (点か辺か)
	 * @author NT
	 *
	 */
	public enum TYPE {
		POINT,
		SIDE,
		CORNER;
	}

	private TYPE type;
	private PS left, right, opposite;

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
		NORTHEAST.left = NORTHWEST;
		NORTHEAST.right = SOUTHEAST;
		NORTHEAST.opposite = SOUTHWEST;
		NORTHWEST.left = SOUTHWEST;
		NORTHWEST.right = NORTHEAST;
		NORTHWEST.opposite = SOUTHEAST;
		SOUTHEAST.left = NORTHEAST;
		SOUTHEAST.right = SOUTHWEST;
		SOUTHEAST.opposite = NORTHWEST;
		SOUTHWEST.left = SOUTHEAST;
		SOUTHWEST.right = NORTHWEST;
		SOUTHWEST.opposite = NORTHEAST;
		NORTH_NORTHEAST.left = WEST_NORTHWEST;
		NORTH_NORTHEAST.right = EAST_SOUTHEAST;
		NORTH_NORTHEAST.opposite = SOUTH_SOUTHWEST;
		NORTH_NORTHWEST.left = WEST_SOUTHWEST;
		NORTH_NORTHWEST.right = EAST_NORTHEAST;
		NORTH_NORTHWEST.opposite = SOUTH_SOUTHEAST;
		EAST_NORTHEAST.left = NORTH_NORTHWEST;
		EAST_NORTHEAST.right = SOUTH_SOUTHEAST;
		EAST_NORTHEAST.opposite = WEST_SOUTHWEST;
		EAST_SOUTHEAST.left = NORTH_NORTHEAST;
		EAST_SOUTHEAST.right = SOUTH_SOUTHWEST;
		EAST_SOUTHEAST.opposite = WEST_NORTHWEST;
		SOUTH_SOUTHEAST.left = EAST_NORTHEAST;
		SOUTH_SOUTHEAST.right = WEST_SOUTHWEST;
		SOUTH_SOUTHEAST.opposite = NORTH_NORTHWEST;
		SOUTH_SOUTHWEST.left = EAST_SOUTHEAST;
		SOUTH_SOUTHWEST.right = WEST_NORTHWEST;
		SOUTH_SOUTHWEST.opposite = NORTH_NORTHEAST;
		WEST_NORTHWEST.left = SOUTH_SOUTHWEST;
		WEST_NORTHWEST.right = NORTH_NORTHEAST;
		WEST_NORTHWEST.opposite = EAST_SOUTHEAST;
		WEST_SOUTHWEST.left = SOUTH_SOUTHEAST;
		WEST_SOUTHWEST.right = NORTH_NORTHWEST;
		WEST_SOUTHWEST.opposite = EAST_NORTHEAST;
	}

	private PS(TYPE type) {
		this.type = type;
	}

	/**
	 * PSの種類を返す
	 * @return PSの種類
	 */
	public TYPE getType() {
		return type;
	}

	public abstract PS getOpposite();

	public PS getRelative(SignDir.RELATIVE dir) {
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

	public static PS getEnum(String str) {
		PS [] pss = PS.values();
		for (PS ps : pss) {
			if (str.equals(ps.name())) {
				return ps;
			}
		}
		return null;
	}
}
