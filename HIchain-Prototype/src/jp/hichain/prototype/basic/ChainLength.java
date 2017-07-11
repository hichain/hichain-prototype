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
	private ChainNode thisNode;
	private EnumMap<Relation, Integer> lengthMap;

	public ChainLength(ChainNode node) {
		thisNode = node;
		lengthMap = new EnumMap<Relation, Integer>(Relation.class) {{
			put(Relation.PARENT, 0);
			put(Relation.CHILD, 0);
		}};
	}

	public ChainLength(ChainNode node, ChainLength source, Relation updateRelation) {
		thisNode = node;
		lengthMap = source.lengthMap.clone();
		int maxLength = getMaxLength() + 1;
		updateLength(1, updateRelation, maxLength, true);
		for (ChainNode nextNode : getNode().get(updateRelation)) {
			updateLength(1, updateRelation.getOpposite(), maxLength, false);
		}
	}

	public ChainNode getNode() {
		return thisNode;
	}

	public int getMaxLength(Relation relation) {
		return lengthMap.get(relation);
	}

	public int getMaxLength() {
		int sumLength = 1;
		for (int length : lengthMap.values()) {
			sumLength += length;
		}
		return sumLength;
	}

	private void updateLength(int addition, Relation updateRelation, int limitLength, boolean forceUpdate) {
		if (!forceUpdate && limitLength <= getMaxLength()) return;
		System.out.println("Updated ChainLength");
		lengthMap.put(updateRelation, getMaxLength(updateRelation) + addition);

		for (ChainNode nextNode : getNode().get(updateRelation.getOpposite())) {
			nextNode.getLength().updateLength(addition, updateRelation, limitLength, false);
		}
	}
}
