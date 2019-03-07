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
  void putPS(int _player, int _ps, int _x, int _y) {
    bPS[_x][_y] = _ps;
    bPlayer[_x][_y] = _player;
  }


  //指定した座標に置ける"可能性があるか"判定する
  //mayPut(プレイヤー番号, x座標, y座標)
  boolean mayPut(int _player, int _x, int _y) {
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
  int canPut(int _player, int _ps, int _x, int _y) {
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
