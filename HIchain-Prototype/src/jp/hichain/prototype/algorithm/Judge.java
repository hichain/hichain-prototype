package jp.hichain.prototype.algorithm;

import java.awt.Window.Type;
import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.RRChainSign;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.AroundDir.TYPE;
import jp.hichain.prototype.concept.PS;

public class Judge {

	public static PS.CONTACT getContactType(Player player, ChainSign holding, RRChainSign target) {
		Map <AroundDir, Square> aroundSigns = target.getAroundAll();
		SignPS holdingPS = holding.getPS();

		Map <AroundDir, SignPS> aroundPS = new HashMap<AroundDir, SignPS>();
		for (Map.Entry<AroundDir, Square> signEntry : aroundSigns.entrySet()) {
			AroundDir aroundDir = signEntry.getKey();
			RRChainSign aroundSign;
			if (signEntry.getValue() instanceof RRChainSign) {
				aroundSign = (RRChainSign)signEntry.getValue();
			} else {
				continue;
			}
			aroundPS.put(aroundDir, aroundSign.getSign().getPS());
		}

		for (PS ps : SignPS.getOnlyType(holdingPS, PS.TYPE.SIDE).getSetPS()) {
			if (ps == aroundPS)
		}
	}
}
