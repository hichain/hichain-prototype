package jp.hichain.prototype.basic;


import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.Direction;

/**
 * 文字番号
 * @author NT
 *
 */
public class SignNum {
	Map <Direction, Character> snMap;

	public SignNum() {
		snMap = new HashMap<Direction, Character>(4);
	}

	public SignNum(Map <Direction, Character> map) {
		snMap = map;
	}

	public void add(Direction dir, char sc) {
		snMap.put(dir, sc);
	}

	public char get(Direction dir) {
		return snMap.get(dir);
	}

	public void rotate(Direction.Relation _dir) {
		Map <Direction, Character> newMap = new HashMap<Direction, Character>(snMap);
		for (Map.Entry<Direction, Character> e : snMap.entrySet()) {
			newMap.put(e.getKey().getRelation(_dir), e.getValue());
		}
	}
}
