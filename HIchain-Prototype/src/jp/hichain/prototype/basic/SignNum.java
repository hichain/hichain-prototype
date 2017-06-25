package jp.hichain.prototype.basic;


import java.util.EnumMap;
import java.util.Map;

import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.SignDir;

/**
 * 文字番号
 * @author NT
 *
 */
public class SignNum {
	EnumMap<SignDir, SignChar> snMap;

	public SignNum() {
		snMap = new EnumMap<>(SignDir.class);
	}

	public SignNum(EnumMap<SignDir, SignChar> map) {
		snMap = map;
	}

	public void put(SignDir dir, SignChar sc) {
		snMap.put(dir, sc);
	}

	public SignChar get(SignDir dir) {
		return snMap.get(dir);
	}

	public void rotate(Direction.Relation _dir) {
		EnumMap<SignDir, SignChar> newMap = new EnumMap<>(SignDir.class);
		for (Map.Entry<SignDir, SignChar> e : snMap.entrySet()) {
			newMap.put(e.getKey().get(_dir), e.getValue());
		}
		snMap = newMap;
	}

	@Override
	public String toString() {
		String string = "";
		for (Map.Entry<SignDir, SignChar> entry : snMap.entrySet()) {
			string += entry.getKey() + ": " + entry.getValue() + " ";
		}
		return string;
	}
}
