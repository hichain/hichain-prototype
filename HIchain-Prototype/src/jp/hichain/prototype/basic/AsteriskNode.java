package jp.hichain.prototype.basic;

import java.util.*;

/**
 * Created by Tokiwa on 2017/06/25.
 */
public class AsteriskNode extends ChainNode {
	private Map<SignChar, ChainNode> substituteNodes;

	public AsteriskNode(Square _thisSq, ChainCombination _combination) {
		super(_thisSq, _combination);
		substituteNodes = new HashMap<>();
		Collections.unmodifiableMap(relationMap);
	}

	public void addSubstituteNode(SignChar signChar, ChainNode node) {
		substituteNodes.put(signChar, node);
	}

	public ChainNode getSubstituteNodes(SignChar signChar) {
		return substituteNodes.get(signChar);
	}

	public Collection<ChainNode> getSubstituteNodes() {
		return substituteNodes.values();
	}

	@Override
	public boolean isEdgeOf(Edge edge) {
		assert false : "Not allow isEdgeOf() from an AsteriskNode";
		return super.isEdgeOf(edge);
	}

	@Override
	public String toString() {
		if (getSubstituteNodes().size() == 0) return "!EMPTY";

		String str = getCombination() + "\n";
		ChainNode substituteNode = (ChainNode)getSubstituteNodes().toArray()[0];
		List<ChainNode> roots = substituteNode.getEdges(Edge.ROOT);
		for (int i = 0; i < roots.size(); i++) {
			ChainNode root = roots.get(i);
			str += root.toScoredString("");
			if (i < roots.size() - 1) str += "\n";
		}
		return str;
	}

}
