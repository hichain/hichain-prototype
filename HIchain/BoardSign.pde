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
  int getPlayer() {
    return player;
  }

  //座標を返す
  int [] getPos() {
    return pos;
  }

  //X座標を返す
  int getX() {
    return pos[0];
  }

  //Y座標を返す
  int getY() {
    return pos[1];
  }

  //文字を返す
  char getChar() {
    return ch;
  }

  //文字の向きを返す
  int getDir() {
    return dir;
  }

  //プレイヤー番号が等しいか
  boolean equalsPl(int _player) {
    return (player == _player);
  }

  //文字と文字の向きが等しいか
  boolean equalsChDir(char _ch, int _dir) {
    return ( (ch == _ch) && (dir == _dir) );
  }

  //文字と文字の向きが等しいか (プレイヤー番号込み)
  boolean equalsChDir(int _player, char _ch, int _dir) {
    return ( (player == _player) && (ch == _ch) && (dir == _dir) );
  }

  //座標が等しいか
  boolean equalsPos(int _x, int _y) {
    return ( (pos[0] == _x) && (pos[1] == _y) );
  }

  //座標が等しいか (プレイヤー番号込み)
  boolean equalsPos(int _player, int _x, int _y) {
    return ( (player == _player) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //全ての値が等しいか (プレイヤー番号抜き)
  boolean equals(char _ch, int _dir, int _x, int _y) {
    return ( (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //全ての値が等しいか
  boolean equals(int _player, char _ch, int _dir, int _x, int _y) {
    return ( (player == _player) && (ch == _ch) && (dir == _dir) && (pos[0] == _x) && (pos[1] == _y) );
  }

  //プレイヤー番号が等しいか (BoardSignクラスから)
  boolean equalsPl(BoardSign _bs) {
    return (player == _bs.player);
  }

  //文字と文字の向きが等しいか (BoardSignクラスから)
  boolean equalsChDir(BoardSign _bs) {
    return ( (player == _bs.player) && (ch == _bs.ch) && (dir == _bs.dir) );
  }

  //座標が等しいか (BoardSignクラスから)
  boolean equalsPos(BoardSign _bs) {
    return ( (player == _bs.player) && (pos[0] == _bs.pos[0]) && (pos[1] == _bs.pos[1]) );
  }

  //全ての値が等しいか (BoardSignクラスから)
  boolean equals(BoardSign _bs) {
    return ( (player == _bs.player) && (ch == _bs.ch) && (dir == _bs.dir) && (pos[0] == _bs.pos[0]) && (pos[1] == _bs.pos[1]) );
  }
}