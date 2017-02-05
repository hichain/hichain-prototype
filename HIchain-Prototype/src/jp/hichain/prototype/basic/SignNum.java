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
}
