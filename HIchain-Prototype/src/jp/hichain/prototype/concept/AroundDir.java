package jp.hichain.prototype.concept;

/**
 * 文字の周り
 * 8方向
 * @author NT
 *
 */
public enum AroundDir {
	NORTH {
		@Override
		public AroundDir getOpposite() {
			return SOUTH;
		}
	},
	NORTHEAST {
		@Override
		public AroundDir getOpposite() {
			return SOUTHWEST;
		}
	},
	EAST {
		@Override
		public AroundDir getOpposite() {
			return WEST;
		}
	},
	SOUTHEAST {
		@Override
		public AroundDir getOpposite() {
			return NORTHWEST;
		}
	},
	SOUTH {
		@Override
		public AroundDir getOpposite() {
			return NORTH;
		}
	},
	SOUTHWEST {
		@Override
		public AroundDir getOpposite() {
			return NORTHEAST;
		}
	},
	WEST {
		@Override
		public AroundDir getOpposite() {
			return EAST;
		}
	},
	NORTHWEST {
		@Override
		public AroundDir getOpposite() {
			return SOUTHEAST;
		}
	};

	public abstract AroundDir getOpposite();
	
}
