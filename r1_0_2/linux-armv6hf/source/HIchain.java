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



final String VERSION = "r1.0.2";
final String [] IMAGEPATH = { //\u30ab\u30fc\u30c9\u306e\u753b\u50cf\u306e\u30d1\u30b9
  "cards\\D", "cards\\R"
};
final char [] INITFIELDSIGNS = {  //\u6700\u521d\u306b\u5834\u306b\u7f6e\u304f\u6587\u5b57
  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '*'
};
int sSize = 684;  //\u753b\u9762\u30b5\u30a4\u30ba (\u6b63\u65b9\u5f62)

//\u30af\u30e9\u30b9
Board board;          //\u76e4(\u30eb\u30fc\u30eb\u90e8\u5206)
BoardUI boardUI;      //\u76e4\u306eUI
FieldUI [] fieldUI;   //\u5834\u306eUI (\u30d7\u30ec\u30a4\u30e4\u30fc\u5225)
StatusUI [] statusUI; //\u30b9\u30c6\u30fc\u30bf\u30b9\u306eUI (\u30d7\u30ec\u30a4\u30e4\u30fc\u5225)

//\u30db\u30fc\u30eb\u30c9\uff65\u30c9\u30e9\u30c3\u30b0\u95a2\u9023
BoardSign holdingBS;  //\u30db\u30fc\u30eb\u30c9\u4e2d\u306eBoardSign
boolean holding;      //\u30db\u30fc\u30eb\u30c9\u4e2d\u304b
int dragging;         //\u76e4\u304c\u30c9\u30e9\u30c3\u30b0\u4e2d\u304b (0:\u30c9\u30e9\u30c3\u30b0\u4e2d\u3067\u306a\u3044 1:\u76e4\u3092\u30af\u30ea\u30c3\u30af\u3057\u305f 2:\u76e4\u3092\u30c9\u30e9\u30c3\u30b0\u4e2d)

//GIF\u30a2\u30cb\u30e1\uff65\u30b9\u30af\u30ea\u30fc\u30f3\u30b7\u30e7\u30c3\u30c8\u95a2\u9023
GifMaker gifMaker;        //GIF\u30a2\u30cb\u30e1\u30e1\u30fc\u30ab\u30fc
boolean recoding = false; //GIF\u30a2\u30cb\u30e1\u9332\u753b\u4e2d\u304b
int recodeTimes = 0;      //\u9332\u753b\u56de\u6570 (\u30d5\u30a1\u30a4\u30eb\u540d\u306b\u4f7f\u7528)
int shotTimes = 0;        //\u30b9\u30af\u30ea\u30fc\u30f3\u30b7\u30e7\u30c3\u30c8\u56de\u6570 (\u30d5\u30a1\u30a4\u30eb\u540d\u306b\u4f7f\u7528)

boolean autoMode = false; //\u30aa\u30fc\u30c8\u30e2\u30fc\u30c9 (\u81ea\u52d5\u3067\u30ab\u30fc\u30c9\u3092\u7f6e\u304f)
int gameRound = 1;        //\u8a66\u5408\u56de\u6570

public void settings() {
  size(sSize, sSize);
}

public void setup() {
  
  PFont font = createFont("Arial", 48, true);
  textFont(font, 25);
  surface.setTitle("HIchain Prototype " + VERSION);
  //surface.setResizable(true);
}

public void draw() {
  switch (GameData.turn) {
    case -3 : //\u30ed\u30fc\u30c7\u30a3\u30f3\u30b0\u753b\u9762\u8868\u793a\u4e2d
      drawLoading();  //\u30ed\u30fc\u30c7\u30a3\u30f3\u30b0\u753b\u9762\u306e\u8868\u793a
      println("Loading...");
      GameData.turn = -2;
    break;
    case -2 : //\u30ed\u30fc\u30c7\u30a3\u30f3\u30b0\u4e2d
      loadSignData(IMAGEPATH, INITFIELDSIGNS);   //\u6587\u5b57\u30c7\u30fc\u30bf\u3092\u30ed\u30fc\u30c9\u3059\u308b (SignData\u30af\u30e9\u30b9\u306b\u683c\u7d0d)
      GameData.turn = -1;
    break;
    case -1 : //\u521d\u671f\u5316\u4e2d
      initialize(INITFIELDSIGNS); //\u521d\u671f\u5316
      GameData.turn = 2;  //\u4eee\u306b\u73fe\u5728\u306e\u30bf\u30fc\u30f3\u3092\u30bb\u30c3\u30c8\u3059\u308b (2P\u304b\u30891P\u306b\u30bf\u30fc\u30f3\u30c1\u30a7\u30f3\u30b8)
      board.turnChange(); //\u30bf\u30fc\u30f3\u30c1\u30a7\u30f3\u30b8 (\u30b2\u30fc\u30e0\u958b\u59cb)
      println("CARDSIZE: " + GameData.cardSize);
    break;
    default :
      int cardSize = GameData.cardSize;        //\u30ab\u30fc\u30c9\u30b5\u30a4\u30ba
      int rowNum = GameData.getFieldRowNum();  //\u5834\u306e\u5217\u6570

      background(255);
      if (holding) {
        boardUI.draw(holdingBS);  //\u7f6e\u3051\u308b\u5ea7\u6a19\u3092\u30cf\u30a4\u30e9\u30a4\u30c8\u3042\u308a
      } else {
        boardUI.draw();
      }
      fieldUI[0].draw(0, height - cardSize*2);      //1P\u306e\u5834
      fieldUI[1].draw(rowNum*cardSize, cardSize*2); //2P\u306e\u5834
      if (holding) drawHoldingBS(mouseX, mouseY);   //\u30db\u30fc\u30eb\u30c9\u4e2d\u306e\u30ab\u30fc\u30c9
      statusUI[1].draw( (width + rowNum*cardSize)/2, height/2, (width - rowNum*cardSize), height/2 ); //2P\u306e\u30b9\u30c6\u30fc\u30bf\u30b9
      statusUI[0].draw( (width + rowNum*cardSize)/2, height/2, width - rowNum*cardSize, height/2 );   //1P\u306e\u30b9\u30c6\u30fc\u30bf\u30b9
    break;
  }
  if (autoMode) autoPut(true);  //\u81ea\u52d5\u3067\u7f6e\u304f
  //\u9332\u753b\u4e2d\u306e\u5834\u5408
  if (recoding) {
    //\u30de\u30a6\u30b9\u3092\u52d5\u304b\u3057\u3066\u3044\u305f\u3089\u30d5\u30ec\u30fc\u30e0\u3092\u8ffd\u52a0
    if (mouseX != pmouseX || mouseY != pmouseY) {
      gifMaker.addFrame();
      println("ADD FRAME: " + frameCount);
    }
  }
}

//\u521d\u671f\u5316
public void initialize(char [] _fieldSigns) {
  int rowNum = _fieldSigns.length/2;  //\u5834\u306e\u5217\u6570
  if (rowNum % 2 == 1) rowNum++;
  int lineNum = _fieldSigns.length/rowNum + 1; //\u5834\u306e\u884c\u6570
  int cardSize = height/(rowNum+4);           //\u30ab\u30fc\u30c9\u30b5\u30a4\u30ba

  GameData.init(cardSize, rowNum);    //\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u3092\u521d\u671f\u5316 (GameData\u30af\u30e9\u30b9\u306b\u5834\u306e\u6587\u5b57\u3092\u683c\u7d0d)
  addInitialFieldSigns(_fieldSigns);  //\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u521d\u671f\u306e\u5834\u306e\u6587\u5b57\u3092\u8ffd\u52a0
  SignData.resizeAll(cardSize);       //\u30ab\u30fc\u30c9\u306e\u753b\u50cf\u3092\u30ea\u30b5\u30a4\u30ba

  int players = GameData.PLAYERS;  //\u30d7\u30ec\u30a4\u30e4\u30fc\u6570

  board = new Board("HHHHH");
  fieldUI = new FieldUI[players];
  fieldUI[0] = new FieldUI(1, rowNum, lineNum);
  fieldUI[1] = new FieldUI(2, rowNum, lineNum);
  boardUI = new BoardUI( rowNum*cardSize/2, height/2, board.getBoardSize(), board.getBoardSize() );
  statusUI = new StatusUI[players];
  statusUI[0] = new StatusUI(1);
  statusUI[1] = new StatusUI(2);

  putInitialCards();  //\u76e4\u306b\u521d\u671f\u914d\u7f6e\u3092\u7f6e\u304f

  println("Done Initializing");
}
//\u76e4 (\u30eb\u30fc\u30eb\u90e8\u5206) (\u6709\u9650\u76e4)

class Board {
	int boardSize;			//\u76e4\u306e\u30b5\u30a4\u30ba (\u884c\u6570=\u5217\u6570)
	int [][] boardRange = new int [2][2];	//\u4f7f\u7528\u3057\u3066\u3044\u308b\u76e4\u306e\u7bc4\u56f2
	String godString;	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217 (CHAIN\u3067\u52dd\u5229)

	boolean [][] exist;				//\u7f6e\u3044\u3066\u3042\u308b\u304b\u3069\u3046\u304b
	NumLayers [] numLayers;		//\u6587\u5b57\u756a\u53f7\u30ec\u30a4\u30e4\u30fc (\u30d7\u30ec\u30a4\u30e4\u30fc\u3054\u3068\uff65\u30a4\u30f3\u30c7\u30c3\u30af\u30b9\u304c0\u304b\u3089\u59cb\u307e\u308b\u306e\u3067\u6ce8\u610f)
	PSLayer psLayer;					//\u70b9\uff65\u8fba\u30ec\u30a4\u30e4\u30fc (\u30d7\u30ec\u30a4\u30e4\u30fc\u6df7\u540c)

	//Board(\u30d7\u30ec\u30a4\u30e4\u30fc\u6570, \u5148\u884c\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7)
	//\u76e4\u306e\u5927\u304d\u3055\u306f\u4f7f\u7528\u3059\u308b\u6587\u5b57\u6570\u306b\u3088\u3063\u3066\u6c7a\u5b9a
	Board(String _godString) {
		godString = _godString;
		boardSize = (GameData.getFieldSignNum()+GameData.PLAYERS/2)*2;	//\u4f7f\u7528\u3059\u308b\u6587\u5b57\u6570\u306e\u500d\u304c\u76e4\u306e\u30b5\u30a4\u30ba
		boardRange[0][0] = boardSize/2-2;
		boardRange[0][1] = boardRange[0][0];
		boardRange[1][0] = boardSize/2+1;
		boardRange[1][1] = boardRange[1][0];

		exist = new boolean[boardSize][boardSize];
		numLayers = new NumLayers[GameData.PLAYERS];
		psLayer = new PSLayer(boardSize);

		//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304b\u30891\u6587\u5b57\u305a\u3064\u53d6\u308a\u51fa\u3057\u3066\u6587\u5b57\u756a\u53f7\u306b\u5909\u63db\u3059\u308b
		char [] ch = godString.toCharArray();
		int [] num = new int [ch.length];
		for (int i = 0; i < num.length; i++) {
			num[i] = SignData.getNum(ch[i], 0)[0];
		}

		for (int i = 0; i < GameData.PLAYERS; ++i) {
			numLayers[i] = new NumLayers(boardSize, num);
		}
	}

	//\u76e4\u306e\u30b5\u30a4\u30ba\u3092\u8fd4\u3059
	public int getBoardSize() {
		return boardSize;
	}

	//\u76e4\u306e\u4f7f\u7528\u3057\u3066\u3044\u308b\u7bc4\u56f2\u3092\u8fd4\u3059
	public int [][] getBoardRange() {
		return boardRange;
	}

	//\u6307\u5b9a\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u30dd\u30a4\u30f3\u30c8\u3092\u8a08\u7b97\u3057\u3066\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u30bb\u30c3\u30c8
	public void setPoints(int _player) {
		GameData.setPoints( _player, numLayers[_player-1].calPoints() );
	}

	//\u30d7\u30ec\u30a4\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u30dd\u30a4\u30f3\u30c8\u3092\u8a08\u7b97\u3057\u3066\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u30bb\u30c3\u30c8
	public void setPoints() {
		setPoints(GameData.turn);
	}

	//\u6307\u5b9a\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u52a0\u70b9\u6587\u5b57\u5217\u3092\u8a08\u7b97\u3057\u3066\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u8ffd\u52a0
	public void addSStrings(int _player) {
		ArrayList <ScoredString> ssList = new ArrayList <ScoredString> ( numLayers[_player-1].calScoredStrings(boardRange) );
		for (int i = 0; i < ssList.size(); i++) {
			ssList.get(i).setPlayer(_player);
		}
		GameData.addSStrings(_player, ssList);
	}

	//\u30d7\u30ec\u30a4\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u52a0\u70b9\u6587\u5b57\u5217\u3092\u8a08\u7b97\u3057\u3066\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u8ffd\u52a0
	public void addSStrings() {
		addSStrings(GameData.turn);
	}

