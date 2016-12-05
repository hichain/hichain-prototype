//場の表示 (プレイヤー別)

class FieldUI extends BoardUI {
  int player;  //割り当てられたプレイヤー番号
  //bcX, bcYはFieldUIでは左上の座標とする

  FieldUI (int _player, int _rowNum, int _lineNum) {
    super(0, 0, _rowNum, _lineNum);
    player = _player;
  }

  //盤の座標系に変換して返す
  int [] convertBoardPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = {
      ((player == 1) ? 1 : -1) * (_pos[0] - bcX), ((player == 1) ? 1 : -1) * (_pos[1] - bcY)
    };//mouseX,mouseY

    pos[0] = (pos[0] <= 0) ? (pos[0]-cardSize)/cardSize : (pos[0]+cardSize)/cardSize;
    pos[1] = (pos[1] <= 0) ? (pos[1]-cardSize)/cardSize : (pos[1]+cardSize)/cardSize;

    return pos;
  }

  //場の表示範囲を返す (左上､右下)
  int [][] getFieldRange() {
    int cardSize = GameData.cardSize;
    int [] pos1 = new int [2];
    int [] pos2 = new int [2];

    pos1[0] = bcX - ( (player == 1) ? 0 : rowNum*cardSize );
    pos1[1] = bcY - ( (player == 1) ? 0 : lineNum*cardSize );
    pos2[0] = bcX + ( (player == 1) ? rowNum*cardSize : 0 );
    pos2[1] = bcY + ( (player == 1) ? lineNum*cardSize : 0 );

    return new int [][] {pos1, pos2};
  }

  void draw(int _x, int _y) {
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
      translate( (bs.getX()-0.5) * cardSize, (bs.getY()-0.5) * cardSize );
      if (player == 2) rotate(PI);
      rotate( -radians(bs.getDir()*90) );
      image( SignData.getImage( bs.getPlayer(), bs.getChar() ), 0, 0, cardSize, cardSize );
      popMatrix();
    }

    popMatrix();
  }
}