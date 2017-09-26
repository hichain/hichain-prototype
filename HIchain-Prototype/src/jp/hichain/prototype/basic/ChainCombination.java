package jp.hichain.prototype.basic;

import java.util.Objects;

import jp.hichain.prototype.concept.Chain;
import jp.hichain.prototype.concept.SignDir;

/**
 * 連鎖の組み合わせ
 * 文字の向き、連鎖の種類を持つ
 */
public class ChainCombination {
	private final SignDir signDir;
	private final Chain kind;

	/**
	 * 組み合わせを生成する
	 * @param signDir 文字の向き
	 * @param kind 連鎖の種類
	 */
	public ChainCombination(SignDir signDir, Chain kind) {
		this.signDir = signDir;
		this.kind = kind;
	}

	/**
	 * 文字の向きを取得する
	 * @return 文字の向き
	 */
	public SignDir getSignDir() {
		return signDir;
	}

	/**
	 * 連鎖の種類を取得する
	 * @return 連鎖の種類
	 */
	public Chain getKind() {
		return kind;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof ChainCombination) {
			ChainCombination chainCnd = (ChainCombination)obj;
			return (signDir == chainCnd.signDir && kind == chainCnd.kind);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(signDir, kind);
	}

	@Override
	public String toString() {
		return "[SignDir] " + signDir + " [Kind] " + kind;
	}
}
