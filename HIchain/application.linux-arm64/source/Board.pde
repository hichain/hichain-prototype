//盤 (ルール部分) (有限盤)

class Board {
	int boardSize;			//盤のサイズ (行数=列数)
	int [][] boardRange = new int [2][2];	//使用している盤の範囲
	String godString;	//勝利確定文字列 (CHAINで勝利)

	boolean [][] exist;				//置いてあるかどうか
	NumLayers [] numLayers;		//文字番号レイヤー (プレイヤーごと･インデックスが0から始まるので注意)
	PSLayer psLayer;					//点･辺レイヤー (プレイヤー混同)

	//Board(プレイヤー数, 先行のプレイヤー番号)
	//盤の大きさは使用する文字数によって決定
	Board(String _godString) {
		godString = _godString;
		boardSize = (GameData.getFieldSignNum()+GameData.PLAYERS/2)*2;	//使用する文字数の倍が盤のサイズ
		boardRange[0][0] = boardSize/2-2;
		boardRange[0][1] = boardRange[0][0];
		boardRange[1][0] = boardSize/2+1;
		boardRange[1][1] = boardRange[1][0];

		exist = new boolean[boardSize][boardSize];
		numLayers = new NumLayers[GameData.PLAYERS];
		psLayer = new PSLayer(boardSize);

		//勝利確定文字列から1文字ずつ取り出して文字番号に変換する
		char [] ch = godString.toCharArray();
		int [] num = new int [ch.length];
		for (int i = 0; i < num.length; i++) {
			num[i] = SignData.getNum(ch[i], 0)[0];
		}

		for (int i = 0; i < GameData.PLAYERS; ++i) {
			numLayers[i] = new NumLayers(boardSize, num);
		}
	}

	//盤のサイズを返す
	int getBoardSize() {
		return boardSize;
	}

	//盤の使用している範囲を返す
	int [][] getBoardRange() {
		return boardRange;
	}

	//指定プレイヤーのポイントを計算してデータベースにセット
	void setPoints(int _player) {
		GameData.setPoints( _player, numLayers[_player-1].calPoints() );
	}

	//プレイ中のプレイヤーのポイントを計算してデータベースにセット
	void setPoints() {
		setPoints(GameData.turn);
	}

	//指定プレイヤーの加点文字列を計算してデータベースに追加
	void addSStrings(int _player) {
		ArrayList <ScoredString> ssList = new ArrayList <ScoredString> ( numLayers[_player-1].calScoredStrings(boardRange) );
		for (int i = 0; i < ssList.size(); i++) {
			ssList.get(i).setPlayer(_player);
		}
		GameData.addSStrings(_player, ssList);
	}

	//プレイ中のプレイヤーの加点文字列を計算してデータベースに追加
	void addSStrings() {
		addSStrings(GameData.turn);
	}

	/*
	カードを置く・置けるか判定する･データベースに追加する
		putCard(プレイヤー番号, 文字, 文字の向き, x座標, y座標)
	既に置いてある座標に置く場合や不正な座標の場合falseを返す、置ける条件判定はcanPut()で行う
	座標は中心座標系で記述する (初期配置を中心とする)
	返り値; 置けるかどうか (falseならエラー)
	*/
	boolean putCard(int _player, char _ch, int _dir, int _x, int _y) {
		int [] num = SignData.getNum(_ch, _dir);	//文字番号
		int ps = SignData.getPS(_ch, _dir);				//点･辺データ (PS: Points/Sides)
		int [] newPos = convertUpperLeftFrame( new int []{_x, _y} );	//中心座標系から左上座標系に変換

		//xかyが0または盤外ならfalseを返す
		if (_x == 0 || _y == 0 || abs(_x) > boardSize/2 || abs(_y) > boardSize/2) {
			println("!Wrong Position");
			return false;
		}
		//既に置かれている座標ならfalseを返す
		if (exist[newPos[0]][newPos[1]]) {
			println("!The Position has already existed a card");
			return false;
		}

		//使用中の盤の範囲外なら拡張
    if (newPos[0] <= boardRange[0][0] && newPos[0] > 0) {
      boardRange[0][0] = newPos[0]-1;
    } else if (newPos[0] >= boardRange[1][0] && newPos[0] < boardSize) {
      boardRange[1][0] = newPos[0]+1;
    }
    if (newPos[1] <= boardRange[0][1] && newPos[1] > 0) {
      boardRange[0][1] = newPos[1]-1;
    } else if (newPos[1] >= boardRange[1][1] && newPos[1] < boardSize) {
      boardRange[1][1] = newPos[1]+1;
    }

    numLayers[_player-1].putSignNum(num, newPos[0], newPos[1]);	//文字番号の代入
    psLayer.putPS(_player, ps, newPos[0], newPos[1]);	//点･辺データの代入

		exist[newPos[0]][newPos[1]] = true;

		//データベースに追加
		GameData.addBoardSign( new BoardSign(_player, _ch, _dir, _x, _y) );

		return true;
	}

