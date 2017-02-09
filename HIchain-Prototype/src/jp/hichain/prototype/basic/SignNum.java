package jp.hichain.prototype.basic;


import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.SignDir;

/**
 * 文字番号
 * @author NT
 *
 */
public class SignNum {
	Map <SignDir, Character> snMap;

	public SignNum() {
		snMap = new HashMap<SignDir, Character>(4);
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

	public void rotate(SignDir.RELATIVE _dir) {
		Map <SignDir, Character> newMap = new HashMap<SignDir, Character>(snMap);
		for (Map.Entry<SignDir, Character> e : snMap.entrySet()) {
			newMap.put(e.getKey().getRelative(_dir), e.getValue());
		}
	}
}
