package jp.hichain.prototype.concept;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * 点・辺データ (Points/Sides)
 * 16方向
 * @author NT
 *
 */
public enum PS {
	NORTH(Direction.NORTH, Type.POINT),
	EAST(Direction.EAST, Type.POINT),
	SOUTH(Direction.SOUTH, Type.POINT),
	WEST(Direction.WEST, Type.POINT),
	NORTHEAST(Direction.NORTHEAST, Type.POINT, Type.CORNER),
	NORTHWEST(Direction.NORTHWEST, Type.POINT, Type.CORNER),
	SOUTHEAST(Direction.SOUTHEAST, Type.POINT, Type.CORNER),
	SOUTHWEST(Direction.SOUTHWEST, Type.POINT, Type.CORNER),
	NORTH_NORTHEAST(Direction.NORTH_NORTHEAST, Type.SIDE),
	NORTH_NORTHWEST(Direction.NORTH_NORTHWEST, Type.SIDE),
	EAST_NORTHEAST(Direction.EAST_NORTHEAST, Type.SIDE),
	EAST_SOUTHEAST(Direction.EAST_SOUTHEAST, Type.SIDE),
	SOUTH_SOUTHEAST(Direction.SOUTH_SOUTHEAST, Type.SIDE),
	SOUTH_SOUTHWEST(Direction.SOUTH_SOUTHWEST, Type.SIDE),
	WEST_NORTHWEST(Direction.WEST_NORTHWEST, Type.SIDE),
	WEST_SOUTHWEST(Direction.WEST_SOUTHWEST, Type.SIDE);

	/**
	 * 種類
	 * @author NT
	 *
	 */
	public enum Type {
		POINT,
		SIDE,
		CORNER;
	}

	/**
	 * 接点の種類
	 * @author NT
	 *
	 */
	public enum Contact {
		NONE,		//接点なし
		POINT_POINT,	//点と点で接する
		SIDE_SIDE;	//辺と辺で接する

	}

	private Direction commonDir;
	private EnumSet<Type> types;
	private EnumMap<SignDir.Rotation, PS> rotations;

	static {
		set( new PS [] {
			NORTH, EAST, SOUTH, WEST
		});
		set( new PS [] {
			NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST
		});
		set( new PS [] {
			NORTH_NORTHEAST, EAST_NORTHEAST, EAST_SOUTHEAST, SOUTH_SOUTHEAST, SOUTH_SOUTHWEST, WEST_SOUTHWEST, WEST_NORTHWEST, NORTH_NORTHWEST
		});
	}

	private PS(Direction direction, Type type) {
		commonDir = direction;
		types.add(type);
	}

	private PS(Direction direction, Type type1, Type type2) {
		this(direction, type1);
		types.add(type2);
	}

	public Direction getCommonDir() {
		return commonDir;
	}

	public EnumSet<Type> getTypes() {
		return types;
	}

	public PS get(SignDir.Rotation relation) {
		return rotations.get(relation);
	}

	private static void set(PS [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			dirs[i].rotations.put( SignDir.Rotation.LEFT, dirs[l] );
			dirs[i].rotations.put( SignDir.Rotation.RIGHT, dirs[r] );
		}
	}
}
