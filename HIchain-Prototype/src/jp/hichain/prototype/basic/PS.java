package jp.hichain.prototype.basic;

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
	NORTHEAST(TYPE.POINT) {
		@Override
		public PS getOpposite() {
			return SOUTHWEST;
		}
	},
	EAST_NORTHEAST(TYPE.SIDE),
	EAST(TYPE.POINT),
	EAST_SOUTHEAST(TYPE.SIDE),
	SOUTHEAST(TYPE.POINT),
	SOUTH_SOUTHEAST(TYPE.SIDE),
	SOUTH(TYPE.POINT),
	SOUTH_SOUTHWEST(TYPE.SIDE),
	SOUTHWEST(TYPE.POINT),
	WEST_SOUTHWEST(TYPE.SIDE),
	WEST(TYPE.POINT),
	WEST_NORTHWEST(TYPE.SIDE),
	NORTHWEST(TYPE.POINT),
	NORTH_NORTHWEST(TYPE.SIDE);

	public enum TYPE {
		POINT,
		SIDE;
	}

	private final TYPE type;
	private final PS opposite;

	private PS(final TYPE type) {
		this.type = type;
	}

	public TYPE getType() {
		return type;
	}

	public abstract PS getOpposite();
}
