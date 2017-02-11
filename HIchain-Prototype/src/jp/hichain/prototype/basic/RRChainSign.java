package jp.hichain.prototype.basic;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.SignDir;

public class RRChainSign extends Square {
	private ChainSign sign;
	Map <Directions, Chain> chainMap;

	public RRChainSign(Square _source, AroundDir _dir) {
		super(_source, _dir);
		chainMap = new HashMap<Directions, Chain>();
	}

	public RRChainSign(Square _source, AroundDir _dir, ChainSign _sign) {
		this(_source, _dir);
		sign = _sign;
	}

	/**
	 * 手を打つ
	 * @param _sign ChainSign
	 */
	public void make(ChainSign _sign) {
		sign = _sign;
	}

	/**
	 * Chainを追加する
	 * @param _chain Chain
	 * @param _aroundDir ArounDir
	 * @param _signDir SignDir
	 */
	public void addChain(Chain _chain, AroundDir _aroundDir, SignDir _signDir) {
		chainMap.put(new Directions(_aroundDir, _signDir), _chain);
	}

	public ChainSign getSign() {
		return sign;
	}

	/**
	 * 指定のChainを返す
	 * @param _aroundDir AroundDir
	 * @param _signDir SignDir
	 * @return Chain
	 */
	public Chain getChain(AroundDir _aroundDir, SignDir _signDir) {
		return chainMap.get( new Directions(_aroundDir, _signDir) );
	}

	/**
	 * ArounDirとSignDir
	 * @author NT
	 *
	 */
	private class Directions {
		private AroundDir aroundDir;
		private SignDir signDir;

		public Directions(AroundDir _around, SignDir _sign) {
			aroundDir = _around;
			signDir = _sign;
		}

		public boolean equals(AroundDir _around, SignDir _sign) {
			return (aroundDir == _around && signDir == _sign);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Directions dirs = (Directions)obj;
			if (aroundDir == dirs.aroundDir && signDir == dirs.signDir) {
				return true;
			}
			return false;
		}
	}
}
