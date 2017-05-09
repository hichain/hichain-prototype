package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.ChainMap;
import jp.hichain.prototype.basic.SignChar;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public class ChainSearcher {

	public static int search(Square root, AroundDir aroundDir) {
		int hits = 0;

		Square next = (Square)root.getAround(aroundDir);
		if (root == null || next == null) {
			return hits;
		}
		if (root.isEmpty() || next.isEmpty()) {
			return hits;
		}

		ChainMap chainMap = root.getChainMap();
		for (SignDir signDir : SignDir.values()) {
			SignChar rootSC = root.getSign().getSN().get(signDir);
			SignChar nextSC = next.getSign().getSN().get(signDir);
			if (rootSC == null || nextSC == null) {
				continue;
			}

			for (ScoredString kind : ScoredString.values()) {
				for (ScoredString.Order order : ScoredString.Order.values()) {
					SignChar targetSC = rootSC.getNext(kind, order);
					if (targetSC == null) {
						continue;
					}
					boolean result = targetSC.equals(nextSC);
					if (result) {
						chainMap.put(aroundDir, signDir, kind, order);
						hits++;
					}
				}
			}
		}

		char ch = root.getSign().getSC().get();
		System.out.println("'" + ch + "'\n" + chainMap.toString());
		return hits;
	}
}
