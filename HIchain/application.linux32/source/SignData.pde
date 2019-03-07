//文字データ (文字番号､点･辺､カードの画像)
static class SignData {
  static HashMap <Character, int []> num = new HashMap <Character, int []>();  //文字番号
  static HashMap <Character, Integer> ps = new HashMap <Character, Integer>(); //点･辺
  static HashMap <Character, PImage []> orgCards = new HashMap <Character, PImage []>();  //カードの元画像
  static HashMap <Character, PImage []> cards = new HashMap <Character, PImage []>();//カードの画像

  //ロードした文字数を返す
  static public int getSignSize() {
    return cards.size();
  }

  //文字のSetを返す
  static public Set <Character> getSignSet() {
    return cards.keySet();
  }

  /*
  文字番号を取得する
    getNum(文字のchar, 文字の向き)
  */
  static public int [] getNum(char _ch, int _dir) {
    if (!cards.containsKey(_ch)) {
      return num.get(' ');
    }
    int [] newNum = Arrays.copyOf( num.get(_ch), 4 );
    return rotateNum(newNum, _dir);
  }

  /*
  点･辺データを取得する
    getPS(文字のchar, 文字の向き)
  */
  static public int getPS(char _ch, int _dir) {
    if (!cards.containsKey(_ch)) {
      return ps.get(' ');
    }
    return rotatePS(ps.get(_ch), _dir);
  }

  /*
  画像を取得する
    getImage(プレイヤー番号, 文字のchar, 文字の向き)
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

  //カードの画像を代入し､文字データをロードする
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

  //データを代入する
  //  putHashMap(文字, 文字番号(北), (西), (南), (東), 画像)
  static private void putHashMap(char _ch, int _num0, int _num1, int _num2, int _num3, int _ps, PImage [] _img) {
    int [] n = {_num0, _num1, _num2, _num3};
    num.put(_ch, n);
    ps.put(_ch, _ps);
    orgCards.put(_ch, _img);
    cards.put(_ch, _img);
  }

  //文字番号を回転させる
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

  //点･辺データを回転させる
  static private int rotatePS(int _ps, int _dir) {
    int newPs = 0;

    //回転なし
    if (_dir == 0) {
      newPs = _ps;
    }

    //90度回転 (反時計回り)
    if (_dir == 1 || _dir == 3) {
      for (int i = 0; i < 4; i++) {
        newPs ^= (_ps & (0x800 >>> 2*i)) << 4+i;    //0000 1000 0000 0000
        newPs ^= (_ps & (0x400 >>> 2*i)) >>> 7-i;   //0000 0100 0000 0000
        newPs ^= (_ps & (0x8000 >>> i)) >>> 11-3*i; //1000 0000 0000 0000
        newPs ^= (_ps & (0x8 >>> i)) << 2+3*i;      //0000 0000 0000 1000
      }
      _ps = newPs;
    }
    //180度回転
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
