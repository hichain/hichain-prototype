package jp.hichain.prototype.basic;

import java.util.EnumMap;
import jp.hichain.prototype.concept.Chain.Relation;

/**
 * Created by NT on 2017/06/28.
 */
public final class ChainPair {
	private boolean alone;
	private ChainNode aloneNode;
	private EnumMap<Relation, ChainNode> pairs;

	public ChainPair(ChainNode aloneNode) {
		alone = true;
		this.aloneNode = aloneNode;
		pairs = new EnumMap<>(Relation.class);
	}

	public ChainPair(ChainPair chainPair, ChainNode node, Relation relation) {
		aloneNode = chainPair.aloneNode;
		pairs = new EnumMap<>(Relation.class);
		add(node, relation);
	}

	public boolean isAlone() {
		return alone;
	}

	public ChainNode getAloneNode() {
		if (alone) return aloneNode;
		return null;
	}

	public ChainNode get(Relation relation) {
		return pairs.get(relation);
	}

	public void add(ChainNode node, Relation relation) {
		pairs.put(relation, node);
		pairs.put(relation.getOpposite(), aloneNode);
		alone = false;
	}

	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}
}
