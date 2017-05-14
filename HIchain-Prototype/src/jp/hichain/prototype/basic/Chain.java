package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.hichain.prototype.concept.ScoredString.Edge;

public final class Chain {
	private boolean active = true;
	private final ChainCondition condition;
	private final List<Square> chainSquares;

	public Chain(Square _head, Square _tail, ChainCondition _condition) {
		condition = _condition;
		chainSquares = new ArrayList<>(Arrays.asList(_head, _tail));
	}

	public Chain(Chain _chain, Edge _edge, Square _square) {
		_chain.active = false;
		condition = _chain.condition;
		chainSquares = new ArrayList<>(_chain.chainSquares);
		if (_edge == Edge.HEAD) {
			chainSquares.add(0, _square);
		} else {
			chainSquares.add(_square);
		}
	}

	public boolean isActive() {
		return active;
	}

	public ChainCondition getCondition() {
		return condition;
	}

	public Square getEdge(Edge _edge) {
		if (_edge == Edge.HEAD) {
			return chainSquares.get(0);
		}
		return chainSquares.get(chainSquares.size()-1);
	}

	public List<Square> getAll() {
		return chainSquares;
	}
}
