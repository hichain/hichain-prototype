package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.RRChainSign;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.Direction.Relative;
import jp.hichain.prototype.concept.PS;
import jp.hichain.prototype.concept.PS.Type;

public class Judge {

	public static PS.Contact getContact(Player player, ChainSign holding, RRChainSign target) {
		SignPS holdingSPS = holding.getSPS();

		//辺と辺の判定
		if ( getSideJudge(holdingSPS, target) ) {
			return PS.Contact.SIDE_SIDE;
		}
		//点と点の判定
		//角と角の判定

		return PS.Contact.NONE;
	}

	private static boolean getSideJudge(SignPS holdingSPS, RRChainSign target) {
		SignPS signPS = SignPS.getOnlyType(holdingSPS, Type.SIDE);

		for (PS ps : signPS.getSetPS()) {
			RRChainSign around = (RRChainSign)target.getAround( AroundDir.getByComp( ps.getComp() ) );
			if (around == null) {
				continue;
			}
			boolean existAntiPS = around.getSign().getSPS().get( ps.getRelative(Relative.OPPOSITE) );
			if (existAntiPS) {
				return true;
			}
		}

		return false;
	}
}
