package jp.hichain.prototype.basic;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.SignDir;

public class RRChainSign extends Square {
	private Player player;
	private ChainSign sign;
	private Map <Directions, Chain> chainMap;

	public RRChainSign(Square _source, Direction _dir) {
		super(_source, _dir);
		chainMap = new HashMap<Directions, Chain>();
	}

	public RRChainSign(Square _source, Direction _dir, Player _player, ChainSign _sign) {
		this(_source, _dir);
		player = _player;
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
	public void addChain(Chain _chain, Direction _aroundDir, SignDir _signDir) {
		chainMap.put(new Directions(_aroundDir, _signDir), _chain);
	}

	/**
	 * 空のマスか返す
	 * @return true/false
	 */
	public boolean isEmpty() {
		return (sign == null);
	}

	/**
	 * プレイヤーを帰す
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * 文字を返す
	 * @return ChainSign
	 */
	public ChainSign getSign() {
		return sign;
	}

	/**
	 * 指定のChainを返す
	 * @param _aroundDir Direction
	 * @param _signDir SignDir
	 * @return Chain
	 */
	public Chain getChain(Direction _aroundDir, SignDir _signDir) {
		return chainMap.get( new Directions(_aroundDir, _signDir) );
	}

	/**
	 * ArounDirとSignDir
	 * @author NT
	 *
	 */
	private class Directions {
		private Direction aroundDir;
		private SignDir signDir;

		public Directions(Direction _around, SignDir _sign) {
			aroundDir = _around;
			signDir = _sign;
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
