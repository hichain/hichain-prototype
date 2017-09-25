package jp.hichain.prototype.algorithm;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import jp.hichain.prototype.basic.*;
import jp.hichain.prototype.concept.Chain;
import jp.hichain.prototype.concept.SignDir;

/**
 * 連鎖探索
 * 連鎖ツリーを構築する
 * 成熟・有効判定
 */
public class ChainSearcher {

	/**
	 * 連鎖を探索する
	 * マス1→マス2における連鎖を探索し成熟・有効を判定する
	 * @param me マス1
	 * @param you マス2
	 * @return ヒットした連鎖数
	 */
	public static int search(Square me, Square you) {
		int hits = 0;

		if (me == null || you == null) {
			return 0;
		}
		if (me.isEmpty() || you.isEmpty()) {
			return 0;
		}

		for (SignDir signDir : SignDir.values()) {
			SignChar mySC = me.getSign().getSN().get(signDir);
			SignChar yourSC = you.getSign().getSN().get(signDir);
			if (mySC == null || yourSC == null)	continue;

			for (Chain kind : Chain.values()) {
				ChainCombination combination = new ChainCombination(signDir, kind);
				hits += search(me, you, combination);
			}
		}

		return hits;
	}

	/**
	 * 指定の連鎖の組み合わせにおける連鎖を判定する
	 * マス1→マス2における連鎖を探索し成熟・有効を判定する
	 * @param me マス1
	 * @param you マス2
	 * @param combination 連鎖の組み合わせ
	 * @return ヒットした連鎖数
	 */
	private static int search(Square me, Square you, ChainCombination combination) {
		int hits = 0;

		SignDir signDir = combination.getSignDir();
		Chain ssKind = combination.getKind();
		SignChar mySC = me.getSign().getSN().get(signDir);
		SignChar yourSC = you.getSign().getSN().get(signDir);

		EnumSet<Chain.Relation> relations = EnumSet.of(Chain.Relation.PARENT);
		if (ssKind != Chain.IDENTICAL) {
			relations.add(Chain.Relation.CHILD);
		}
		for (Chain.Relation relation : relations) {
			SignChar targetSC = mySC.getRelation(ssKind, relation);
			if (targetSC == null) continue;

			boolean result = targetSC.equals(yourSC);
			if (!result) continue;
			addChainNode(me, you, combination, relation);
			judgeMatureAndValid(me, you, combination);

			hits++;
		}

		return hits;
	}

	/**
	 * 成熟・有効を判定する
	 * マス1→マス2における連鎖の成熟・有効を判定
	 * @param me マス1
	 * @param you マス2
	 * @param combination 連鎖の組み合わせ
	 */
	private static void judgeMatureAndValid(Square me, Square you, ChainCombination combination) {
		ChainNode myNode = me.getChainNode(combination);
		ChainNode yourNode = you.getChainNode(combination);
		if (yourNode.isMature()) {
			myNode.setMature(true);
		} else {
			ScoreSearcher.judgeMature(myNode, combination);
		}
		if (!yourNode.isValid()) {
			ScoreSearcher.judgeValid(yourNode);
		}
		if (!myNode.isValid()) {
			ScoreSearcher.judgeValid(myNode);
		}
	}

	/**
	 * 連鎖ツリーにノードを追加する
	 * @param me マス1
	 * @param you マス2
	 * @param combination 連鎖の組み合わせ
	 * @param relation 連鎖関係
	 */
	private static void addChainNode(Square me, Square you, ChainCombination combination, Chain.Relation relation) {
		System.out.println(me.getPosition() + " -> " + you.getPosition() + ":" + combination.getSignDir() + " => " + combination.getKind() + " (" + relation + ")");
		ChainNode myNode = me.getChainNode(combination);
		ChainNode yourNode = you.getChainNode(combination);
		if (myNode == null && yourNode == null) {
			yourNode = new ChainNode(you, combination);
			myNode = new ChainNode(me, combination, yourNode, relation);
		} else if (yourNode == null) {
			yourNode = new ChainNode(you, combination, myNode, relation.getOpposite());
		} else if (myNode == null) {
			myNode = new ChainNode(me, combination, yourNode, relation);
		}
	}
}
