package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.AroundDir;

/**
 * マス
 * @author NT
 *
 */
public class Square {
	private Position position;

	private Player player;
	private ChainSign chainSign;
	private ChainMap chainMap;

	private Move move;

	/**
	 * ルートマス
	 */
	public Square() {
		position = new Position(this, -1, -1);
	}

	/**
	 * 通常のマス
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(Square _source, AroundDir _dir) {
		position = new Position(this, _source.getPosition(), _dir);
	}

	/**
	 * 手を打つ
	 * @param _sign ChainSign
	 */
	public void make(Player _player, ChainSign _sign) {
		player = _player;
		chainSign = _sign;
		position.createAroundsAll();
	}

	/**
	 * 空のマスか返す
	 * @return true/false
	 */
	public boolean isEmpty() {
		return (chainSign == null);
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
		return chainSign;
	}

	/**
	 * 連鎖関係を返す
	 * @return ChainMap
	 */
	public ChainMap getChainMap() {
		return chainMap;
	}

	/**
	 * 座標を返す
	 * @return Position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * 手の評価などを返す
	 * @return Move
	 */
	public Move getMove() {
		return move;
	}
}
