package jp.hichain.prototype.algorithm;

import java.util.List;

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
		if ( getPointJudge(player, holdingSPS, target) ) {
			return PS.Contact.POINT_POINT;
		}
		//角と角の判定
		if ( getCornerJudge(player, holdingSPS, target) ) {
			return PS.Contact.POINT_POINT;
		}
		return PS.Contact.NONE;
	}

	private static boolean getSideJudge(SignPS holdingSPS, RRChainSign target) {
		SignPS signPS = SignPS.getOnlyType(holdingSPS, Type.SIDE);

		for (PS ps : signPS.getSetPS()) {
			RRChainSign around = (RRChainSign)target.getAround( AroundDir.get( ps.getComp() ) );
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

	private static boolean getPointJudge(Player player, SignPS holdingSPS, RRChainSign target) {
		SignPS signPS = SignPS.getOnlyType(holdingSPS, Type.POINT);

		for (PS ps : signPS.getSetPS()) {
			RRChainSign around = (RRChainSign)target.getAround( AroundDir.get( ps.getComp() ) );
			if (around == null || around.getPlayer() == player) {
				continue;
			}
			boolean existAntiPS = around.getSign().getSPS().get( ps.getRelative(Relative.OPPOSITE) );
			if (existAntiPS) {
				return true;
			}
		}

		return false;
	}

	private static boolean getCornerJudge(Player player, SignPS holdingSPS, RRChainSign target) {
		SignPS signPS = SignPS.getOnlyType(holdingSPS, Type.CORNER);

		for (PS ps : signPS.getSetPS()) {
			boolean [] existAntiPS = new boolean [3];
			RRChainSign [] arounds = new RRChainSign [3];

			List<AroundDir> dirs = AroundDir.breakup( AroundDir.get( ps.getComp() ) );
			dirs.add(0, AroundDir.get( ps.getComp() ));

			for (int i = 0; i < 3; i++) {
				arounds[i] = (RRChainSign)target.getAround( dirs.get(i) );
			}

			Relative [] relatives = new Relative [3];
			relatives[0] = Relative.OPPOSITE;

			AroundDir left = AroundDir.get( dirs.get(1).getComp().getRelative(Relative.LEFT) );
			if (dirs.get(2) == left) {
				relatives[1] = Relative.LEFT;
				relatives[2] = Relative.RIGHT;
			} else {
				relatives[1] = Relative.RIGHT;
				relatives[2] = Relative.LEFT;
			}

			for (int i = 0; i < 3; i++) {
				if (arounds[i] == null || arounds[i].getPlayer() == player) {
					continue;
				}
				existAntiPS[i] = arounds[i].getSign().getSPS().get( ps.getRelative(relatives[i]) );
				if (existAntiPS[i]) {
					return true;
				}
			}
		}

		return false;
	}
}
