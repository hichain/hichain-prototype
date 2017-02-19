package jp.hichain.prototype.concept;

import jp.hichain.prototype.basic.DirComp;

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

	private SignDir left, right;
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
	public SignDir getRelative(Direction.Relation dir) {
		switch (dir) {
		case LEFT:
			return left;
		case RIGHT:
			return right;
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
		left = getByComp( components.getRelative(Direction.Relation.LEFT) );
		right = getByComp( components.getRelative(Direction.Relation.RIGHT) );
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
