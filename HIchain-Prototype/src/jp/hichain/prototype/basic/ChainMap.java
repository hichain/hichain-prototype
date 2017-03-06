package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public class ChainMap {
	Map<Chain, List<AroundDir>> chainMap;

	public ChainMap() {
		chainMap = new HashMap<>();
	}

	public Map<Chain, List<AroundDir>> get() {
		return chainMap;
	}

	public List<AroundDir> get(Chain chain) {
		return chainMap.get(chain);
	}

	public int getSize(Chain chain) {
		if (!chainMap.containsKey(chain)) {
			return 0;
		}
		return chainMap.get(chain).size();
	}

	public void put(AroundDir aroundDir, SignDir signDir, ScoredString kind, ScoredString.Order order) {
		Chain chain = new Chain(signDir, kind, order);
		List<AroundDir> list;
		if (chainMap.containsKey(chain)) {
			list = chainMap.get(chain);
		} else {
			list = new ArrayList<>();
		}
		list.add(aroundDir);
		chainMap.put(chain, list);
	}

	@Override
	public String toString() {
		String string = "";
		for (Map.Entry<Chain, List<AroundDir>> entry : chainMap.entrySet()) {
			string += entry.getKey().toString() + "\n";
			string += "[AroundDir] ";
			for (AroundDir dir : entry.getValue()) {
				string += dir + " ";
			}
			string += "\n";
		}
		return string;
	}
}
