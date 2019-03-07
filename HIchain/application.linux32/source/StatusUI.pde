//ステータスを表示 (プレイヤー別)

class StatusUI {
  int player;  //割り当てられたプレイヤー番号
  int bcX, bcY; //中央上の座標
  int w, h; //ステータス部分のサイズ

  StatusUI (int _player) {
    player = _player;
  }

  //中心座標をセット
  void setPos(int _x, int _y) {
    bcX = _x;
    bcY = _y;
  }

  //表示領域をセット
  void setSize(int _w, int _h) {
    w = _w;
    h = _h;
  }

  //場の表示範囲を返す (左上､右下)
  int [][] getStatusRange() {
    int [] pos1 = { bcX - w/2, bcY - (player == 1 ? 0 : h) };
    int [] pos2 = { bcX + w/2, bcY + (player == 1 ? h : 0) };
    return new int [][] {pos1, pos2};
  }

  //ステータスを描画
  void draw(int _x, int _y, int _w, int _h) {
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

    color myColor = color(0, 0, 0);
    if (player == 1){
      myColor = color(0, 0, 0);
    } else {
      myColor = color(255, 0, 0);
    }

    fill(myColor);
    textAlign(CENTER);
    textSize(20);
    text(GameData.getPoints(player) + " Point(s)", 0, h*2.0/3.0);
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
