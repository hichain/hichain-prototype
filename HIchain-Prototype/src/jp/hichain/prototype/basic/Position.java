package jp.hichain.prototype.basic;

import java.util.Objects;

import jp.hichain.prototype.concept.AroundDir.Axis;

public final class Position {
	private final int v, h;

	public Position(int _v, int _h) {
		v = _v;
		h = _h;
	}

	public int get(Axis axis) {
		return (axis == Axis.VERTICAL) ? v : h;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Position)) {
			return false;
		}
		Position position = (Position)obj;
		return (v == position.v) && (h == position.h);
	}

    @Override
    public int hashCode() {
        return Objects.hash(v, h);
    }
}
