package jp.hichain.prototype.basic;

public class Chain {
	/*
	  SSの種類:
	    [連番]: 1
	       1:  昇順に連番
	       -1: 降順に連番
	       2:  アスタリスクに連番 (連鎖している)
	       -2: ルートがアスタリスク (連鎖しているか判定できない)
	    [ぞろ目]: 2
	      連鎖の種類:
	       1:  ぞろ目の連鎖
	       2:  アスタリスクに連番 (連鎖している)
	       -2: ルートがアスタリスク (連鎖しているか判定できない)
	    [ロイヤル]:  3
	      連鎖の種類:
	       1:  昇順の連鎖
	       -1: 降順に連鎖
	       2:  アスタリスクに連番 (連鎖している)
	       -2: ルートがアスタリスク (連鎖しているか判定できない)
	 */
	AdBoardSign root;   //ルートのBS
	AdBoardSign target; //目標のBS
	int dir;    //文字の向き
	int kind;     //SSの種類 (連鎖・ぞろ目など)
	int state;    //連鎖の種類 (昇順/降順など)

	Chain (AdBoardSign _root, AdBoardSign _target, int _dir, int _kind, int _state) {
		root = _root;
		target = _target;
		dir = _dir;
		kind = _kind;
		state = _state;
	}

	String getRecode() {
		return "(" + root.getRecode() + ") has a chain: (" + target.getRecode() + ") [SignDir=" + dir + " SSKind=" + kind + " ChainState=" + state + "]";
	}

	//ルートのBSを返す
	AdBoardSign getRootBS() {
		return root;
	}

	//目標のBSを返す
	AdBoardSign getTargetBS() {
		return target;
	}

	//文字の向きを返す
	int getSignDir() {
		return dir;
	}

	//SSの種類を返す
	int getSSKind() {
		return kind;
	}

	//連鎖の種類を返す
	int getChainState() {
		return state;
	}

	//文字の向きとSSの種類が等しいか返す
	boolean equals(int _dir, int _kind) {
		return (dir == _dir) && (kind == _kind);
	}
}

