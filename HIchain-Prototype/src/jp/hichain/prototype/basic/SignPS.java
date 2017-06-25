package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.PS;

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
			put(PS.Type.SIDE, new TypePart(AroundDir.NORTH, AroundDir.EAST, AroundDir.SOUTH, AroundDir.WEST));
			put(PS.Type.POINT, new TypePart(AroundDir.NORTH, AroundDir.EAST, AroundDir.SOUTH, AroundDir.WEST));
			put(PS.Type.CORNER, new TypePart(AroundDir.NORTHEAST, AroundDir.SOUTHEAST, AroundDir.SOUTHWEST, AroundDir.NORTHWEST));
		}};
	}


	public TypePart getTypePart(PS.Type type) {
		return typeparts.get(type);
	}

	public void add(PS ps) {
		oldSet.add(ps);
		for ( AroundDir squareside : ps.getSquareSides() ) {
			PS.Block block = ps.getBlock(squareside);
			PS.Type type = ps.getType(squareside);
			typeparts.get(type).get(squareside).add(block);
			//System.out.println("[" + squareside + "] add " + block + " (" + type + ")");
		}
	}

	public void rotate(Direction.Relation rel) {
		EnumSet<PS> newSet = EnumSet.copyOf(oldSet);
		for (PS ps : oldSet) {
			newSet.add(ps.get(rel));
		}

		for (Map.Entry<PS.Type, TypePart> entry : typeparts.entrySet()) {
			TypePart typePart = entry.getValue();
			for (AroundDir dir : typePart.keySet()) {
				typePart.put(dir, typePart.get( dir.get(rel) ));
			}
		}
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
		private EnumMap<AroundDir, Block> blocks;

		private TypePart(AroundDir dir1, AroundDir dir2, AroundDir dir3, AroundDir dir4) {
			this.blocks = new EnumMap<AroundDir, Block>(AroundDir.class) {{
				put(dir1, new Block());
				put(dir2, new Block());
				put(dir3, new Block());
				put(dir4, new Block());
			}};
		}

		public boolean contains(AroundDir dir, PS.Block block) {
			return blocks.get(dir).get(block);
		}

		private Block get(AroundDir dir) {
			return blocks.get(dir);
		}

		private void put(AroundDir dir, Block block) {
			blocks.put(dir, block);
		}

		public Set<AroundDir> keySet() {
			return blocks.keySet();
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
