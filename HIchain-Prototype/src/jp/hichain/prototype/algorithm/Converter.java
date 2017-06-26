package jp.hichain.prototype.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.basic.*;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

/*
 * 考慮してない事項:
 * アスタリスク
 * 連鎖ループ問題 (A-Z問題)
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

			Collection<ChainNode> nodes = square.getChainNodes();
			for (ChainNode node : nodes) {
				if (node instanceof AsteriskNode) {
					for (ChainNode substituteNode : ((AsteriskNode) node).getSubstituteNodes()) {
						int point = getPoints(substituteNode, substituteNode.getCombination().getKind());
						if (point == -1) return -1;
						points += point;
					}
				} else {
					int point = getPoints(node, node.getCombination().getKind());
					if (point == -1) return -1;
					points += point;
				}
			}
		}

		return points;
	}

	private static boolean completedRoyalString(ChainNode root) {
		int royalPoints = getPoints(root, ScoredString.ROYAL, 1);
		int royalMin = getChainLengthMin(ScoredString.ROYAL);
		return royalPoints >= royalMin*royalMin;
	}

	private static int getPoints(ChainNode node, ScoredString kind) {
		if (!(node.isActive() && node.isEdgeOf(ChainNode.Edge.ROOT))) {
			return 0;
		}
		if (!(node.isMature() && node.isValid())) {
			return 0;
		}
		if (kind == ScoredString.ROYAL) {
			if ( completedRoyalString(node) ) return -1;
		}
		return getPoints(node, kind, 1);
	}

	private static int getPoints(ChainNode root, ScoredString kind, int length) {
		int points = 0;

		if (root.isEdgeOf(ChainNode.Edge.LEAF)) {
			if( getChainLengthMin(kind) <= length ) {
				return length*length;
			}
			return 0;
		}

		for (ChainNode child : root.get(ChainNode.Relation.CHILD)) {
			points += getPoints(child, kind, length+1);
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
}