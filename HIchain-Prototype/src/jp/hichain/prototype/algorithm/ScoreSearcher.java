package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.AsteriskNode;
import jp.hichain.prototype.basic.ChainCombination;
import jp.hichain.prototype.basic.ChainNode;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.ScoredString;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by NT on 2017/06/11.
 */
public class ScoreSearcher {
	public static boolean judgeMature(ChainNode node) {
		ScoredString kind = node.getCombination().getKind();
		int maxLength = getMaxLength(node);
		if (Converter.getChainLengthMin(kind) <= maxLength) {
			node.setMatureAll(true);
			return true;
		}
		return false;
	}

	public static boolean judgeValid(ChainNode node) {
		int maxLength = getMaxLength(node);
		int anotherMaxLength = 0;

		Collection<ChainNode> nodes;
		if (node.isSubstituteForAsterisk()) {
			AsteriskNode asteriskNode = (AsteriskNode)(node.getSquare().getChainNode( node.getCombination() ));
			nodes = asteriskNode.getSubstituteNodes();
		} else {
			nodes = node.getSquare().getChainNodes();
		}
		for (ChainNode overlappingNode : nodes) {
			if (overlappingNode == node) continue;
			int max = getMaxLength(overlappingNode);
			if (anotherMaxLength < max) {
				anotherMaxLength = max;
			}
		}
		if (maxLength <= anotherMaxLength) {
			return false;
		}
		for (ChainNode overlappingNode : nodes) {
			overlappingNode.setValidAll(false);
		}
		node.setValidAll(true);
		return true;
	}

	public static int getMaxLength(ChainNode node) {
		int max = 1;
		max += getMaxLength(node, ChainNode.Relation.PARENT, 0);
		max += getMaxLength(node, ChainNode.Relation.CHILD,0);
		return max;
	}

	private static int getMaxLength(ChainNode root, ChainNode.Relation relation, int length) {
		if (root.isEdgeOf( relation.getEdge() )) {
			if (!root.isActive()) length--;
			return length;
		}

		int max = 0;
		for (ChainNode node : root.get(relation)) {
			int branchMax = getMaxLength(node, relation,length+1);
			if (max < branchMax) {
				max = branchMax;
			}
		}

		return max;
	}
}
