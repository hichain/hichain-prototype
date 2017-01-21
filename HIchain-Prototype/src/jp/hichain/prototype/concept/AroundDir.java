package jp.hichain.prototype.concept;

/**
 * 文字の周り
 * 8方向
 * [AroundDIr 種類]
 * Anti-AroundDir: 反対の方角のAroundDir (北 <-> 南)
 * center: 中心
 * source: 始点 (center視点)
 * target: 終点 (center視点)
 * sum: source視点のtarget
 * route: sourceからtargetに行くルート
 * @author NT
 *
 */

/*
ex)
source = SOUTH(0, 1)

target - source = sum -> route
NORTH(0, -1) - SOUTH(0, 1) = (0, -2) -> NORTHWEST(-1, -1) + NORTHEAST(1, -1)
NORTHEAST(1, -1) - SOUTH(0, 1) = (1, -2) -> NORTHEAST(1, -1) + NORTH(0, -1)
NORTHWEST(-1, -1) - SOUTH(0, 1) = (-1, -2) -> NORTHWEST(-1, -1) + NORTH(0, -1)
SOUTH(0, 1) - SOUTH(0, 1) = (0, 0) -> THIS(0, 0)
SOUTHEAST(1, 1) - SOUTH(0, 1) = (1, 0) -> EAST(1, 0)
SOUTHWEST(-1, 1) - SOUTH(0, 1) = (-1, 0) -> WEST(-1, 0)
EAST(1, 0) - SOUTH(0, 1) = (1, -1) -> NORTHEAST(1, -1)
WEST(-1, 0) - SOUTH(0, 1) = (-1, -1) -> NORTHWEST(-1, -1)

sumがAroundDirの中になければ分解する (1段階広い方角)
[sum分解式]
-2 -> -1 + -1
-1 -> -1 + 0
0 -> -1 + 1
1 -> 1 + 0
2 -> 1 + 1
*/

public enum AroundDir {
	//center
	THIS(0, 0) {
		@Override
		public AroundDir getOpposite() {
			return THIS;
		}
	},
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

	private int dx;	//相対x座標
	private int dy;	//相対y座標

	/**
	 * コンストラクタ
	 * @param dx 相対x座標
	 * @param dy 相対y座標
	 */
	private AroundDir(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * 相対x座標を返す
	 * @return 相対x座標
	 */
	public int getDx() {
		return dx;
	}

	/**
	 * 相対y座標を返す
	 * @return 相対y座標
	 */
	public int getDy() {
		return dy;
	}

	/**
	 * 反対の方角を返す
	 * @return Anti-AroundDir
	 */
	public abstract AroundDir getOpposite();

	/**
	 * routeを返す
	 * @param aroundDir target
	 * @return route
	 */
	public AroundDir [] getRoute(AroundDir aroundDir) {
		AroundDir [] route = null;
		int sumDx = aroundDir.getDx() - this.dx;
		int sumDy = aroundDir.getDy() - this.dy;

		AroundDir sum = get(sumDx, sumDy);
		if (sum == null) {
			int [] dirDx = getOutsideDir(sumDx);
			int [] dirDy = getOutsideDir(sumDy);
			route = new AroundDir [] {
					get(dirDx[0], dirDy[0]),
					get(dirDx[1], dirDy[1])
			};
		} else {
			route = new AroundDir [] {sum	};
		}

		return route;
	}

	/**
	 * 範囲外のAroudDirの相対座標(x or y)を返す
	 * sumからrouteに分解する
	 * @param value 相対座標(x/y)
	 * @return 相対座標(x/y)
	 */
	public static int [] getOutsideDir(int value) {
		int [] result = {
			(value > 0) ? 1 : -1,
			(value % 2 == 0) ? ((value >= 0) ? 1 : -1) : 0
		};
		return result;
	}

	/**
	 * 相対座標からAroudDirを返す
	 * @param dx 相対x座標
	 * @param dy 相対y座標
	 * @return 条件を満たすAroudDir (該当なしはnull)
	 */
	public static AroundDir get(int dx, int dy) {
		for (AroundDir dir : AroundDir.values()) {
			if (dir.getDx() == dx && dir.getDy() == dy) {
				return dir;
			}
		}
		return null;
	}
}
