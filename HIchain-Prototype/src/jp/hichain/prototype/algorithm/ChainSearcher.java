package jp.hichain.prototype.algorithm;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import jp.hichain.prototype.basic.*;
import jp.hichain.prototype.concept.Chain;
import jp.hichain.prototype.concept.SignDir;

public class ChainSearcher {

	public static int search(Square me, Square you) {
		int hits = 0;

		if (me == null || you == null) {
			return hits;
		}
		if (me.isEmpty() || you.isEmpty()) {
			return hits;
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
