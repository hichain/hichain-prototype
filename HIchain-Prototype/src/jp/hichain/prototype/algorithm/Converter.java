package jp.hichain.prototype.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.hichain.prototype.basic.*;
import jp.hichain.prototype.concept.Chain;

/*
 * 考慮してない事項:
 * アスタリスク (WIP)
 * 回帰
 */
public class Converter {
	private static Map<Chain, Integer> chainLengthMin;

	public static int getChainLengthMin(Chain kind) {
		return chainLengthMin.get(kind);
	}

	public static int getPointsAll(Player player) {
		int points = 0;

		for (Position position : Position.getAll()) {
			Square square = position.getSquare();
			if (square.isEmpty() || square.getPlayer() != player) {
				continue;
			}

			for (Map.Entry<ChainCombination, ChainNode> chainEntry : square.getChainMap().entrySet()) {
				ChainCombination combination = chainEntry.getKey();
				ChainNode targetNode = chainEntry.getValue();

				Set<ChainNode> startNodes = new HashSet<>();
				//*ノードならその中の孤立ノードを探す
				if (targetNode instanceof AsteriskNode) {
					AsteriskNode asteriskNode = (AsteriskNode)targetNode;
					for (ChainPair pair : asteriskNode.getPairs()) {
						if (pair.isAlone()) {
							startNodes.add(pair.getAloneNode());
						}
					}
					if (startNodes.size() == 0) continue;
				} else {
					if (!targetNode.isEdgeOf(Chain.Relation.PARENT)) {
						continue;
					}
					if (!(targetNode.isMature() && targetNode.isValid())) {
						continue;
					}
					startNodes.add(targetNode);
				}

				for (ChainNode node : startNodes) {
					if (combination.getKind() == Chain.ROYAL) {
						if (completedRoyalString(node)) return -1;
					}

					points += getPoints(node, null);
				}

			}
		}

		return points;
	}

	public static void init(int alphabetical, int identical, int royal) {
		chainLengthMin = new HashMap<Chain, Integer>() {{
			put(
				Chain.ALPHABETICAL, alphabetical
			);
			put(
				Chain.IDENTICAL, identical
			);
			put(
				Chain.ROYAL, royal
			);
		}};
	}

	private static boolean completedRoyalString(ChainNode root) {
		int royalPoints = getPoints(root, null);
		int royalMin = getChainLengthMin(Chain.ROYAL);
		return royalPoints >= royalMin*royalMin;
	}

	private static int getPoints(ChainNode root, ChainNode sourceNode) {
		int points = 0;

		if (root instanceof AsteriskNode) return getPoints((AsteriskNode)root, sourceNode);

		if (!(root.isMature() && root.isValid())) {
			int sourceChildLength = sourceNode.getLength(Chain.Relation.CHILD);
			sourceChildLength++;
			return sourceChildLength*sourceChildLength;
		}

		int childLength = root.getLength(Chain.Relation.CHILD);
		if (childLength == 0) {
			int parentLength = root.getLength(Chain.Relation.PARENT);
			parentLength++;
			return parentLength*parentLength;
		}

		for (ChainNode child : root.get(Chain.Relation.CHILD)) {
			points += getPoints(child, root);
		}

		return points;
	}

	private static int getPoints(AsteriskNode root, ChainNode sourceNode) {
		int points = 0;

		for (ChainNode child : root.get(sourceNode, Chain.Relation.CHILD)) {
			points += getPoints(child, root);
		}

		return points;
	}
}