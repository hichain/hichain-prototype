package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.RRChainSign;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.concept.PS;

public class Judge {

	public static PS.Contact getContactType(Player player, ChainSign holding, RRChainSign target) {
		SignPS holdingSPS = holding.getPS();

		//辺と辺の判定
		//点と点の判定
		//角と角の判定

		return PS.Contact.NONE;
	}
}
