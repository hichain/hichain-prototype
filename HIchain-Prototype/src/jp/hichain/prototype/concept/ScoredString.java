package jp.hichain.prototype.concept;

public enum ScoredString {
	ALPHABETICAL,	//連番
	IDENTICAL,	//ぞろ目
	ROYAL;		//ロイヤル

	public enum Order {
		ASCEND,	//昇順
		DESCEND,	//降順
		SAME;	//同一
	}
}
