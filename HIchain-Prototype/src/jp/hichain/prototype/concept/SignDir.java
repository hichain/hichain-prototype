package jp.hichain.prototype.concept;

import java.util.EnumMap;

/**
 * 文字の向き (4方向)
 * @author NT
 *
 */
public enum SignDir {
	NORTH,
	EAST,
	SOUTH,
	WEST;

	private EnumMap<Direction.Relation, SignDir> rotations;

	private SignDir() {
		rotations = new EnumMap<>(Direction.Relation.class);
	}

	static {
		set( new SignDir [] {
			NORTH, EAST, SOUTH, WEST
		});
	}

	public Direction getCommonDir() {
		return Direction.valueOf(this.name());
	}

	public SignDir get(Direction.Relation relation) {
		return rotations.get(relation);
	}

	private static void set(SignDir [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			int o = (i+2) % dirs.length;
			dirs[i].rotations.put( Direction.Relation.LEFT, dirs[l] );
			dirs[i].rotations.put( Direction.Relation.RIGHT, dirs[r] );
			dirs[i].rotations.put( Direction.Relation.OPPOSITE, dirs[o] );
		}
	}
}
