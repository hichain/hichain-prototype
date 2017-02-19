package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.RRChainSign;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.Direction.Relation;
import jp.hichain.prototype.concept.PS;
import jp.hichain.prototype.concept.PS.Contact;
import jp.hichain.prototype.concept.PS.Type;

public class Judge {

	public static PS.Contact getContact(Player player, ChainSign holding, RRChainSign target) {
		SignPS holdingSPS = holding.getSPS();

		//辺判定
		if (getSideJudge(holdingSPS, target)) {
			return Contact.SIDE_SIDE;
		}
		//点判定
		if (getPointJudge(player, holdingSPS, target)) {
			return Contact.POINT_POINT;
		}
		//角判定
		if (getCornerJudge(player, holdingSPS, target)) {
			return Contact.POINT_POINT;
		}

		return Contact.NONE;
	}

	private static boolean getSideJudge(SignPS holdingSPS, RRChainSign target) {
		for (Direction direction : holdingSPS.get(Type.SIDE)) {
			RRChainSign aroundSign = (RRChainSign)target.getAround( direction.getSquareSide() );
			if (aroundSign == null) {
				continue;
			}
			SignPS antiSPS = aroundSign.getSign().getSPS();
			Relation rightleft = direction.getSquareSidePos();
			boolean flag = antiSPS.exist( Type.SIDE, direction.getRelation(rightleft, 3) );
			if (flag) {
				return true;
			}
		}
		return false;
	}

	private static boolean getPointJudge(Player player, SignPS holdingSPS, RRChainSign target) {
		for (Direction direction : holdingSPS.get(Type.POINT)) {
			RRChainSign aroundSign = (RRChainSign)target.getAround( direction );
			if (aroundSign == null || player == aroundSign.getPlayer()) {
				continue;
			}
			SignPS antiSPS = aroundSign.getSign().getSPS();
			Relation rightleft = direction.getSquareSidePos();
			int times = (direction.getDenominator() == 8) ? 1 : 2;
			boolean flag = antiSPS.exist( Type.POINT, direction.getRelation(rightleft, times) );
			if (flag) {
				return true;
			}
		}
		return false;
	}

	private static boolean getCornerJudge(Player player, SignPS holdingSPS, RRChainSign target) {
		for (Direction direction : holdingSPS.get(Type.CORNER)) {
			RRChainSign aroundSign = (RRChainSign)target.getAround(direction);
			if (aroundSign == null || player == aroundSign.getPlayer()) {
				continue;
			}
			SignPS antiSPS = aroundSign.getSign().getSPS();
			boolean flag = antiSPS.exist( Type.CORNER, direction.getRelation(Relation.LEFT, 2) );
			if (flag) {
				return true;
			}
		}
		return false;
	}
}
