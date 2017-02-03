package jp.hichain.prototype.basic;

import java.util.HashSet;
import java.util.Set;

import jp.hichain.prototype.concept.PS;

/**
 * 文字のPS
 * 16個のPSの集合
 * @author NT
 *
 */
public class SignPS {
	//バイナリ型の記述順
	private static final PS [] ORDER = {
			PS.NORTHWEST, PS.NORTH_NORTHWEST, PS.NORTH, PS.NORTH_NORTHEAST, PS.NORTHEAST,
			PS.WEST_NORTHWEST, PS.EAST_NORTHEAST, PS.WEST, PS.EAST, PS.WEST_SOUTHWEST, PS.EAST_SOUTHEAST,
			PS.SOUTHWEST, PS.SOUTH_SOUTHWEST, PS.SOUTH, PS.SOUTH_SOUTHEAST, PS.SOUTHEAST
	};
	//従来の16bit表記のPS (バイナリ型SignPS)
	int binaryPS;
	//データ構造によらないenum"PS"を用いた新構造 (セット型SignPS)
	Set <PS> psSet;

	/**
	 * バイナリ型から生成
	 * @param _ps バイナリ型SignPS
	 */
	public SignPS(int _ps) {
		psSet = new HashSet<PS>();
		binaryPS = _ps;
		convertFormat(_ps);
	}

	/**
	 * セット型から生成
	 * @param _psSet セット型SignPS
	 */
	public SignPS(Set <PS> _psSet) {
		psSet = _psSet;
	}

	public int getBinaryPS() {
		if (binaryPS == 0) {
			convertFormat(psSet);
		}
		return binaryPS;
	}

	public boolean contains(PS _kind) {
		return psSet.contains(_kind);
	}

	public boolean containsOpposite(PS _kind) {
		return psSet.contains(_kind.getOpposite());
	}

	/**
	 * バイナリ型からセット型に変換する
	 * @param _binaryPS バイナリ型SignPS
	 */
	private void convertFormat(int _binaryPS) {
		int i = 0;
		for (PS ps : ORDER) {
			int binary = (_binaryPS & (0x80 >>> i)) >>> 15-i;
			if (binary == 1) {
				psSet.add(ps);
			}
			i++;
		}
	}

	/**
	 * セット型からバイナリ型に変換する
	 * @param _psSet セット型SignPS
	 */
	private void convertFormat(Set <PS> _psSet) {

	}
}
