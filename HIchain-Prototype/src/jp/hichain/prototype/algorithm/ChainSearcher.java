package jp.hichain.prototype.algorithm;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import jp.hichain.prototype.basic.*;
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

		boolean searching;

		ChainNode myNode = me.getChainNode(combination);
		if (myNode == null) {
			myNode = new ChainNode(me);
			me.addChainNode(combination, myNode);
		}

		AsteriskNode asteriskNode = null;
		ChainNode asteriskNodeTmp = you.getChainNode(combination);
		if (asteriskNodeTmp instanceof AsteriskNode) {
			asteriskNode = (AsteriskNode) asteriskNodeTmp;
			searching = true;
		} else {
			asteriskNode = new AsteriskNode(you);
			you.addChainNode(combination, asteriskNode);
			searching = false;
		}

		EnumSet<ScoredString.Relation> relations = EnumSet.of(ScoredString.Relation.PREVIOUS);
		if (!(ssKind == ScoredString.IDENTICAL)) {
			relations.add(ScoredString.Relation.NEXT);
		}
		for (ScoredString.Relation ssRelation : relations) {
			SignChar targetSC = mySC.getRelation(combination.getKind(), ssRelation, 2);
			if (targetSC == null) continue;

			boolean hit = false;
			if (searching) {
				Set<ChainNode> sourceNodes = new HashSet<>();
				for (ChainPair pair : asteriskNode.getAlonePairs()) {
					ChainNode aloneNode = pair.getAloneNode();
					SignChar aloneSC = aloneNode.getSquare().getSign().getSN().get(signDir);
					if (targetSC.equals(aloneSC)) {
						sourceNodes.add(aloneNode);
					}
				}
				ChainNode.Relation nodeRelation = (ssRelation == ScoredString.Relation.PREVIOUS) ? ChainNode.Relation.PARENT : ChainNode.Relation.CHILD;
				for (ChainNode sourceNode : sourceNodes) {
					asteriskNode.add(myNode, sourceNode, nodeRelation);
				}
				hit = sourceNodes.size() != 0;
			}

			if (!hit) addChainNode(me, you, combination, ssRelation);

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
