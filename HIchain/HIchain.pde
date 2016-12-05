import gifAnimation.*;

final String VERSION = "r1.0.1";   //バージョン
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

void settings() {
  size(sSize, sSize);
}

void setup() {
  smooth(2);
  PFont font = createFont("Arial", 48, true);
  textFont(font, 25);
  surface.setTitle("HIchain Prototype " + VERSION);
  //surface.setResizable(true);
}

void draw() {
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
void initialize(char [] _fieldSigns) {
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