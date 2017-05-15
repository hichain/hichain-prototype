package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public class ChainMap {
	Map<ChainCondition, List<AroundDir>> chainMap;
	List<ChainEdge> chainEdges;

	public ChainMap() {
		chainMap = new HashMap<>();
	}

	public Map<ChainCondition, List<AroundDir>> get() {
		return chainMap;
	}

	public List<AroundDir> get(ChainCondition chain) {
		return chainMap.get(chain);
	}

	public int getSize(ChainCondition chain) {
		if (!chainMap.containsKey(chain)) {
			return 0;
		}
		return chainMap.get(chain).size();
	}

	public void put(AroundDir aroundDir, SignDir signDir, ScoredString kind, ScoredString.Order order) {
		ChainCondition chainCnd = new ChainCondition(signDir, kind, order);
		List<AroundDir> list;
		if (chainMap.containsKey(chainCnd)) {
			list = chainMap.get(chainCnd);
		} else {
			list = new ArrayList<>();
		}
		list.add(aroundDir);
		chainMap.put(chainCnd, list);
	}

	@Override
	public String toString() {
		String string = "";
		for (Map.Entry<ChainCondition, List<AroundDir>> entry : chainMap.entrySet()) {
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