	//上のputCardのBoardSign版
	boolean putCard(BoardSign _bs) {
		return putCard(_bs.getPlayer(), _bs.getChar(), _bs.getDir(), _bs.getX(), _bs.getY());
	}

	//ランダムにカードを1つ置く
	boolean putCardRandom() {
	  ArrayList <BoardSign> fs = new ArrayList<BoardSign>( GameData.getFieldSigns() );
		if (fs.size() == 0) return true;
	  BoardSign bs = fs.get( (int)random(fs.size()) );
	  GameData.removeFieldSign(bs);

	  bs.dir = (int)random(4);
	  int [][] canPutPos = GameData.getCanPutPos(bs);
		bs.pos = canPutPos[ (int)random(canPutPos.length) ];
	  //println(bs.getPlayer(), bs.getChar(), bs.getDir(), bs.getX(), bs.getY());

	  return putCard(bs);
	}

	/*
	次のターンを返す
		turnChange(場にあるBoardSignのリスト)
	返り値: 次のターンのプレイヤー番号 (次のプレイヤーが置けない場合は0が返る 勝利確定文字列を揃えたら-1)
	*/
	private int turnChange(int _cnt) {
		if (_cnt > GameData.PLAYERS) {
			GameData.turn = 0;
			return 0;
		}

		int currentTurn = GameData.turn;			//現在のターン
		int nextTurn = currentTurn%GameData.PLAYERS + 1;	//次のターン

		addSStrings();	//加点文字列を計算しデータベースに追加
		setPoints();		//ポイントを計算しデータベースに追加

    //勝利確定文字列を揃えていたならゲームセット
    if (numLayers[currentTurn-1].hitGodString()) {
      GameData.turn = 0;
      GameData.winner = currentTurn;
      return 0;
    }

		GameData.turn = nextTurn;

		//BoardSignから文字を取得しリストに入れる
		ArrayList <Character> ch = new ArrayList<Character>();
		ArrayList <BoardSign> fs = GameData.getFieldSigns(nextTurn);
		for (BoardSign bs : fs) {
			ch.add(bs.getChar());
		}

		//次のプレイヤーが置ける座標を計算し､あれば次のターンへ
		ArrayList <BoardSign> bs = canPutAll(nextTurn);
		if (bs.size() == 0){
			return turnChange(++_cnt);
		}
		GameData.setCanPutCC(bs);	//データベースにセット

		return nextTurn;
	}

	int turnChange() {
		int turn = turnChange(1);
		return turn;
	}

	/*
	指定した範囲で指定したプレイヤーが置ける可能性がある座標を返す (アルゴリズムについてはcanPutAllで)
		getMayPutPos(プレイヤー番号, 盤の範囲)
	*/
	private int [][] getMayPutPos(int _player, int [][] _bRange) {
		ArrayList <int []> pos = new ArrayList <int []>();

		for (int y = _bRange[0][1]; y <= _bRange[1][1]; ++y) {
			for (int x = _bRange[0][0]; x <= _bRange[1][0]; ++x) {
				if ( psLayer.mayPut(_player, x, y) ) {
					pos.add( convertCenterFrame(new int []{x, y}) );
				}
			}
		}

		return pos.toArray(new int [pos.size()][2]);
	}

