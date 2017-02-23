package jp.hichain.prototype.basic;


import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.SignDir;

/**
 * 文字番号
 * @author NT
 *
 */
public class SignNum {
	Map <SignDir, Character> snMap;

	public SignNum() {
		snMap = new HashMap<>(4);
	}

	public SignNum(Map <SignDir, Character> map) {
		snMap = map;
	}

	public void add(SignDir dir, char sc) {
		snMap.put(dir, sc);
	}

	public char get(SignDir dir) {
		return snMap.get(dir);
	}

	public void rotate(Direction.Relation _dir) {
		Map <SignDir, Character> newMap = new HashMap<>(snMap);
		for (Map.Entry<SignDir, Character> e : snMap.entrySet()) {
			newMap.put(e.getKey().get(_dir), e.getValue());
		}
	}
}
