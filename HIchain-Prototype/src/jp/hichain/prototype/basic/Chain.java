package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.ScoredString;

public class Chain {
	private ScoredString state;   	//連鎖の種類
	private ScoredString.Order order;	//連鎖の順序

	public Chain (ScoredString _state, ScoredString.Order _order) {
		state = _state;
		order = _order;
	}

	public ScoredString getState() {
		return state;
	}

	public ScoredString.Order getOrder() {
		return order;
	}
}

