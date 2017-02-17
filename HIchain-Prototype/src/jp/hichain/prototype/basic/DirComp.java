package jp.hichain.prototype.basic;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.Direction;

public class DirComp {
	private Map<Direction, Integer> components;

	public DirComp(int north, int east, int south, int west) {
		components = new HashMap<Direction, Integer>() {
			{put(Direction.NORTH, north);}
			{put(Direction.EAST, east);}
			{put(Direction.SOUTH, south);}
			{put(Direction.WEST, west);}
		};
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

	public DirComp getRelative(Direction.Relative relative) {
		Map <Direction, Integer> newMap = new HashMap<Direction, Integer>();
		for (Map.Entry<Direction, Integer> entry : components.entrySet()) {
			newMap.put(entry.getKey().getRelative(relative), entry.getValue());
		}
		return new DirComp(newMap);
	}

	public int get(Direction dir) {
		return components.get(dir);
	}

	private void put(Direction dir, Integer value) {
		components.put(dir, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DirComp comp = (DirComp)obj;

		if (comp.getDenominator() == 16) {
			for (Direction dir : Direction.values()) {
				int value = comp.get(dir);
				if (value != 0) {
					comp.put(dir, --value);
				}
			}
		}

		return components.equals(comp);
	}
}
