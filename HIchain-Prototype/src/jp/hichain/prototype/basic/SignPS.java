package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.EnumSet;

import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.PS;
import jp.hichain.prototype.concept.SignDir;

/**
 * 文字のPS
 * 16個のPSの集合
 * @author NT
 *
 */
public class SignPS {
	private EnumSet<PS> oldSet;
	private EnumMap<PS.Type, TypePart> typeparts;

	public SignPS() {
		oldSet = EnumSet.noneOf(PS.class);
		typeparts = new EnumMap<PS.Type, TypePart>(PS.Type.class) {{
			put(PS.Type.SIDE, new TypePart(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
			put(PS.Type.POINT, new TypePart(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
			put(PS.Type.CORNER, new TypePart(Direction.NORTHEAST, Direction.SOUTHEAST, Direction.SOUTHWEST, Direction.NORTHWEST));
		}};
	}


	public TypePart getTypePart(PS.Type type) {
		return typeparts.get(type);
	}

	public void add(PS ps) {
		oldSet.add(ps);
		for ( Direction squareside : ps.getSquareSides() ) {
			PS.Block block = ps.getBlock(squareside);
			PS.Type type = ps.getType(squareside);
			typeparts.get(type).get(squareside).add(block);
		}
	}

	public void rotate(SignDir.Rotation rel) {

	}

	@Override
	public String toString() {
		String string = "";
		for (PS ps : oldSet) {
			string += ps + " ";
		}
		return string;
	}

	public class TypePart {
		private EnumMap<Direction, Block> blocks;

		private TypePart(Direction dir1, Direction dir2, Direction dir3, Direction dir4) {
			this.blocks = new EnumMap<Direction, Block>(Direction.class) {{
				put(dir1, new Block());
				put(dir2, new Block());
				put(dir3, new Block());
				put(dir4, new Block());
			}};
		}

		public boolean contains(Direction dir, PS.Block block) {
			return blocks.get(dir).get(block);
		}

		private Block get(Direction dir) {
			return blocks.get(dir);
		}
	}

	private class Block {
		private EnumSet<PS.Block> map;

		private Block() {
			map = EnumSet.noneOf(PS.Block.class);
		}

		private boolean get(PS.Block dir) {
			return map.contains(dir);
		}

		private void add(PS.Block dir) {
			map.add(dir);
		}
	}
}
