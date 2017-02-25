package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.ScoredString;

public class SignChar {
	private static Map<Character, SignChar> signs = new HashMap<>();;

	private char signChar;
	private EnumMap<ScoredString.Order, SignChar> nextMap;

	private SignChar(char ch) {
		signChar = ch;
		nextMap = new EnumMap<>(ScoredString.Order.class);
	}

	static {
		signs.putAll(
			getAllSC(new char [] {
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
			})
		);

		signs.put( ' ', new SignChar(' ') );
		SignChar asterisk = new SignChar('*');
		for (ScoredString.Order order : ScoredString.Order.values()) {
			asterisk.nextMap.put(order, asterisk);
		}
		signs.put(asterisk.get(), asterisk);

	}

	public char get() {
		return signChar;
	}

	public SignChar getNext(ScoredString.Order order) {
		return nextMap.get(order);
	}

	public static SignChar get(char ch) {
		return signs.get(ch);
	}

	private static Map<Character, SignChar> getAllSC(char [] chs) {
		Map<Character, SignChar> map = new HashMap<>();

		for (char ch : chs) {
			map.put( ch, new SignChar(ch) );
		}

		for (int i = 0; i < chs.length; i++) {
			int l = (i == 0) ? chs.length-1 : i-1;
			int r = (i == chs.length-1) ? 0 : i+1;

			SignChar sc = map.get(chs[i]);
			SignChar lsc = map.get(chs[l]);
			SignChar rsc = map.get(chs[r]);
			sc.nextMap.put( ScoredString.Order.ASCEND, rsc );
			sc.nextMap.put( ScoredString.Order.DESCEND, lsc );
			sc.nextMap.put( ScoredString.Order.SAME, sc );
		}

		return map;
	}
}
