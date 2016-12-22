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
	NORTHEAST(TYPE.POINT) {
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
	SOUTHEAST(TYPE.POINT) {
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
			return PS.NORTH_NORTHWEST;
		}
	},
	SOUTHWEST(TYPE.POINT) {
		@Override
		public PS getOpposite() {
			return NORTHEAST;
		}
	},
	WEST_SOUTHWEST(TYPE.SIDE) {
		@Override
		public PS getOpposite() {
			return PS.EAST_SOUTHEAST;
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
			return PS.EAST_NORTHEAST;
		}
	},
	NORTHWEST(TYPE.POINT) {
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
		SIDE;
	}

	private final TYPE type;

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

	/**
	 * 反対側のPSを返す
	 * @return 反対側のPS
	 */
	public abstract PS getOpposite();
}
