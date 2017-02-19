package jp.hichain.prototype.concept;

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
}
