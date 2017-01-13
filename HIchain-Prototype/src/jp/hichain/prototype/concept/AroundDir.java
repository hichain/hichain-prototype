package jp.hichain.prototype.concept;

/**
 * 文字の周り
 * 8方向
 * @author NT
 *
 */
public enum AroundDir {
	NORTH(0, -1) {
		@Override
		public AroundDir getOpposite() {
			return SOUTH;
		}
	},
	NORTHEAST(1, -1) {
		@Override
		public AroundDir getOpposite() {
			return SOUTHWEST;
		}
	},
	EAST(1, 0) {
		@Override
		public AroundDir getOpposite() {
			return WEST;
		}
	},
	SOUTHEAST(1, 1) {
		@Override
		public AroundDir getOpposite() {
			return NORTHWEST;
		}
	},
	SOUTH(0, 1) {
		/*
		source = SOUTH(0, 1)
		sum = target - source
		sum = (0, 0) は本来this(0, 0)だが、以下の分解式に当てはめるとsum = (-1, 1) + (1, -1)となる
		どちらでも問題なく動く

		sumの分解式(x, y) (交換法則は成り立たない):
		-2 -> -1 + -1
		-1 -> -1 + 0
		0 -> -1 + 1
		1 -> 1 + 0
		2 -> 1 + 1

		target:
			centerからみて -> sourceからみて
		NORTH(0, -1) -> NORTHWEST(-1, -1) + NORTHEAST(1, -1)
		NORTHEAST(1, -1) -> NORTHEAST(1, -1) + NORTH(0, -1)
		EAST(1, 0) -> NORTHEAST(1, -1)
		SOUTHEAST(1, 1) -> EAST(1, 0)
		SOUTH(0, 1) -> this(0, 0)
		SOUTHWEST(-1, 1) -> WEST(-1, 0)
		WEST(-1, 0) -> NORTHWEST(-1, -1)
		NORTHWEST(-1, -1) -> NORTHWEST(-1, -1) + NORTH(0, -1)
		*/
		@Override
		public AroundDir getOpposite() {
			return NORTH;
		}
	},
	SOUTHWEST(-1, 1) {
		@Override
		public AroundDir getOpposite() {
			return NORTHEAST;
		}
	},
	WEST(-1, 0) {
		@Override
		public AroundDir getOpposite() {
			return EAST;
		}
	},
	NORTHWEST(-1, -1) {
		@Override
		public AroundDir getOpposite() {
			return SOUTHEAST;
		}
	};

	private int dx;
	private int dy;

	private AroundDir(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	public abstract AroundDir getOpposite();

	public AroundDir [] getRoute(AroundDir dir) {
		AroundDir sum = get(dir.getDx() - this.dx, dir.getDy() - this.dy);
		int targetDx = sum.getDx();
		int targetDy = sum.getDy();
		if (targetDx == 0 && targetDy == 0) {
			return new AroundDir[] {this};
		}
		int [] formulaX = getFormula(targetDx);
		int [] formulaY = getFormula(targetDy);
		AroundDir [] dirs = new AroundDir [2];
		dirs[0] = get(formulaX[0], formulaY[0]);
		dirs[1] = get(formulaX[1], formulaY[1]);
		return dirs;
	}

	public static int [] getFormula(int value) {
		int [] result = new int [2];
		if (value > 0) {
			result[0] = value - 1;
			result[1] -= result[0];
		} else if (value < 0) {
			result[0] = value + 1;
			result[1] -= result[0];
		} else {
			result[0] = -1;
			result[1] = 1;
		}
		return result;
	}

	public static AroundDir get(int dx, int dy) {
		for (AroundDir dir : AroundDir.values()) {
			if (dir.getDx() == dx && dir.getDy() == dy) {
				return dir;
			}
		}
		return null;
	}
}
