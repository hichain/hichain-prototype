package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public class Chain {
	private SignDir dir;				//文字の向き
	private ScoredString state;   	//連鎖の種類
	private ScoredString.Order order;	//連鎖の順序

	public Chain (SignDir _dir, ScoredString _state, ScoredString.Order _order) {
		dir = _dir;
		state = _state;
		order = _order;
	}

	public String getRecode() {
		return "";
	}

	public SignDir getDir() {
		return dir;
	}

	public ScoredString getState() {
		return state;
	}

	public ScoredString.Order getOrder() {
		return order;
	}

	/**
	 * 文字の向きと連鎖の種類が等しいか返す
	 * @param _dir 文字の向き
	 * @param _state 連鎖の種類
	 * @return true/false
	 */
	public boolean equals(SignDir _dir, ScoredString _state) {
		return (dir == _dir) && (state == _state);
	}
}

