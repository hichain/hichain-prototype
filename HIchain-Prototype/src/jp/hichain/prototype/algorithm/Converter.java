package jp.hichain.prototype.algorithm;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.basic.ChainCombination;
import jp.hichain.prototype.basic.ChainNode;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.Position;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

/*
 * 考慮してない事項:
 * アスタリスク
 * ぞろ目
 * 重複除去 (X*Z問題)
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
			for (SignDir signDir : SignDir.values()) {
				boolean over = gameIsOver(square, signDir);
				if (over) return -1;
				points += getPoints(square, signDir, ScoredString.ALPHABETICAL);
				points += getPoints(square, signDir, ScoredString.IDENTICAL);
			}
		}

		return points;
	}

	private static boolean gameIsOver(Square root, SignDir signDir) {
		int royalPoints = getPoints(root, signDir, ScoredString.ROYAL);
		int royalMin = getChainLengthMin(ScoredString.ROYAL);
		return royalPoints >= royalMin*royalMin;
	}

	private static int getPoints(Square root, SignDir signDir, ScoredString ssKind) {
		ChainCombination condition = new ChainCombination(signDir, ssKind);
		ChainNode rootNode = root.getChainNode(condition);
		if (rootNode == null || !rootNode.isEdgeOf(ChainNode.Edge.ROOT)) return 0;

		return getPoints(rootNode, condition, 1);
	}

	private static int getPoints(ChainNode root, ChainCombination condition, int length) {
		int points = 0;

		if (root.isEdgeOf(ChainNode.Edge.LEAF)) {
			if( getChainLengthMin(condition.getKind()) <= length ) {
				return length*length;
			}
			return 0;
		}

		for (ChainNode child : root.get(ChainNode.Relation.CHILD)) {
			points += getPoints(child, condition, length+1);
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