package jp.hichain.prototype.basic;

import java.util.EnumMap;

import com.sun.org.apache.regexp.internal.RE;
import jp.hichain.prototype.concept.Chain.Relation;

/**
 * 端のChainNodeのペア
 * 成熟判定、ポイント換算で利用する
 * Created by NT on 2017/07/04.
 * @deprecated 使われなくなったため
 */
public class ChainEdgePair {
	private EnumMap<Relation, ChainNode> nodeMap;

	public ChainEdgePair(ChainNode node) {
		nodeMap = new EnumMap<Relation, ChainNode>(Relation.class) {{
			put(Relation.PARENT, node);
			put(Relation.CHILD, node);
		}};
	}

	public ChainEdgePair(ChainNode node, ChainEdgePair source, Relation updateRelation) {
		nodeMap = source.nodeMap.clone();
		nodeMap.put(updateRelation, node);
	}

	public void updateEdgeNode(ChainNode node, Relation relation) {
		nodeMap.put(relation, node);
	}

	public ChainNode getEdgeNode(Relation relation) {
		return nodeMap.get(relation);
	}
}
