package jp.hichain.prototype.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jp.hichain.prototype.concept.Direction;

public class DirComp implements Cloneable {
	private Map<Direction, Integer> components;

	public DirComp(int north, int east, int south, int west) {
		components = new HashMap<Direction, Integer>() {
			{put(Direction.NORTH, north);}
			{put(Direction.EAST, east);}
			{put(Direction.SOUTH, south);}
			{put(Direction.WEST, west);}
		};
	}

	public DirComp(Direction direction) {
		components = new HashMap<Direction, Integer>();
		for (Direction dir : Direction.values()) {
			components.put(dir, (dir == direction) ? 1 : 0);
		}
	}

	public DirComp(Map <Direction, Integer> comp) {
		components = comp;
	}

	public Map<Direction, Integer> getMap() {
		return components;
	}

	public int getDenominator() {
		int sum = 0;
		for (int comp : components.values()) {
			sum += comp;
		}

		if (sum == 0) {
			return 0;
		}
		return 4 * (int)Math.pow(2, sum-1);
	}

	public DirComp getRelative(Direction.Relation relative) {
		Map <Direction, Integer> newMap = new HashMap<Direction, Integer>();
		for (Map.Entry<Direction, Integer> entry : components.entrySet()) {
			newMap.put(entry.getKey().get(relative), entry.getValue());
		}
		return new DirComp(newMap);
	}

	public int get(Direction dir) {
		return components.get(dir);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DirComp comp = (DirComp)obj;
		return components.equals(comp.components);
	}

	@Override
	public int hashCode() {
		return Objects.hash(components);
	}

	@Override
	public DirComp clone() throws CloneNotSupportedException {
		DirComp comp = new DirComp(this.getMap());
		return comp;
	}
}
