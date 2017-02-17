package jp.hichain.prototype.concept;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.hichain.prototype.basic.DirComp;

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
	THIS(0, 0, 0, 0),
	NORTH(1, 0, 0, 0),
	NORTHEAST(1, 1, 0, 0),
	EAST(0, 1, 0, 0),
	SOUTHEAST(0, 1, 1, 0),
	SOUTH(0, 0, 1, 0),
	SOUTHWEST(0, 0, 1, 1),
	WEST(0, 0, 0, 1),
	NORTHWEST(1, 0, 0, 1);

	public enum TYPE {
		THIS,
		NEXT,
		CORNER;
	}

	private DirComp components;	//方角成分
	private int dx, dy;	//相対座標
	private TYPE type;	//タイプ

	private AroundDir(int north, int east, int south, int west) {
		components = new DirComp(north, east, south, west);
		setType();
		setAbPos();
	}

	/**
	 * タイプを返す
	 * @return AroundDir.TYPE
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * 方角の成分を返す
	 * @return DirComp
	 */
	public DirComp getComp() {
		return components;
	}

	public static List<AroundDir> breakup(AroundDir dir) {
		List<AroundDir> dirs = new ArrayList<AroundDir>(2);
		DirComp comp = dir.getComp();

		if (comp.getDenominator() != 8) {
			return null;
		}

		for (Map.Entry<Direction, Integer> entry : comp.getMap().entrySet()) {
			if (entry.getValue() == 1) {
				dirs.add( get( new DirComp(entry.getKey()) ) );
			}
		}

		return dirs;
	}

	/**
	 * 方角成分からAroundDirを返す
	 * @param comp 方角成分
	 * @return AroundDir
	 */
	public static AroundDir get(DirComp comp) {
		DirComp dirComp = comp;
		if (comp.getDenominator() == 16) {
			for (Direction dir : Direction.values()) {
				int value = comp.get(dir);
				if (value == 2) {
					dirComp = new DirComp(dir);
				}
			}
		}

		for (AroundDir dir : AroundDir.values()) {
			if (comp == dir.getComp()) {
				return dir;
			}
		}
		return null;
	}

	/**
	 * routeを返す
	 * @param aroundDir target
	 * @return route
	 */
	public AroundDir [] getRoute(AroundDir aroundDir) {
		AroundDir [] route = null;
		int sumDx = aroundDir.dx - this.dx;
		int sumDy = aroundDir.dy - this.dy;

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
	private static int [] getOutsideDir(int value) {
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
	private static AroundDir get(int dx, int dy) {
		for (AroundDir dir : AroundDir.values()) {
			if (dir.dx == dx && dir.dy == dy) {
				return dir;
			}
		}
		return null;
	}

	private void setType() {
		switch (components.getDenominator()) {
		case 4:
			type = TYPE.NEXT;
			break;
		case 8:
			type = TYPE.CORNER;
		default:
			type = TYPE.THIS;
			break;
		}
	}

	private void setAbPos() {
		dx = components.get(Direction.EAST) - components.get(Direction.WEST);
		dy = components.get(Direction.SOUTH) - components.get(Direction.NORTH);
	}

}
