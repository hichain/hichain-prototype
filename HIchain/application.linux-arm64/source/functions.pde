//初期配置のカードを置く
void putInitialCards() {
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
void putHoldingCard() {
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
void holdingInField(){
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
void cancelHolding() {
  GameData.addFieldSign(holdingBS); //場にholdingBSを戻す
  holding = false;
}

//ホールド中のカードを描画
//引数: 描画するカードの中心座標
void drawHoldingBS(int _centerX, int _centerY) {
  PImage img = SignData.getImage( holdingBS.getPlayer(), holdingBS.getChar() );
  imageMode(CENTER);
  pushMatrix();
  translate(_centerX, _centerY);
  rotate( -holdingBS.getDir()*PI*0.5 );
  image(img, 0, 0, GameData.cardSize, GameData.cardSize);
  popMatrix();
}

//ホールド中のカードを回転する (反時計回り)
void rotateHoldingBS() {
  holdingBS.dir = ( holdingBS.getDir() + 1 ) % 4;
  println("Rotate Holding Card");
}

//盤を動かす
void moveBoard() {
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
int getPointingField(int _x, int _y) {
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
int getPointingStatus(int _x, int _y) {
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
void drawLoading() {
  background(255);
  fill(#559CF7);
  textAlign(CENTER, CENTER);
  text("LOADING...", width/2, height/2);
  textAlign(RIGHT, DOWN);
  fill(0);
  textSize(20);
  text(VERSION, width-20, height-20);
}

//データベースに初期の場の文字を追加
void addInitialFieldSigns(char [] _fieldSigns) {
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
void loadSignData(String [] _path, char [] _fieldSigns) {
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

void clear() {
  GameData.turn = -1;
  GameData.clear();
  gameRound++;
  println("RESET");
}

void reload() {
  GameData.turn = -3;
  GameData.clear();
  println("RESET ALL");
}
