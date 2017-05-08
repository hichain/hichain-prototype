package jp.hichain.prototype.basic;

import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.AroundDir.Axis;

/**
 * マス
 * @author NT
 *
 */
public class Square {
	private static Map <Position, Square> abPosMap;

	private Player player;
	private ChainSign chainSign;
	private ChainMap chainMap;

	private Position position;

	private Move move;

	/**
	 * ルートマス
	 */
	public Square() {
		init();
		position = new Position(-1, -1);
		abPosMap.put(position, this);
	}

	/**
	 * 通常のマス
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(Square _source, AroundDir _dir) {
		init();
		position = new Position(
			_source.getPosition(Axis.VERTICAL) + _dir.getComp(Axis.VERTICAL),
			_source.getPosition(Axis.HORIZONTAL) + _dir.getComp(Axis.HORIZONTAL)
		);
		abPosMap.put(position, this);
		setArounds(_source, _dir);
	}

	static {
		abPosMap = new HashMap<>();
	}

	public static Square get(int _v, int _h) {
		return abPosMap.get( new Position(_v, _h) );
	}

	/**
	 * 手を打つ
	 * @param _sign ChainSign
	 */
	public void make(Player _player, ChainSign _sign) {
		player = _player;
		chainSign = _sign;
		createAroundAll();
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
	 * ChainMapを返す
	 * @return ChainMap
	 */
	public ChainMap getChainMap() {
		return chainMap;
	}

	public Position getPosition() {
		return position;
	}

	public Move getMove() {
		return move;
	}
}
