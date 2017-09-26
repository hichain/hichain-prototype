package jp.hichain.prototype.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.hichain.prototype.basic.*;
import jp.hichain.prototype.concept.Chain;

/**
 * 連鎖から得点に換算する
 * 考慮してない事項:
 * 回帰 (A-Z問題)
 */
public class Converter {
	private static Map<Chain, Integer> chainLengthMin;

	/**
	 * 加点される文字列長の下限を取得
	 * @param kind 連鎖の種類
	 * @return 加点される文字列長の下限
	 */
	public static int getChainLengthMin(Chain kind) {
		return chainLengthMin.get(kind);
	}

	/**
	 * 全マスから得点を換算する
	 * @param player プレイヤー
	 * @return 得点
	 */
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
				if (!targetNode.isEdgeOf(Chain.Relation.PARENT)) {
					continue;
				}
				if (!(targetNode.isMature() && targetNode.isValid())) {
					continue;
				}
				startNodes.add(targetNode);

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

	/**
	 * 加点される文字列長の下限を設定する
	 * @param alphabetical 連番
	 * @param identical ぞろ目
	 * @param royal ロイヤル
	 */
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

	/**
	 * ロイヤル文字列が完成しているか返す
	 * @param root 連鎖ノード
	 */
	private static boolean completedRoyalString(ChainNode root) {
		int royalPoints = getPoints(root, null);
		int royalMin = getChainLengthMin(Chain.ROYAL);
		return royalPoints >= royalMin*royalMin;
	}

	/**
	 * 得点を換算する
	 * @param root 連鎖ノード
	 * @param sourceNode rootのソースノード
	 * @return 得点
	 */
	private static int getPoints(ChainNode root, ChainNode sourceNode) {
		int points = 0;

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
}