package jp.hichain.prototype.concept;

/**
 * 連鎖
 * Created by NT on 2017/07/04.
 */
public enum Chain {
	//連番 (アルファベット順)
	ALPHABETICAL(
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	),
	//ぞろ目
	IDENTICAL(
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	),
	//ロイヤル
	ROYAL(
		"CHAIN"
	);

	private final String orderString;

	Chain(String str) {
		orderString = str;
	}

	public enum Relation {
		PARENT,
		CHILD;

		private Relation opposite;

		static {
			PARENT.opposite = CHILD;
			CHILD.opposite = PARENT;
		}

		public Relation getOpposite() {
			return opposite;
		}

	}

	public String getOrderString() {
		return orderString;
	}
}
