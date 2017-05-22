package jp.hichain.prototype.concept;

public enum ScoredString {
	//アルファベット順
	ALPHABETICAL(
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	),
	//ロイヤル順
	ROYAL(
		"CHAIN"
	);

	public enum Relation {
		PREVIOUS,	//前
		NEXT,	    //次
		SAME;       //同一
	}

	private String orderString;

	private ScoredString(String str) {
		orderString = str;
	}

	public String getOrderString() {
		return orderString;
	}
}
