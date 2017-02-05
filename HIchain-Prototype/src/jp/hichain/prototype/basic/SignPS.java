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
	//データ構造によらないenum"PS"を用いた新構造 (セット型SignPS)
	Set <PS> psSet;

	/**
	 * セット型から生成
	 * @param _psSet セット型SignPS
	 */
	public SignPS() {
		psSet = new HashSet<PS>();
	}

	public SignPS(Set <PS> _psSet) {
		psSet = _psSet;
	}

	public void add(PS ps) {
		psSet.add(ps);
	}

	public void remove(PS ps) {
		psSet.remove(ps);
	}

	/**
	 * 指定のタイプだけ抽出したSignPSを返す
	 * @param _signPS 元SignPS
	 * @param _type PSのタイプ
	 * @return SignPS
	 */
	public static SignPS getOnlyType(SignPS _signPS, PS.TYPE _type) {
		SignPS signPS = new SignPS();
		for (PS ps : _signPS.getSetPS()) {
			if (ps.getType() == _type) {
				signPS.add(ps);
			}
		}
		return signPS;
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
}
