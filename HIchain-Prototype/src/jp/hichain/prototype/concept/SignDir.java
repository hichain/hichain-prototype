package jp.hichain.prototype.concept;

/**
 * 文字の向き
 * 4方向
 * @author NT
 *
 */
public enum SignDir {
	NORTH {
		@Override
		public SignDir getOpposite() {
			return SOUTH;
		}
		@Override
		public SignDir getLeft() {
			return WEST;
		}
		@Override
		public SignDir getRight() {
			return EAST;
		}
	},
	EAST {
		@Override
		public SignDir getOpposite() {
			return WEST;
		}
		@Override
		public SignDir getLeft() {
			return NORTH;
		}
		@Override
		public SignDir getRight() {
			return SOUTH;
		}
	},
	SOUTH {
		@Override
		public SignDir getOpposite() {
			return NORTH;
		}
		@Override
		public SignDir getLeft() {
			return EAST;
		}
		@Override
		public SignDir getRight() {
			return WEST;
		}
	},
	WEST {
		@Override
		public SignDir getOpposite() {
			return EAST;
		}
		@Override
		public SignDir getLeft() {
			return SOUTH;
		}
		@Override
		public SignDir getRight() {
			return NORTH;
		}
	};

	public abstract SignDir getOpposite();
	public abstract SignDir getLeft();
	public abstract SignDir getRight();
}
