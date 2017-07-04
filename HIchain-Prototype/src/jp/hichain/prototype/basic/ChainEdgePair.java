package jp.hichain.prototype.basic;

import java.util.EnumMap;
import jp.hichain.prototype.basic.ChainNode.Edge;
import jp.hichain.prototype.basic.ChainNode.Relation;

/**
 * 端のChainNodeのペア
 * 成熟判定、ポイント換算で利用する
 * Created by NT on 2017/07/04.
 */
public class ChainEdgePair {
	private EnumMap<Edge, ChainNode> nodeMap;

	public ChainEdgePair(ChainNode node) {
		nodeMap = new EnumMap<Edge, ChainNode>(Edge.class) {{
			put(Edge.ROOT, node);
			put(Edge.LEAF, node);
		}};
	}

	public ChainEdgePair(ChainNode node, ChainEdgePair source, Relation updateRelation) {
		nodeMap = source.nodeMap.clone();
		nodeMap.put(updateRelation.getEdge(), node);
	}

	public void updateEdgeNode(ChainNode node, Edge edge) {
		nodeMap.put(edge, node);
	}

	public ChainNode getEdgeNode(Edge edge) {
		return nodeMap.get(edge);
	}
}
