package jp.hichain.prototype.concept;

import java.util.EnumMap;
import java.util.Set;

/**
 * 点・辺データ (Points/Sides)
 * 16方向
 * @author NT
 *
 */
public enum PS {
	NORTH(AroundDir.NORTH, Block.CENTER, Type.POINT),
	EAST(AroundDir.EAST, Block.CENTER, Type.POINT),
	SOUTH(AroundDir.SOUTH, Block.CENTER, Type.POINT),
	WEST(AroundDir.WEST, Block.CENTER, Type.POINT),
	NORTHEAST(AroundDir.NORTH, Block.RIGHT, Type.POINT, AroundDir.EAST, Block.LEFT, Type.POINT, AroundDir.NORTHEAST, Block.CENTER, Type.CORNER),
	NORTHWEST(AroundDir.NORTH, Block.LEFT, Type.POINT, AroundDir.WEST, Block.RIGHT, Type.POINT, AroundDir.NORTHWEST, Block.CENTER, Type.CORNER),
	SOUTHEAST(AroundDir.SOUTH, Block.LEFT, Type.POINT, AroundDir.EAST, Block.RIGHT, Type.POINT, AroundDir.SOUTHEAST, Block.CENTER, Type.CORNER),
	SOUTHWEST(AroundDir.SOUTH, Block.RIGHT, Type.POINT, AroundDir.WEST, Block.LEFT, Type.POINT, AroundDir.SOUTHWEST, Block.CENTER, Type.CORNER),
	NORTH_NORTHEAST(AroundDir.NORTH, Block.RIGHT, Type.SIDE),
	NORTH_NORTHWEST(AroundDir.NORTH, Block.LEFT, Type.SIDE),
	EAST_NORTHEAST(AroundDir.EAST, Block.LEFT, Type.SIDE),
	EAST_SOUTHEAST(AroundDir.EAST, Block.RIGHT, Type.SIDE),
	SOUTH_SOUTHEAST(AroundDir.SOUTH, Block.LEFT, Type.SIDE),
	SOUTH_SOUTHWEST(AroundDir.SOUTH, Block.RIGHT, Type.SIDE),
	WEST_NORTHWEST(AroundDir.WEST, Block.RIGHT, Type.SIDE),
	WEST_SOUTHWEST(AroundDir.WEST, Block.LEFT, Type.SIDE);

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

	private EnumMap<Direction.Relation, PS> rotations;
	private EnumMap<AroundDir, Part> blocktypes;

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

	private PS(AroundDir direction, Block block, Type type) {
		rotations = new EnumMap<>(Direction.Relation.class);
		blocktypes = new EnumMap<>(AroundDir.class);
		blocktypes.put( direction, new Part(block, type) );
	}

	private PS(AroundDir direction1, Block block1, Type type1, AroundDir direction2, Block block2, Type type2, AroundDir direction3, Block block3, Type type3) {
		this(direction1, block1, type1);
		blocktypes.put( direction2, new Part(block2, type2) );
		blocktypes.put( direction3, new Part(block3, type3) );
	}

	public Direction getCommonDir() {
		return Direction.valueOf(this.name());
	}

	public Set<AroundDir> getSquareSides() {
		return blocktypes.keySet();
	}

	public Block getBlock(AroundDir dir) {
		return blocktypes.get(dir).block;
	}

	public Type getType(AroundDir dir) {
		return blocktypes.get(dir).type;
	}

	public PS get(Direction.Relation relation) {
		return rotations.get(relation);
	}

	public static PS getEnum(Direction dir) {
		for (PS ps : PS.values()) {
			if (ps.getCommonDir() == dir) {
				return ps;
			}
		}
		return null;
	}

	private static void set(PS [] dirs) {
		for (int i = 0; i < dirs.length; i++) {
			int l = (i == 0) ? dirs.length-1 : i-1;
			int r = (i == dirs.length-1) ? 0 : i+1;
			int o = ( (dirs.length == 2) ? (i+2) : (i+3) ) % dirs.length;
			dirs[i].rotations.put( Direction.Relation.LEFT, dirs[l] );
			dirs[i].rotations.put( Direction.Relation.RIGHT, dirs[r] );
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
