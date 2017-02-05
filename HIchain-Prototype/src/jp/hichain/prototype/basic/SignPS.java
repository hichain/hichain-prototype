package jp.hichain.prototype.basic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
	private static final List<PS> ORDER = Arrays.asList(
			PS.NORTHWEST, PS.NORTH_NORTHWEST, PS.NORTH, PS.NORTH_NORTHEAST, PS.NORTHEAST,
			PS.WEST_NORTHWEST, PS.EAST_NORTHEAST, PS.WEST, PS.EAST, PS.WEST_SOUTHWEST, PS.EAST_SOUTHEAST,
			PS.SOUTHWEST, PS.SOUTH_SOUTHWEST, PS.SOUTH, PS.SOUTH_SOUTHEAST, PS.SOUTHEAST
	);
	//従来の16bit表記のPS (バイナリ型SignPS)
	int binaryPS;
	//データ構造によらないenum"PS"を用いた新構造 (セット型SignPS)
	Set <PS> psSet;

	/**
	 * バイナリ型から生成
	 * @param _ps バイナリ型SignPS
	 */
	public SignPS(int _ps) {
		binaryPS = _ps;
		psSet = convertFormat(_ps);
	}

	/**
	 * セット型から生成
	 * @param _psSet セット型SignPS
	 */
	public SignPS(Set <PS> _psSet) {
		psSet = _psSet;
	}

	/**
	 * 指定のタイプだけ抽出したSignPSを返す
	 * @param _signPS 元SignPS
	 * @param _type PSのタイプ
	 * @return SignPS
	 */
	public static SignPS getOnlyType(SignPS _signPS, PS.TYPE _type) {
		Set <PS> set = new HashSet<>();
		for (PS ps : _signPS.getSetPS()) {
			if (ps.getType() == _type) {
				set.add(ps);
			}
		}
		return new SignPS(set);
	}

	/**
	 * バイナリ型PSを返す
	 * @return バイナリ型PS
	 */
	public int getBinaryPS() {
		if (binaryPS == 0) {
			convertFormat(psSet);
		}
		return binaryPS;
	}

	public Set<PS> getSetPS() {
		return psSet;
	}

	/**
	 * 指定のPSのビットを返す
	 * @param _kind PS
	 * @return ビットが1ならtrue, 0ならfalse
	 */
	public boolean contains(PS _kind) {
		return psSet.contains(_kind);
	}

	/**
	 * 指定のPSの反対側のビットを返す
	 * @param _kind PS
	 * @return ビットが1ならtrue, 0ならfalse
	 */
	public boolean containsOpposite(PS _kind) {
		return psSet.contains(_kind.getOpposite());
	}

	/**
	 * バイナリ型からセット型に変換する
	 * @param _binaryPS バイナリ型SignPS
	 * @return セット型SignPS
	 */
	private Set <PS> convertFormat(int _binaryPS) {
		Set <PS> set = new HashSet <PS>();
		int i = 0;
		for (PS ps : ORDER) {
			int binary = (_binaryPS & (0x80 >>> i)) >>> 15-i;
			if (binary == 1) {
				set.add(ps);
			}
			i++;
		}
		return set;
	}

	/**
	 * セット型からバイナリ型に変換する
	 * @param _psSet セット型SignPS
	 * @return バイナリ型SignPS
	 */
	private int convertFormat(Set <PS> _psSet) {
		int binary = 0;
		for (PS ps : _psSet) {
			int order = ORDER.indexOf(ps);
			if (order != -1) {
				binary |= 80 >>> order;
			}
		}
		return binary;
	}
}
