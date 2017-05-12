package jp.hichain.prototype.basic;

import java.util.Objects;

import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.concept.SignDir;

public final class Chain {
	private final SignDir signDir;
	private final ScoredString kind;
	private final ScoredString.Order order;

	public Chain(SignDir signDir, ScoredString kind, ScoredString.Order order) {
		this.signDir = signDir;
		this.kind = kind;
		this.order = order;
	}

	public Chain(ScoredString kind, ScoredString.Order order) {
		this(null, kind, order);
	}

	public Chain(SignDir signDir, ScoredString kind) {
		this(signDir, kind, null);
	}

	public SignDir getSignDir() {
		return signDir;
	}

	public ScoredString getKind() {
		return kind;
	}

	public ScoredString.Order getOrder() {
		return order;
	}

	public boolean equalsWithoutDir(Chain chain) {
		return kind == chain.kind && order == chain.order;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof Chain) {
			Chain chain = (Chain)obj;
			return (signDir == chain.signDir && kind == chain.kind && order == chain.order);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(signDir, kind, order);
	}

	@Override
	public String toString() {
		String string = "[SignDir] " + signDir + " [Kind] " + kind + " [Order] " + order;
		return string;
	}
}
