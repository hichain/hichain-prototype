package jp.hichain.prototype.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;

/**
 * マス
 * @author NT
 *
 */
public class Square {
	private final Position position;

	private Player player;
	private ChainSign chainSign;
	private Map<ChainCombination, ChainNode> chainMap;

	private Move move;

	/**
	 * ルートマス
	 */
	private Square() {
		chainMap = new HashMap<>();
		position = new Position(this, -1, -1);
	}

	/**
	 * 通常のマス
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(Square _source, AroundDir _dir) {
		chainMap = new HashMap<>();
		position = new Position(this, _source.getPosition(), _dir);
		position.updateAroundsAll();
	}

	public static void init() {
		new Square();
	}

	/**
	 * 絶対座標で指定してマスを取得する
	 * @param _v V座標
	 * @param _h H座標
	 * @return Square
	 */
	public static Square get(int _v, int _h) {
		Position pos = Position.get(_v, _h);
		if (pos == null) return null;
		return pos.getSquare();
	}

	/**
	 * 手を打つ
	 * @param _sign ChainSign
	 */
	public void make(Player _player, ChainSign _sign) {
		player = _player;
		chainSign = _sign;
		position.searchChainsAll();
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
	 * 連鎖ノードを返す
	 * @return ChainNode
	 */
	public ChainNode getChainNode(ChainCombination _ChainCombination) {
		return chainMap.get(_ChainCombination);
	}

	/**
	 * 連鎖ノードのマップを返す
	 * @return 連鎖ノードマップ
	 */
	public Map<ChainCombination, ChainNode> getChainMap() {
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

	/**
	 * 周囲マスを返す
	 * @param _dir 自身(this)からみた方向
	 * @return Square
	 */
	public Square getAround(AroundDir _dir) {
		Position pos = position.getAround(_dir);
		if (pos == null) return null;
		return pos.getSquare();
	}

	public void addChainNode(ChainCombination _condition, ChainNode _node) {
		chainMap.put(_condition, _node);
	}

	public boolean hasPluralChains() {
		for (ChainNode node : getChainMap().values()) {
			if (node.isValid()) {
				return true;
			}
		}
		return false;
	}


	public String chainsToString() {
		String string = "";
		int i = 0;
		for (Map.Entry<ChainCombination, ChainNode> entry : chainMap.entrySet()) {
			string += entry.getKey() + "\n" + entry.getValue();
			if (i < chainMap.size()-1) string += "\n";
			i++;
		}
		return string;
	}
}