	/*
	\u30ab\u30fc\u30c9\u3092\u7f6e\u304f\u30fb\u7f6e\u3051\u308b\u304b\u5224\u5b9a\u3059\u308b\uff65\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u8ffd\u52a0\u3059\u308b
		putCard(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, \u6587\u5b57, \u6587\u5b57\u306e\u5411\u304d, x\u5ea7\u6a19, y\u5ea7\u6a19)
	\u65e2\u306b\u7f6e\u3044\u3066\u3042\u308b\u5ea7\u6a19\u306b\u7f6e\u304f\u5834\u5408\u3084\u4e0d\u6b63\u306a\u5ea7\u6a19\u306e\u5834\u5408false\u3092\u8fd4\u3059\u3001\u7f6e\u3051\u308b\u6761\u4ef6\u5224\u5b9a\u306fcanPut()\u3067\u884c\u3046
	\u5ea7\u6a19\u306f\u4e2d\u5fc3\u5ea7\u6a19\u7cfb\u3067\u8a18\u8ff0\u3059\u308b (\u521d\u671f\u914d\u7f6e\u3092\u4e2d\u5fc3\u3068\u3059\u308b)
	\u8fd4\u308a\u5024; \u7f6e\u3051\u308b\u304b\u3069\u3046\u304b (false\u306a\u3089\u30a8\u30e9\u30fc)
	*/
	public boolean putCard(int _player, char _ch, int _dir, int _x, int _y) {
		int [] num = SignData.getNum(_ch, _dir);	//\u6587\u5b57\u756a\u53f7
		int ps = SignData.getPS(_ch, _dir);				//\u70b9\uff65\u8fba\u30c7\u30fc\u30bf (PS: Points/Sides)
		int [] newPos = convertUpperLeftFrame( new int []{_x, _y} );	//\u4e2d\u5fc3\u5ea7\u6a19\u7cfb\u304b\u3089\u5de6\u4e0a\u5ea7\u6a19\u7cfb\u306b\u5909\u63db

		//x\u304by\u304c0\u307e\u305f\u306f\u76e4\u5916\u306a\u3089false\u3092\u8fd4\u3059
		if (_x == 0 || _y == 0 || abs(_x) > boardSize/2 || abs(_y) > boardSize/2) {
			println("!Wrong Position");
			return false;
		}
		//\u65e2\u306b\u7f6e\u304b\u308c\u3066\u3044\u308b\u5ea7\u6a19\u306a\u3089false\u3092\u8fd4\u3059
		if (exist[newPos[0]][newPos[1]]) {
			println("!The Position has already existed a card");
			return false;
		}

		//\u4f7f\u7528\u4e2d\u306e\u76e4\u306e\u7bc4\u56f2\u5916\u306a\u3089\u62e1\u5f35
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

    numLayers[_player-1].putSignNum(num, newPos[0], newPos[1]);	//\u6587\u5b57\u756a\u53f7\u306e\u4ee3\u5165
    psLayer.putPS(_player, ps, newPos[0], newPos[1]);	//\u70b9\uff65\u8fba\u30c7\u30fc\u30bf\u306e\u4ee3\u5165

		exist[newPos[0]][newPos[1]] = true;

		//\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u8ffd\u52a0
		GameData.addBoardSign( new BoardSign(_player, _ch, _dir, _x, _y) );

		return true;
	}

	//\u4e0a\u306eputCard\u306eBoardSign\u7248
	public boolean putCard(BoardSign _bs) {
		return putCard(_bs.getPlayer(), _bs.getChar(), _bs.getDir(), _bs.getX(), _bs.getY());
	}

	//\u30e9\u30f3\u30c0\u30e0\u306b\u30ab\u30fc\u30c9\u30921\u3064\u7f6e\u304f
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
	\u6b21\u306e\u30bf\u30fc\u30f3\u3092\u8fd4\u3059
		turnChange(\u5834\u306b\u3042\u308bBoardSign\u306e\u30ea\u30b9\u30c8)
	\u8fd4\u308a\u5024: \u6b21\u306e\u30bf\u30fc\u30f3\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7 (\u6b21\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u304c\u7f6e\u3051\u306a\u3044\u5834\u5408\u306f0\u304c\u8fd4\u308b \u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u3092\u63c3\u3048\u305f\u3089-1)
	*/
	private int turnChange(int _cnt) {
		if (_cnt > GameData.PLAYERS) {
			GameData.turn = 0;
			return 0;
		}

		int currentTurn = GameData.turn;			//\u73fe\u5728\u306e\u30bf\u30fc\u30f3
		int nextTurn = currentTurn%GameData.PLAYERS + 1;	//\u6b21\u306e\u30bf\u30fc\u30f3

		//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u3092\u63c3\u3048\u3066\u3044\u305f\u306a\u3089\u30b2\u30fc\u30e0\u30bb\u30c3\u30c8
		if (numLayers[currentTurn-1].hitGodString()) {
			GameData.winner = currentTurn;
			return -1;
		}

		addSStrings();	//\u52a0\u70b9\u6587\u5b57\u5217\u3092\u8a08\u7b97\u3057\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u8ffd\u52a0
		setPoints();		//\u30dd\u30a4\u30f3\u30c8\u3092\u8a08\u7b97\u3057\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u8ffd\u52a0
		GameData.turn = nextTurn;

		//BoardSign\u304b\u3089\u6587\u5b57\u3092\u53d6\u5f97\u3057\u30ea\u30b9\u30c8\u306b\u5165\u308c\u308b
		ArrayList <Character> ch = new ArrayList<Character>();
		ArrayList <BoardSign> fs = GameData.getFieldSigns(nextTurn);
		for (BoardSign bs : fs) {
			ch.add(bs.getChar());
		}

		//\u6b21\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u304c\u7f6e\u3051\u308b\u5ea7\u6a19\u3092\u8a08\u7b97\u3057\uff64\u3042\u308c\u3070\u6b21\u306e\u30bf\u30fc\u30f3\u3078
		ArrayList <BoardSign> bs = canPutAll(nextTurn);
		if (bs.size() == 0){
			return turnChange(++_cnt);
		}
		GameData.setCanPutCC(bs);	//\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u30bb\u30c3\u30c8

		return nextTurn;
	}

	public int turnChange() {
		int turn = turnChange(1);
		return turn;
	}

