package jp.hichain.prototype.algorithm;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.basic.ChainCondition;
import jp.hichain.prototype.basic.ChainNode;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.Position;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.ScoredString.Order;
import jp.hichain.prototype.concept.SignDir;

/*
 * 考慮してない事項:
 * アスタリスク
 * ぞろ目
 * 重複除去 (X*Z問題)
 * 連鎖ループ問題 (UR)
 */
public class Converter {
	private static Map<ChainCondition, Integer> chainLengthMin;

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
				ChainCondition abcalCondition = new ChainCondition(signDir, ScoredString.ALPHABETICAL, Order.ASCEND);
				ChainNode node = square.getChainNode(abcalCondition);
				if (node != null && node.isRoot()) {
					System.out.println(points);
					points += getPoints(node, abcalCondition);
				}
				ChainCondition idCondition = new ChainCondition(signDir, ScoredString.ALPHABETICAL, Order.SAME);
				node = square.getChainNode(idCondition);
				if (node != null && node.isRoot()) {
					points += getPoints(node, idCondition);
				}
			}
		}

		return points;
	}

	private static boolean gameIsOver(Square root, SignDir signDir) {
		ChainCondition royalCondition = new ChainCondition(signDir, ScoredString.ROYAL, ScoredString.Order.ASCEND);
		ChainNode royalNode = root.getChainNode(royalCondition);
		if (royalNode == null || !royalNode.isRoot()) {
			return false;
		}
		int royalPoints = getPoints(royalNode, royalCondition);
		int royalMin = getChainLengthMin(royalCondition);
		if (royalPoints >= royalMin*royalMin) {
			return true;
		}
		return false;
	}

	private static int getPoints(ChainNode root, ChainCondition condition) {
		return getPoints(root, condition, 0);
	}

	private static int getPoints(ChainNode root, ChainCondition condition, int length) {
		int points = 0;

		if (root.isLeaf()) {
			if( getChainLengthMin(condition) <= length ) {
				return length*length;
			}
			return 0;
		}

		for (ChainNode child : root.getChildren()) {
			points += getPoints(child, condition, length+1);
		}

		return points;
	}

	private static int getChainLengthMin(ChainCondition condition) {
		return chainLengthMin.get(new ChainCondition(condition.getKind(), condition.getOrder()));
	}

	public static void init(int alphabetical, int identical, int royal) {
		chainLengthMin = new HashMap<ChainCondition, Integer>() {{
			put(
				new ChainCondition(ScoredString.ALPHABETICAL, Order.ASCEND), alphabetical
			);
			put(
				new ChainCondition(ScoredString.ALPHABETICAL, Order.SAME), identical
			);
			put(
				new ChainCondition(ScoredString.ROYAL, Order.ASCEND), royal
			);
		}};
	}
}