package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.AroundDir;

public class RRChainSign extends Square {
	public static RRChainSign ROOT;

	private Player player;
	private ChainSign sign;
	private ChainMap chainMap;

	protected RRChainSign() {
		super();
		chainMap = new ChainMap();
	}

	public RRChainSign(Square _source, AroundDir _dir) {
		super(_source, _dir);
		chainMap = new ChainMap();
	}

	public RRChainSign(Square _source, AroundDir _dir, Player _player, ChainSign _sign) {
		this(_source, _dir);
		player = _player;
		sign = _sign;
	}

	public static void createRoot() {
		ROOT = new RRChainSign();
	}

	/**
	 * 手を打つ
	 * @param _sign ChainSign
	 */
	public void make(Player _player, ChainSign _sign) {
		player = _player;
		sign = _sign;
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
	 * ChainMapを返す
	 * @return ChainMap
	 */
	public ChainMap getChainMap() {
		return chainMap;
	}
}
