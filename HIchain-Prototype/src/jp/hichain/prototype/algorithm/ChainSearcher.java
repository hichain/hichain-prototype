package jp.hichain.prototype.algorithm;

import java.util.EnumSet;

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
			judgeMatureAndValid(me.getChainNode(combination), you.getChainNode(combination), combination);

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

		ChainNode myNode = me.getChainNode(combination);
		AsteriskNode asteNode = null;

		//孤立なら探索をスキップ
		ChainNode asteNodeTmp = you.getChainNode(combination);
		//同時にnull判定を行う (false => null)
		boolean skipSearching = (asteNodeTmp instanceof AsteriskNode);  //探索をスキップするか
		if (skipSearching) {
			asteNode = (AsteriskNode)asteNodeTmp;
		} else {
			asteNode = new AsteriskNode(you, combination);
			you.addChainNode(combination, asteNode);
		}

		EnumSet<ScoredString.Relation> relations = EnumSet.of(ScoredString.Relation.PREVIOUS);
		if (ssKind != ScoredString.IDENTICAL) {
			relations.add(ScoredString.Relation.NEXT);
		}

		for (ScoredString.Relation ssRelation : relations) {
			SignChar targetSC = mySC.getRelation(ssKind, ssRelation, 2);
			if (targetSC == null) continue;

			ChainNode substituteNode = null;    //*が代わるノード
			if (skipSearching) {
				SignChar signChar = mySC.getRelation(ssKind, ssRelation.getReverse());
				substituteNode = new ChainNode(you, combination, signChar);
				asteNode.addSubstituteNode(signChar, substituteNode);
			} else {
				substituteNode = searchChainedSubstituteNode(asteNode, targetSC, ssRelation, combination);
			}
			if (substituteNode == null) continue;

			if (myNode == null) {
				myNode = new ChainNode(you, combination);
				me.addChainNode(combination, myNode);
			}

			addRelation(myNode, substituteNode, ssRelation);

			judgeMatureAndValid(myNode, substituteNode, combination);

			hits++;
		}

		return hits;
	}

	private static ChainNode searchChainedSubstituteNode(ChainNode root, SignChar targetSC, ScoredString.Relation ssRelation, ChainCombination combination) {
		AsteriskNode asteNode = (AsteriskNode)root;
		SignDir signDir = combination.getSignDir();
		ScoredString ssKind = combination.getKind();

		ChainNode.Relation nodeRelation = (ssRelation == ScoredString.Relation.PREVIOUS) ? ChainNode.Relation.PARENT : ChainNode.Relation.CHILD;
		for (ChainNode substituteNode : asteNode.getSubstituteNodes()) {
			for (ChainNode nextNode : substituteNode.get(nodeRelation)) {
				SignChar nextSC = nextNode.getSignChar();
				if (nextSC.equals(targetSC)) {
					return substituteNode;
				}
			}
		}
		return null;
	}

	private static void judgeMatureAndValid(ChainNode myNode, ChainNode yourNode, ChainCombination combination) {
		if (yourNode.isMature()) {
			myNode.setMature(true);
		} else {
			ScoreSearcher.judgeMature(myNode);
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
			myNode = new ChainNode(me, combination);
			me.addChainNode(combination, myNode);
		}
		ChainNode yourNode = you.getChainNode(combination);
		if (yourNode == null) {
			yourNode = new ChainNode(you, combination);
			you.addChainNode(combination, yourNode);
		}

		addRelation(myNode, yourNode, relation);
	}

	private static void addRelation(ChainNode me, ChainNode you, ScoredString.Relation relation) {
		switch (relation) {
			case PREVIOUS:
				me.add(you, ChainNode.Relation.PARENT);
				you.add(me, ChainNode.Relation.CHILD);
				break;
			case NEXT:
				me.add(you, ChainNode.Relation.CHILD);
				me.add(me, ChainNode.Relation.PARENT);
				break;
		}
	}
}
