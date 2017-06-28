package jp.hichain.prototype.basic;

import java.util.EnumMap;

/**
 * Created by NT on 2017/06/28.
 */
public final class ChainPair {
	private ChainNode aloneNode;
	private EnumMap<ChainNode.Relation, ChainNode> pairs;

	public ChainPair(ChainNode aloneNode) {
		this.aloneNode = aloneNode;
		pairs = new EnumMap<>(ChainNode.Relation.class);
	}

	public ChainNode getAloneNode() {
		return aloneNode;
	}

	public ChainNode get(ChainNode.Relation relation) {
		return pairs.get(relation);
	}

	public void add(ChainNode node, ChainNode.Relation relation) {
		pairs.put(relation, node);
	}
}
