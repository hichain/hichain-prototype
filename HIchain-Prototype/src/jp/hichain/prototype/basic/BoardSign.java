package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.AroundDir;

public class BoardSign {
	private Square square;			//周囲マス

	private Player player;			//プレイヤー
	private ChainSign chainSign;	//文字

	private ChainMap chainMap;		//連鎖関係
	private Move move;				//手の評価

	//ルート
	public BoardSign() {
		square = new Square(this);
		chainMap = new ChainMap();
	}

	//空マス
	public BoardSign(BoardSign _source, AroundDir _dir) {
		square = new Square(this, _source.getSquare(), _dir);
		chainMap = new ChainMap();
	}

	static {
		new BoardSign();
	}

	/**
	 * 手を打つ
	 * @param _sign ChainSign
	 */
	public void make(Player _player, ChainSign _sign) {
		player = _player;
		chainSign = _sign;
		move = new Move(this);
		createAroundsAll();
	}

	/**
	 * 空のマスかどうか返す
	 * @return true/false
	 */
	public boolean isEmpty() {
		return (chainSign == null);
	}

	/**
	 * Playerを返す
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Squareを返す
	 * @return Square
	 */
	public Square getSquare() {
		return square;
	}

	/**
	 * ChainSignを返す
	 * @return ChainSign
	 */
	public ChainSign getSign() {
		return chainSign;
	}

	/**
	 * ChainMapを返す
	 * @return ChainMap
	 */
	public ChainMap getChainMap() {
		return chainMap;
	}

	/**
	 * Moveを返す
	 * @return Move
	 */
	public Move getMove() {
		return move;
	}

	private void createAroundsAll() {
		for (AroundDir dir : AroundDir.values()) {
			if (square.hasAround(dir)) continue;
			new BoardSign(this, dir);
		}
	}
}
