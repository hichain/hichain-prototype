package jp.hichain.prototype.algorithm;

import java.util.List;
import java.util.Map;

import jp.hichain.prototype.basic.Chain;
import jp.hichain.prototype.basic.ChainMap;
import jp.hichain.prototype.basic.RRChainSign;
import jp.hichain.prototype.concept.AroundDir;

public class Converter {
	public static int getAdditionalPoints(RRChainSign root) {
		ChainMap chainMap = root.getChainMap();

		for (Map.Entry<Chain, List<AroundDir>> entry : chainMap.get().entrySet()) {
			Chain chain = entry.getKey();
			for (AroundDir aroundDir : entry.getValue()) {
				RRChainSign next = (RRChainSign)root.getAround(aroundDir);

			}
		}
	}
}
