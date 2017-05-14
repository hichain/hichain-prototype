package jp.hichain.prototype.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hichain.prototype.basic.ChainCondition;
import jp.hichain.prototype.basic.ChainMap;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.Position;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.ScoredString.Order;
import jp.hichain.prototype.concept.SignDir;

/*
 * タスク:
 * アスタリスク
 * ぞろ目
 * 重複除去
 */
public class Converter {
	private static Map<ChainCondition, Integer> chainLengthMin;

	public static int getPointsAll(Player player) {
		int points = 0;
		for (SignDir signDir : SignDir.values()) {
			int royalPoints = getConsecutivePoints(
				Position.getAll(), player, new ChainCondition(signDir, ScoredString.ROYAL)
			);
			if (royalPoints >= getChainLengthMin(ScoredString.ROYAL, Order.DESCEND)) {
				return -1;
			}
			points += getConsecutivePoints(
				Position.getAll(), player, new ChainCondition(signDir, ScoredString.ALPHABETICAL)
			);
		}
		return points;
	}

	private static int getConsecutivePoints(List<Position> _positions, Player _player, ChainCondition _chain) {
		int points = 0;
		ChainCondition ascendChain = new ChainCondition(_chain.getSignDir(), _chain.getKind(), Order.ASCEND);
		ChainCondition descendChain = new ChainCondition(_chain.getSignDir(), _chain.getKind(), Order.DESCEND);

		for (Position position : _positions) {
			Square square = position.getSquare();
			if (square.isEmpty() || square.getPlayer() != _player) continue;
			ChainMap chainMap = square.getChainMap();
			List<AroundDir> descendDirs = chainMap.get(ascendChain);
			if (descendDirs != null) continue;
			points += getConsecutivePoints(square, descendChain);
		}

		return points;
	}

	private static int getConsecutivePoints(Square _square, ChainCondition _chain) {
		return getConsecutivePoints(_square, _chain, 1);
	}

	private static int getConsecutivePoints(Square _square, ChainCondition _chain, int _depth) {
		int points = 0;
		int min =  getChainLengthMin(_chain.getKind(), _chain.getOrder());

		System.out.println("Depth: " + _depth);

		ChainMap chainMap = _square.getChainMap();
		List<AroundDir> dirs = chainMap.get(_chain);
		if (dirs == null) {
			if (_depth >= min) {
				return _depth*_depth;
			} else {
				return 0;
			}
		}
		for (AroundDir _aroundDir : dirs) {
			Square aroundSq = _square.getAround(_aroundDir);
			points += getConsecutivePoints(aroundSq, _chain, _depth+1);
		}

		return points;
	}

	private static int getChainLengthMin(ScoredString ssKind, Order order) {
		return chainLengthMin.get(new ChainCondition(ssKind, order));
	}

	public static void init(int alphabetical, int identical, int royal) {
		chainLengthMin = new HashMap<ChainCondition, Integer>() {{
			put(
				new ChainCondition(ScoredString.ALPHABETICAL, Order.DESCEND), alphabetical
			);
			put(
				new ChainCondition(ScoredString.ALPHABETICAL, Order.SAME), identical
			);
			put(
				new ChainCondition(ScoredString.ROYAL, Order.DESCEND), royal
			);
		}};
	}
}