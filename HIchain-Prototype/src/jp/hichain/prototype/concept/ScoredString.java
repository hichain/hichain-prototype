package jp.hichain.prototype.concept;

public enum ScoredString {
	//連番
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

	public enum Relation {
		PREVIOUS,	//前
		NEXT,	    //次
	}

	private final String orderString;

	private ScoredString(String str) {
		orderString = str;
	}

	public String getOrderString() {
		return orderString;
	}
}
