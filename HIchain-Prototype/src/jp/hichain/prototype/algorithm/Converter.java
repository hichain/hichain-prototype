package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.ChainMap;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.Position;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public class Converter {
	public static int getPointsAll(Player player) {
		for (ScoredString ssKind : ScoredString.values()) {
			for (SignDir signDir : SignDir.values()) {
				for (Position position : Position.getAll()) {
					Square square = position.getSquare();
					ChainMap chainMap = square.getChainMap();

				}
			}
		}
		return 0;
	}
}
