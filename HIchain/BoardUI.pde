
//盤の表示
class BoardUI {
  protected int initX, initY; //盤の中心座標(初期値)
  protected int bcX, bcY;     //盤の中心座標
  int rowNum, lineNum;        //盤の行列数
  color [][] sStringsColor;   //加点文字列のカラー
  color canPutColor;          //置ける座標のカラー

  BoardUI(int _x, int _y, int _rowNum, int _lineNum) {
    initX = _x;
    initY = _y;
    bcX = _x;
    bcY = _y;
    rowNum = _rowNum;
    lineNum = _lineNum;
    setColor();
  }

  void setColor() {
    colorMode(HSB,360,100,100);
    sStringsColor = new color [][] {
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
  void movePos(int _x, int _y) {
    bcX += _x;
    bcY += _y;
  }

  //中心座標を初期値に戻す
  void movePos() {
    bcX = initX;
    bcY = initY;
  }

  //盤の座標系に変換して返す
  int [] convertBoardPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = {
      _pos[0] - bcX, _pos[1] - bcY
    };//mouseX,mouseY
    pos[0] = (pos[0] <= 0) ? (pos[0]-cardSize)/cardSize : (pos[0]+cardSize)/cardSize;
    pos[1] = (pos[1] <= 0) ? (pos[1]-cardSize)/cardSize : (pos[1]+cardSize)/cardSize;
    return pos;
  }

  //ウィンドウの座標系にして返す (マスの左上)
  int [] convertWindowPos(int [] _pos) {
    int cardSize = GameData.cardSize;
    int [] pos = new int [2];
    pos[0] = (_pos[0] <= 0) ? _pos[0]*cardSize : _pos[0]*cardSize-cardSize;
    pos[1] = (_pos[1] <= 0) ? _pos[1]*cardSize : _pos[1]*cardSize-cardSize;
    pos[0] += bcX;
    pos[1] += bcY;
    return pos;
  }

  void draw(BoardSign _holdingBS) {
    drawLattice();
    highlightCanPutPos(_holdingBS);
    highlightSStrings();
    drawCards();
  }

  void draw() {
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
    translate( pos[0]+cardSize*0.5, pos[1]+cardSize*0.5 );
    rotate( -_bs.getDir()*PI*0.5 );
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