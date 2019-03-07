import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gifAnimation.*; 
import java.util.*; 
import java.util.*; 
import java.util.Comparator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class HIchain extends PApplet {



final String VERSION = "r1.0.2";   //バージョン
//カードの画像のパス
final String [] IMAGEPATH = {
  "cards/D", "cards/R"
};
//最初に場に置く文字
final char [] INITFIELDSIGNS = {
  //'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I', 'H', 'I'
  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '*'
};
final String GODSTRING = "CHAIN";  //勝利確定文字列
int sSize = 684; //画面サイズ (正方形)

//クラス
Board board;          //盤(ルール部分)
BoardUI boardUI;      //盤のUI
FieldUI [] fieldUI;   //場のUI (プレイヤー別)
StatusUI [] statusUI; //ステータスのUI (プレイヤー別)

//ホールド･ドラッグ関連
BoardSign holdingBS;  //ホールド中のBoardSign
boolean holding;      //ホールド中か
int dragging;         //盤がドラッグ中か (0:ドラッグ中でない 1:盤をクリックした 2:盤をドラッグ中)

//GIFアニメ･スクリーンショット関連
GifMaker gifMaker;        //GIFアニメメーカー
boolean recoding = false; //GIFアニメ録画中か
int recodeTimes = 0;      //録画回数 (ファイル名に使用)
int shotTimes = 0;        //スクリーンショット回数 (ファイル名に使用)

//オートモード (自動でカードを置く)
//1: 通常 | 2: ゲームセット後に試合結果をエクスポート
int autoMode = -1;
int gameRound = 1;   //試合回数

public void settings() {
  size(sSize, sSize);
  smooth(2);
}

public void setup() {
  PFont font = createFont("Arial", 48, true);
  textFont(font, 25);
  surface.setTitle("HIchain Prototype " + VERSION);
  //surface.setResizable(true);
}

public void draw() {
  switch (GameData.turn) {
    case -3 : //ローディング画面表示中
      drawLoading();  //ローディング画面の表示
      println("Loading...");
      GameData.turn = -2;
    break;
    case -2 : //ローディング中
      loadSignData(IMAGEPATH, INITFIELDSIGNS);   //文字データをロードする (SignDataクラスに格納)
      GameData.turn = -1;
    break;
    case -1 : //初期化中
      initialize(INITFIELDSIGNS); //初期化
      GameData.turn = 2;  //仮に現在のターンをセットする (2Pから1Pにターンチェンジ)
      board.turnChange(); //ターンチェンジ (ゲーム開始)
      println("CARDSIZE: " + GameData.cardSize);
    break;
    default :
      int cardSize = GameData.cardSize;        //カードサイズ
      int rowNum = GameData.getFieldRowNum();  //場の列数

      background(255);
      if (holding) {
        boardUI.draw(holdingBS);  //置ける座標をハイライトあり
      } else {
        boardUI.draw();
      }
      fieldUI[0].draw(0, height - cardSize*2);      //1Pの場
      fieldUI[1].draw(rowNum*cardSize, cardSize*2); //2Pの場
      if (holding) drawHoldingBS(mouseX, mouseY);   //ホールド中のカード
      statusUI[1].draw( (width + rowNum*cardSize)/2, height/2, (width - rowNum*cardSize), height/2 ); //2Pのステータス
      statusUI[0].draw( (width + rowNum*cardSize)/2, height/2, (width - rowNum*cardSize), height/2 );   //1Pのステータス
    break;
  }
  if (autoMode >= 1) autoPut();  //自動で置く
  //録画中の場合
  if (recoding) {
    //マウスを動かしていたらフレームを追加
    if (mouseX != pmouseX || mouseY != pmouseY) {
      gifMaker.addFrame();
      println("ADD FRAME: " + frameCount);
    }
  }
}

//初期化
public void initialize(char [] _fieldSigns) {
  int rowNum = _fieldSigns.length/2;            //場の列数
  if (rowNum % 2 == 1) rowNum++;
  int lineNum = _fieldSigns.length/rowNum + 1;  //場の行数
  int cardSize = height/(rowNum+4);             //カードサイズ

  GameData.init(cardSize, rowNum);    //データベースを初期化 (GameDataクラスに場の文字を格納)
  addInitialFieldSigns(_fieldSigns);  //データベースに初期の場の文字を追加
  SignData.resizeAll(cardSize);       //カードの画像をリサイズ

  int players = GameData.PLAYERS;  //プレイヤー数

  board = new Board(GODSTRING);
  fieldUI = new FieldUI[players];
  fieldUI[0] = new FieldUI(1, rowNum, lineNum);
  fieldUI[1] = new FieldUI(2, rowNum, lineNum);
  boardUI = new BoardUI( rowNum*cardSize/2, height/2, board.getBoardSize(), board.getBoardSize() );
  statusUI = new StatusUI[players];
  statusUI[0] = new StatusUI(1);
  statusUI[1] = new StatusUI(2);

  putInitialCards();  //盤に初期配置を置く

  println("Done Initializing");
}
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
	public int getBoardSize() {
		return boardSize;
	}

	//盤の使用している範囲を返す
	public int [][] getBoardRange() {
		return boardRange;
	}

	//指定プレイヤーのポイントを計算してデータベースにセット
	public void setPoints(int _player) {
		GameData.setPoints( _player, numLayers[_player-1].calPoints() );
	}

	//プレイ中のプレイヤーのポイントを計算してデータベースにセット
	public void setPoints() {
		setPoints(GameData.turn);
	}

	//指定プレイヤーの加点文字列を計算してデータベースに追加
	public void addSStrings(int _player) {
		ArrayList <ScoredString> ssList = new ArrayList <ScoredString> ( numLayers[_player-1].calScoredStrings(boardRange) );
		for (int i = 0; i < ssList.size(); i++) {
			ssList.get(i).setPlayer(_player);
		}
		GameData.addSStrings(_player, ssList);
	}

	//プレイ中のプレイヤーの加点文字列を計算してデータベースに追加
	public void addSStrings() {
		addSStrings(GameData.turn);
	}

	/*
	カードを置く・置けるか判定する･データベースに追加する
		putCard(プレイヤー番号, 文字, 文字の向き, x座標, y座標)
	既に置いてある座標に置く場合や不正な座標の場合falseを返す、置ける条件判定はcanPut()で行う
	座標は中心座標系で記述する (初期配置を中心とする)
	返り値; 置けるかどうか (falseならエラー)
	*/
	public boolean putCard(int _player, char _ch, int _dir, int _x, int _y) {
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
	public boolean putCard(BoardSign _bs) {
		return putCard(_bs.getPlayer(), _bs.getChar(), _bs.getDir(), _bs.getX(), _bs.getY());
	}

	//ランダムにカードを1つ置く
	public boolean putCardRandom() {
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

	public int turnChange() {
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
//文字のchar、プレイヤー番号、文字の向き、座標を保持する (プレイヤー番号は省略可)
//UIに近いクラスの多くで使用する

class BoardSign {
  int player = 0;      //プレイヤー番号
  char ch = ' ';       //文字
  int dir = 0;         //文字の向き
  int [] pos = {0, 0}; //座標

  BoardSign(int _player, char _ch, int _dir, int _x, int _y) {
    player = _player;
    ch = _ch;
    dir = _dir;
    pos[0] = _x;
    pos[1] = _y;
  }

  BoardSign(char _ch, int _dir, int _x, int _y) {
    ch = _ch;
    dir = _dir;
    pos[0] = _x;
    pos[1] = _y;
  }

  BoardSign(int _player, int _x, int _y) {
    player = _player;
    pos[0] = _x;
    pos[1] = _y;
  }

  BoardSign(BoardSign _bs) {
    player = _bs.getPlayer();
    ch = _bs.getChar();
    dir = _bs.getDir();
    pos[0] = _bs.getX();
    pos[1] = _bs.getY();
  }

  //プレイヤー番号を返す
  public int getPlayer() {
    return player;
  }

  //座標を返す
  public int [] getPos() {
    return pos;
  }

  //X座標を返す
  public int getX() {
    return pos[0];
  }

  //Y座標を返す
  public int getY() {
    return pos[1];
  }

  //文字を返す
  public char getChar() {
    return ch;
  }

  //文字の向きを返す
  public int getDir() {
    return dir;
  }

  //プレイヤー番号が等しいか
  public boolean equalsPl(int _player) {
    return (player == _player);
  }

  //文字と文字の向きが等しいか
  public boolean equalsChDir(char _ch, int _dir) {
    return ( (ch == _ch) && (dir == _dir) );
  }

  //文字と文字の向きが等しいか (プレイヤー番号込み)
  public boolean equalsChDir(int _player, char _ch, int _dir) {
    return ( (player == _player) && (ch == _ch) && (dir == _dir) );
  }

  //座標が等しいか
  public boolean equalsPos(int _x, int _y) {
    return ( (pos[0] == _x) && (pos[1] == _y) );
  }

  //座標が等しいか (プレイヤー番号込み)
  public boolean equalsPos(int _player, int _x, int _y) {
    return ( (player == _player) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //全ての値が等しいか (プレイヤー番号抜き)
  public boolean equals(char _ch, int _dir, int _x, int _y) {
    return ( (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //全ての値が等しいか
  public boolean equals(int _player, char _ch, int _dir, int _x, int _y) {
    return ( (player == _player) && (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //プレイヤー番号が等しいか (BoardSignクラスから)
  public boolean equalsPl(BoardSign _bs) {
    return (player == _bs.player);
  }

  //文字と文字の向きが等しいか (BoardSignクラスから)
  public boolean equalsChDir(BoardSign _bs) {
    return ( (player == _bs.player) && (ch == _bs.ch) && (dir == _bs.dir) );
  }

  //座標が等しいか (BoardSignクラスから)
  public boolean equalsPos(BoardSign _bs) {
    return ( (player == _bs.player) && (pos[0] == _bs.pos[0]) && (pos[1] == _bs.pos[1]) );
  }

  //全ての値が等しいか (BoardSignクラスから)
  public boolean equals(BoardSign _bs) {
    return ( (player == _bs.player) && (ch == _bs.ch) && (dir == _bs.dir) && (pos[0] == _bs.pos[0]) && (pos[1] == _bs.pos[1]) );
  }
}

//盤の表示
class BoardUI {
  protected int initX, initY; //盤の中心座標(初期値)
  protected int bcX, bcY;     //盤の中心座標
  int rowNum, lineNum;        //盤の行列数
  int [][] sStringsColor;   //加点文字列のカラー
  int canPutColor;          //置ける座標のカラー

  BoardUI(int _x, int _y, int _rowNum, int _lineNum) {
    initX = _x;
    initY = _y;
    bcX = _x;
    bcY = _y;
    rowNum = _rowNum;
    lineNum = _lineNum;
    setColor();
  }

  public void setColor() {
    colorMode(HSB,360,100,100);
    sStringsColor = new int [][] {
      {
        color(165, 70, 99, 60), color(190, 75, 99, 60), color(205, 80, 99, 60), color(220, 85, 99, 60), color(235, 90, 99, 60), color(250, 95, 99, 60)
      },
      {
        color(70, 70, 100, 60), color(54, 75, 100, 60), color(48, 80, 100, 60), color(32, 85, 100, 60), color(16, 90, 100, 60), color(0, 95, 100, 60)
      }
    };
    colorMode(RGB,256,256,256);
    canPutColor = color(0, 255, 0, 150);
  }

  //中心座標を移動 (差分)
  public void movePos(int _x, int _y) {
    bcX += _x;
    bcY += _y;
  }

  //中心座標を初期値に戻す
  public void movePos() {
    bcX = initX;
    bcY = initY;
  }

  //盤の座標系に変換して返す
  public int [] convertBoardPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = {
      _pos[0] - bcX, _pos[1] - bcY
    };//mouseX,mouseY
    pos[0] = (pos[0] <= 0) ? (pos[0]-cardSize)/cardSize : (pos[0]+cardSize)/cardSize;
    pos[1] = (pos[1] <= 0) ? (pos[1]-cardSize)/cardSize : (pos[1]+cardSize)/cardSize;
    return pos;
  }

  //ウィンドウの座標系にして返す (マスの左上)
  public int [] convertWindowPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = new int [2];
    pos[0] = (_pos[0] <= 0) ? _pos[0]*cardSize : _pos[0]*cardSize-cardSize;
    pos[1] = (_pos[1] <= 0) ? _pos[1]*cardSize : _pos[1]*cardSize-cardSize;
    pos[0] += bcX;
    pos[1] += bcY;
    return pos;
  }

  public void draw(BoardSign _holdingBS) {
    drawLattice();
    highlightCanPutPos(_holdingBS);
    highlightSStrings();
    drawCards();
  }

  public void draw() {
    drawLattice();
    highlightSStrings();
    drawCards();
  }

  //マス目を描画
  private void drawLattice() {
    int cardSize = GameData.cardSize;

    pushMatrix();
    translate(bcX - rowNum/2*cardSize, bcY - lineNum/2*cardSize);

    fill(255);
    rect(0, 0, rowNum*cardSize, lineNum*cardSize);
    drawLines();

    popMatrix();
  }

  private void drawLines() {
    int cardSize = GameData.cardSize;

    for (int i = 1; i < rowNum; i++) {
      line(i*cardSize, 0, i*cardSize, lineNum*cardSize);
    }
    for (int i = 1; i < lineNum; i++) {
      line(0, i*cardSize, rowNum*cardSize, i*cardSize);
    }
  }

  //盤上のカード全てを描画
  private void drawCards() {
    imageMode(CENTER);
    for (BoardSign bs : GameData.getBoardSigns()) {
      drawBS(bs);
    }
  }

  private void drawBS(BoardSign _bs) {
    int cardSize = GameData.cardSize;
    int [] pos = convertWindowPos(_bs.getPos());

    pushMatrix();
    translate( pos[0]+cardSize*0.5f, pos[1]+cardSize*0.5f );
    rotate( -_bs.getDir()*PI*0.5f );
    image( SignData.getImage( _bs.getPlayer(), _bs.getChar() ), 0, 0, cardSize, cardSize );
    popMatrix();
  }

  //加点文字列をハイライトする
  private void highlightSStrings() {
    for (int i = 2; i <= GameData.getSStringsMax(); i++) {
      ArrayList <ScoredString> sStrings = new ArrayList <ScoredString> (GameData.getSStrings(i));
      if (sStrings.size() != 0) {
        boardUI.highlightChain(sStrings, i);
      }
    }
  }

  //加点文字列をハイライトする
  private void highlightChain(ArrayList <ScoredString> _sStrings, int _chain) {
    for (ScoredString ss : _sStrings) {
      int [][] poss = ss.getAbsolutePos();
      for (int [] pos : poss) {
        int [] newPos = Arrays.copyOf(pos, 2); //左上座標系の座標
        //中心座標系に変換
    		newPos[0] += - rowNum/2 + ( (newPos[0] < rowNum/2) ? 0 : 1 );
    		newPos[1] += - lineNum/2 + ( (newPos[1] < lineNum/2) ? 0 : 1 );
        //ウィンドウ上の座標に変換
        newPos = convertWindowPos(newPos);
        fill( sStringsColor [ss.getPlayer()-1] [(_chain-2 >= 5) ? 5 : _chain-2] );
        rect(newPos[0], newPos[1], GameData.cardSize, GameData.cardSize);
      }
    }
  }

  //置ける座標をハイライトする
  private void highlightCanPutPos(BoardSign _bs) {
    for (int [] pos : GameData.getCanPutPos(_bs)) {
      pos = convertWindowPos(pos);
      fill(canPutColor);
      rect(pos[0], pos[1], GameData.cardSize, GameData.cardSize);
    }
  }
}
//場の表示 (プレイヤー別)

class FieldUI extends BoardUI {
  int player;  //割り当てられたプレイヤー番号
  //bcX, bcYはFieldUIでは左上の座標とする

  FieldUI (int _player, int _rowNum, int _lineNum) {
    super(0, 0, _rowNum, _lineNum);
    player = _player;
  }

  //盤の座標系に変換して返す
  public int [] convertBoardPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = {
      ((player == 1) ? 1 : -1) * (_pos[0] - bcX), ((player == 1) ? 1 : -1) * (_pos[1] - bcY)
    };//mouseX,mouseY

    pos[0] = (pos[0] <= 0) ? (pos[0]-cardSize)/cardSize : (pos[0]+cardSize)/cardSize;
    pos[1] = (pos[1] <= 0) ? (pos[1]-cardSize)/cardSize : (pos[1]+cardSize)/cardSize;

    return pos;
  }

  //場の表示範囲を返す (左上､右下)
  public int [][] getFieldRange() {
    int cardSize = GameData.cardSize;
    int [] pos1 = new int [2];
    int [] pos2 = new int [2];

    pos1[0] = bcX - ( (player == 1) ? 0 : rowNum*cardSize );
    pos1[1] = bcY - ( (player == 1) ? 0 : lineNum*cardSize );
    pos2[0] = bcX + ( (player == 1) ? rowNum*cardSize : 0 );
    pos2[1] = bcY + ( (player == 1) ? lineNum*cardSize : 0 );

    return new int [][] {pos1, pos2};
  }

  public void draw(int _x, int _y) {
    movePos(_x - bcX, _y - bcY);
    drawLattice();
    drawCards();
  }

  //マス目を描画
  private void drawLattice() {
    int cardSize = GameData.cardSize;

    pushMatrix();
    translate(bcX, bcY);
    if (player == 2) rotate(PI);

    fill(255, 200);
    rect(0, 0, rowNum*cardSize, lineNum*cardSize);
    strokeWeight(3);
    line(0, 0, rowNum*cardSize, 0);
    strokeWeight(1);
    super.drawLines();

    popMatrix();
  }

  //場にあるカードを全て描画
  private void drawCards() {
    int cardSize = GameData.cardSize;

    pushMatrix();
    translate(bcX, bcY);

    if (player == 2) rotate(PI);
    imageMode(CENTER);

    for (BoardSign bs : GameData.getFieldSigns(player)) {
      pushMatrix();
      translate( (bs.getX()-0.5f) * cardSize, (bs.getY()-0.5f) * cardSize );
      if (player == 2) rotate(PI);
      rotate( -radians(bs.getDir()*90) );
      image( SignData.getImage( bs.getPlayer(), bs.getChar() ), 0, 0, cardSize, cardSize );
      popMatrix();
    }

    popMatrix();
  }
}
//盤・場・ステータスのデータを保持する



static class GameData {
  public static final int PLAYERS = 2;  //プレイヤー数
  public static int cardSize;           //カードサイズ
  public static int fieldRowNum;        //場の列数
  public static int turn = -3;          //ターン (-3:起動前 -2:画像のローディング中 -1:初期化中 0:ゲームセット 1～:プレイ中のプレイヤー番号)
  private static int [] points;         //ポイント
  public static int winner;             //優勢のプレイヤー (0: 引き分け 1～: 優勢のプレイヤー番号)

  private static ArrayList <BoardSign> boardSigns;    //盤上の文字 (BoardSign)
  private static ArrayList <BoardSign> fieldSigns;    //場にある文字 (BoardSign)
  private static ArrayList <BoardSign> canPutCC;      //特定の文字・向きで置ける座標の組み合わせ (BoardSign)
  private static ArrayList <ScoredString> sStrings;   //加点文字列 (ScoredString)

  public static void clear() {
    boardSigns = new ArrayList <BoardSign>();
    fieldSigns = new ArrayList <BoardSign>();
    canPutCC = new ArrayList <BoardSign>();
    sStrings = new ArrayList <ScoredString>();
    points = new int [PLAYERS];
    winner = 0;
  }

  //初期化 (カードサイズと場の列数をセット)
  public static void init(int _cardSize, int _num) {
    cardSize = _cardSize;
    fieldRowNum = _num;
    clear();
  }

  //場に文字を追加する
  public static void addFieldSign(BoardSign _fs) {
    if (_fs.getPlayer() == 1) {
      fieldSigns.add(0, _fs);
    } else if (_fs.getPlayer() == 2) {
      fieldSigns.add(_fs);
    }
  }

  //指定プレイヤーのポイントを返す
  public static int getPoints(int _player) {
    return points[_player-1];
  }

  //プレイ中のプレイヤーのポイントを返す
  public static int getPoints() {
    return points[turn-1];
  }

  //盤上のBoardSignのリストを返す
  public static ArrayList <BoardSign> getBoardSigns() {
    return boardSigns;
  }

  //場の列数を返す
  public static int getFieldRowNum() {
    return fieldRowNum;
  }

  //場にある文字数を返す (全プレイヤー)
  public static int getFieldSignNum() {
    return fieldSigns.size();
  }

  //指定プレイヤーの場にある文字を返す
  public static ArrayList <BoardSign> getFieldSigns(int _player) {
    int from = 0, to = fieldSigns.size();
    if (_player == 1) {
      for (int i = 0; i < fieldSigns.size(); i++) {
        if (fieldSigns.get(i).getPlayer() != 1) {
          to = i;
          break;
        }
      }
    } else if (_player == 2) {
      for (int i = fieldSigns.size()-1; i >= 0; i--) {
        if (fieldSigns.get(i).getPlayer() != 2) {
          from = i+1;
          break;
        }
      }
    }
    return new ArrayList <BoardSign> ( fieldSigns.subList(from, to) );
  }

  //プレイ中のプレイヤーの場にある文字を返す
  public static ArrayList <BoardSign> getFieldSigns() {
    return getFieldSigns(turn);
  }

  /*
  指定した文字と向きで置ける座標を返す
    getCanPutPos(BoardSign)
  返り値: int[][座標x,y]
  */
  public static int [][] getCanPutPos(BoardSign _bs) {
    ArrayList <int []> pos = new ArrayList <int []>();

    for (int i = 0; i < canPutCC.size(); i++) {
      if ( canPutCC.get(i).equalsChDir(_bs) ) {
          pos.add( canPutCC.get(i).getPos() );
      }
    }

    return pos.toArray(new int [pos.size()][2]);
  }

  //置ける座標の数(手数)を返す
  public static int getCanPutCCSum() {
    return canPutCC.size();
  }

  /*
  指定プレイヤーの指定した連鎖の加点文字列の座標を返す
    getSStringsPos(連鎖数)
  返り値: int[加点文字列][座標][x,y] (座標は左上座標系)
  */
  public static ArrayList <ScoredString> getSStrings(int _chain) {
    ArrayList <ScoredString> ssList = new ArrayList <ScoredString>();	//座標

    for (ScoredString ss : sStrings) {
      if (ss.size() == _chain-1) {
        ssList.add(ss);
      }
    }

    return ssList;
  }

  //加点文字列の連鎖の最大値を返す
  public static int getSStringsMax() {
    if (sStrings.size() == 0) {
      return 0;
    }
    int maxChain = 0;
    for (ScoredString ss : sStrings) {
      if (ss.size() > maxChain) {
        maxChain = ss.size();
      }
    }
    return maxChain+1;
  }

  //指定プレイヤーのポイントをセット
  public static void setPoints(int _player, int _points) {
    points[_player-1] = _points;

    winner = 0;
    int maxPoints = 0;
    for (int i = 0; i < PLAYERS; i++) {
      if (maxPoints < points[i]) {
        maxPoints = points[i];
        winner = i+1;
      }
    }

    println("Points: " + _points + " (Winner: " + winner + "P)");
  }

  //プレイ中のプレイヤーのポイントをセット
  public static void setPoints(int _points) {
    setPoints(turn, _points);
  }

  //盤上に文字を追加
  public static void addBoardSign(BoardSign _bs) {
    boardSigns.add(_bs);
    println("Player " + _bs.getPlayer() + " put \"" + _bs.getChar() + ":" + _bs.getDir() + "\" at (" + _bs.getX() + ", " + _bs.getY() + ")");
  }

  //指定のBoardSignを場から削除する
  public static void removeFieldSign(BoardSign _bs) {
    fieldSigns.remove(_bs);
  }

  //置ける組み合わせをセット
  public static void setCanPutCC(ArrayList <BoardSign> _bs) {
    canPutCC = new ArrayList <BoardSign>(_bs);
    println("All Moves: " + canPutCC.size());
  }

  //加点文字列を追加 (指定のプレイヤーの加点文字列を予め削除してから追加)
  public static void addSStrings(int _player, ArrayList <ScoredString> _ssList) {
    for (int i = 0; i < sStrings.size(); i++) {
      if (sStrings.get(i).getPlayer() == _player) {
        sStrings.remove(i);
        i--;
      }
    }
    sStrings.addAll(_ssList);
		println(sStrings.size() + " ScoredStrings Were Found");
  }
}
//一方向の文字番号レイヤー
//加点文字列の検索
class NumLayer {
	int [][] num;	//文字番号
	ArrayList <ScoredString> strings = new ArrayList<ScoredString>();	//加点文字列群
	ScoredString string;	//加点文字列

	int [] godStringNum;	//勝利確定文字列
	int godStringNumLevel = 0;	//勝利確定文字列がどこまで成立しているか
	boolean hitGodString = false;	//勝利確定文字列がヒットしたか

	int asteNum = -1;	//アスタリスクが連番列上でなりうる文字番号

	/*
	[方向に関して]
	0:北 1:西 2:南 3:東
	(それぞれの方角にカードが上を向く)
	*/

	NumLayer(int _size, int [] _godStringNum) {
		num = new int [_size][_size];
		godStringNum = _godStringNum;
	}

	//文字番号を代入
	public void subNum(int _num, int _x, int _y) {
		num[_x][_y] = _num;
		//println("(" + _x + ", " + _y + ") set " + _num);
	}

	//勝利確定文字列がヒットしたか返す
	public boolean hitGodString() {
		return hitGodString;
	}

	//指定の盤の範囲から加点文字列群を返す
	public ArrayList <ScoredString> calScoredStrings(int [][] _bRange) {
		strings = new ArrayList<ScoredString>();	//初期化

		//連番を検索
		//println("[Calculate Consecutive Signs]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(0, i, j);
			}
		}

		//ぞろ目を検索
		//println("[Calculate Repdigit]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(1, i, j);
			}
		}

		//勝利確定文字列を検索
		//println("[Calculate God String]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(2, i, j);
			}
		}

		return strings;
	}

	/*
	calChains(検索する加点文字列の種類, ルートx座標, y座標)
	○ 指定した座標から連なる連番列を検索
	1. ルート座標が*の場合はreturn
	2. 連番[_kind == 0]						4方向から昇順に連番を見つける (降順に連番があれば返る)
	2. ぞろ目[_kind == 1]				 4方向からぞろ目を見つける (ぞろ目列が周りに2つ以上ある場合は返る)
	2. 勝利確定文字列[_kind == 0] 4方向から勝利確定文字列を先頭から見つける
	3.	(この時点で指定の座標=ルート座標が決定)
	4. ルート座標から昇順に連番か調べる (再帰処理)
	*/
	public void calChains(int _kind, int _x, int _y) {
		int root = num[_x][_y];	//ルート座標

		if (root == 27) {	//ルート座標がアスタリスクなら
			return;
		}

		int [] sum = calTwoChains(_x, _y, -1);	//ルートから連番を計算

		boolean hitChains = false;	//加点文字列がヒットしたか
		switch (_kind) {
			case 0 :	//連番を検索
				for (int i = 0; i < 4; ++i) {
					if (sum[i] == -1) {	//iの方向において降順に連番なら
						return;
					} else if (sum[i] == 1) {	//昇順に連番なら
						hitChains = true;
						//break;
					} else if (sum[i] == 2) {	//アスタリスクに接続なら
						asteNum = (root-1)%27;	//アスタリスクが"降順に"連番になりうる文字を代入
						//A→Zの処理
						if (asteNum == 0) {
							asteNum = 26;
						}
						//目標座標までの差分を更新
						int nextDx = (int)cos(-0.5f*(i+1)*PI);
						int nextDy = (int)sin(-0.5f*(i+1)*PI);
						int [] asteSum = calTwoChains(_x + nextDx, _y + nextDy, (i+2)%4);
						for (int j = 0; j < 4; j++) {
							//降順に連番なら
							if (asteSum[i] == -1) {
								return;
							}
						}
						hitChains = true;
					}
				}
			break;
			case 1 :	//ぞろ目を検索
				for (int i = 0; i < 4; i++) {
					if (sum[i] == -2 || sum[i] == 2) {	//iの方向においてぞろ目なら
						//println("hitChains");
						if (!hitChains) {
								hitChains = true;
						} else {	//ぞろ目列が周りに2つ以上ある場合は返る
							return;
						}
					}
				}
			break;
			case 2 :	//勝利確定文字列を計算
				godStringNumLevel = 0;	//勝利確定文字列の先頭から検索する
				for (int i = 0; i < 4; i++) {
					if (sum[i] == 3 || sum[i] == 2) {
						hitChains = true;
						break;
					}
				}
			break;
		}

		//どの方向においても連番でなかったら
		if (!hitChains) {
			return;
		}

		//この時点で(_x, _y)が加点文字列のルート座標であることが決定
		string = new ScoredString(_x, _y, _kind);
		calHighChains(_kind, _x, _y);	//ルートから高い順に加点文字列を探索
	}

	/*
	calHighChains(ルートx座標, y座標, 目標座標までのx座標差分, 目標座標までのy座標差分, 前の目標座標の向き)
	○ 指定した座標から2連鎖以上の昇順の加点文字列を検索
	---
	root(ルート座標):		 連番列の始点座標
	current(現在座標):		ここから4方向に調べる
	next(目標座標):				現在座標から調べる周りの座標
	---
		1. 現在座標から4方向に調べる
		2. ヒットしたらstringに登録 (目標座標がアスタリスクならアスタリスクが加点文字列になり得る文字番号を代入)
		3. 目標座標を現在座標として再帰
		4. 1～3をループ 加点文字列が見つからなくなったらその前の座標が加点文字列の終点
		5. 加点文字列の終点を見つけたら、stringをstringsに追加 (一つも連番列が見つからない場合は追加しない)
	返り値: boolean型 指定の加点文字列がヒットしたか
	*/
	public boolean calHighChains(int _kind, int _x, int _y, int _dx, int _dy, int _preDir) {
		boolean hitChains = false;	//連鎖を発見したか
		int current = num[_x+_dx][_y+_dy];	//現在座標の文字番号

		//勝利確定文字列の終点のときfalseを返す
		if (_kind == 2 && godStringNumLevel == godStringNum.length-1) {
			return false;
		}

		int [] sum = calTwoChains(_x+_dx, _y+_dy, _preDir);	//現在座標から連番を計算

		for (int i = 0; i < 4; ++i) {
			boolean consecutive = (sum[i] == 1 || sum[i] == 2);	//高い順に連番か
			boolean identical = (sum[i] == -2 || sum[i] == 2);	//ぞろ目か
			boolean god = (sum[i] == 3 || sum[i] == 2);					//勝利確定文字列か

			//指定の加点文字列がヒットしたら
			if ( (_kind == 0 && consecutive) || (_kind == 1 && identical) || (_kind == 2 && god)) {
				hitChains = true;
				string.add(i);	//座標を登録
				//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") add " + i);

				//勝利確定文字列なら次のレベルにする
				if (_kind == 2) {
					godStringNumLevel++;
					//println("godStringNumLevel++: " + godStringNumLevel);
				}

				//目標座標がアスタリスクの場合
				if (sum[i] == 2) {
					switch (_kind) {
						case 0 :	//連番
							asteNum = (current+1)%27;
							if (asteNum == 0) {	//Z→Aの処理
								asteNum++;
							}
						break;
						case 1 :	//ぞろ目
							asteNum = current;
						break;
						case 2 :	//勝利確定文字列
							asteNum = godStringNum[godStringNumLevel];
						break;
					}
				}

				//目標座標までの差分を更新
				int nextDx = _dx + (int)cos(-0.5f*(i+1)*PI);
				int nextDy = _dy + (int)sin(-0.5f*(i+1)*PI);

				boolean nextHitChains = calHighChains(_kind, _x, _y, nextDx, nextDy, (i+2)%4);	//目標の座標で再帰

				//連番を見つけられなかった場合 (連鎖の終点) (*1)
				if (!nextHitChains) {
					if (sum[i] == 2) {	//アスタリスクが連番列の終点の場合は無効
						hitChains = false;
					} else if (string.size() != 0) {	//2連鎖以上なら
						//加点文字列群に登録
						if (_kind == 2) {
							//勝利確定文字列が終点の場合
							if (godStringNumLevel == godStringNum.length-1) {
								strings.add(new ScoredString(string));
								hitGodString = true;
							}
						} else {
							strings.add(new ScoredString(string));
						}
					}
				}

				//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") removed " + string.get(string.size()-1));
				string.remove(string.size()-1);	//最後のstringの座標を削除
				//勝利確定文字列のレベルを下げる
				if (_kind == 2) {
					godStringNumLevel--;
					//println("godStringNumLevel--: " + godStringNumLevel);
				}
			}
		}

		//現在座標がアスタリスクのとき連鎖が見つからない場合､
		//*が終点となってしまうのでfalseを返して*を追加せずにその前の文字で連鎖の終点処理(*1)を行う (1つ戻る)
		if (!hitChains && current == 27) {
			if (current == 27) {
				hitChains = false;
			}
			//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") end");
		}

		return hitChains;
	}

	//calHighChainsのルート座標から調べるバージョン (第3～5引数省略)
	public boolean calHighChains(int _kind, int _x, int _y) {
		return calHighChains(_kind, _x, _y, 0, 0, -1);
	}

	/*
	○ calTwoChainを4方向行う
		calTwoChains(現在のx座標, y座標, 前の目標座標の向き(ルート座標の場合は-1))
	※ 前の目標座標の計算は省いている
	*/
	public int [] calTwoChains(int _x, int _y, int _preDir) {
		int [] sum = new int [4];

		for (int i = 0; i < sum.length; ++i) {
			//前の目標座標を調べないようにする
			if (i == _preDir) {
				sum[i] = 0;
				continue;
			}

			int xd = (int)cos(-0.5f*(i+1)*PI);
			int yd = (int)sin(-0.5f*(i+1)*PI);
			if (_x+xd < 0 || _x+xd > num.length || _y+yd < 0 || _y+yd > num[0].length) {
				sum[i] = 0;
				continue;
			}

			sum[i] = calTwoChain(_x, _y, xd, yd);
		}

		return sum;
	}

	/*
	calTwoChain(現在x座標, y座標, 連番を調べる向きx, y) (向きは0～3)
	○ 2連鎖の判定をする
	返り値: 方向順に格納された連番の状態
		-2: ぞろ目
		-1: 降順に連番
		0: 連番なし
		1: 昇順に連番
		2: アスタリスクに接続
		3: 勝利確定文字列に連番
	*/
	public int calTwoChain(int _x, int _y, int _dx, int _dy) {
		int current = num[_x][_y];			//現在座標の文字番号
		int next = num[_x+_dx][_y+_dy];	//目標座標の文字番号

		//現在座標がアスタリスクの場合はasteNumから取得してくる
		if (current == 27) {
			current = asteNum;
		}

		//現在座標と目標座標のどちらかが0なら返す
		if (current == 0 || next == 0) {
			return 0;
		}
		//目標座標がアスタリスクの場合
		if (next == 27) {
			return 2;
		}

		//連番の判定
		if (next - current == 1 || (next == 1 && current == 26)) {	//昇順に連番
			return 1;
		} else if (next - current == -1 || (next == 26 && current == 1)) {	//降順に連番
			return -1;
		}
		//ぞろ目の判定
		if (next == current) {
			//println("repdigit");
			return -2;
		}
		//勝利確定文字列の判定
		if (current == godStringNum[godStringNumLevel] && next == (godStringNum[godStringNumLevel+1])) {
			return 3;
		}

		return 0;
	}
}
//全方向の文字番号レイヤー
//ポイント計算､加点文字列の取得



class NumLayers {
	boolean hitGodString = false;	//勝利確定文字列がヒットしたか
	ArrayList <ScoredString> sStrings = new ArrayList <ScoredString>();	//全方向の加点文字列

	NumLayer [] numLayer = new NumLayer[4];	//各方向の文字番号レイヤー

	//NumLayers(盤のサイズ, 勝利確定文字列の文字番号)
	NumLayers(int _size, int [] _godStringNum) {
		for (int i = 0; i < numLayer.length; ++i) {
			numLayer[i] = new NumLayer(_size, _godStringNum);
		}
	}

	//勝利確定文字列がヒットしたか
	public boolean hitGodString() {
		return hitGodString;
	}

	//文字番号を代入
	public void putSignNum(int [] _num, int _x, int _y) {
		for (int i = 0; i < numLayer.length; ++i) {
			numLayer[i].subNum(_num[i], _x, _y);
		}
	}

	//加点文字列からポイントを換算する (3連鎖以上の加点文字列)
	//※先にcalScoredStrings()をしないと更新されません
	public int calPoints() {
		int points = 0;	//ポイント

		for (ScoredString ss : sStrings) {
			if (ss.getKind() == 0 || ss.getKind() == 2) {	//2連鎖を省く
				if (ss.size() >= 2)	points += (int)pow(ss.size()+1, 2);
			} else if (ss.getKind() == 1) {
				if (ss.size() >= 1)	points += (int)pow(ss.size()+1, 2);
			}
		}

		return points;
	}

	/*
	○ 指定範囲の全ての加点文字列を返す(連鎖の小さい順) (2連鎖を含む)
		calScoredStrings(検索する盤の範囲)
	*/
	public ArrayList <ScoredString> calScoredStrings(int [][] _bRange) {
		//println(" - START Calculating SStrings - (" + _bRange[0][0] + ", " + _bRange[0][1] + ") ~ (" + _bRange[1][0] + ", " + _bRange[1][1] + ")");

		sStrings = new ArrayList<ScoredString>();	//初期化

		//numLayerから各方向の加点文字列を取得
		for (int i = 0; i < numLayer.length; ++i) {
			//println("  - START " + i + " Direction -");
			sStrings.addAll( new ArrayList <ScoredString> (numLayer[i].calScoredStrings(_bRange)) );	//i方向の加点文字列を取得し代入
			//勝利確定文字列がヒットしたらtrue
			if (numLayer[i].hitGodString()) {
				hitGodString = true;
				//println("  ! A God String Was Found");
			}
			//println("  - END " + i + " Direction -");
		}

		//連鎖の大きい順(降順)に並び替える
		Collections.sort(sStrings, new SStringConparator());
		Collections.reverse(sStrings);

		//等しい加点文字列を除く
		for (int i = 0; i < sStrings.size(); ++i) {
			ScoredString sString = new ScoredString(sStrings.get(i));
			for (int j = i+1; j < sStrings.size(); ++j) {
				if ( sString.includingStrings(sStrings.get(j)) ) {
					//println("The same chain was removed");
					sStrings.remove(j);
					j--;
				}
			}
		}


		//println(" - END Calculating SStrings -");

		Collections.reverse(sStrings);	//昇順に並び替える
		return sStrings;
	}

}
//PS = Points/Sides = 点･辺データ
//論理演算(AND / OR)・シフト演算(>>> / <<)を使用して判定する

//点・辺レイヤー
//置ける条件判定を行う

class PSLayer {
  int [][] bPS;       //盤上の点･辺
  int [][] bPlayer;   //盤上のプレイヤー番号

  PSLayer (int _size) {
    bPS = new int [_size][_size];
    bPlayer = new int [_size][_size];
    for (int j = 0; j < _size; j++) {
      for (int i = 0; i < _size; i++) {
        bPS[i][j] = 0;
        bPlayer[i][j] = 0;
      }
    }
  }

  //putPS(プレイヤー番号, PS, x座標, y座標)
  //点･辺データをを置く
  public void putPS(int _player, int _ps, int _x, int _y) {
    bPS[_x][_y] = _ps;
    bPlayer[_x][_y] = _player;
  }


  //指定した座標に置ける"可能性があるか"判定する
  //mayPut(プレイヤー番号, x座標, y座標)
  public boolean mayPut(int _player, int _x, int _y) {
    //既に置いてあったらfalse
    if (bPlayer[_x][_y] != 0) {
      return false;
    }
    //周りに文字がない、または周りに指定したプレイヤー番号以外の文字がない場合false
    for (int j = -1; j <= 1; ++j) {
      for (int i = -1; i <= 1; ++i) {
        if (_x+i < 0 || _x+i > bPlayer.length || _y+j < 0 || _y+j > bPlayer[0].length) {
          continue;
        }
        if (bPlayer[_x+i][_y+j] != 0 && bPlayer[_x+i][_y+j] != _player) {
          return true;
        }
      }
    }
    return false;
  }

  //指定した座標に置けるか判定する
  //canPut(プレイヤー番号, PS, x座標, y座標)
  public int canPut(int _player, int _ps, int _x, int _y) {
    //既に置いてあったら
    if (bPlayer[_x][_y] != 0) {
      return 0;
    }

    int current = _ps;  //現在のPS
    int around = getAroundPS(_player, _x, _y);  //周りのPS
    /*
    StringBuilder cs = new StringBuilder("0000000000000000");
    cs.append( Integer.toBinaryString(current) ).delete(0, cs.length()-16).insert(5, ' ').insert(12, ' ');
    StringBuilder as = new StringBuilder("0000000000000000");
    as.append( Integer.toBinaryString(around) ).delete(0, as.length()-16).insert(5, ' ').insert(12, ' ');
    println("current: " + cs.toString() + " | around: " + as.toString());
    */

    int sum = current & around;
    //println("sum" + sum);
    int sides = sum & 0x566A;  //辺だけ取り出す
    //println("sides: " + sides);
    //辺と辺で接している
    if (sides != 0) {
      return -1;
    }
    int points = sum & 0xA995; //点だけ取り出す
    //println("points" + points);
    //相手の点で接してない
    if (points == 0) {
      return -2;
    }
    return 1;
  }

  //指定した座標周りのPSを計算する
  //getAroundPS(プレイヤー番号, x座標, y座標)
  private int getAroundPS(int _player, int _x, int _y) {
    int around = 0; //生成する周りのPS
    int currentPlayer = _player;  //現在のプレイヤー番号
    int [] partPS = new int [8];  //aroundの区画8つ
    int [][] aroundBPS = new int [3][3];  //周りのPS

    for (int j = -1; j <= 1; j++) {
      for (int i = -1; i <= 1; i++) {
        if (_x+i < 0 || _x+i > bPlayer.length || _y+j < 0 || _y+j > bPlayer[0].length) {
          aroundBPS[1+i][1+j] = 0;
          continue;
        }
        //目標座標と現在座標のプレイヤー番号が同じなら
        if (bPlayer[_x+i][_y+j] == currentPlayer) {
          aroundBPS[1+i][1+j] = bPS[_x+i][_y+j] & 0x566A; //辺だけ取り出す(点データを削除)
          //println("same player: " + i + ", " + j);
        } else {
          aroundBPS[1+i][1+j] = bPS[_x+i][_y+j];  //bPSからPSを拾ってくる
        }
      }
    }

    //周りからPSを取得
    partPS[0] = getCenterBottom( aroundBPS[1][0] );
    partPS[1] = getCenterTop( aroundBPS[1][2] );
    partPS[2] = getCenterLeft( aroundBPS[0][1] );
    partPS[3] = getCenterRight( aroundBPS[2][1] );
    partPS[4] = getCorner(0, 0, aroundBPS[0][1], aroundBPS[1][0], aroundBPS[0][0]);
    partPS[5] = getCorner(4, aroundBPS[2][1], 0, aroundBPS[2][0], aroundBPS[1][0]);
    partPS[6] = getCorner(11, aroundBPS[1][2], aroundBPS[0][2], 0, aroundBPS[0][1]);
    partPS[7] = getCorner(15, aroundBPS[2][2], aroundBPS[1][2], aroundBPS[2][1], 0);

    //aroundにpartPSを入れていく
    for (int i = 0; i < partPS.length; i++) {
      around += partPS[i];
    }

    return around;
  }

  //下中央3つ(12/13/14)を取り出す
  private int getCenterBottom(int _ps) {
    return (_ps & 0xE) << 11;
  }

  //上中央3つ(1/2/3)を取り出す
  private int getCenterTop(int _ps) {
    return (_ps & 0x7000) >>> 11;
  }

  //左中央3つ(5/7/9)を取り出す
  private int getCenterLeft(int _ps) {
    return (_ps & 0x2A0) << 1;
  }

  //右中央3つ(6/8/10)を取り出す
  private int getCenterRight(int _ps) {
    return (_ps & 0x540) >>> 1;
  }

  //角(0/4/11/15)を取り出す
  private int getCorner(int _currentCorner, int _ps0, int _ps4, int _ps11, int _ps15) {
    int a = _ps0 & 0x8000;
    int b = (_ps4 & 0x800) << 4;
    int c = (_ps11 & 0x10) << 11;
    int d = (_ps15 & 0x1) << 15;
    return (a | b | c | d) >>> _currentCorner;
  }
}
//加点文字列を比較する



class SStringConparator implements Comparator<ScoredString> {

  public int compare(ScoredString a, ScoredString b) {
    //サイズを取得
    int sizeA = a.size();
    int sizeB = b.size();

    if (sizeA > sizeB) {
      return 1;
    } else if (sizeA == sizeB) {
      return 0;
    } else {
      return -1;
    }
  }

}
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

	public void setValue(T _val) {
		arg = _val;
	}

	public T getValue() {
		return arg;
	}

	//プレイヤー番号をセットする
	public void setPlayer(int _player) {
		player = _player;
	}

	public int getPlayer() {
		return player;
	}

	//加点文字列の種類を返す
	public int getKind() {
		return kind;
	}

	//ルートからの方向を返す
	public int [] getDirInt() {
		int [] d = new int [super.size()];
		for (int i = 0; i < d.length; ++i) {
			d[i] = super.get(i);
		}
		return d;
	}

	//ルート座標を返す
	public int [] getRoot() {
		return root;
	}

	//相対座標を返す(最初はルートの絶対座標)
	public int [][] getRelativePos() {
		int [][] pos = new int [super.size()+1][2];
		pos[0][0] = root[0];
		pos[0][1] = root[1];
		for (int i = 0; i < super.size(); ++i) {
			int n = super.get(i);
			pos[i+1][0] = (int)cos(-0.5f*(n+1)*PI);
			pos[i+1][1] = (int)sin(-0.5f*(n+1)*PI);
		}
		return pos;
	}

	//絶対座標で返す(最初はルートの絶対座標)
	public int [][] getAbsolutePos() {
		int [][] pos = getRelativePos();
		for (int i = 1; i < pos.length; ++i) {
			pos[i][0] += pos[i-1][0];
			pos[i][1] += pos[i-1][1];
		}
		return pos;
	}

	//部分集合または等価な加点文字列か判定する
	//このScoredStringをAとし､引数oをBとしたとき､A⊃BまたはA=Bのときtrueを返す
	public boolean includingStrings(ScoredString o) {
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
//文字データ (文字番号､点･辺､カードの画像)
static class SignData {
  static HashMap <Character, int []> num = new HashMap <Character, int []>();  //文字番号
  static HashMap <Character, Integer> ps = new HashMap <Character, Integer>(); //点･辺
  static HashMap <Character, PImage []> orgCards = new HashMap <Character, PImage []>();  //カードの元画像
  static HashMap <Character, PImage []> cards = new HashMap <Character, PImage []>();//カードの画像

  //ロードした文字数を返す
  static public int getSignSize() {
    return cards.size();
  }

  //文字のSetを返す
  static public Set <Character> getSignSet() {
    return cards.keySet();
  }

  /*
  文字番号を取得する
    getNum(文字のchar, 文字の向き)
  */
  static public int [] getNum(char _ch, int _dir) {
    if (!cards.containsKey(_ch)) {
      return num.get(' ');
    }
    int [] newNum = Arrays.copyOf( num.get(_ch), 4 );
    return rotateNum(newNum, _dir);
  }

  /*
  点･辺データを取得する
    getPS(文字のchar, 文字の向き)
  */
  static public int getPS(char _ch, int _dir) {
    if (!cards.containsKey(_ch)) {
      return ps.get(' ');
    }
    return rotatePS(ps.get(_ch), _dir);
  }

  /*
  画像を取得する
    getImage(プレイヤー番号, 文字のchar, 文字の向き)
  */
  static public PImage getImage(int _player, char _ch) {
    if (!cards.containsKey(_ch) || _ch == ' ' || _player == 0) {
      return cards.get(' ')[0];
    }
    return ( cards.get(_ch)[_player-1] );
  }

  static public void resizeAll(int _size) {
    cards = new HashMap<Character, PImage []>(orgCards);
    for (Map.Entry<Character, PImage []> imgs : orgCards.entrySet()) {
      for (PImage img : imgs.getValue()) {
        img.resize(_size, _size);
      }
    }
  }

  //カードの画像を代入し､文字データをロードする
  static public void load(char _ch, PImage [] _img) {
    switch (_ch) {
      case ' ' :
        putHashMap(' ', 0, 0, 0, 0, 0, _img);
      break;
      case 'A' :
        putHashMap('A', 1, 0, 0, 0, 0x2011, _img);
      break;
      case 'B' :
        putHashMap('B', 2, 0, 0, 0, 0xFD5F, _img);
      break;
      case 'C' :
        putHashMap('C', 3, 21, 0, 0, 0xFD5F, _img);
      break;
      case 'D' :
        putHashMap('D', 4, 0, 0, 0, 0xE5DC, _img);
      break;
      case 'E' :
        putHashMap('E', 5, 0, 0, 0, 0xFDDF, _img);
      break;
      case 'F' :
        putHashMap('F', 6, 0, 0, 0, 0xFD50, _img);
      break;
      case 'G' :
        putHashMap('G', 7, 0, 0, 0, 0xFDFF, _img);
      break;
      case 'H' :
        putHashMap('H', 8, 9, 8, 9, 0x8FF1, _img);
      break;
      case 'I' :
        putHashMap('I', 9, 8, 9, 8, 0xF81F, _img);
      break;
      case 'J' :
        putHashMap('J', 10, 0, 0, 0, 0xBFF, _img);
      break;
      case 'K' :
        putHashMap('K', 11, 0, 0, 0, 0x8D51, _img);
      break;
      case 'L' :
        putHashMap('L', 12, 0, 0, 0, 0x855F, _img);
      break;
      case 'M' :
        putHashMap('M', 13, 0, 23, 0, 0x8FF5, _img);
      break;
      case 'N' :
        putHashMap('N', 14, 26, 14, 26, 0x8FF1, _img);
      break;
      case 'O' :
        putHashMap('O', 15, 15, 15, 15, 0x2184, _img);
      break;
      case 'P' :
        putHashMap('P', 16, 0, 0, 0, 0xFFD0, _img);
      break;
      case 'Q' :
        putHashMap('Q', 17, 0, 0, 0, 0x2185, _img);
      break;
      case 'R' :
        putHashMap('R', 18, 0, 0, 0, 0xFFD1, _img);
      break;
      case 'S' :
        putHashMap('S', 19, 0, 19, 0, 0xFDBF, _img);
      break;
      case 'T' :
        putHashMap('T', 20, 0, 0, 0, 0xF804, _img);
      break;
      case 'U' :
        putHashMap('U', 21, 3, 0, 0, 0x8FFF, _img);
      break;
      case 'V' :
        putHashMap('V', 22, 0, 0, 0, 0x8804, _img);
      break;
      case 'W' :
        putHashMap('W', 23, 0, 13, 0, 0xAFF1, _img);
      break;
      case 'X' :
        putHashMap('X', 24, 24, 24, 24, 0x8811, _img);
      break;
      case 'Y' :
        putHashMap('Y', 25, 0, 0, 0, 0x8804, _img);
      break;
      case 'Z' :
        putHashMap('Z', 26, 14, 26, 14, 0xF81F, _img);
      break;
      case '*' :
        putHashMap('*', 27, 27, 27, 27, 0xA815, _img);
      break;
    }
  }

  //データを代入する
  //  putHashMap(文字, 文字番号(北), (西), (南), (東), 画像)
  static private void putHashMap(char _ch, int _num0, int _num1, int _num2, int _num3, int _ps, PImage [] _img) {
    int [] n = {_num0, _num1, _num2, _num3};
    num.put(_ch, n);
    ps.put(_ch, _ps);
    orgCards.put(_ch, _img);
    cards.put(_ch, _img);
  }

  //文字番号を回転させる
  static private int [] rotateNum(int [] _num, int _dir) {
    for (int i = 0; i < _dir; ++i) {
      int tmp = _num[3];
      for (int j = _num.length-1; j > 0; --j) {
        _num[j] = _num[j-1];
      }
      _num[0] = tmp;
    }

    return _num;
  }

  //点･辺データを回転させる
  static private int rotatePS(int _ps, int _dir) {
    int newPs = 0;

    //回転なし
    if (_dir == 0) {
      newPs = _ps;
    }

    //90度回転 (反時計回り)
    if (_dir == 1 || _dir == 3) {
      for (int i = 0; i < 4; i++) {
        newPs ^= (_ps & (0x800 >>> 2*i)) << 4+i;    //0000 1000 0000 0000
        newPs ^= (_ps & (0x400 >>> 2*i)) >>> 7-i;   //0000 0100 0000 0000
        newPs ^= (_ps & (0x8000 >>> i)) >>> 11-3*i; //1000 0000 0000 0000
        newPs ^= (_ps & (0x8 >>> i)) << 2+3*i;      //0000 0000 0000 1000
      }
      _ps = newPs;
    }
    //180度回転
    if (_dir >= 2) {
      newPs = 0;
      for (int i = 0; i < 8; i++) {
        newPs ^= (_ps & (0x1 << i)) << 15-i*2;
        newPs ^= (_ps & (0x1 << i+8)) >>> 1+i*2;
      }
    }

    return newPs;
  }
}
//ステータスを表示 (プレイヤー別)

class StatusUI {
  int player;  //割り当てられたプレイヤー番号
  int bcX, bcY; //中央上の座標
  int w, h; //ステータス部分のサイズ

  StatusUI (int _player) {
    player = _player;
  }

  //中心座標をセット
  public void setPos(int _x, int _y) {
    bcX = _x;
    bcY = _y;
  }

  //表示領域をセット
  public void setSize(int _w, int _h) {
    w = _w;
    h = _h;
  }

  //場の表示範囲を返す (左上､右下)
  public int [][] getStatusRange() {
    int [] pos1 = { bcX - w/2, bcY - (player == 1 ? 0 : h) };
    int [] pos2 = { bcX + w/2, bcY + (player == 1 ? h : 0) };
    return new int [][] {pos1, pos2};
  }

  //ステータスを描画
  public void draw(int _x, int _y, int _w, int _h) {
    setPos(_x, _y);
    setSize(_w, _h);

    pushMatrix();
    translate(bcX, bcY);

    fill(220, 220, 220, 220);
    strokeWeight(2);
    noStroke();
    if (player == 1){
      rect(-w/2, 0, w, h);
      stroke(0);
      line(-w/2, 0, -w/2, h);
    } else {
      rotate(PI);
      rect(-w/2, 0, w, h);
      stroke(0);
      line(w/2, 0, w/2, h);
    }
    strokeWeight(1);

    int myColor = color(0, 0, 0);
    if (player == 1){
      myColor = color(0, 0, 0);
    } else {
      myColor = color(255, 0, 0);
    }

    fill(myColor);
    textAlign(CENTER);
    textSize(20);
    text(GameData.getPoints(player) + " Point(s)", 0, h*2.0f/3.0f);
    if (GameData.turn == player) {
      textSize(17);
      text(GameData.getCanPutCCSum() + " Move(s)", 0, h-30);
    }

    textSize(25);
    if (GameData.turn >= 1) {
      //ターン表示
      if (GameData.turn == player){
        fill(43, 69, 241);
        text("Your Turn",0, h/3);
      }
    } else if (GameData.turn == 0) {  //ゲームセットなら
      int winner = GameData.winner;
      String message = "";
      //勝敗表示
      if (winner == player) {
        message = "WINNER";
      } else if (winner == 0){
        message = "DRAW";
      } else {
        message = "LOSER";
      }
      fill(myColor);
      text(message, 0, h/3);

      if (player == 1) {
        fill(245, 128, 39);
        textAlign(CENTER, CENTER);
        text("GAME SET",0, 0);
      }
    }

    popMatrix();
  }
}
public void mousePressed() {
  int fieldID = getPointingField(mouseX, mouseY);   //マウスポインタが指し示す場のプレイヤー番号
  int statusID = getPointingStatus(mouseX, mouseY); // 〃 ステータスのプレイヤー番号

  if (GameData.turn >= 1) {
    switch (mouseButton) {
      case LEFT :
        //プレイ中のプレイヤーの場の中ならホールドする
        if (fieldID == GameData.turn) holdingInField();
      break;
      case CENTER :
        //ホールド中のカードを回転させる
        if (holding) rotateHoldingBS();
      break;
    }
  }

  if (mouseButton == LEFT) {
    //盤上なら盤を移動するフラグを立てる
    if (fieldID == 0 && statusID == 0) {
      dragging = 1;
      surface.setCursor(13);
    }
  }
}

public void mouseReleased() {
  int fieldID = getPointingField(mouseX, mouseY);   //マウスポインタが指し示す場のプレイヤー番号
  int statusID = getPointingStatus(mouseX, mouseY); // 〃 ステータスのプレイヤー番号

  //盤をドラッグ中なら解除する
  if (dragging >= 1) {
    dragging = 0;
    surface.setCursor(0);
  }

  //プレイ中でないなら返る
  if (GameData.turn < 1) {
    return;
  }

  switch (mouseButton) {
    case LEFT :
      //場にもステータス上にもない (盤内)
      if (fieldID == 0 && statusID == 0) {
        //ホールド中かつ盤をドラッグ中でないならカードを置く
        if (holding && dragging != 2) putHoldingCard();
      }
    break;

    //右クリックでホールドをキャンセルする
    case RIGHT :
      if (holding) cancelHolding();
    break;
  }
}

public void mouseDragged() {
  //盤を移動
  if (mouseButton == LEFT) {
    moveBoard();
  }
}

public void keyPressed() {
  boolean playing = (GameData.turn >= 1);
  switch (keyCode) {
    case ' ' :        //ホールド中のカードを回転
      if (playing && holding) rotateHoldingBS();
    break;
    case 'P' :        //場からランダムにホールドする
      if (playing) holdingRandom();
    break;
    case 'A' :        //オートモードの切り替え
      if (playing) autoMode *= -1;
    break;
    case 'C' :        //盤を中心に戻す
      boardUI.movePos();
      println("Return at the Board Center");
    break;
    case 'R' :        //ランダムにカードを置く
      if (playing) putCardRandom();
    break;
    case 'G' :        //画面を録画する(GIFアニメの生成)
      turnMakingGIF();
    break;
    case 'S' :        //スクリーンショットを撮る
      shotScreen();
    break;
    case 'M' :
      saveResult("result" + gameRound);
    break;
    case DELETE :     //リロードする
      if (!holding) reload();
    break;
    case BACKSPACE :  //盤面をクリアにする
      if (!holding) clear();
    break;
  }
}
//初期配置のカードを置く
public void putInitialCards() {
  BoardSign [] bs = {
    new BoardSign(1, 'H', 0, -1, -1),
    new BoardSign(2, 'H', 1, 1, -1),
    new BoardSign(1, 'I', 1, 1, 1),
    new BoardSign(2, 'I', 0, -1, 1)
  };
  for (int i = 0; i < bs.length; i++) {
    board.putCard(bs[i]);
  }
}

//ホールド中のカードを置いてターンチェンジ
public void putHoldingCard() {
  //マウスポインタが指している左上座標系の座標
  int [] pos = boardUI.convertBoardPos( new int [] {mouseX, mouseY} );
  int [][] canPutPos = GameData.getCanPutPos(holdingBS); //置ける座標
  boolean hit = false;  //置ける座標に置こうとしているか

  //置ける座標からマウスポインタが指している座標を検索
  for (int i = 0; i < canPutPos.length; i++) {
    if (canPutPos[i][0] == pos[0] && canPutPos[i][1] == pos[1]) {
      hit = true;
      break;
    }
  }
  if (!hit) return;

  holdingBS.pos = pos;  //ホールド中のBSに座標を代入
  holding = false;

  if ( !board.putCard(holdingBS) ) {
    println("Failed to Put a Card");
  }

  int next = board.turnChange();  //次のターンのプレイヤー番号
  if (next != 0) {
    println("Next Turn: " + next);
    println("All Moves: " + GameData.getCanPutCCSum());
  } else {
    println("GAME SET");
  }
}

//指定プレイヤーがカードをホールドする
public void holdingInField(){
  int player = GameData.turn; //現在のターン
  int [] pos = fieldUI[player-1].convertBoardPos( new int []{mouseX, mouseY} ); //左上座標系の座標
  ArrayList <BoardSign> bsList = new ArrayList<BoardSign> ( GameData.getFieldSigns() ); //場にあるBS

  for (BoardSign bs : bsList){
    //マウスポインタが指す座標と場の座標が一致したら
    if ( bs.equalsPos(player, pos[0], pos[1]) ){
      //ホールド中は持つカードを切り替える (ホールド中のカードを場に戻す)
      if (holding) {
        GameData.addFieldSign(holdingBS);
      }
      holdingBS = new BoardSign(bs);  //場のBSをholdingBSにコピー
      GameData.removeFieldSign(bs);   //場からBSを削除
      holding = true;
      println("Player " + GameData.turn + " held " + holdingBS.getChar() + ":" + holdingBS.getDir() + "\" at (" + holdingBS.getX() + ", " + holdingBS.getY() + ")");
    }
  }
}

//ホールドをキャンセルする
public void cancelHolding() {
  GameData.addFieldSign(holdingBS); //場にholdingBSを戻す
  holding = false;
}

//ホールド中のカードを描画
//引数: 描画するカードの中心座標
public void drawHoldingBS(int _centerX, int _centerY) {
  PImage img = SignData.getImage( holdingBS.getPlayer(), holdingBS.getChar() );
  imageMode(CENTER);
  pushMatrix();
  translate(_centerX, _centerY);
  rotate( -holdingBS.getDir()*PI*0.5f );
  image(img, 0, 0, GameData.cardSize, GameData.cardSize);
  popMatrix();
}

//ホールド中のカードを回転する (反時計回り)
public void rotateHoldingBS() {
  holdingBS.dir = ( holdingBS.getDir() + 1 ) % 4;
  println("Rotate Holding Card");
}

//盤を動かす
public void moveBoard() {
  if (dragging != 0) {
    //現フレームと前フレームのマウスポインタ座標の差分
    int dx = mouseX - pmouseX;
    int dy = mouseY - pmouseY;
    if (dx != 0 || dy != 0) {
      boardUI.movePos(dx, dy);  //差分だけ盤を動かす
      dragging = 2;
    }
  }
}

//指定した座標がどの場の上にあるかを返す
//返り値: 0:該当なし 1以降:場のプレイヤー番号
public int getPointingField(int _x, int _y) {
  int id = 0;
  int [][] f1 = fieldUI[0].getFieldRange(); //1Pの場の表示範囲
  int [][] f2 = fieldUI[1].getFieldRange(); //2Pの 〃
  if (f1[0][0] < _x && f1[1][0] > _x && f1[0][1] < _y && f1[1][1] > _y) {
    id = 1;
  }
  if (f2[0][0] < _x && f2[1][0] > _x && f2[0][1] < _y && f2[1][1] > _y) {
    id = 2;
  }
  return id;
}

//指定した座標がどのステータス表示上にあるかを返す
//返り値: 0:該当なし 1以降:場のプレイヤー番号
public int getPointingStatus(int _x, int _y) {
  int id = 0;
  int [][] s1 = statusUI[0].getStatusRange(); //1Pのステータスの表示範囲
  int [][] s2 = statusUI[1].getStatusRange(); //2Pの 〃
  if (s1[0][0] < _x && s1[1][0] > _x && s1[0][1] < _y && s1[1][1] > _y) {
    id = 1;
  }
  if (s2[0][0] < _x && s2[1][0] > _x && s2[0][1] < _y && s2[1][1] > _y) {
    id = 2;
  }
  return id;
}

//ローディング画面の表示
public void drawLoading() {
  background(255);
  fill(0xff559CF7);
  textAlign(CENTER, CENTER);
  text("LOADING...", width/2, height/2);
  textAlign(RIGHT, DOWN);
  fill(0);
  textSize(20);
  text(VERSION, width-20, height-20);
}

//データベースに初期の場の文字を追加
public void addInitialFieldSigns(char [] _fieldSigns) {
  int cnt = 0;
  int dir;

  for (char ch : _fieldSigns) {
    for (int i = 0; i < GameData.PLAYERS; i++) {
      dir = (i+1 == 1) ? 0 : 2;
      BoardSign fs = new BoardSign(i+1, ch, dir, cnt%GameData.getFieldRowNum() + 1, cnt/GameData.getFieldRowNum() + 1);
      GameData.addFieldSign(fs);
    }
    cnt++;
  }
}

/*
文字のデータをロードする (必ず他のインスタンス化より先に行うこと)
  loadSignData(ロードする画像の入ったパス[色別], 場に置く文字の配列)
指定した場に置く文字の画像のみロードし、
charとPImageのセットをSignDataに送る (SignData内でそのほかの文字データがロードされる)
*/
public void loadSignData(String [] _path, char [] _fieldSigns) {
  println("Loading SignData: ");

  //HashSetを利用して場の文字から重複を削除する
  Set <Character> signs = new HashSet <Character>();
  signs.add(' ');
  for (char ch : _fieldSigns) {
    signs.add(ch);
  }

  for (char ch : signs) {
    PImage [] img = new PImage[GameData.PLAYERS];
    print("\'" + String.valueOf(ch) + "\' ");
    char newCh = ch;
    //ファイル名に使用できない文字を変換する
    if (ch == ' ') {
      newCh = '@';
    } else if (ch == '*') {
      newCh = '＊';
    }

    for (int i = 0; i < GameData.PLAYERS; i++) {
      img[i] = loadImage(_path[i] + "/" + newCh + ".png"); //画像をロードする
    }

    SignData.load(ch, img); //SignDataに画像を代入
  }
  print("\n");
}

public void clear() {
  GameData.turn = -1;
  GameData.clear();
  gameRound++;
  println("RESET");
}

public void reload() {
  GameData.turn = -3;
  GameData.clear();
  println("RESET ALL");
}
//場からランダムにホールドする
public void holdingRandom() {
  ArrayList <BoardSign> bsList = new ArrayList<BoardSign> ( GameData.getFieldSigns() ); //場にあるBS
  if (bsList.size() == 0) return;
  BoardSign bs = bsList.get( (int)random(bsList.size()) );  //場のBSからランダムに選ぶ

  if (holding) {
    GameData.addFieldSign(holdingBS);
  }
  holdingBS = new BoardSign(bs);  //holdingBSにコピー
  GameData.removeFieldSign(bs);   //場からBSを削除
  holding = true;
  println("Player " + GameData.turn + " held " + holdingBS.getChar() + ":" + holdingBS.getDir() + "\" at (" + holdingBS.getX() + ", " + holdingBS.getY() + ") at random");
}

//ランダムにカードを置いてターンチェンジ
public void putCardRandom() {
  if (holding) return;  //ホールド中は置けない

  if ( board.putCardRandom() ) {
  } else {
    println("Failed to Put a Card");
  }

  int next = board.turnChange();  //次のターンのプレイヤー番号
  if (next != 0) {
    println("Next Turn: " + next);
  } else {
    println("GAME SET");
  }
}

//自動でカードを置いてターンチェンジ
public void autoPut() {
  if (GameData.turn >= 1 && !holding) {
    if (!board.putCardRandom()) {
      println("Failed to Put a Card");
    }
    if (board.turnChange() == 0) {
      println("GAME SET");
      if (autoMode == 2) saveResult("result" + gameRound);
      clear();
    }
  }
}

//GIFアニメの録画の切り替え
public void turnMakingGIF() {
  if (recoding) {
    gifMaker.finish();
    recoding = false;
    recodeTimes++;
    println("Finish Recoding");
  } else {
    gifMaker = new GifMaker(this, "animation_" + recodeTimes + ".gif");
    gifMaker.setRepeat(0);  //GIFを無限ループさせる
    gifMaker.setDelay(2);   //ウェイトを設ける
    recoding = true;
    println("Start Recoding...");
  }
}

//スクリーンショットを撮る
public void shotScreen() {
  save("screenshot_" + shotTimes + ".png");
  shotTimes++;
  println("Captured the Screen");
}

//対局結果をエクスポート
public void saveResult(String _filename) {
  PrintWriter output = createWriter(_filename + ".csv");
  for (BoardSign bs : GameData.getBoardSigns()) {
    output.println(bs.getPlayer() + "," + bs.getChar() + "," + bs.getDir() + "," + bs.getX() + "," + bs.getY());
  }
  output.println("");
  for (int i = 0; i < GameData.PLAYERS; i++) {
    output.println( i+1 + "," + GameData.getPoints(i+1) );
  }
  output.flush();
  output.close();
  println("Save Result");
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "HIchain" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
