package jp.hichain.prototype.basic;

import java.util.Objects;

import jp.hichain.prototype.concept.Chain;
import jp.hichain.prototype.concept.SignDir;

public class ChainCombination {
	private final SignDir signDir;
	private final Chain kind;

	public ChainCombination(SignDir signDir, Chain kind) {
		this.signDir = signDir;
		this.kind = kind;
	}

	public SignDir getSignDir() {
		return signDir;
	}

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
		String string = "[SignDir] " + signDir + " [Kind] " + kind;
		return string;
	}
}
