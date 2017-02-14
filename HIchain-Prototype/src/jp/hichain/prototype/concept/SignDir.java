package jp.hichain.prototype.concept;

import jp.hichain.prototype.basic.DirComp;
import jp.hichain.prototype.concept.Direction.Relative;

/**
 * 文字の向き
 * 4方向
 * @author NT
 *
 */
public enum SignDir {
	NORTH(1, 0, 0, 0),
	EAST(0, 1, 0, 0),
	SOUTH(0, 0, 1, 0),
	WEST(0, 0, 0, 1);

	public enum RELATIVE {
		LEFT,
		RIGHT,
		OPPOSITE;
	}

	private SignDir left, right, opposite;
	private DirComp components;

	private SignDir(int north, int east, int south, int west) {
		components = new DirComp(north, east, south, west);
		setRelative();
	}

	/**
	 * 方角の成分を返す
	 * @return DirComp
	 */
	public DirComp getComp() {
		return components;
	}

	/**
	 * 相対方角を返す
	 * @param relative 相対方角
	 * @return SignDir
	 */
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

	/**
	 * 文字列からそれに該当するenumを返す
	 * @param str 文字列
	 * @return SignDir
	 */
	public static SignDir getEnum(String str) {
		SignDir [] dirs = SignDir.values();
		for (SignDir signDir : dirs) {
			if (str.equals(signDir.name())) {
				return signDir;
			}
		}
		return null;
	}

	private void setRelative() {
		left = getByComp( components.getRelative(Relative.LEFT) );
		right = getByComp( components.getRelative(Relative.RIGHT) );
		opposite = getByComp( components.getRelative(Relative.OPPOSITE) );
	}

	private static SignDir getByComp(DirComp comp) {
		for (SignDir dir : SignDir.values()) {
			if (comp == dir.getComp()) {
				return dir;
			}
		}
		return null;
	}
}
