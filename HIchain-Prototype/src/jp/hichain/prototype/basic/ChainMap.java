package jp.hichain.prototype.basic;

import java.util.EnumMap;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public class ChainMap {
	EnumMap
		<AroundDir, EnumMap
			<SignDir, EnumMap
				<ScoredString, ScoredString.Order>
			>
		>
	chainMap;

	public ChainMap() {
		chainMap = new EnumMap<>(AroundDir.class);
	}

	public boolean contains(AroundDir aroundDir) {
		return chainMap.containsKey(aroundDir);
	}

	public EnumMap<SignDir, EnumMap<ScoredString, ScoredString.Order>> get(AroundDir aroundDir) {
		return chainMap.get(aroundDir);
	}

	public void put(AroundDir aroundDir, SignDir signDir, ScoredString kind, ScoredString.Order order) {
		if (!chainMap.containsKey(aroundDir)) {
			chainMap.put(aroundDir, new EnumMap<>(SignDir.class) );
		}
		EnumMap<SignDir, EnumMap<ScoredString, ScoredString.Order>> signDirMap
		= chainMap.get(aroundDir);

		if (!signDirMap.containsKey(signDir)) {
			signDirMap.put(signDir, new EnumMap<>(ScoredString.class));
		}
		EnumMap<ScoredString, ScoredString.Order> ssMap = signDirMap.get(signDir);

		if (ssMap.containsKey(kind)) {
			System.out.println("!Overwrite ChainMap");
		}

		ssMap.put(kind, order);
	}

	@Override
	public String toString() {
		String string = "";
		for (AroundDir aroundDir : chainMap.keySet()){
			string += "[" + aroundDir + "]\n";
			for (SignDir signDir : chainMap.get(aroundDir).keySet()) {
				string += " :" + signDir + "\n";
				for (ScoredString kind : chainMap.get(aroundDir).get(signDir).keySet()) {
					ScoredString.Order order = chainMap.get(aroundDir).get(signDir).get(kind);
					string += "  <" + kind + "> " + order + "\n";
				}
			}
		}
		return string;
	}
}
