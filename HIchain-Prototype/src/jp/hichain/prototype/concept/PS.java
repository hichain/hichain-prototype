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
	NORTH(Direction.NORTH, Block.CENTER, Type.POINT),
	EAST(Direction.EAST, Block.CENTER, Type.POINT),
	SOUTH(Direction.SOUTH, Block.CENTER, Type.POINT),
	WEST(Direction.WEST, Block.CENTER, Type.POINT),
	NORTHEAST(Direction.NORTH, Block.RIGHT, Type.POINT, Direction.EAST, Block.LEFT, Type.POINT, Direction.NORTHEAST, Block.CENTER, Type.CORNER),
	NORTHWEST(Direction.NORTH, Block.LEFT, Type.POINT, Direction.WEST, Block.RIGHT, Type.POINT, Direction.NORTHWEST, Block.CENTER, Type.CORNER),
	SOUTHEAST(Direction.SOUTH, Block.LEFT, Type.POINT, Direction.EAST, Block.RIGHT, Type.POINT, Direction.SOUTHEAST, Block.CENTER, Type.CORNER),
	SOUTHWEST(Direction.SOUTH, Block.RIGHT, Type.POINT, Direction.WEST, Block.LEFT, Type.POINT, Direction.SOUTHWEST, Block.CENTER, Type.CORNER),
	NORTH_NORTHEAST(Direction.NORTH, Block.RIGHT, Type.SIDE),
	NORTH_NORTHWEST(Direction.NORTH, Block.LEFT, Type.SIDE),
	EAST_NORTHEAST(Direction.EAST, Block.LEFT, Type.SIDE),
	EAST_SOUTHEAST(Direction.EAST, Block.RIGHT, Type.SIDE),
	SOUTH_SOUTHEAST(Direction.SOUTH, Block.LEFT, Type.SIDE),
	SOUTH_SOUTHWEST(Direction.SOUTH, Block.RIGHT, Type.SIDE),
	WEST_NORTHWEST(Direction.WEST, Block.RIGHT, Type.SIDE),
	WEST_SOUTHWEST(Direction.WEST, Block.LEFT, Type.SIDE);

	public enum Block {
		LEFT,
		CENTER,
		RIGHT;
	}

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
	private EnumMap<SignDir.Rotation, PS> rotations;
	private EnumMap<Direction, Part> blocktypes;

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

	private PS(Direction direction, Block block, Type type) {
		commonDir = Direction.valueOf(this.toString());
		blocktypes = new EnumMap<>(Direction.class);
		blocktypes.put( direction, new Part(block, type) );
	}

	private PS(Direction direction1, Block block1, Type type1, Direction direction2, Block block2, Type type2, Direction direction3, Block block3, Type type3) {
		this(direction1, block1, type1);
		blocktypes.put( direction2, new Part(block2, type2) );
		blocktypes.put( direction3, new Part(block3, type3) );
	}

	public Direction getCommonDir() {
		return commonDir;
	}

	public EnumSet<Direction> getSquareSides() {
		return (EnumSet<Direction>) blocktypes.keySet();
	}

	public Block getBlock(Direction dir) {
		return blocktypes.get(dir).block;
	}

	public Type getType(Direction dir) {
		return blocktypes.get(dir).type;
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

	private class Part {
		private Block block;
		private Type type;

		public Part(Block block, Type type) {
			this.block = block;
			this.type = type;
		}
	}
}
