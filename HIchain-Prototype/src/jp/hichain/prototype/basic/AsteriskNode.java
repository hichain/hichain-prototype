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
		System.out.println("WARNING");
		return super.isEdgeOf(edge);
	}
}
