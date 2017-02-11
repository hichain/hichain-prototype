package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.AroundDir;

public class RRChainSign extends Square {
	private ChainSign sign;

	public RRChainSign(Square _source, AroundDir _dir) {
		super(_source, _dir);
	}

	public RRChainSign(Square _source, AroundDir _dir, ChainSign _sign) {
		super(_source, _dir);
		sign = _sign;
	}

	/**
	 * 手を打つ
	 * @param _sign ChainSign
	 */
	public void make(ChainSign _sign) {
		sign = _sign;
	}

	public ChainSign getSign() {
		return sign;
	}
}
