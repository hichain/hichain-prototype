package jp.hichain.prototype.basic;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.Direction.Relative;

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

	public DirComp getRelative(Relative relative) {
		Map <Direction, Integer> newMap = new HashMap<Direction, Integer>();
		for (Map.Entry<Direction, Integer> entry : components.entrySet()) {
			newMap.put(entry.getKey().getRelative(relative), entry.getValue());
		}
		return new DirComp(newMap);
	}

	public boolean isToward(Direction dir) {
		Direction [] maxDir = new Direction [2];
		int max = 0;
		for (Map.Entry<Direction, Integer> entry : components.entrySet()) {
			if (entry.getValue() > max) {
				maxDir[0] = entry.getKey();
				max = entry.getValue();
			} else if (entry.getValue() == max) {
				maxDir[1] = entry.getKey();
			}
		}
		return (maxDir[0] == dir) || (maxDir[1] == dir);
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
		return components.equals(comp);
	}
}
