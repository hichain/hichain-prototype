/*
加点文字列 (相対表記)
方角表記(0-3)の値を受け取る→相対・絶対座標で返す
*/

class ScoredString<T> extends ArrayList<Integer> {
	private T arg;
	private int [] root;	//ルート座標(絶対表記)
	private int kind;			//加点文字列の種類
	private int player;		//プレイヤー番号 (NumLayer/NumLayersでは使用しない)

	ScoredString(int _x, int _y, int _kind) {
		super();
		root = new int []{_x, _y};
		kind = _kind;
	}

	ScoredString(ScoredString _ss) {
		super(_ss);
		root = _ss.getRoot();
		kind = _ss.getKind();
	}

	void setValue(T _val) {
		arg = _val;
	}

	T getValue() {
		return arg;
	}

	//プレイヤー番号をセットする
	void setPlayer(int _player) {
		player = _player;
	}

	int getPlayer() {
		return player;
	}

	//加点文字列の種類を返す
	int getKind() {
		return kind;
	}

	//ルートからの方向を返す
	int [] getDirInt() {
		int [] d = new int [super.size()];
		for (int i = 0; i < d.length; ++i) {
			d[i] = super.get(i);
		}
		return d;
	}

	//ルート座標を返す
	int [] getRoot() {
		return root;
	}

	//相対座標を返す(最初はルートの絶対座標)
	int [][] getRelativePos() {
		int [][] pos = new int [super.size()+1][2];
		pos[0][0] = root[0];
		pos[0][1] = root[1];
		for (int i = 0; i < super.size(); ++i) {
			int n = super.get(i);
			pos[i+1][0] = (int)cos(-0.5*(n+1)*PI);
			pos[i+1][1] = (int)sin(-0.5*(n+1)*PI);
		}
		return pos;
	}

	//絶対座標で返す(最初はルートの絶対座標)
	int [][] getAbsolutePos() {
		int [][] pos = getRelativePos();
		for (int i = 1; i < pos.length; ++i) {
			pos[i][0] += pos[i-1][0];
			pos[i][1] += pos[i-1][1];
		}
		return pos;
	}

	//部分集合または等価な加点文字列か判定する
	//このScoredStringをAとし､引数oをBとしたとき､A⊃BまたはA=Bのときtrueを返す
	boolean includingStrings(ScoredString o) {
		int [][] shorterString = o.getAbsolutePos();		//短いと仮定したときのScoredStringの絶対座標
		int [][] longerString = this.getAbsolutePos();	//長いと〃

		//shorterStringがlongerStringより長かったらfalseを返す
		if ( longerString.length - shorterString.length < 0 ) {
			return false;
		}

		boolean b = true;
		//昇順と昇順に検索
		for (int i = 0; i < shorterString.length; i++) {
			//println("(" + shorterString[i][0] + ", " + shorterString[i][1] + ") : (" + longerString[i][0] + ", " + longerString[i][1] + ")");
			//座標が等しくなかったら抜ける
			if ( !Arrays.equals(shorterString[i], longerString[i]) ) {
				b = false;
				break;
			}
		}
		if (b) {
			return true;
		}

		//昇順と降順に検索
		for (int i = 0; i < shorterString.length; i++) {
			//println("(" + shorterString[i][0] + ", " + shorterString[i][1] + ") : (" + longerString[i][0] + ", " + longerString[i][1] + ")");
			if ( !Arrays.equals(shorterString[shorterString.length-i-1], longerString[i]) ) {
				return false;
			}
		}

		return true;
	}
}
