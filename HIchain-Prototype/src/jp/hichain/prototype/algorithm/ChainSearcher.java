package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.ChainCondition;
import jp.hichain.prototype.basic.ChainNode;
import jp.hichain.prototype.basic.SignChar;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.ScoredString;
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
			SignChar meSC = me.getSign().getSN().get(signDir);
			SignChar youSC = you.getSign().getSN().get(signDir);
			if (meSC == null || youSC == null) {
				continue;
			}

			for (ScoredString kind : ScoredString.values()) {
				for (ScoredString.Relation relation : ScoredString.Relation.values()) {
					SignChar targetSC = meSC.getRelation(kind, relation);
					if (targetSC == null) {
						continue;
					}
					boolean result = targetSC.equals(youSC);
					if (result) {
						ChainCondition condition = new ChainCondition(signDir, kind, relation);
						addChainNode(me, you, condition);
						hits++;
					}
				}
			}
		}

		return hits;
	}

	public static void addChainNode(Square me, Square you, ChainCondition condition) {
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

		switch (condition.getRelation()) {
			case ASCEND:
				myNode.addChild(yourNode);
				yourNode.addParent(myNode);
			break;
			case DESCEND:
				myNode.addParent(yourNode);
				yourNode.addChild(myNode);
			break;
			case SAME:
				myNode.addChild(yourNode);
				yourNode.addParent(myNode);
			break;
		}
	}
}
