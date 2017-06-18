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
		//自分が*ならmeとyouを入れ替える (処理の簡略化のため)
		if (me.getSign().getSC().get() == '*') {
			return search(you, me);
		}

		for (SignDir signDir : SignDir.values()) {
			SignChar mySC = me.getSign().getSN().get(signDir);
			SignChar yourSC = you.getSign().getSN().get(signDir);
			if (mySC == null || yourSC == null) {
				continue;
			}
			boolean asterisk = yourSC.get() == '*';

			for (ScoredString kind : ScoredString.values()) {
				EnumSet<ScoredString.Relation> relations = EnumSet.of(ScoredString.Relation.PREVIOUS);
				if (kind != ScoredString.IDENTICAL) {
					relations.add(ScoredString.Relation.NEXT);
				}
				for (ScoredString.Relation relation : relations) {
					SignChar targetSC = mySC.getRelation(kind, relation);
					if (targetSC == null) {
						continue;
					}

					ChainCombination combination = new ChainCombination(signDir, kind);
					boolean result;
					if (asterisk) {
						ChainNode asteriskNode = you.getChainNode(combination);
						if (asteriskNode == null) {
							result = true;
						} else {

						}
					}
					boolean result = asterisk || targetSC.equals(yourSC);
					if (!result) continue;
					addChainNode(me, you, combination, relation);

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

					hits++;
				}
			}
		}

		return hits;
	}

	private static void addChainNode(Square me, Square you, ChainCombination condition, ScoredString.Relation relation) {
		ChainNode myNode = me.getChainNode(condition);
		if (myNode == null) {
			myNode = new ChainNode(me);
			me.addChainNode(condition, myNode);
		}
		ChainNode yourNode = you.getChainNode(condition);
		if (yourNode == null) {
			yourNode = new ChainNode(you);
			you.addChainNode(condition, yourNode);
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

	private static boolean isChainableToAsterisk(ChainNode asteNode, SignChar rootSC, ChainCombination combination) {
		for (ScoredString.Relation ssRelation : ScoredString.Relation.values()) {
			SignChar targetSC = rootSC.getRelation(combination.getKind(), ssRelation);
			for (ChainNode.Relation nodeRelation : ChainNode.Relation.values()) {
				for (ChainNode nextNode : asteNode.get(nodeRelation)) {
					ScoredString.Relation reverseSSRelation = ssRelation.getReverse();
					SignChar nextSC = nextNode.getSquare().getSign().getSN().get( combination.getSignDir() );
					if (nextSC.getRelation(combination.getKind(), reverseSSRelation).equals(targetSC)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
