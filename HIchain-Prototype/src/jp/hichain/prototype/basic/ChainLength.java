package jp.hichain.prototype.basic;

import java.util.EnumMap;

import jp.hichain.prototype.concept.Chain;
import jp.hichain.prototype.concept.Chain.Relation;

/**
 * 連鎖の長さ
 * 連鎖ツリーにおける、あるノードからみた端ノードまでの長さ
 * 成熟判定でChainEdgePairと共に用いる
 * Created by NT on 2017/07/04.
 */
public class ChainLength {
	private EnumMap<Relation, Integer> lengthMap;

	public ChainLength() {
		lengthMap = new EnumMap<Relation, Integer>(Relation.class) {{
			put(Relation.PARENT, 0);
			put(Relation.CHILD, 0);
		}};
	}

	public ChainLength(ChainLength source, Relation updateRelation) {
		lengthMap = source.lengthMap.clone();
		updateLength(1, updateRelation);
	}

	public int getLength(Relation relation) {
		return lengthMap.get(relation);
	}

	private void updateLength(int addition, Relation relation) {
		lengthMap.put(relation, getLength(relation) + addition);
		for (ChainNode nextNode : get(relation)) {
			nextNode.updateLength(addition, relation.getOpposite());
		}
		for (ChainNode nextNode : get(relation.getOpposite())) {
			nextNode.updateLength(addition, relation);
		}
	}
}