	/*
	指定した範囲の座標の中で置けそうな座標を予め調べてから、場にあるカードが置ける組み合わせを調べる
		canPutAll(プレイヤー番号, 盤の範囲)
	予め省く座標: 既に置いてある、周りに文字がない、周りに置くプレイヤー番号以外の文字がない
	場にカードがない場合もfalseが返る
	*/
	private ArrayList <BoardSign> canPutAll(int _player) {
		ArrayList <BoardSign> canPutCC = new ArrayList <BoardSign>();	//置ける組み合わせ (BoardSign)
		int [][] mayPutPos = getMayPutPos(_player, boardRange);	//置ける可能性がある座標
		ArrayList <BoardSign> fsList = GameData.getFieldSigns(GameData.turn);

		for (BoardSign fs : fsList) {	//場にある文字
			for (int j = 0; j < 4; ++j) {	//方向: 0-3
				for (int [] pos : mayPutPos) {	//置ける可能性がある座標
					BoardSign bs = new BoardSign(_player, fs.getChar(), j, pos[0], pos[1]);
					if ( canPut(bs) ) {
						canPutCC.add(bs);
					}
				}
			}
		}

		return canPutCC;
	}

  /*
	指定した座標に置けるかどうか判定する (点・辺データから置ける条件判定を行う)
	(PSLayerのcanPutにprintlnを追加しているだけ)
		canPut(BoardSign)
	返り値: 置けるかどうか
	*/
	private boolean canPut(BoardSign _bs) {
    int ps = SignData.getPS(_bs.getChar(), _bs.getDir());	//PS
		int [] pos = convertUpperLeftFrame( _bs.getPos() );	//座標(左上座標系)
    int i = psLayer.canPut(_bs.getPlayer(), ps, pos[0], pos[1]);	//置けるか判定
    boolean b = (i == 1);
		/*
		pos[0] -= (pos[0] <= boardSize/2-1) ? boardSize/2 : boardSize/2 - 1;
		pos[1] -= (pos[1] <= boardSize/2-1) ? boardSize/2 : boardSize/2 - 1;
    print(_bs.getPlayer() + "P can put \'" + _bs.getChar() + ":" + _bs.getDir() + "\' at (" + pos[0] + ", " + pos[1] + "): " + b);

    switch(i) {
      case 1:	//置ける
				print("\n");
        break;
      case 0:	//既に置かれているので置けない
        println(" (Already Exist)");
        break;
      case -1:	//辺と辺が接しているので置けない
        println(" (The sides come in contact with the opposite sides)");
        break;
      case -2:	//相手の点と自分の点が1つも接していないので置けない
        println(" (The points don't come in contact with the opposite sides of the opponent)");
        break;
      default:
        break;
    }

    //println("ps: " + Integer.toBinaryString(ps));
		*/
    return b;
  }

	//中心座標系から左上座標系に変換する
	private int [] convertUpperLeftFrame(int [] _pos) {
		int [] newPos = Arrays.copyOf(_pos, 2);
		newPos[0] += (_pos[0] < 0) ? boardSize/2 : boardSize/2 - 1;
		newPos[1] += (_pos[1] < 0) ? boardSize/2 : boardSize/2 - 1;
		return newPos;
	}

	//左上座標系から中心座標系に変換する
	private int [] convertCenterFrame(int [] _pos) {
		int [] newPos = Arrays.copyOf(_pos, 2);
		newPos[0] += - getBoardSize()/2 + ( (_pos[0] < getBoardSize()/2) ? 0 : 1 );
		newPos[1] += - getBoardSize()/2 + ( (_pos[1] < getBoardSize()/2) ? 0 : 1 );
		return newPos;
	}
}
