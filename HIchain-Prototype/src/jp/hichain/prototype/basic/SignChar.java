package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.ScoredString;

public class SignChar {
	private static Map<Character, SignChar> signs = new HashMap<>();

	private char signChar;
	private EnumMap<ScoredString, EnumMap<ScoredString.Relation, SignChar>> relationMap;

	private SignChar(char ch) {
		signChar = ch;
		relationMap = new EnumMap<>(ScoredString.class);
	}

	static {
		init();
	}

	public char get() {
		return signChar;
	}

	public SignChar getRelation(ScoredString kind, ScoredString.Relation relation) {
		if (!relationMap.containsKey(kind)) {
			return null;
		}
		return relationMap.get(kind).get(relation);
	}

	public static boolean contains(char ch) {
		return signs.containsKey(ch);
	}

	public static SignChar get(char ch) {
		return signs.get(ch);
	}

	private static void init() {
		signs.put( ' ', new SignChar(' ') );

		for (ScoredString kind : ScoredString.values()) {
			String orderString = kind.getOrderString();
			char [] chs = orderString.toCharArray();
			//signs生成
			for (char ch : chs) {
				if (signs.containsKey(ch)) {
					continue;
				}
				signs.put( ch, new SignChar(ch) );
			}

			//nextMap生成
			for (int i = 0; i < chs.length; i++) {
				int l = (i == 0) ? chs.length-1 : i-1;
				int r = (i == chs.length-1) ? 0 : i+1;

				SignChar sc = signs.get(chs[i]);
				SignChar lsc = signs.get(chs[l]);
				SignChar rsc = signs.get(chs[r]);

				EnumMap<ScoredString.Relation, SignChar> map = new EnumMap<>(ScoredString.Relation.class);
				map.put( ScoredString.Relation.NEXT, rsc );
				map.put( ScoredString.Relation.PREVIOUS, lsc );
				map.put( ScoredString.Relation.SAME, sc );

				sc.relationMap.put(kind, map);
			}
		}

		signs.put( '*', new SignChar('*') );
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		SignChar that = (SignChar)obj;
		return signChar == that.signChar;
	}

	@Override
	public String toString() {
		return String.valueOf(signChar);
	}
}
