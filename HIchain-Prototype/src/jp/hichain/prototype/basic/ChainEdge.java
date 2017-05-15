package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.ScoredString.Edge;

public class ChainEdge {
	private Edge edge;
	private Chain chain;

	public ChainEdge(Edge _edge, Chain _chain) {
		edge = _edge;
		chain = _chain;
	}

	public Edge getEdge() {
		return edge;
	}

	public Chain getChain() {
		return chain;
	}
}
