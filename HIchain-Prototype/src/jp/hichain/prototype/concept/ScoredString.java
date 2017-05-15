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

	public enum Order {
		ASCEND,		//昇順
		DESCEND,	//降順
		SAME;		//同一
	}

	public enum Edge {
		HEAD,
		TAIL;
	}

	private String orderString;

	private ScoredString(String str) {
		orderString = str;
	}

	public String getOrderString() {
		return orderString;
	}
}
