package jp.hichain.prototype.concept;

import java.util.EnumSet;

/**
 * 点・辺データ (Points/Sides)
 * 16方向
 * @author NT
 *
 */
public class PS {
	/**
	 * 種類
	 * @author NT
	 *
	 */
	public enum Type {
		POINT,
		SIDE,
		CORNER;
	}

	/**
	 * 接点の種類
	 * @author NT
	 *
	 */
	public enum Contact {
		NONE,		//接点なし
		POINT_POINT,	//点と点で接する
		SIDE_SIDE;	//辺と辺で接する

	}

	public static EnumSet<Type> getType(Direction dir) {
		EnumSet<Type> set = EnumSet.noneOf(Type.class);
		int denom = dir.getDenominator();
		switch (denom) {
		case 4:
			set.add(Type.POINT);
			break;
		case 8:
			set.add(Type.POINT);
			set.add(Type.CORNER);
			break;
		case 16:
			set.add(Type.SIDE);
			break;
		}
		return set;
	}
}
