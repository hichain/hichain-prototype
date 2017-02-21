package jp.hichain.prototype.concept;

import java.util.EnumMap;

/**
 * 文字の向き (4方向)
 * @author NT
 *
 */
public enum SignDir {
	NORTH(Direction.NORTH),
	EAST(Direction.EAST),
	SOUTH(Direction.SOUTH),
	WEST(Direction.WEST);

	public enum Rotation {
		LEFT,
		RIGHT;
	}

	private Direction commonDir;
	private EnumMap<Rotation, SignDir> rotations;

	private SignDir(Direction direction) {
		commonDir = direction;
	}

	static {
		set( new SignDir [] {
			NORTH, EAST, SOUTH, WEST
		});
	}

	public Direction getCommonDir() {
		return commonDir;
	}

	public SignDir get(Rotation relation) {
		return rotations.get(relation);
	}

	private static void set(SignDir [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			dirs[i].rotations.put( SignDir.Rotation.LEFT, dirs[l] );
			dirs[i].rotations.put( SignDir.Rotation.RIGHT, dirs[r] );
		}
	}
}
