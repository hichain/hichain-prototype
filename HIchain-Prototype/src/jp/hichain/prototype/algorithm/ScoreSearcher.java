package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.AsteriskNode;
import jp.hichain.prototype.basic.ChainCombination;
import jp.hichain.prototype.basic.ChainNode;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.Chain;

import java.util.Map;

/**
 * Created by NT on 2017/06/11.
 */
public class ScoreSearcher {
	public static boolean judgeMature(ChainNode node, ChainCombination combination) {
		int maxLength = getMaxLength(node);
		if (Converter.getChainLengthMin(combination.getKind()) <= maxLength) {
			node.setMatureAll(true);
			return true;
		}
		return false;
	}

	public static boolean judgeValid(ChainNode node) {
		if (node instanceof AsteriskNode) {
			return true;
		}

		int maxLength = getMaxLength(node);
		int anotherMaxLength = 0;
		Map<ChainCombination, ChainNode> nodes = node.getSquare().getChainMap();
		for (ChainNode overlappingNode : nodes.values()) {
			if (overlappingNode == node) continue;
			int max = getMaxLength(overlappingNode);
			if (anotherMaxLength < max) {
				anotherMaxLength = max;
			}
		}
		if (maxLength <= anotherMaxLength) {
			return false;
		}
		for (ChainNode overlappingNode : nodes.values()) {
			overlappingNode.setValidAll(false);
		}
		node.setValidAll(true);
		return true;
	}

	private static int getMaxLength(ChainNode node) {
		int max = 0;
		max += getMaxLength(node, Chain.Relation.PARENT, null, 1);
		max += getMaxLength(node, Chain.Relation.CHILD, null, 1);
		return --max;
	}

	private static int getMaxLength(ChainNode root, Chain.Relation relation, ChainNode source, int length) {
		if (root instanceof AsteriskNode) {
			return getMaxLength((AsteriskNode)root, relation, source, length);
		}
		if (root.isEdgeOf(relation)) {
			return length;
		}

		int max = 0;
		for (ChainNode node : root.get(relation)) {
			int branchMax = getMaxLength(node, relation, root, length+1);
			if (max < branchMax) {
				max = branchMax;
			}
		}

		return max;
	}

	private static int getMaxLength(AsteriskNode root, Chain.Relation relation, ChainNode source, int length) {
		if (root.isEdgeOf( relation, source )) {
			return length-1;
		}

		int max = 0;
		for (ChainNode node : root.get(source, relation)) {
			int branchMax = getMaxLength(node, relation, root, length+1);
			if (max < branchMax) {
				max = branchMax;
			}
		}

		return max;
	}
}
