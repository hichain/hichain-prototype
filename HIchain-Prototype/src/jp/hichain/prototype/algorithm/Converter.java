package jp.hichain.prototype.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.hichain.prototype.basic.*;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

/*
 * 考慮してない事項:
 * アスタリスク (WIP)
 * 回帰
 */
public class Converter {
	private static Map<ScoredString, Integer> chainLengthMin;

	public static int getChainLengthMin(ScoredString kind) {
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
					if (!targetNode.isEdgeOf(ChainNode.Edge.ROOT)) {
						continue;
					}
					if (!(targetNode.isMature() && targetNode.isValid())) {
						continue;
					}
					startNodes.add(targetNode);
				}

				if (combination.getKind() == ScoredString.ROYAL) {
					for (ChainNode node : startNodes) {
						if ( completedRoyalString(node)) return -1;
						points += getPoints(node, null, 1);
					}
				}

			}
		}

		return points;
	}

	public static void init(int alphabetical, int identical, int royal) {
		chainLengthMin = new HashMap<ScoredString, Integer>() {{
			put(
					ScoredString.ALPHABETICAL, alphabetical
			);
			put(
					ScoredString.IDENTICAL, identical
			);
			put(
					ScoredString.ROYAL, royal
			);
		}};
	}

	private static boolean completedRoyalString(ChainNode root) {
		int royalPoints = getPoints(root, null, 1);
		int royalMin = getChainLengthMin(ScoredString.ROYAL);
		return royalPoints >= royalMin*royalMin;
	}

	private static int getPoints(ChainNode root, ChainNode sourceNode, int length) {
		int points = 0;

		if (root instanceof AsteriskNode) return getPoints((AsteriskNode)root, sourceNode, length);
		if (!(root.isMature() && root.isValid())) {
			return (length-1)*(length-1);
		}

		for (ChainNode child : root.get(ChainNode.Relation.CHILD)) {
			points += getPoints(child, root, length+1);
		}

		return points;
	}

	private static int getPoints(AsteriskNode root, ChainNode sourceNode, int length) {
		int points = 0;

		if (!root.isMature(sourceNode, ChainNode.Relation.CHILD) ) {
			return (length-1)*(length-1);
		}

		for (ChainNode child : root.get(sourceNode, ChainNode.Relation.CHILD)) {
			points += getPoints(child, root, length+1);
		}

		return points;
	}
}