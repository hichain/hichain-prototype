package jp.hichain.prototype.basic;

import java.util.Objects;

import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public class ChainCondition {
	private final SignDir signDir;
	private final ScoredString kind;
	private final ScoredString.Relation relation;

	public ChainCondition(SignDir signDir, ScoredString kind, ScoredString.Relation relation) {
		this.signDir = signDir;
		this.kind = kind;
		this.relation = relation;
	}

	public ChainCondition(ScoredString kind, ScoredString.Relation relation) {
		this(null, kind, relation);
	}

	public ChainCondition(SignDir signDir, ScoredString kind) {
		this(signDir, kind, null);
	}

	public SignDir getSignDir() {
		return signDir;
	}

	public ScoredString getKind() {
		return kind;
	}

	public ScoredString.Relation getRelation() {
		return relation;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof ChainCondition) {
			ChainCondition chainCnd = (ChainCondition)obj;
			return (signDir == chainCnd.signDir && kind == chainCnd.kind && relation == chainCnd.relation);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(signDir, kind, relation);
	}

	@Override
	public String toString() {
		String string = "[SignDir] " + signDir + " [Kind] " + kind + " [Relation] " + relation;
		return string;
	}
}
