package jp.hichain.prototype.basic;

import java.util.EnumMap;

import jp.hichain.prototype.concept.Chain;
import jp.hichain.prototype.concept.Chain.Relation;

/**
 * 連鎖の長さ
 * 連鎖ツリーにおける、あるノードからみた端ノードまでの長さの最大値
 * 成熟判定で用いる
 */
public class ChainLength {
	private ChainNode thisNode;
	private EnumMap<Relation, Integer> lengthMap;

	/**
	 * 連鎖の長さを初期化して長さ0の連鎖を表す
	 * @param node 対応する連鎖ノード
	 */
	ChainLength(ChainNode node) {
		thisNode = node;
		lengthMap = new EnumMap<Relation, Integer>(Relation.class) {{
			put(Relation.PARENT, 0);
			put(Relation.CHILD, 0);
		}};
	}

	/**
	 * ある連鎖の長さから拡張して生成する
	 * @param node 対応する連鎖ノード
	 * @param source 連鎖ノードのソース
	 * @param updateRelation ソースから見た拡張する方向
	 */
	ChainLength(ChainNode node, ChainLength source, Relation updateRelation) {
		thisNode = node;
		lengthMap = source.lengthMap.clone();
		lengthMap.put(updateRelation.getOpposite(), 0);
		int maxLength = getMaxLength() + 1;
		updateLength(1, updateRelation, maxLength, true);
		for (ChainNode nextNode : thisNode.get(updateRelation)) {
			nextNode.getLength().updateLength(1, updateRelation.getOpposite(), maxLength, false);
		}
	}

	/**
	 * 対応する連鎖ノードを取得する
	 * @return 連鎖ノード
	 */
	public ChainNode getNode() {
		return thisNode;
	}

	/**
	 * 指定の方向の連鎖の長さの最大値を取得する
	 * @param relation 連鎖の方向
	 * @return 連鎖の長さの最大値
	 */
	public int getMaxLength(Relation relation) {
		return lengthMap.get(relation);
	}

	/**
	 * この連鎖ツリーの長さの最大値を取得する
	 * @return 連鎖の長さの最大値
	 */
	public int getMaxLength() {
		int sumLength = 1;
		for (int length : lengthMap.values()) {
			sumLength += length;
		}
		return sumLength;
	}

	/**
	 * 連鎖の長さを更新する
	 * @param addition 長さの増加分
	 * @param updateRelation 更新する連鎖の方向
	 * @param limitLength 更新する連鎖の長さの上限
	 * @param forceUpdate 上限によらず強制的に更新するかどうか
	 */
	private void updateLength(int addition, Relation updateRelation, int limitLength, boolean forceUpdate) {
		if (!forceUpdate && limitLength <= getMaxLength()) return;

		lengthMap.put(updateRelation, getMaxLength(updateRelation) + addition);

		for (ChainNode nextNode : getNode().get(updateRelation.getOpposite())) {
			nextNode.getLength().updateLength(addition, updateRelation, limitLength, false);
		}
	}
}
