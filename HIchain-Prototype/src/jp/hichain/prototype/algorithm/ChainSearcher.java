package jp.hichain.prototype.algorithm;

import java.util.EnumSet;

import jp.hichain.prototype.basic.ChainCombination;
import jp.hichain.prototype.basic.ChainNode;
import jp.hichain.prototype.basic.SignChar;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;
import jp.hichain.prototype.ui.SignData;

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
			boolean hasAsterisk = (mySC.get() == '*') || (yourSC.get() == '*');

			for (ScoredString kind : ScoredString.values()) {
				ChainCombination combination = new ChainCombination(signDir, kind);
				if (hasAsterisk) {
					hits += searchAsterisk(me, you, combination);
				} else {
					hits += search(me, you, combination);
				}
			}
		}

		return hits;
	}

	private static int search(Square me, Square you, ChainCombination combination) {
		int hits = 0;

		SignDir signDir = combination.getSignDir();
		ScoredString ssKind = combination.getKind();
		SignChar mySC = me.getSign().getSN().get(signDir);
		SignChar yourSC = you.getSign().getSN().get(signDir);

		EnumSet<ScoredString.Relation> relations = EnumSet.of(ScoredString.Relation.PREVIOUS);
		if (ssKind != ScoredString.IDENTICAL) {
			relations.add(ScoredString.Relation.NEXT);
		}
		for (ScoredString.Relation relation : relations) {
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

	private static int searchAsterisk(Square me, Square you, ChainCombination combination) {
		int hits = 0;

		SignDir signDir = combination.getSignDir();
		ScoredString ssKind = combination.getKind();
		SignChar mySC = me.getSign().getSN().get(signDir);
		SignChar yourSC = you.getSign().getSN().get(signDir);

		//必ずyouが*になるようにする
		if (mySC.get() == '*') return searchAsterisk(you, me, combination);

		ChainNode asteNode = you.getChainNode(combination);
		boolean skipSearching = (asteNode == null);

		EnumSet<ScoredString.Relation> relations = EnumSet.of(ScoredString.Relation.PREVIOUS);
		if (!(ssKind == ScoredString.IDENTICAL)) {
			relations.add(ScoredString.Relation.NEXT);
		}
		for (ScoredString.Relation ssRelation : relations) {
			SignChar targetSC = mySC.getRelation(combination.getKind(), ssRelation, 2);
			if (targetSC == null) continue;

			boolean hit = skipSearching || isChainedToAsterisk(asteNode, targetSC, ssRelation, combination);
			if (!hit) continue;

			addChainNode(me, you, combination, ssRelation);

			if (skipSearching) {
				asteNode = you.getChainNode(combination);
				ChainNode.Relation nodeRelation = (ssRelation == ScoredString.Relation.PREVIOUS) ? ChainNode.Relation.CHILD : ChainNode.Relation.PARENT;
				asteNode.setActive(nodeRelation, false);
			}

			judgeMatureAndValid(me, you, combination);

			hits++;
		}

		return hits;
	}

	private static boolean isChainedToAsterisk(ChainNode asteNode, SignChar targetSC, ScoredString.Relation ssRelation, ChainCombination combination) {
		SignDir signDir = combination.getSignDir();
		ScoredString ssKind = combination.getKind();

		ChainNode.Relation nodeRelation = (ssRelation == ScoredString.Relation.PREVIOUS) ? ChainNode.Relation.PARENT : ChainNode.Relation.CHILD;
		asteNode.setActive(nodeRelation, true);
		for (ChainNode nextNode : asteNode.get(nodeRelation)) {
			SignChar nextSC = nextNode.getSquare().getSign().getSN().get(signDir);
			if (nextSC.equals(targetSC)) {
				return true;
			}
		}
		asteNode.setActive(nodeRelation, false);
		return false;
	}

	private static void judgeMatureAndValid(Square me, Square you, ChainCombination combination) {
		ChainNode myNode = me.getChainNode(combination);
		ChainNode yourNode = you.getChainNode(combination);
		if (yourNode.isMature()) {
			myNode.setMature(true);
		} else {
			ScoreSearcher.judgeMature(me, combination);
		}
		if (!yourNode.isValid()) {
			ScoreSearcher.judgeValid(yourNode);
		}
		if (!myNode.isValid()) {
			ScoreSearcher.judgeValid(myNode);
		}
	}

	private static void addChainNode(Square me, Square you, ChainCombination combination, ScoredString.Relation relation) {
		System.out.println(me.getPosition() + " -> " + you.getPosition() + ":" + combination.getSignDir() + " => " + combination.getKind() + " (" + relation + ")");
		ChainNode myNode = me.getChainNode(combination);
		if (myNode == null) {
			myNode = new ChainNode(me);
			me.addChainNode(combination, myNode);
		}
		ChainNode yourNode = you.getChainNode(combination);
		if (yourNode == null) {
			yourNode = new ChainNode(you);
			you.addChainNode(combination, yourNode);
		}

		switch (relation) {
			case PREVIOUS:
				myNode.add(yourNode, ChainNode.Relation.PARENT);
				yourNode.add(myNode, ChainNode.Relation.CHILD);
				break;
			case NEXT:
				myNode.add(yourNode, ChainNode.Relation.CHILD);
				yourNode.add(myNode, ChainNode.Relation.PARENT);
				break;
		}
	}
}