	/*
	\u6307\u5b9a\u3057\u305f\u7bc4\u56f2\u3067\u6307\u5b9a\u3057\u305f\u30d7\u30ec\u30a4\u30e4\u30fc\u304c\u7f6e\u3051\u308b\u53ef\u80fd\u6027\u304c\u3042\u308b\u5ea7\u6a19\u3092\u8fd4\u3059 (\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0\u306b\u3064\u3044\u3066\u306fcanPutAll\u3067)
		getMayPutPos(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, \u76e4\u306e\u7bc4\u56f2)
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
	\u6307\u5b9a\u3057\u305f\u7bc4\u56f2\u306e\u5ea7\u6a19\u306e\u4e2d\u3067\u7f6e\u3051\u305d\u3046\u306a\u5ea7\u6a19\u3092\u4e88\u3081\u8abf\u3079\u3066\u304b\u3089\u3001\u5834\u306b\u3042\u308b\u30ab\u30fc\u30c9\u304c\u7f6e\u3051\u308b\u7d44\u307f\u5408\u308f\u305b\u3092\u8abf\u3079\u308b
		canPutAll(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, \u76e4\u306e\u7bc4\u56f2)
	\u4e88\u3081\u7701\u304f\u5ea7\u6a19: \u65e2\u306b\u7f6e\u3044\u3066\u3042\u308b\u3001\u5468\u308a\u306b\u6587\u5b57\u304c\u306a\u3044\u3001\u5468\u308a\u306b\u7f6e\u304f\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u4ee5\u5916\u306e\u6587\u5b57\u304c\u306a\u3044
	\u5834\u306b\u30ab\u30fc\u30c9\u304c\u306a\u3044\u5834\u5408\u3082false\u304c\u8fd4\u308b
	*/
	private ArrayList <BoardSign> canPutAll(int _player) {
		ArrayList <BoardSign> canPutCC = new ArrayList <BoardSign>();	//\u7f6e\u3051\u308b\u7d44\u307f\u5408\u308f\u305b (BoardSign)
		int [][] mayPutPos = getMayPutPos(_player, boardRange);	//\u7f6e\u3051\u308b\u53ef\u80fd\u6027\u304c\u3042\u308b\u5ea7\u6a19
		ArrayList <BoardSign> fsList = GameData.getFieldSigns(GameData.turn);

		for (BoardSign fs : fsList) {	//\u5834\u306b\u3042\u308b\u6587\u5b57
			for (int j = 0; j < 4; ++j) {	//\u65b9\u5411: 0-3
				for (int [] pos : mayPutPos) {	//\u7f6e\u3051\u308b\u53ef\u80fd\u6027\u304c\u3042\u308b\u5ea7\u6a19
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
	\u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u306b\u7f6e\u3051\u308b\u304b\u3069\u3046\u304b\u5224\u5b9a\u3059\u308b (\u70b9\u30fb\u8fba\u30c7\u30fc\u30bf\u304b\u3089\u7f6e\u3051\u308b\u6761\u4ef6\u5224\u5b9a\u3092\u884c\u3046)
	(PSLayer\u306ecanPut\u306bprintln\u3092\u8ffd\u52a0\u3057\u3066\u3044\u308b\u3060\u3051)
		canPut(BoardSign)
	\u8fd4\u308a\u5024: \u7f6e\u3051\u308b\u304b\u3069\u3046\u304b
	*/
	private boolean canPut(BoardSign _bs) {
    int ps = SignData.getPS(_bs.getChar(), _bs.getDir());	//PS
		int [] pos = convertUpperLeftFrame( _bs.getPos() );	//\u5ea7\u6a19(\u5de6\u4e0a\u5ea7\u6a19\u7cfb)
    int i = psLayer.canPut(_bs.getPlayer(), ps, pos[0], pos[1]);	//\u7f6e\u3051\u308b\u304b\u5224\u5b9a
    boolean b = (i == 1);
		/*
		pos[0] -= (pos[0] <= boardSize/2-1) ? boardSize/2 : boardSize/2 - 1;
		pos[1] -= (pos[1] <= boardSize/2-1) ? boardSize/2 : boardSize/2 - 1;
    print(_bs.getPlayer() + "P can put \'" + _bs.getChar() + ":" + _bs.getDir() + "\' at (" + pos[0] + ", " + pos[1] + "): " + b);

    switch(i) {
      case 1:	//\u7f6e\u3051\u308b
				print("\n");
        break;
      case 0:	//\u65e2\u306b\u7f6e\u304b\u308c\u3066\u3044\u308b\u306e\u3067\u7f6e\u3051\u306a\u3044
        println(" (Already Exist)");
        break;
      case -1:	//\u8fba\u3068\u8fba\u304c\u63a5\u3057\u3066\u3044\u308b\u306e\u3067\u7f6e\u3051\u306a\u3044
        println(" (The sides come in contact with the opposite sides)");
        break;
      case -2:	//\u76f8\u624b\u306e\u70b9\u3068\u81ea\u5206\u306e\u70b9\u304c1\u3064\u3082\u63a5\u3057\u3066\u3044\u306a\u3044\u306e\u3067\u7f6e\u3051\u306a\u3044
        println(" (The points don't come in contact with the opposite sides of the opponent)");
        break;
      default:
        break;
    }

    //println("ps: " + Integer.toBinaryString(ps));
		*/
    return b;
  }

	//\u4e2d\u5fc3\u5ea7\u6a19\u7cfb\u304b\u3089\u5de6\u4e0a\u5ea7\u6a19\u7cfb\u306b\u5909\u63db\u3059\u308b
	private int [] convertUpperLeftFrame(int [] _pos) {
		int [] newPos = Arrays.copyOf(_pos, 2);
		newPos[0] += (_pos[0] < 0) ? boardSize/2 : boardSize/2 - 1;
		newPos[1] += (_pos[1] < 0) ? boardSize/2 : boardSize/2 - 1;
		return newPos;
	}

	//\u5de6\u4e0a\u5ea7\u6a19\u7cfb\u304b\u3089\u4e2d\u5fc3\u5ea7\u6a19\u7cfb\u306b\u5909\u63db\u3059\u308b
	private int [] convertCenterFrame(int [] _pos) {
		int [] newPos = Arrays.copyOf(_pos, 2);
		newPos[0] += - getBoardSize()/2 + ( (_pos[0] < getBoardSize()/2) ? 0 : 1 );
		newPos[1] += - getBoardSize()/2 + ( (_pos[1] < getBoardSize()/2) ? 0 : 1 );
		return newPos;
	}
}
//\u6587\u5b57\u306echar\u3001\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u3001\u6587\u5b57\u306e\u5411\u304d\u3001\u5ea7\u6a19\u3092\u4fdd\u6301\u3059\u308b (\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u306f\u7701\u7565\u53ef)
//UI\u306b\u8fd1\u3044\u30af\u30e9\u30b9\u306e\u591a\u304f\u3067\u4f7f\u7528\u3059\u308b

class BoardSign {
  int player = 0;      //\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
  char ch = ' ';       //\u6587\u5b57
  int dir = 0;         //\u6587\u5b57\u306e\u5411\u304d
  int [] pos = {0, 0}; //\u5ea7\u6a19

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

  //\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u3092\u8fd4\u3059
  public int getPlayer() {
    return player;
  }

  //\u5ea7\u6a19\u3092\u8fd4\u3059
  public int [] getPos() {
    return pos;
  }

  //X\u5ea7\u6a19\u3092\u8fd4\u3059
  public int getX() {
    return pos[0];
  }

  //Y\u5ea7\u6a19\u3092\u8fd4\u3059
  public int getY() {
    return pos[1];
  }

  //\u6587\u5b57\u3092\u8fd4\u3059
  public char getChar() {
    return ch;
  }

  //\u6587\u5b57\u306e\u5411\u304d\u3092\u8fd4\u3059
  public int getDir() {
    return dir;
  }

  //\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u304c\u7b49\u3057\u3044\u304b
  public boolean equalsPl(int _player) {
    return (player == _player);
  }

  //\u6587\u5b57\u3068\u6587\u5b57\u306e\u5411\u304d\u304c\u7b49\u3057\u3044\u304b
  public boolean equalsChDir(char _ch, int _dir) {
    return ( (ch == _ch) && (dir == _dir) );
  }

  //\u6587\u5b57\u3068\u6587\u5b57\u306e\u5411\u304d\u304c\u7b49\u3057\u3044\u304b (\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u8fbc\u307f)
  public boolean equalsChDir(int _player, char _ch, int _dir) {
    return ( (player == _player) && (ch == _ch) && (dir == _dir) );
  }

  //\u5ea7\u6a19\u304c\u7b49\u3057\u3044\u304b
  public boolean equalsPos(int _x, int _y) {
    return ( (pos[0] == _x) && (pos[1] == _y) );
  }

  //\u5ea7\u6a19\u304c\u7b49\u3057\u3044\u304b (\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u8fbc\u307f)
  public boolean equalsPos(int _player, int _x, int _y) {
    return ( (player == _player) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //\u5168\u3066\u306e\u5024\u304c\u7b49\u3057\u3044\u304b (\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u629c\u304d)
  public boolean equals(char _ch, int _dir, int _x, int _y) {
    return ( (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //\u5168\u3066\u306e\u5024\u304c\u7b49\u3057\u3044\u304b
  public boolean equals(int _player, char _ch, int _dir, int _x, int _y) {
    return ( (player == _player) && (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u304c\u7b49\u3057\u3044\u304b (BoardSign\u30af\u30e9\u30b9\u304b\u3089)
  public boolean equalsPl(BoardSign _bs) {
    return (player == _bs.player);
  }

  //\u6587\u5b57\u3068\u6587\u5b57\u306e\u5411\u304d\u304c\u7b49\u3057\u3044\u304b (BoardSign\u30af\u30e9\u30b9\u304b\u3089)
  public boolean equalsChDir(BoardSign _bs) {
    return ( (player == _bs.player) && (ch == _bs.ch) && (dir == _bs.dir) );
  }

  //\u5ea7\u6a19\u304c\u7b49\u3057\u3044\u304b (BoardSign\u30af\u30e9\u30b9\u304b\u3089)
  public boolean equalsPos(BoardSign _bs) {
    return ( (player == _bs.player) && (pos[0] == _bs.pos[0]) && (pos[1] == _bs.pos[1]) );
  }

  //\u5168\u3066\u306e\u5024\u304c\u7b49\u3057\u3044\u304b (BoardSign\u30af\u30e9\u30b9\u304b\u3089)
  public boolean equals(BoardSign _bs) {
    return ( (player == _bs.player) && (ch == _bs.ch) && (dir == _bs.dir) && (pos[0] == _bs.pos[0]) && (pos[1] == _bs.pos[1]) );
  }
}

//\u76e4\u306e\u8868\u793a
class BoardUI {
  protected int initX, initY; //\u76e4\u306e\u4e2d\u5fc3\u5ea7\u6a19(\u521d\u671f\u5024)
  protected int bcX, bcY;     //\u76e4\u306e\u4e2d\u5fc3\u5ea7\u6a19
  int rowNum, lineNum;        //\u76e4\u306e\u884c\u5217\u6570
  int [][] sStringsColor;   //\u52a0\u70b9\u6587\u5b57\u5217\u306e\u30ab\u30e9\u30fc
  int canPutColor;          //\u7f6e\u3051\u308b\u5ea7\u6a19\u306e\u30ab\u30e9\u30fc

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

  //\u4e2d\u5fc3\u5ea7\u6a19\u3092\u79fb\u52d5 (\u5dee\u5206)
  public void movePos(int _x, int _y) {
    bcX += _x;
    bcY += _y;
  }

  //\u4e2d\u5fc3\u5ea7\u6a19\u3092\u521d\u671f\u5024\u306b\u623b\u3059
  public void movePos() {
    bcX = initX;
    bcY = initY;
  }

  //\u76e4\u306e\u5ea7\u6a19\u7cfb\u306b\u5909\u63db\u3057\u3066\u8fd4\u3059
  public int [] convertBoardPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = {
      _pos[0] - bcX, _pos[1] - bcY
    };//mouseX,mouseY
    pos[0] = (pos[0] <= 0) ? (pos[0]-cardSize)/cardSize : (pos[0]+cardSize)/cardSize;
    pos[1] = (pos[1] <= 0) ? (pos[1]-cardSize)/cardSize : (pos[1]+cardSize)/cardSize;
    return pos;
  }

  //\u30a6\u30a3\u30f3\u30c9\u30a6\u306e\u5ea7\u6a19\u7cfb\u306b\u3057\u3066\u8fd4\u3059 (\u30de\u30b9\u306e\u5de6\u4e0a)
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

  //\u30de\u30b9\u76ee\u3092\u63cf\u753b
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

  //\u76e4\u4e0a\u306e\u30ab\u30fc\u30c9\u5168\u3066\u3092\u63cf\u753b
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

  //\u52a0\u70b9\u6587\u5b57\u5217\u3092\u30cf\u30a4\u30e9\u30a4\u30c8\u3059\u308b
  private void highlightSStrings() {
    for (int i = 2; i <= GameData.getSStringsMax(); i++) {
      ArrayList <ScoredString> sStrings = new ArrayList <ScoredString> (GameData.getSStrings(i));
      if (sStrings.size() != 0) {
        boardUI.highlightChain(sStrings, i);
      }
    }
  }

  //\u52a0\u70b9\u6587\u5b57\u5217\u3092\u30cf\u30a4\u30e9\u30a4\u30c8\u3059\u308b
  private void highlightChain(ArrayList <ScoredString> _sStrings, int _chain) {
    for (ScoredString ss : _sStrings) {
      int [][] poss = ss.getAbsolutePos();
      for (int [] pos : poss) {
        int [] newPos = Arrays.copyOf(pos, 2); //\u5de6\u4e0a\u5ea7\u6a19\u7cfb\u306e\u5ea7\u6a19
        //\u4e2d\u5fc3\u5ea7\u6a19\u7cfb\u306b\u5909\u63db
    		newPos[0] += - rowNum/2 + ( (newPos[0] < rowNum/2) ? 0 : 1 );
    		newPos[1] += - lineNum/2 + ( (newPos[1] < lineNum/2) ? 0 : 1 );
        //\u30a6\u30a3\u30f3\u30c9\u30a6\u4e0a\u306e\u5ea7\u6a19\u306b\u5909\u63db
        newPos = convertWindowPos(newPos);
        fill( sStringsColor [ss.getPlayer()-1] [(_chain-2 >= 5) ? 5 : _chain-2] );
        rect(newPos[0], newPos[1], GameData.cardSize, GameData.cardSize);
      }
    }
  }

  //\u7f6e\u3051\u308b\u5ea7\u6a19\u3092\u30cf\u30a4\u30e9\u30a4\u30c8\u3059\u308b
  private void highlightCanPutPos(BoardSign _bs) {
    for (int [] pos : GameData.getCanPutPos(_bs)) {
      pos = convertWindowPos(pos);
      fill(canPutColor);
      rect(pos[0], pos[1], GameData.cardSize, GameData.cardSize);
    }
  }
}
//\u5834\u306e\u8868\u793a (\u30d7\u30ec\u30a4\u30e4\u30fc\u5225)

class FieldUI extends BoardUI {
  int player;  //\u5272\u308a\u5f53\u3066\u3089\u308c\u305f\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
  //bcX, bcY\u306fFieldUI\u3067\u306f\u5de6\u4e0a\u306e\u5ea7\u6a19\u3068\u3059\u308b

  FieldUI (int _player, int _rowNum, int _lineNum) {
    super(0, 0, _rowNum, _lineNum);
    player = _player;
  }

  //\u76e4\u306e\u5ea7\u6a19\u7cfb\u306b\u5909\u63db\u3057\u3066\u8fd4\u3059
  public int [] convertBoardPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = {
      ((player == 1) ? 1 : -1) * (_pos[0] - bcX), ((player == 1) ? 1 : -1) * (_pos[1] - bcY)
    };//mouseX,mouseY

    pos[0] = (pos[0] <= 0) ? (pos[0]-cardSize)/cardSize : (pos[0]+cardSize)/cardSize;
    pos[1] = (pos[1] <= 0) ? (pos[1]-cardSize)/cardSize : (pos[1]+cardSize)/cardSize;

    return pos;
  }

  //\u5834\u306e\u8868\u793a\u7bc4\u56f2\u3092\u8fd4\u3059 (\u5de6\u4e0a\uff64\u53f3\u4e0b)
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

  //\u30de\u30b9\u76ee\u3092\u63cf\u753b
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

  //\u5834\u306b\u3042\u308b\u30ab\u30fc\u30c9\u3092\u5168\u3066\u63cf\u753b
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
//\u76e4\u30fb\u5834\u30fb\u30b9\u30c6\u30fc\u30bf\u30b9\u306e\u30c7\u30fc\u30bf\u3092\u4fdd\u6301\u3059\u308b



static class GameData {
  public static final int PLAYERS = 2;  //\u30d7\u30ec\u30a4\u30e4\u30fc\u6570
  public static int cardSize;           //\u30ab\u30fc\u30c9\u30b5\u30a4\u30ba
  public static int fieldRowNum;        //\u5834\u306e\u5217\u6570
  public static int turn = -3;          //\u30bf\u30fc\u30f3 (-3:\u8d77\u52d5\u524d -2:\u753b\u50cf\u306e\u30ed\u30fc\u30c7\u30a3\u30f3\u30b0\u4e2d -1:\u521d\u671f\u5316\u4e2d 0:\u30b2\u30fc\u30e0\u30bb\u30c3\u30c8 1\uff5e:\u30d7\u30ec\u30a4\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7)
  private static int [] points = new int [PLAYERS];  //\u30dd\u30a4\u30f3\u30c8
  public static int winner = 0;         //\u512a\u52e2\u306e\u30d7\u30ec\u30a4\u30e4\u30fc (0: \u5f15\u304d\u5206\u3051 1\uff5e: \u512a\u52e2\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7)

  private static ArrayList <BoardSign> boardSigns = new ArrayList <BoardSign>();       //\u76e4\u4e0a\u306e\u6587\u5b57 (BoardSign)
  private static ArrayList <BoardSign> fieldSigns = new ArrayList <BoardSign>();       //\u5834\u306b\u3042\u308b\u6587\u5b57 (BoardSign)
  private static ArrayList <BoardSign> canPutCC = new ArrayList <BoardSign>();         //\u7279\u5b9a\u306e\u6587\u5b57\u30fb\u5411\u304d\u3067\u7f6e\u3051\u308b\u5ea7\u6a19\u306e\u7d44\u307f\u5408\u308f\u305b (BoardSign)
  private static ArrayList <ScoredString> sStrings = new ArrayList <ScoredString>();   //\u52a0\u70b9\u6587\u5b57\u5217 (ScoredString)

  public static void clear() {
    boardSigns = new ArrayList <BoardSign>();
    fieldSigns = new ArrayList <BoardSign>();
    canPutCC = new ArrayList <BoardSign>();
    sStrings = new ArrayList <ScoredString>();
  }

  //\u521d\u671f\u5316 (\u30ab\u30fc\u30c9\u30b5\u30a4\u30ba\u3068\u5834\u306e\u5217\u6570\u3092\u30bb\u30c3\u30c8)
  public static void init(int _cardSize, int _num) {
    cardSize = _cardSize;
    fieldRowNum = _num;
  }

  //\u5834\u306b\u6587\u5b57\u3092\u8ffd\u52a0\u3059\u308b
  public static void addFieldSign(BoardSign _fs) {
    if (_fs.getPlayer() == 1) {
      fieldSigns.add(0, _fs);
    } else if (_fs.getPlayer() == 2) {
      fieldSigns.add(_fs);
    }
  }

  //\u6307\u5b9a\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u30dd\u30a4\u30f3\u30c8\u3092\u8fd4\u3059
  public static int getPoints(int _player) {
    return points[_player-1];
  }

  //\u30d7\u30ec\u30a4\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u30dd\u30a4\u30f3\u30c8\u3092\u8fd4\u3059
  public static int getPoints() {
    return points[turn-1];
  }

  //\u76e4\u4e0a\u306eBoardSign\u306e\u30ea\u30b9\u30c8\u3092\u8fd4\u3059
  public static ArrayList <BoardSign> getBoardSigns() {
    return boardSigns;
  }

  //\u5834\u306e\u5217\u6570\u3092\u8fd4\u3059
  public static int getFieldRowNum() {
    return fieldRowNum;
  }

  //\u5834\u306b\u3042\u308b\u6587\u5b57\u6570\u3092\u8fd4\u3059 (\u5168\u30d7\u30ec\u30a4\u30e4\u30fc)
  public static int getFieldSignNum() {
    return fieldSigns.size();
  }

  //\u6307\u5b9a\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u5834\u306b\u3042\u308b\u6587\u5b57\u3092\u8fd4\u3059
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

  //\u30d7\u30ec\u30a4\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u5834\u306b\u3042\u308b\u6587\u5b57\u3092\u8fd4\u3059
  public static ArrayList <BoardSign> getFieldSigns() {
    return getFieldSigns(turn);
  }

  /*
  \u6307\u5b9a\u3057\u305f\u6587\u5b57\u3068\u5411\u304d\u3067\u7f6e\u3051\u308b\u5ea7\u6a19\u3092\u8fd4\u3059
    getCanPutPos(BoardSign)
  \u8fd4\u308a\u5024: int[][\u5ea7\u6a19x,y]
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

  //\u7f6e\u3051\u308b\u5ea7\u6a19\u306e\u6570(\u624b\u6570)\u3092\u8fd4\u3059
  public static int getCanPutCCSum() {
    return canPutCC.size();
  }

  /*
  \u6307\u5b9a\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u6307\u5b9a\u3057\u305f\u9023\u9396\u306e\u52a0\u70b9\u6587\u5b57\u5217\u306e\u5ea7\u6a19\u3092\u8fd4\u3059
    getSStringsPos(\u9023\u9396\u6570)
  \u8fd4\u308a\u5024: int[\u52a0\u70b9\u6587\u5b57\u5217][\u5ea7\u6a19][x,y] (\u5ea7\u6a19\u306f\u5de6\u4e0a\u5ea7\u6a19\u7cfb)
  */
  public static ArrayList <ScoredString> getSStrings(int _chain) {
    ArrayList <ScoredString> ssList = new ArrayList <ScoredString>();	//\u5ea7\u6a19

    for (ScoredString ss : sStrings) {
      if (ss.size() == _chain-1) {
        ssList.add(ss);
      }
    }

    return ssList;
  }

  //\u52a0\u70b9\u6587\u5b57\u5217\u306e\u9023\u9396\u306e\u6700\u5927\u5024\u3092\u8fd4\u3059
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

  //\u6307\u5b9a\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u30dd\u30a4\u30f3\u30c8\u3092\u30bb\u30c3\u30c8
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

  //\u30d7\u30ec\u30a4\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u30dd\u30a4\u30f3\u30c8\u3092\u30bb\u30c3\u30c8
  public static void setPoints(int _points) {
    setPoints(turn, _points);
  }

  //\u76e4\u4e0a\u306b\u6587\u5b57\u3092\u8ffd\u52a0
  public static void addBoardSign(BoardSign _bs) {
    boardSigns.add(_bs);
    println("Player " + _bs.getPlayer() + " put \"" + _bs.getChar() + ":" + _bs.getDir() + "\" at (" + _bs.getX() + ", " + _bs.getY() + ")");
  }

  //\u6307\u5b9a\u306eBoardSign\u3092\u5834\u304b\u3089\u524a\u9664\u3059\u308b
  public static void removeFieldSign(BoardSign _bs) {
    fieldSigns.remove(_bs);
  }

  //\u7f6e\u3051\u308b\u7d44\u307f\u5408\u308f\u305b\u3092\u30bb\u30c3\u30c8
  public static void setCanPutCC(ArrayList <BoardSign> _bs) {
    canPutCC = new ArrayList <BoardSign>(_bs);
    println("All Moves: " + canPutCC.size());
  }

  //\u52a0\u70b9\u6587\u5b57\u5217\u3092\u8ffd\u52a0 (\u6307\u5b9a\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u52a0\u70b9\u6587\u5b57\u5217\u3092\u4e88\u3081\u524a\u9664\u3057\u3066\u304b\u3089\u8ffd\u52a0)
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
//\u4e00\u65b9\u5411\u306e\u6587\u5b57\u756a\u53f7\u30ec\u30a4\u30e4\u30fc
//\u52a0\u70b9\u6587\u5b57\u5217\u306e\u691c\u7d22
class NumLayer {
	int [][] num;	//\u6587\u5b57\u756a\u53f7
	ArrayList <ScoredString> strings = new ArrayList<ScoredString>();	//\u52a0\u70b9\u6587\u5b57\u5217\u7fa4
	ScoredString string;	//\u52a0\u70b9\u6587\u5b57\u5217

	int [] godStringNum;	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217
	int godStringNumLevel = 0;	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304c\u3069\u3053\u307e\u3067\u6210\u7acb\u3057\u3066\u3044\u308b\u304b
	boolean hitGodString = false;	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u304b

	int asteNum = -1;	//\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u304c\u9023\u756a\u5217\u4e0a\u3067\u306a\u308a\u3046\u308b\u6587\u5b57\u756a\u53f7

	/*
	[\u65b9\u5411\u306b\u95a2\u3057\u3066]
	0:\u5317 1:\u897f 2:\u5357 3:\u6771
	(\u305d\u308c\u305e\u308c\u306e\u65b9\u89d2\u306b\u30ab\u30fc\u30c9\u304c\u4e0a\u3092\u5411\u304f)
	*/

	NumLayer(int _size, int [] _godStringNum) {
		num = new int [_size][_size];
		godStringNum = _godStringNum;
	}

	//\u6587\u5b57\u756a\u53f7\u3092\u4ee3\u5165
	public void subNum(int _num, int _x, int _y) {
		num[_x][_y] = _num;
		//println("(" + _x + ", " + _y + ") set " + _num);
	}

	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u304b\u8fd4\u3059
	public boolean hitGodString() {
		return hitGodString;
	}

	//\u6307\u5b9a\u306e\u76e4\u306e\u7bc4\u56f2\u304b\u3089\u52a0\u70b9\u6587\u5b57\u5217\u7fa4\u3092\u8fd4\u3059
	public ArrayList <ScoredString> calScoredStrings(int [][] _bRange) {
		strings = new ArrayList<ScoredString>();	//\u521d\u671f\u5316

		//\u9023\u756a\u3092\u691c\u7d22
		//println("[Calculate Consecutive Signs]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(0, i, j);
			}
		}

		//\u305e\u308d\u76ee\u3092\u691c\u7d22
		//println("[Calculate Repdigit]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(1, i, j);
			}
		}

		//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u3092\u691c\u7d22
		//println("[Calculate God String]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(2, i, j);
			}
		}

		return strings;
	}

	/*
	calChains(\u691c\u7d22\u3059\u308b\u52a0\u70b9\u6587\u5b57\u5217\u306e\u7a2e\u985e, \u30eb\u30fc\u30c8x\u5ea7\u6a19, y\u5ea7\u6a19)
	\u25cb \u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u304b\u3089\u9023\u306a\u308b\u9023\u756a\u5217\u3092\u691c\u7d22
	1. \u30eb\u30fc\u30c8\u5ea7\u6a19\u304c*\u306e\u5834\u5408\u306freturn
	2. \u9023\u756a[_kind == 0]						4\u65b9\u5411\u304b\u3089\u6607\u9806\u306b\u9023\u756a\u3092\u898b\u3064\u3051\u308b (\u964d\u9806\u306b\u9023\u756a\u304c\u3042\u308c\u3070\u8fd4\u308b)
	2. \u305e\u308d\u76ee[_kind == 1]				 4\u65b9\u5411\u304b\u3089\u305e\u308d\u76ee\u3092\u898b\u3064\u3051\u308b (\u305e\u308d\u76ee\u5217\u304c\u5468\u308a\u306b2\u3064\u4ee5\u4e0a\u3042\u308b\u5834\u5408\u306f\u8fd4\u308b)
	2. \u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217[_kind == 0] 4\u65b9\u5411\u304b\u3089\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u3092\u5148\u982d\u304b\u3089\u898b\u3064\u3051\u308b
	3.	(\u3053\u306e\u6642\u70b9\u3067\u6307\u5b9a\u306e\u5ea7\u6a19=\u30eb\u30fc\u30c8\u5ea7\u6a19\u304c\u6c7a\u5b9a)
	4. \u30eb\u30fc\u30c8\u5ea7\u6a19\u304b\u3089\u6607\u9806\u306b\u9023\u756a\u304b\u8abf\u3079\u308b (\u518d\u5e30\u51e6\u7406)
	*/
	public void calChains(int _kind, int _x, int _y) {
		int root = num[_x][_y];	//\u30eb\u30fc\u30c8\u5ea7\u6a19

		if (root == 27) {	//\u30eb\u30fc\u30c8\u5ea7\u6a19\u304c\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306a\u3089
			return;
		}

		int [] sum = calTwoChains(_x, _y, -1);	//\u30eb\u30fc\u30c8\u304b\u3089\u9023\u756a\u3092\u8a08\u7b97

		boolean hitChains = false;	//\u52a0\u70b9\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u304b
		switch (_kind) {
			case 0 :	//\u9023\u756a\u3092\u691c\u7d22
				for (int i = 0; i < 4; ++i) {
					if (sum[i] == -1) {	//i\u306e\u65b9\u5411\u306b\u304a\u3044\u3066\u964d\u9806\u306b\u9023\u756a\u306a\u3089
						return;
					} else if (sum[i] == 1) {	//\u6607\u9806\u306b\u9023\u756a\u306a\u3089
						hitChains = true;
						//break;
					} else if (sum[i] == 2) {	//\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306b\u63a5\u7d9a\u306a\u3089
						asteNum = (root-1)%27;	//\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u304c"\u964d\u9806\u306b"\u9023\u756a\u306b\u306a\u308a\u3046\u308b\u6587\u5b57\u3092\u4ee3\u5165
						//A\u2192Z\u306e\u51e6\u7406
						if (asteNum == 0) {
							asteNum = 26;
						}
						//\u76ee\u6a19\u5ea7\u6a19\u307e\u3067\u306e\u5dee\u5206\u3092\u66f4\u65b0
						int nextDx = (int)cos(-0.5f*(i+1)*PI);
						int nextDy = (int)sin(-0.5f*(i+1)*PI);
						int [] asteSum = calTwoChains(_x + nextDx, _y + nextDy, (i+2)%4);
						for (int j = 0; j < 4; j++) {
							//\u964d\u9806\u306b\u9023\u756a\u306a\u3089
							if (asteSum[i] == -1) {
								return;
							}
						}
						hitChains = true;
					}
				}
			break;
			case 1 :	//\u305e\u308d\u76ee\u3092\u691c\u7d22
				for (int i = 0; i < 4; i++) {
					if (sum[i] == -2 || sum[i] == 2) {	//i\u306e\u65b9\u5411\u306b\u304a\u3044\u3066\u305e\u308d\u76ee\u306a\u3089
						//println("hitChains");
						if (!hitChains) {
								hitChains = true;
						} else {	//\u305e\u308d\u76ee\u5217\u304c\u5468\u308a\u306b2\u3064\u4ee5\u4e0a\u3042\u308b\u5834\u5408\u306f\u8fd4\u308b
							return;
						}
					}
				}
			break;
			case 2 :	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u3092\u8a08\u7b97
				godStringNumLevel = 0;	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u306e\u5148\u982d\u304b\u3089\u691c\u7d22\u3059\u308b
				for (int i = 0; i < 4; i++) {
					if (sum[i] == 3 || sum[i] == 2) {
						hitChains = true;
						break;
					}
				}
			break;
		}

		//\u3069\u306e\u65b9\u5411\u306b\u304a\u3044\u3066\u3082\u9023\u756a\u3067\u306a\u304b\u3063\u305f\u3089
		if (!hitChains) {
			return;
		}

		//\u3053\u306e\u6642\u70b9\u3067(_x, _y)\u304c\u52a0\u70b9\u6587\u5b57\u5217\u306e\u30eb\u30fc\u30c8\u5ea7\u6a19\u3067\u3042\u308b\u3053\u3068\u304c\u6c7a\u5b9a
		string = new ScoredString(_x, _y, _kind);
		calHighChains(_kind, _x, _y);	//\u30eb\u30fc\u30c8\u304b\u3089\u9ad8\u3044\u9806\u306b\u52a0\u70b9\u6587\u5b57\u5217\u3092\u63a2\u7d22
	}

	/*
	calHighChains(\u30eb\u30fc\u30c8x\u5ea7\u6a19, y\u5ea7\u6a19, \u76ee\u6a19\u5ea7\u6a19\u307e\u3067\u306ex\u5ea7\u6a19\u5dee\u5206, \u76ee\u6a19\u5ea7\u6a19\u307e\u3067\u306ey\u5ea7\u6a19\u5dee\u5206, \u524d\u306e\u76ee\u6a19\u5ea7\u6a19\u306e\u5411\u304d)
	\u25cb \u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u304b\u30892\u9023\u9396\u4ee5\u4e0a\u306e\u6607\u9806\u306e\u52a0\u70b9\u6587\u5b57\u5217\u3092\u691c\u7d22
	---
	root(\u30eb\u30fc\u30c8\u5ea7\u6a19):		 \u9023\u756a\u5217\u306e\u59cb\u70b9\u5ea7\u6a19
	current(\u73fe\u5728\u5ea7\u6a19):		\u3053\u3053\u304b\u30894\u65b9\u5411\u306b\u8abf\u3079\u308b
	next(\u76ee\u6a19\u5ea7\u6a19):				\u73fe\u5728\u5ea7\u6a19\u304b\u3089\u8abf\u3079\u308b\u5468\u308a\u306e\u5ea7\u6a19
	---
		1. \u73fe\u5728\u5ea7\u6a19\u304b\u30894\u65b9\u5411\u306b\u8abf\u3079\u308b
		2. \u30d2\u30c3\u30c8\u3057\u305f\u3089string\u306b\u767b\u9332 (\u76ee\u6a19\u5ea7\u6a19\u304c\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306a\u3089\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u304c\u52a0\u70b9\u6587\u5b57\u5217\u306b\u306a\u308a\u5f97\u308b\u6587\u5b57\u756a\u53f7\u3092\u4ee3\u5165)
		3. \u76ee\u6a19\u5ea7\u6a19\u3092\u73fe\u5728\u5ea7\u6a19\u3068\u3057\u3066\u518d\u5e30
		4. 1\uff5e3\u3092\u30eb\u30fc\u30d7 \u52a0\u70b9\u6587\u5b57\u5217\u304c\u898b\u3064\u304b\u3089\u306a\u304f\u306a\u3063\u305f\u3089\u305d\u306e\u524d\u306e\u5ea7\u6a19\u304c\u52a0\u70b9\u6587\u5b57\u5217\u306e\u7d42\u70b9
		5. \u52a0\u70b9\u6587\u5b57\u5217\u306e\u7d42\u70b9\u3092\u898b\u3064\u3051\u305f\u3089\u3001string\u3092strings\u306b\u8ffd\u52a0 (\u4e00\u3064\u3082\u9023\u756a\u5217\u304c\u898b\u3064\u304b\u3089\u306a\u3044\u5834\u5408\u306f\u8ffd\u52a0\u3057\u306a\u3044)
	\u8fd4\u308a\u5024: boolean\u578b \u6307\u5b9a\u306e\u52a0\u70b9\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u304b
	*/
	public boolean calHighChains(int _kind, int _x, int _y, int _dx, int _dy, int _preDir) {
		boolean hitChains = false;	//\u9023\u9396\u3092\u767a\u898b\u3057\u305f\u304b
		int current = num[_x+_dx][_y+_dy];	//\u73fe\u5728\u5ea7\u6a19\u306e\u6587\u5b57\u756a\u53f7

		//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u306e\u7d42\u70b9\u306e\u3068\u304dfalse\u3092\u8fd4\u3059
		if (_kind == 2 && godStringNumLevel == godStringNum.length-1) {
			return false;
		}

		int [] sum = calTwoChains(_x+_dx, _y+_dy, _preDir);	//\u73fe\u5728\u5ea7\u6a19\u304b\u3089\u9023\u756a\u3092\u8a08\u7b97

		for (int i = 0; i < 4; ++i) {
			boolean consecutive = (sum[i] == 1 || sum[i] == 2);	//\u9ad8\u3044\u9806\u306b\u9023\u756a\u304b
			boolean identical = (sum[i] == -2 || sum[i] == 2);	//\u305e\u308d\u76ee\u304b
			boolean god = (sum[i] == 3 || sum[i] == 2);					//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304b

			//\u6307\u5b9a\u306e\u52a0\u70b9\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u3089
			if ( (_kind == 0 && consecutive) || (_kind == 1 && identical) || (_kind == 2 && god)) {
				hitChains = true;
				string.add(i);	//\u5ea7\u6a19\u3092\u767b\u9332
				//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") add " + i);

				//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u306a\u3089\u6b21\u306e\u30ec\u30d9\u30eb\u306b\u3059\u308b
				if (_kind == 2) {
					godStringNumLevel++;
					//println("godStringNumLevel++: " + godStringNumLevel);
				}

				//\u76ee\u6a19\u5ea7\u6a19\u304c\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306e\u5834\u5408
				if (sum[i] == 2) {
					switch (_kind) {
						case 0 :	//\u9023\u756a
							asteNum = (current+1)%27;
							if (asteNum == 0) {	//Z\u2192A\u306e\u51e6\u7406
								asteNum++;
							}
						break;
						case 1 :	//\u305e\u308d\u76ee
							asteNum = current;
						break;
						case 2 :	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217
							asteNum = godStringNum[godStringNumLevel];
						break;
					}
				}

				//\u76ee\u6a19\u5ea7\u6a19\u307e\u3067\u306e\u5dee\u5206\u3092\u66f4\u65b0
				int nextDx = _dx + (int)cos(-0.5f*(i+1)*PI);
				int nextDy = _dy + (int)sin(-0.5f*(i+1)*PI);

				boolean nextHitChains = calHighChains(_kind, _x, _y, nextDx, nextDy, (i+2)%4);	//\u76ee\u6a19\u306e\u5ea7\u6a19\u3067\u518d\u5e30

				//\u9023\u756a\u3092\u898b\u3064\u3051\u3089\u308c\u306a\u304b\u3063\u305f\u5834\u5408 (\u9023\u9396\u306e\u7d42\u70b9) (*1)
				if (!nextHitChains) {
					if (sum[i] == 2) {	//\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u304c\u9023\u756a\u5217\u306e\u7d42\u70b9\u306e\u5834\u5408\u306f\u7121\u52b9
						hitChains = false;
					} else if (string.size() != 0) {	//2\u9023\u9396\u4ee5\u4e0a\u306a\u3089
						//\u52a0\u70b9\u6587\u5b57\u5217\u7fa4\u306b\u767b\u9332
						if (_kind == 2) {
							//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304c\u7d42\u70b9\u306e\u5834\u5408
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
				string.remove(string.size()-1);	//\u6700\u5f8c\u306estring\u306e\u5ea7\u6a19\u3092\u524a\u9664
				//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u306e\u30ec\u30d9\u30eb\u3092\u4e0b\u3052\u308b
				if (_kind == 2) {
					godStringNumLevel--;
					//println("godStringNumLevel--: " + godStringNumLevel);
				}
			}
		}

		//\u73fe\u5728\u5ea7\u6a19\u304c\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306e\u3068\u304d\u9023\u9396\u304c\u898b\u3064\u304b\u3089\u306a\u3044\u5834\u5408\uff64
		//*\u304c\u7d42\u70b9\u3068\u306a\u3063\u3066\u3057\u307e\u3046\u306e\u3067false\u3092\u8fd4\u3057\u3066*\u3092\u8ffd\u52a0\u305b\u305a\u306b\u305d\u306e\u524d\u306e\u6587\u5b57\u3067\u9023\u9396\u306e\u7d42\u70b9\u51e6\u7406(*1)\u3092\u884c\u3046 (1\u3064\u623b\u308b)
		if (!hitChains && current == 27) {
			if (current == 27) {
				hitChains = false;
			}
			//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") end");
		}

		return hitChains;
	}

	//calHighChains\u306e\u30eb\u30fc\u30c8\u5ea7\u6a19\u304b\u3089\u8abf\u3079\u308b\u30d0\u30fc\u30b8\u30e7\u30f3 (\u7b2c3\uff5e5\u5f15\u6570\u7701\u7565)
	public boolean calHighChains(int _kind, int _x, int _y) {
		return calHighChains(_kind, _x, _y, 0, 0, -1);
	}

	/*
	\u25cb calTwoChain\u30924\u65b9\u5411\u884c\u3046
		calTwoChains(\u73fe\u5728\u306ex\u5ea7\u6a19, y\u5ea7\u6a19, \u524d\u306e\u76ee\u6a19\u5ea7\u6a19\u306e\u5411\u304d(\u30eb\u30fc\u30c8\u5ea7\u6a19\u306e\u5834\u5408\u306f-1))
	\u203b \u524d\u306e\u76ee\u6a19\u5ea7\u6a19\u306e\u8a08\u7b97\u306f\u7701\u3044\u3066\u3044\u308b
	*/
	public int [] calTwoChains(int _x, int _y, int _preDir) {
		int [] sum = new int [4];

		for (int i = 0; i < sum.length; ++i) {
			//\u524d\u306e\u76ee\u6a19\u5ea7\u6a19\u3092\u8abf\u3079\u306a\u3044\u3088\u3046\u306b\u3059\u308b
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
	calTwoChain(\u73fe\u5728x\u5ea7\u6a19, y\u5ea7\u6a19, \u9023\u756a\u3092\u8abf\u3079\u308b\u5411\u304dx, y) (\u5411\u304d\u306f0\uff5e3)
	\u25cb 2\u9023\u9396\u306e\u5224\u5b9a\u3092\u3059\u308b
	\u8fd4\u308a\u5024: \u65b9\u5411\u9806\u306b\u683c\u7d0d\u3055\u308c\u305f\u9023\u756a\u306e\u72b6\u614b
		-2: \u305e\u308d\u76ee
		-1: \u964d\u9806\u306b\u9023\u756a
		0: \u9023\u756a\u306a\u3057
		1: \u6607\u9806\u306b\u9023\u756a
		2: \u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306b\u63a5\u7d9a
		3: \u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u306b\u9023\u756a
	*/
	public int calTwoChain(int _x, int _y, int _dx, int _dy) {
		int current = num[_x][_y];			//\u73fe\u5728\u5ea7\u6a19\u306e\u6587\u5b57\u756a\u53f7
		int next = num[_x+_dx][_y+_dy];	//\u76ee\u6a19\u5ea7\u6a19\u306e\u6587\u5b57\u756a\u53f7

		//\u73fe\u5728\u5ea7\u6a19\u304c\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306e\u5834\u5408\u306fasteNum\u304b\u3089\u53d6\u5f97\u3057\u3066\u304f\u308b
		if (current == 27) {
			current = asteNum;
		}

		//\u73fe\u5728\u5ea7\u6a19\u3068\u76ee\u6a19\u5ea7\u6a19\u306e\u3069\u3061\u3089\u304b\u304c0\u306a\u3089\u8fd4\u3059
		if (current == 0 || next == 0) {
			return 0;
		}
		//\u76ee\u6a19\u5ea7\u6a19\u304c\u30a2\u30b9\u30bf\u30ea\u30b9\u30af\u306e\u5834\u5408
		if (next == 27) {
			return 2;
		}

		//\u9023\u756a\u306e\u5224\u5b9a
		if (next - current == 1 || (next == 1 && current == 26)) {	//\u6607\u9806\u306b\u9023\u756a
			return 1;
		} else if (next - current == -1 || (next == 26 && current == 1)) {	//\u964d\u9806\u306b\u9023\u756a
			return -1;
		}
		//\u305e\u308d\u76ee\u306e\u5224\u5b9a
		if (next == current) {
			//println("repdigit");
			return -2;
		}
		//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u306e\u5224\u5b9a
		if (current == godStringNum[godStringNumLevel] && next == (godStringNum[godStringNumLevel+1])) {
			return 3;
		}

		return 0;
	}
}
//\u5168\u65b9\u5411\u306e\u6587\u5b57\u756a\u53f7\u30ec\u30a4\u30e4\u30fc
//\u30dd\u30a4\u30f3\u30c8\u8a08\u7b97\uff64\u52a0\u70b9\u6587\u5b57\u5217\u306e\u53d6\u5f97



class NumLayers {
	boolean hitGodString = false;	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u304b
	ArrayList <ScoredString> sStrings = new ArrayList <ScoredString>();	//\u5168\u65b9\u5411\u306e\u52a0\u70b9\u6587\u5b57\u5217

	NumLayer [] numLayer = new NumLayer[4];	//\u5404\u65b9\u5411\u306e\u6587\u5b57\u756a\u53f7\u30ec\u30a4\u30e4\u30fc

	//NumLayers(\u76e4\u306e\u30b5\u30a4\u30ba, \u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u306e\u6587\u5b57\u756a\u53f7)
	NumLayers(int _size, int [] _godStringNum) {
		for (int i = 0; i < numLayer.length; ++i) {
			numLayer[i] = new NumLayer(_size, _godStringNum);
		}
	}

	//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u304b
	public boolean hitGodString() {
		return hitGodString;
	}

	//\u6587\u5b57\u756a\u53f7\u3092\u4ee3\u5165
	public void putSignNum(int [] _num, int _x, int _y) {
		for (int i = 0; i < numLayer.length; ++i) {
			numLayer[i].subNum(_num[i], _x, _y);
		}
	}

	//\u52a0\u70b9\u6587\u5b57\u5217\u304b\u3089\u30dd\u30a4\u30f3\u30c8\u3092\u63db\u7b97\u3059\u308b (3\u9023\u9396\u4ee5\u4e0a\u306e\u52a0\u70b9\u6587\u5b57\u5217)
	//\u203b\u5148\u306bcalScoredStrings()\u3092\u3057\u306a\u3044\u3068\u66f4\u65b0\u3055\u308c\u307e\u305b\u3093
	public int calPoints() {
		int points = 0;	//\u30dd\u30a4\u30f3\u30c8

		for (ScoredString ss : sStrings) {
			if (ss.getKind() == 0 || ss.getKind() == 2) {	//2\u9023\u9396\u3092\u7701\u304f
				if (ss.size() >= 2)	points += (int)pow(ss.size()+1, 2);
			} else if (ss.getKind() == 1) {
				if (ss.size() >= 1)	points += (int)pow(ss.size()+1, 2);
			}
		}

		return points;
	}

	/*
	\u25cb \u6307\u5b9a\u7bc4\u56f2\u306e\u5168\u3066\u306e\u52a0\u70b9\u6587\u5b57\u5217\u3092\u8fd4\u3059(\u9023\u9396\u306e\u5c0f\u3055\u3044\u9806) (2\u9023\u9396\u3092\u542b\u3080)
		calScoredStrings(\u691c\u7d22\u3059\u308b\u76e4\u306e\u7bc4\u56f2)
	*/
	public ArrayList <ScoredString> calScoredStrings(int [][] _bRange) {
		//println(" - START Calculating SStrings - (" + _bRange[0][0] + ", " + _bRange[0][1] + ") ~ (" + _bRange[1][0] + ", " + _bRange[1][1] + ")");

		sStrings = new ArrayList<ScoredString>();	//\u521d\u671f\u5316

		//numLayer\u304b\u3089\u5404\u65b9\u5411\u306e\u52a0\u70b9\u6587\u5b57\u5217\u3092\u53d6\u5f97
		for (int i = 0; i < numLayer.length; ++i) {
			//println("  - START " + i + " Direction -");
			sStrings.addAll( new ArrayList <ScoredString> (numLayer[i].calScoredStrings(_bRange)) );	//i\u65b9\u5411\u306e\u52a0\u70b9\u6587\u5b57\u5217\u3092\u53d6\u5f97\u3057\u4ee3\u5165
			//\u52dd\u5229\u78ba\u5b9a\u6587\u5b57\u5217\u304c\u30d2\u30c3\u30c8\u3057\u305f\u3089true
			if (numLayer[i].hitGodString()) {
				hitGodString = true;
				//println("  ! A God String Was Found");
			}
			//println("  - END " + i + " Direction -");
		}

		//\u9023\u9396\u306e\u5927\u304d\u3044\u9806(\u964d\u9806)\u306b\u4e26\u3073\u66ff\u3048\u308b
		Collections.sort(sStrings, new SStringConparator());
		Collections.reverse(sStrings);

		//\u7b49\u3057\u3044\u52a0\u70b9\u6587\u5b57\u5217\u3092\u9664\u304f
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

		Collections.reverse(sStrings);	//\u6607\u9806\u306b\u4e26\u3073\u66ff\u3048\u308b
		return sStrings;
	}

}
//PS = Points/Sides = \u70b9\uff65\u8fba\u30c7\u30fc\u30bf
//\u8ad6\u7406\u6f14\u7b97(AND / OR)\u30fb\u30b7\u30d5\u30c8\u6f14\u7b97(>>> / <<)\u3092\u4f7f\u7528\u3057\u3066\u5224\u5b9a\u3059\u308b

//\u70b9\u30fb\u8fba\u30ec\u30a4\u30e4\u30fc
//\u7f6e\u3051\u308b\u6761\u4ef6\u5224\u5b9a\u3092\u884c\u3046

class PSLayer {
  int [][] bPS;       //\u76e4\u4e0a\u306e\u70b9\uff65\u8fba
  int [][] bPlayer;   //\u76e4\u4e0a\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7

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

  //putPS(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, PS, x\u5ea7\u6a19, y\u5ea7\u6a19)
  //\u70b9\uff65\u8fba\u30c7\u30fc\u30bf\u3092\u3092\u7f6e\u304f
  public void putPS(int _player, int _ps, int _x, int _y) {
    bPS[_x][_y] = _ps;
    bPlayer[_x][_y] = _player;
  }


  //\u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u306b\u7f6e\u3051\u308b"\u53ef\u80fd\u6027\u304c\u3042\u308b\u304b"\u5224\u5b9a\u3059\u308b
  //mayPut(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, x\u5ea7\u6a19, y\u5ea7\u6a19)
  public boolean mayPut(int _player, int _x, int _y) {
    //\u65e2\u306b\u7f6e\u3044\u3066\u3042\u3063\u305f\u3089false
    if (bPlayer[_x][_y] != 0) {
      return false;
    }
    //\u5468\u308a\u306b\u6587\u5b57\u304c\u306a\u3044\u3001\u307e\u305f\u306f\u5468\u308a\u306b\u6307\u5b9a\u3057\u305f\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u4ee5\u5916\u306e\u6587\u5b57\u304c\u306a\u3044\u5834\u5408false
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

  //\u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u306b\u7f6e\u3051\u308b\u304b\u5224\u5b9a\u3059\u308b
  //canPut(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, PS, x\u5ea7\u6a19, y\u5ea7\u6a19)
  public int canPut(int _player, int _ps, int _x, int _y) {
    //\u65e2\u306b\u7f6e\u3044\u3066\u3042\u3063\u305f\u3089
    if (bPlayer[_x][_y] != 0) {
      return 0;
    }

    int current = _ps;  //\u73fe\u5728\u306ePS
    int around = getAroundPS(_player, _x, _y);  //\u5468\u308a\u306ePS
    /*
    StringBuilder cs = new StringBuilder("0000000000000000");
    cs.append( Integer.toBinaryString(current) ).delete(0, cs.length()-16).insert(5, ' ').insert(12, ' ');
    StringBuilder as = new StringBuilder("0000000000000000");
    as.append( Integer.toBinaryString(around) ).delete(0, as.length()-16).insert(5, ' ').insert(12, ' ');
    println("current: " + cs.toString() + " | around: " + as.toString());
    */

    int sum = current & around;
    //println("sum" + sum);
    int sides = sum & 0x566A;  //\u8fba\u3060\u3051\u53d6\u308a\u51fa\u3059
    //println("sides: " + sides);
    //\u8fba\u3068\u8fba\u3067\u63a5\u3057\u3066\u3044\u308b
    if (sides != 0) {
      return -1;
    }
    int points = sum & 0xA995; //\u70b9\u3060\u3051\u53d6\u308a\u51fa\u3059
    //println("points" + points);
    //\u76f8\u624b\u306e\u70b9\u3067\u63a5\u3057\u3066\u306a\u3044
    if (points == 0) {
      return -2;
    }
    return 1;
  }

  //\u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u5468\u308a\u306ePS\u3092\u8a08\u7b97\u3059\u308b
  //getAroundPS(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, x\u5ea7\u6a19, y\u5ea7\u6a19)
  private int getAroundPS(int _player, int _x, int _y) {
    int around = 0; //\u751f\u6210\u3059\u308b\u5468\u308a\u306ePS
    int currentPlayer = _player;  //\u73fe\u5728\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
    int [] partPS = new int [8];  //around\u306e\u533a\u753b8\u3064
    int [][] aroundBPS = new int [3][3];  //\u5468\u308a\u306ePS

    for (int j = -1; j <= 1; j++) {
      for (int i = -1; i <= 1; i++) {
        if (_x+i < 0 || _x+i > bPlayer.length || _y+j < 0 || _y+j > bPlayer[0].length) {
          aroundBPS[1+i][1+j] = 0;
          continue;
        }
        //\u76ee\u6a19\u5ea7\u6a19\u3068\u73fe\u5728\u5ea7\u6a19\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u304c\u540c\u3058\u306a\u3089
        if (bPlayer[_x+i][_y+j] == currentPlayer) {
          aroundBPS[1+i][1+j] = bPS[_x+i][_y+j] & 0x566A; //\u8fba\u3060\u3051\u53d6\u308a\u51fa\u3059(\u70b9\u30c7\u30fc\u30bf\u3092\u524a\u9664)
          //println("same player: " + i + ", " + j);
        } else {
          aroundBPS[1+i][1+j] = bPS[_x+i][_y+j];  //bPS\u304b\u3089PS\u3092\u62fe\u3063\u3066\u304f\u308b
        }
      }
    }

    //\u5468\u308a\u304b\u3089PS\u3092\u53d6\u5f97
    partPS[0] = getCenterBottom( aroundBPS[1][0] );
    partPS[1] = getCenterTop( aroundBPS[1][2] );
    partPS[2] = getCenterLeft( aroundBPS[0][1] );
    partPS[3] = getCenterRight( aroundBPS[2][1] );
    partPS[4] = getCorner(0, 0, aroundBPS[0][1], aroundBPS[1][0], aroundBPS[0][0]);
    partPS[5] = getCorner(4, aroundBPS[2][1], 0, aroundBPS[2][0], aroundBPS[1][0]);
    partPS[6] = getCorner(11, aroundBPS[1][2], aroundBPS[0][2], 0, aroundBPS[0][1]);
    partPS[7] = getCorner(15, aroundBPS[2][2], aroundBPS[1][2], aroundBPS[2][1], 0);

    //around\u306bpartPS\u3092\u5165\u308c\u3066\u3044\u304f
    for (int i = 0; i < partPS.length; i++) {
      around += partPS[i];
    }

    return around;
  }

  //\u4e0b\u4e2d\u592e3\u3064(12/13/14)\u3092\u53d6\u308a\u51fa\u3059
  private int getCenterBottom(int _ps) {
    return (_ps & 0xE) << 11;
  }

  //\u4e0a\u4e2d\u592e3\u3064(1/2/3)\u3092\u53d6\u308a\u51fa\u3059
  private int getCenterTop(int _ps) {
    return (_ps & 0x7000) >>> 11;
  }

  //\u5de6\u4e2d\u592e3\u3064(5/7/9)\u3092\u53d6\u308a\u51fa\u3059
  private int getCenterLeft(int _ps) {
    return (_ps & 0x2A0) << 1;
  }

  //\u53f3\u4e2d\u592e3\u3064(6/8/10)\u3092\u53d6\u308a\u51fa\u3059
  private int getCenterRight(int _ps) {
    return (_ps & 0x540) >>> 1;
  }

  //\u89d2(0/4/11/15)\u3092\u53d6\u308a\u51fa\u3059
  private int getCorner(int _currentCorner, int _ps0, int _ps4, int _ps11, int _ps15) {
    int a = _ps0 & 0x8000;
    int b = (_ps4 & 0x800) << 4;
    int c = (_ps11 & 0x10) << 11;
    int d = (_ps15 & 0x1) << 15;
    return (a | b | c | d) >>> _currentCorner;
  }
}
//\u52a0\u70b9\u6587\u5b57\u5217\u3092\u6bd4\u8f03\u3059\u308b



class SStringConparator implements Comparator<ScoredString> {

  public int compare(ScoredString a, ScoredString b) {
    //\u30b5\u30a4\u30ba\u3092\u53d6\u5f97
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
\u52a0\u70b9\u6587\u5b57\u5217 (\u76f8\u5bfe\u8868\u8a18)
\u65b9\u89d2\u8868\u8a18(0-3)\u306e\u5024\u3092\u53d7\u3051\u53d6\u308b\u2192\u76f8\u5bfe\u30fb\u7d76\u5bfe\u5ea7\u6a19\u3067\u8fd4\u3059
*/

class ScoredString<T> extends ArrayList<Integer> {
	private T arg;
	private int [] root;	//\u30eb\u30fc\u30c8\u5ea7\u6a19(\u7d76\u5bfe\u8868\u8a18)
	private int kind;			//\u52a0\u70b9\u6587\u5b57\u5217\u306e\u7a2e\u985e
	private int player;		//\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7 (NumLayer/NumLayers\u3067\u306f\u4f7f\u7528\u3057\u306a\u3044)

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

	//\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7\u3092\u30bb\u30c3\u30c8\u3059\u308b
	public void setPlayer(int _player) {
		player = _player;
	}

	public int getPlayer() {
		return player;
	}

	//\u52a0\u70b9\u6587\u5b57\u5217\u306e\u7a2e\u985e\u3092\u8fd4\u3059
	public int getKind() {
		return kind;
	}

	//\u30eb\u30fc\u30c8\u304b\u3089\u306e\u65b9\u5411\u3092\u8fd4\u3059
	public int [] getDirInt() {
		int [] d = new int [super.size()];
		for (int i = 0; i < d.length; ++i) {
			d[i] = super.get(i);
		}
		return d;
	}

	//\u30eb\u30fc\u30c8\u5ea7\u6a19\u3092\u8fd4\u3059
	public int [] getRoot() {
		return root;
	}

	//\u76f8\u5bfe\u5ea7\u6a19\u3092\u8fd4\u3059(\u6700\u521d\u306f\u30eb\u30fc\u30c8\u306e\u7d76\u5bfe\u5ea7\u6a19)
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

	//\u7d76\u5bfe\u5ea7\u6a19\u3067\u8fd4\u3059(\u6700\u521d\u306f\u30eb\u30fc\u30c8\u306e\u7d76\u5bfe\u5ea7\u6a19)
	public int [][] getAbsolutePos() {
		int [][] pos = getRelativePos();
		for (int i = 1; i < pos.length; ++i) {
			pos[i][0] += pos[i-1][0];
			pos[i][1] += pos[i-1][1];
		}
		return pos;
	}

	//\u90e8\u5206\u96c6\u5408\u307e\u305f\u306f\u7b49\u4fa1\u306a\u52a0\u70b9\u6587\u5b57\u5217\u304b\u5224\u5b9a\u3059\u308b
	//\u3053\u306eScoredString\u3092A\u3068\u3057\uff64\u5f15\u6570o\u3092B\u3068\u3057\u305f\u3068\u304d\uff64A\u2283B\u307e\u305f\u306fA=B\u306e\u3068\u304dtrue\u3092\u8fd4\u3059
	public boolean includingStrings(ScoredString o) {
		int [][] shorterString = o.getAbsolutePos();		//\u77ed\u3044\u3068\u4eee\u5b9a\u3057\u305f\u3068\u304d\u306eScoredString\u306e\u7d76\u5bfe\u5ea7\u6a19
		int [][] longerString = this.getAbsolutePos();	//\u9577\u3044\u3068\u3003

		//shorterString\u304clongerString\u3088\u308a\u9577\u304b\u3063\u305f\u3089false\u3092\u8fd4\u3059
		if ( longerString.length - shorterString.length < 0 ) {
			return false;
		}

		boolean b = true;
		//\u6607\u9806\u3068\u6607\u9806\u306b\u691c\u7d22
		for (int i = 0; i < shorterString.length; i++) {
			//println("(" + shorterString[i][0] + ", " + shorterString[i][1] + ") : (" + longerString[i][0] + ", " + longerString[i][1] + ")");
			//\u5ea7\u6a19\u304c\u7b49\u3057\u304f\u306a\u304b\u3063\u305f\u3089\u629c\u3051\u308b
			if ( !Arrays.equals(shorterString[i], longerString[i]) ) {
				b = false;
				break;
			}
		}
		if (b) {
			return true;
		}

		//\u6607\u9806\u3068\u964d\u9806\u306b\u691c\u7d22
		for (int i = 0; i < shorterString.length; i++) {
			//println("(" + shorterString[i][0] + ", " + shorterString[i][1] + ") : (" + longerString[i][0] + ", " + longerString[i][1] + ")");
			if ( !Arrays.equals(shorterString[shorterString.length-i-1], longerString[i]) ) {
				return false;
			}
		}

		return true;
	}
}
//\u6587\u5b57\u30c7\u30fc\u30bf (\u6587\u5b57\u756a\u53f7\uff64\u70b9\uff65\u8fba\uff64\u30ab\u30fc\u30c9\u306e\u753b\u50cf)
static class SignData {
  static HashMap <Character, int []> num = new HashMap <Character, int []>();  //\u6587\u5b57\u756a\u53f7
  static HashMap <Character, Integer> ps = new HashMap <Character, Integer>(); //\u70b9\uff65\u8fba
  static HashMap <Character, PImage []> orgCards = new HashMap <Character, PImage []>();  //\u30ab\u30fc\u30c9\u306e\u5143\u753b\u50cf
  static HashMap <Character, PImage []> cards = new HashMap <Character, PImage []>();//\u30ab\u30fc\u30c9\u306e\u753b\u50cf

  //\u30ed\u30fc\u30c9\u3057\u305f\u6587\u5b57\u6570\u3092\u8fd4\u3059
  static public int getSignSize() {
    return cards.size();
  }

  //\u6587\u5b57\u306eSet\u3092\u8fd4\u3059
  static public Set <Character> getSignSet() {
    return cards.keySet();
  }

  /*
  \u6587\u5b57\u756a\u53f7\u3092\u53d6\u5f97\u3059\u308b
    getNum(\u6587\u5b57\u306echar, \u6587\u5b57\u306e\u5411\u304d)
  */
  static public int [] getNum(char _ch, int _dir) {
    if (!cards.containsKey(_ch)) {
      return num.get(' ');
    }
    int [] newNum = Arrays.copyOf( num.get(_ch), 4 );
    return rotateNum(newNum, _dir);
  }

  /*
  \u70b9\uff65\u8fba\u30c7\u30fc\u30bf\u3092\u53d6\u5f97\u3059\u308b
    getPS(\u6587\u5b57\u306echar, \u6587\u5b57\u306e\u5411\u304d)
  */
  static public int getPS(char _ch, int _dir) {
    if (!cards.containsKey(_ch)) {
      return ps.get(' ');
    }
    return rotatePS(ps.get(_ch), _dir);
  }

  /*
  \u753b\u50cf\u3092\u53d6\u5f97\u3059\u308b
    getImage(\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7, \u6587\u5b57\u306echar, \u6587\u5b57\u306e\u5411\u304d)
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

  //\u30ab\u30fc\u30c9\u306e\u753b\u50cf\u3092\u4ee3\u5165\u3057\uff64\u6587\u5b57\u30c7\u30fc\u30bf\u3092\u30ed\u30fc\u30c9\u3059\u308b
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

  //\u30c7\u30fc\u30bf\u3092\u4ee3\u5165\u3059\u308b
  //  putHashMap(\u6587\u5b57, \u6587\u5b57\u756a\u53f7(\u5317), (\u897f), (\u5357), (\u6771), \u753b\u50cf)
  static private void putHashMap(char _ch, int _num0, int _num1, int _num2, int _num3, int _ps, PImage [] _img) {
    int [] n = {_num0, _num1, _num2, _num3};
    num.put(_ch, n);
    ps.put(_ch, _ps);
    orgCards.put(_ch, _img);
    cards.put(_ch, _img);
  }

  //\u6587\u5b57\u756a\u53f7\u3092\u56de\u8ee2\u3055\u305b\u308b
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

  //\u70b9\uff65\u8fba\u30c7\u30fc\u30bf\u3092\u56de\u8ee2\u3055\u305b\u308b
  static private int rotatePS(int _ps, int _dir) {
    int newPs = 0;

    //\u56de\u8ee2\u306a\u3057
    if (_dir == 0) {
      newPs = _ps;
    }

    //90\u5ea6\u56de\u8ee2 (\u53cd\u6642\u8a08\u56de\u308a)
    if (_dir == 1 || _dir == 3) {
      for (int i = 0; i < 4; i++) {
        newPs ^= (_ps & (0x800 >>> 2*i)) << 4+i;    //0000 1000 0000 0000
        newPs ^= (_ps & (0x400 >>> 2*i)) >>> 7-i;   //0000 0100 0000 0000
        newPs ^= (_ps & (0x8000 >>> i)) >>> 11-3*i; //1000 0000 0000 0000
        newPs ^= (_ps & (0x8 >>> i)) << 2+3*i;      //0000 0000 0000 1000
      }
      _ps = newPs;
    }
    //180\u5ea6\u56de\u8ee2
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
//\u30b9\u30c6\u30fc\u30bf\u30b9\u3092\u8868\u793a (\u30d7\u30ec\u30a4\u30e4\u30fc\u5225)

class StatusUI {
  int player;  //\u5272\u308a\u5f53\u3066\u3089\u308c\u305f\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
  int bcX, bcY; //\u4e2d\u592e\u4e0a\u306e\u5ea7\u6a19
  int w, h; //\u30b9\u30c6\u30fc\u30bf\u30b9\u90e8\u5206\u306e\u30b5\u30a4\u30ba

  StatusUI (int _player) {
    player = _player;
  }

  //\u4e2d\u5fc3\u5ea7\u6a19\u3092\u30bb\u30c3\u30c8
  public void setPos(int _x, int _y) {
    bcX = _x;
    bcY = _y;
  }

  //\u8868\u793a\u9818\u57df\u3092\u30bb\u30c3\u30c8
  public void setSize(int _w, int _h) {
    w = _w;
    h = _h;
  }

  //\u5834\u306e\u8868\u793a\u7bc4\u56f2\u3092\u8fd4\u3059 (\u5de6\u4e0a\uff64\u53f3\u4e0b)
  public int [][] getStatusRange() {
    int [] pos1 = { bcX - w/2, bcY - (player == 1 ? 0 : h) };
    int [] pos2 = { bcX + w/2, bcY + (player == 1 ? h : 0) };
    return new int [][] {pos1, pos2};
  }

  //\u30b9\u30c6\u30fc\u30bf\u30b9\u3092\u63cf\u753b
  public void draw(int _x, int _y, int _w, int _h) {
    setPos(_x, _y);
    setSize(_w, _h);

    pushMatrix();
    translate(bcX, bcY);

    fill(220, 220, 220, 200);
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
      //\u30bf\u30fc\u30f3\u8868\u793a
      if (GameData.turn == player){
        fill(43, 69, 241);
        text("Your Turn",0, h/3);
      }
    } else if (GameData.turn == 0) {  //\u30b2\u30fc\u30e0\u30bb\u30c3\u30c8\u306a\u3089
      int winner = GameData.winner;
      String message = "";
      //\u52dd\u6557\u8868\u793a
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
  int fieldID = getPointingField(mouseX, mouseY);   //\u30de\u30a6\u30b9\u30dd\u30a4\u30f3\u30bf\u304c\u6307\u3057\u793a\u3059\u5834\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
  int statusID = getPointingStatus(mouseX, mouseY); // \u3003 \u30b9\u30c6\u30fc\u30bf\u30b9\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7

  if (GameData.turn >= 1) {
    switch (mouseButton) {
      case LEFT :
        //\u30d7\u30ec\u30a4\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u5834\u306e\u4e2d\u306a\u3089\u30db\u30fc\u30eb\u30c9\u3059\u308b
        if (fieldID == GameData.turn) holdingInField();
      break;
      case CENTER :
        //\u30db\u30fc\u30eb\u30c9\u4e2d\u306e\u30ab\u30fc\u30c9\u3092\u56de\u8ee2\u3055\u305b\u308b
        if (holding) rotateHoldingBS();
      break;
    }
  }

  if (mouseButton == LEFT) {
    //\u76e4\u4e0a\u306a\u3089\u76e4\u3092\u79fb\u52d5\u3059\u308b\u30d5\u30e9\u30b0\u3092\u7acb\u3066\u308b
    if (fieldID == 0 && statusID == 0) {
      dragging = 1;
      surface.setCursor(13);
    }
  }
}

public void mouseReleased() {
  int fieldID = getPointingField(mouseX, mouseY);   //\u30de\u30a6\u30b9\u30dd\u30a4\u30f3\u30bf\u304c\u6307\u3057\u793a\u3059\u5834\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
  int statusID = getPointingStatus(mouseX, mouseY); // \u3003 \u30b9\u30c6\u30fc\u30bf\u30b9\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7

  //\u76e4\u3092\u30c9\u30e9\u30c3\u30b0\u4e2d\u306a\u3089\u89e3\u9664\u3059\u308b
  if (dragging >= 1) {
    dragging = 0;
    surface.setCursor(0);
  }

  //\u30d7\u30ec\u30a4\u4e2d\u3067\u306a\u3044\u306a\u3089\u8fd4\u308b
  if (GameData.turn < 1) {
    return;
  }

  switch (mouseButton) {
    case LEFT :
      //\u5834\u306b\u3082\u30b9\u30c6\u30fc\u30bf\u30b9\u4e0a\u306b\u3082\u306a\u3044 (\u76e4\u5185)
      if (fieldID == 0 && statusID == 0) {
        //\u30db\u30fc\u30eb\u30c9\u4e2d\u304b\u3064\u76e4\u3092\u30c9\u30e9\u30c3\u30b0\u4e2d\u3067\u306a\u3044\u306a\u3089\u30ab\u30fc\u30c9\u3092\u7f6e\u304f
        if (holding && dragging != 2) putHoldingCard();
      }
    break;

    //\u53f3\u30af\u30ea\u30c3\u30af\u3067\u30db\u30fc\u30eb\u30c9\u3092\u30ad\u30e3\u30f3\u30bb\u30eb\u3059\u308b
    case RIGHT :
      if (holding) cancelHolding();
    break;
  }
}

public void mouseDragged() {
  //\u76e4\u3092\u79fb\u52d5
  if (mouseButton == LEFT) {
    moveBoard();
  }
}

public void keyPressed() {
  boolean playing = (GameData.turn >= 1);
  switch (keyCode) {
    case ' ' :        //\u30db\u30fc\u30eb\u30c9\u4e2d\u306e\u30ab\u30fc\u30c9\u3092\u56de\u8ee2
      if (playing && holding) rotateHoldingBS();
    break;
    case 'P' :        //\u5834\u304b\u3089\u30e9\u30f3\u30c0\u30e0\u306b\u30db\u30fc\u30eb\u30c9\u3059\u308b
      if (playing) holdingRandom();
    break;
    case 'A' :        //\u30aa\u30fc\u30c8\u30e2\u30fc\u30c9\u306e\u5207\u308a\u66ff\u3048
      if (playing) autoMode = !autoMode;
    break;
    case 'C' :        //\u76e4\u3092\u4e2d\u5fc3\u306b\u623b\u3059
      boardUI.movePos();
      println("Return at the Board Center");
    break;
    case 'R' :        //\u30e9\u30f3\u30c0\u30e0\u306b\u30ab\u30fc\u30c9\u3092\u7f6e\u304f
      if (playing) putCardRandom();
    break;
    case 'G' :        //\u753b\u9762\u3092\u9332\u753b\u3059\u308b(GIF\u30a2\u30cb\u30e1\u306e\u751f\u6210)
      turnMakingGIF();
    break;
    case 'S' :        //\u30b9\u30af\u30ea\u30fc\u30f3\u30b7\u30e7\u30c3\u30c8\u3092\u64ae\u308b
      shotScreen();
    break;
    case 'M' :
      saveResult("result" + gameRound);
    break;
    case DELETE :     //\u30ea\u30ed\u30fc\u30c9\u3059\u308b
      reload();
    break;
    case BACKSPACE :  //\u76e4\u9762\u3092\u30af\u30ea\u30a2\u306b\u3059\u308b
      clear();
    break;
  }
}
//\u521d\u671f\u914d\u7f6e\u306e\u30ab\u30fc\u30c9\u3092\u7f6e\u304f
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

//\u30db\u30fc\u30eb\u30c9\u4e2d\u306e\u30ab\u30fc\u30c9\u3092\u7f6e\u3044\u3066\u30bf\u30fc\u30f3\u30c1\u30a7\u30f3\u30b8
public void putHoldingCard() {
  //\u30de\u30a6\u30b9\u30dd\u30a4\u30f3\u30bf\u304c\u6307\u3057\u3066\u3044\u308b\u5de6\u4e0a\u5ea7\u6a19\u7cfb\u306e\u5ea7\u6a19
  int [] pos = boardUI.convertBoardPos( new int [] {mouseX, mouseY} );
  int [][] canPutPos = GameData.getCanPutPos(holdingBS); //\u7f6e\u3051\u308b\u5ea7\u6a19
  boolean hit = false;  //\u7f6e\u3051\u308b\u5ea7\u6a19\u306b\u7f6e\u3053\u3046\u3068\u3057\u3066\u3044\u308b\u304b

  //\u7f6e\u3051\u308b\u5ea7\u6a19\u304b\u3089\u30de\u30a6\u30b9\u30dd\u30a4\u30f3\u30bf\u304c\u6307\u3057\u3066\u3044\u308b\u5ea7\u6a19\u3092\u691c\u7d22
  for (int i = 0; i < canPutPos.length; i++) {
    if (canPutPos[i][0] == pos[0] && canPutPos[i][1] == pos[1]) {
      hit = true;
      break;
    }
  }
  if (!hit) return;

  holdingBS.pos = pos;  //\u30db\u30fc\u30eb\u30c9\u4e2d\u306eBS\u306b\u5ea7\u6a19\u3092\u4ee3\u5165
  holding = false;

  if ( !board.putCard(holdingBS) ) {
    println("Failed to Put a Card");
  }

  int next = board.turnChange();  //\u6b21\u306e\u30bf\u30fc\u30f3\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
  if (next != 0) {
    println("Next Turn: " + next);
    println("All Moves: " + GameData.getCanPutCCSum());
  } else {
    println("GAME SET");
  }
}

//\u6307\u5b9a\u30d7\u30ec\u30a4\u30e4\u30fc\u304c\u30ab\u30fc\u30c9\u3092\u30db\u30fc\u30eb\u30c9\u3059\u308b
public void holdingInField(){
  int player = GameData.turn; //\u73fe\u5728\u306e\u30bf\u30fc\u30f3
  int [] pos = fieldUI[player-1].convertBoardPos( new int []{mouseX, mouseY} ); //\u5de6\u4e0a\u5ea7\u6a19\u7cfb\u306e\u5ea7\u6a19
  ArrayList <BoardSign> bsList = new ArrayList<BoardSign> ( GameData.getFieldSigns() ); //\u5834\u306b\u3042\u308bBS

  for (BoardSign bs : bsList){
    //\u30de\u30a6\u30b9\u30dd\u30a4\u30f3\u30bf\u304c\u6307\u3059\u5ea7\u6a19\u3068\u5834\u306e\u5ea7\u6a19\u304c\u4e00\u81f4\u3057\u305f\u3089
    if ( bs.equalsPos(player, pos[0], pos[1]) ){
      //\u30db\u30fc\u30eb\u30c9\u4e2d\u306f\u6301\u3064\u30ab\u30fc\u30c9\u3092\u5207\u308a\u66ff\u3048\u308b (\u30db\u30fc\u30eb\u30c9\u4e2d\u306e\u30ab\u30fc\u30c9\u3092\u5834\u306b\u623b\u3059)
      if (holding) {
        GameData.addFieldSign(holdingBS);
      }
      holdingBS = new BoardSign(bs);  //\u5834\u306eBS\u3092holdingBS\u306b\u30b3\u30d4\u30fc
      GameData.removeFieldSign(bs);   //\u5834\u304b\u3089BS\u3092\u524a\u9664
      holding = true;
      println("Player " + GameData.turn + " held " + holdingBS.getChar() + ":" + holdingBS.getDir() + "\" at (" + holdingBS.getX() + ", " + holdingBS.getY() + ")");
    }
  }
}

//\u30db\u30fc\u30eb\u30c9\u3092\u30ad\u30e3\u30f3\u30bb\u30eb\u3059\u308b
public void cancelHolding() {
  GameData.addFieldSign(holdingBS); //\u5834\u306bholdingBS\u3092\u623b\u3059
  holding = false;
}

//\u30db\u30fc\u30eb\u30c9\u4e2d\u306e\u30ab\u30fc\u30c9\u3092\u63cf\u753b
//\u5f15\u6570: \u63cf\u753b\u3059\u308b\u30ab\u30fc\u30c9\u306e\u4e2d\u5fc3\u5ea7\u6a19
public void drawHoldingBS(int _centerX, int _centerY) {
  PImage img = SignData.getImage( holdingBS.getPlayer(), holdingBS.getChar() );
  imageMode(CENTER);
  pushMatrix();
  translate(_centerX, _centerY);
  rotate( -holdingBS.getDir()*PI*0.5f );
  image(img, 0, 0, GameData.cardSize, GameData.cardSize);
  popMatrix();
}

//\u30db\u30fc\u30eb\u30c9\u4e2d\u306e\u30ab\u30fc\u30c9\u3092\u56de\u8ee2\u3059\u308b (\u53cd\u6642\u8a08\u56de\u308a)
public void rotateHoldingBS() {
  holdingBS.dir = ( holdingBS.getDir() + 1 ) % 4;
  println("Rotate Holding Card");
}

//\u76e4\u3092\u52d5\u304b\u3059
public void moveBoard() {
  if (dragging != 0) {
    //\u73fe\u30d5\u30ec\u30fc\u30e0\u3068\u524d\u30d5\u30ec\u30fc\u30e0\u306e\u30de\u30a6\u30b9\u30dd\u30a4\u30f3\u30bf\u5ea7\u6a19\u306e\u5dee\u5206
    int dx = mouseX - pmouseX;
    int dy = mouseY - pmouseY;
    if (dx != 0 || dy != 0) {
      boardUI.movePos(dx, dy);  //\u5dee\u5206\u3060\u3051\u76e4\u3092\u52d5\u304b\u3059
      dragging = 2;
    }
  }
}

//\u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u304c\u3069\u306e\u5834\u306e\u4e0a\u306b\u3042\u308b\u304b\u3092\u8fd4\u3059
//\u8fd4\u308a\u5024: 0:\u8a72\u5f53\u306a\u3057 1\u4ee5\u964d:\u5834\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
public int getPointingField(int _x, int _y) {
  int id = 0;
  int [][] f1 = fieldUI[0].getFieldRange(); //1P\u306e\u5834\u306e\u8868\u793a\u7bc4\u56f2
  int [][] f2 = fieldUI[1].getFieldRange(); //2P\u306e \u3003
  if (f1[0][0] < _x && f1[1][0] > _x && f1[0][1] < _y && f1[1][1] > _y) {
    id = 1;
  }
  if (f2[0][0] < _x && f2[1][0] > _x && f2[0][1] < _y && f2[1][1] > _y) {
    id = 2;
  }
  return id;
}

//\u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u304c\u3069\u306e\u30b9\u30c6\u30fc\u30bf\u30b9\u8868\u793a\u4e0a\u306b\u3042\u308b\u304b\u3092\u8fd4\u3059
//\u8fd4\u308a\u5024: 0:\u8a72\u5f53\u306a\u3057 1\u4ee5\u964d:\u5834\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
public int getPointingStatus(int _x, int _y) {
  int id = 0;
  int [][] s1 = statusUI[0].getStatusRange(); //1P\u306e\u30b9\u30c6\u30fc\u30bf\u30b9\u306e\u8868\u793a\u7bc4\u56f2
  int [][] s2 = statusUI[1].getStatusRange(); //2P\u306e \u3003
  if (s1[0][0] < _x && s1[1][0] > _x && s1[0][1] < _y && s1[1][1] > _y) {
    id = 1;
  }
  if (s2[0][0] < _x && s2[1][0] > _x && s2[0][1] < _y && s2[1][1] > _y) {
    id = 2;
  }
  return id;
}

//\u30ed\u30fc\u30c7\u30a3\u30f3\u30b0\u753b\u9762\u306e\u8868\u793a
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

//\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306b\u521d\u671f\u306e\u5834\u306e\u6587\u5b57\u3092\u8ffd\u52a0
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
\u6587\u5b57\u306e\u30c7\u30fc\u30bf\u3092\u30ed\u30fc\u30c9\u3059\u308b (\u5fc5\u305a\u4ed6\u306e\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u5316\u3088\u308a\u5148\u306b\u884c\u3046\u3053\u3068)
  loadSignData(\u30ed\u30fc\u30c9\u3059\u308b\u753b\u50cf\u306e\u5165\u3063\u305f\u30d1\u30b9[\u8272\u5225], \u5834\u306b\u7f6e\u304f\u6587\u5b57\u306e\u914d\u5217)
\u6307\u5b9a\u3057\u305f\u5834\u306b\u7f6e\u304f\u6587\u5b57\u306e\u753b\u50cf\u306e\u307f\u30ed\u30fc\u30c9\u3057\u3001
char\u3068PImage\u306e\u30bb\u30c3\u30c8\u3092SignData\u306b\u9001\u308b (SignData\u5185\u3067\u305d\u306e\u307b\u304b\u306e\u6587\u5b57\u30c7\u30fc\u30bf\u304c\u30ed\u30fc\u30c9\u3055\u308c\u308b)
*/
public void loadSignData(String [] _path, char [] _fieldSigns) {
  println("Loading SignData: ");

  //HashSet\u3092\u5229\u7528\u3057\u3066\u5834\u306e\u6587\u5b57\u304b\u3089\u91cd\u8907\u3092\u524a\u9664\u3059\u308b
  Set <Character> signs = new HashSet <Character>();
  signs.add(' ');
  for (char ch : _fieldSigns) {
    signs.add(ch);
  }

  for (char ch : signs) {
    PImage [] img = new PImage[GameData.PLAYERS];
    print("\'" + String.valueOf(ch) + "\' ");
    char newCh = ch;
    //\u30d5\u30a1\u30a4\u30eb\u540d\u306b\u4f7f\u7528\u3067\u304d\u306a\u3044\u6587\u5b57\u3092\u5909\u63db\u3059\u308b
    if (ch == ' ') {
      newCh = '@';
    } else if (ch == '*') {
      newCh = '\uff0a';
    }

    for (int i = 0; i < GameData.PLAYERS; i++) {
      img[i] = loadImage(_path[i] + "\\" + newCh + ".png"); //\u753b\u50cf\u3092\u30ed\u30fc\u30c9\u3059\u308b
    }

    SignData.load(ch, img); //SignData\u306b\u753b\u50cf\u3092\u4ee3\u5165
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
//\u5834\u304b\u3089\u30e9\u30f3\u30c0\u30e0\u306b\u30db\u30fc\u30eb\u30c9\u3059\u308b
public void holdingRandom() {
  ArrayList <BoardSign> bsList = new ArrayList<BoardSign> ( GameData.getFieldSigns() ); //\u5834\u306b\u3042\u308bBS
  if (bsList.size() == 0) return;
  BoardSign bs = bsList.get( (int)random(bsList.size()) );  //\u5834\u306eBS\u304b\u3089\u30e9\u30f3\u30c0\u30e0\u306b\u9078\u3076

  if (holding) {
    GameData.addFieldSign(holdingBS);
  }
  holdingBS = new BoardSign(bs);  //holdingBS\u306b\u30b3\u30d4\u30fc
  GameData.removeFieldSign(bs);   //\u5834\u304b\u3089BS\u3092\u524a\u9664
  holding = true;
  println("Player " + GameData.turn + " held " + holdingBS.getChar() + ":" + holdingBS.getDir() + "\" at (" + holdingBS.getX() + ", " + holdingBS.getY() + ") at random");
}

//\u30e9\u30f3\u30c0\u30e0\u306b\u30ab\u30fc\u30c9\u3092\u7f6e\u3044\u3066\u30bf\u30fc\u30f3\u30c1\u30a7\u30f3\u30b8
public void putCardRandom() {
  if (holding) return;  //\u30db\u30fc\u30eb\u30c9\u4e2d\u306f\u7f6e\u3051\u306a\u3044

  if ( board.putCardRandom() ) {
  } else {
    println("Failed to Put a Card");
  }

  int next = board.turnChange();  //\u6b21\u306e\u30bf\u30fc\u30f3\u306e\u30d7\u30ec\u30a4\u30e4\u30fc\u756a\u53f7
  if (next != 0) {
    println("Next Turn: " + next);
  } else {
    println("GAME SET");
  }
}

//\u81ea\u52d5\u3067\u30ab\u30fc\u30c9\u3092\u7f6e\u3044\u3066\u30bf\u30fc\u30f3\u30c1\u30a7\u30f3\u30b8
public void autoPut(boolean _save) {
  if (GameData.turn >= 1 && !holding) {
    if (!board.putCardRandom()) {
      println("Failed to Put a Card");
    }
    if (board.turnChange() == 0) {
      println("GAME SET");
      if (_save) saveResult("result" + gameRound);
      clear();
    }
  }
}

//GIF\u30a2\u30cb\u30e1\u306e\u9332\u753b\u306e\u5207\u308a\u66ff\u3048
public void turnMakingGIF() {
  if (recoding) {
    gifMaker.finish();
    recoding = false;
    recodeTimes++;
    println("Finish Recoding");
  } else {
    gifMaker = new GifMaker(this, "animation_" + recodeTimes + ".gif");
    gifMaker.setRepeat(0);  //GIF\u3092\u7121\u9650\u30eb\u30fc\u30d7\u3055\u305b\u308b
    gifMaker.setDelay(2);   //\u30a6\u30a7\u30a4\u30c8\u3092\u8a2d\u3051\u308b
    recoding = true;
    println("Start Recoding...");
  }
}

//\u30b9\u30af\u30ea\u30fc\u30f3\u30b7\u30e7\u30c3\u30c8\u3092\u64ae\u308b
public void shotScreen() {
  save("screenshot_" + shotTimes + ".png");
  shotTimes++;
  println("Captured the Screen");
}

//\u5bfe\u5c40\u7d50\u679c\u3092\u30a8\u30af\u30b9\u30dd\u30fc\u30c8
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
