void mousePressed() {
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

void mouseReleased() {
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

void mouseDragged() {
  //盤を移動
  if (mouseButton == LEFT) {
    moveBoard();
  }
}

void keyPressed() {
  boolean playing = (GameData.turn >= 1);
  switch (keyCode) {
    case ' ' :        //ホールド中のカードを回転
      if (playing && holding) rotateHoldingBS();
    break;
    case 'P' :        //場からランダムにホールドする
      if (playing) holdingRandom();
    break;
    case 'A' :        //オートモードの切り替え
      if (playing) autoMode = !autoMode;
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
      reload();
    break;
    case BACKSPACE :  //盤面をクリアにする
      clear();
    break;
  }
}