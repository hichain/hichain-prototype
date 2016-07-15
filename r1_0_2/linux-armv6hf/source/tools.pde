//場からランダムにホールドする
void holdingRandom() {
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
void putCardRandom() {
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
void autoPut(boolean _save) {
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

//GIFアニメの録画の切り替え
void turnMakingGIF() {
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
void shotScreen() {
  save("screenshot_" + shotTimes + ".png");
  shotTimes++;
  println("Captured the Screen");
}

//対局結果をエクスポート
void saveResult(String _filename) {
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