//盤・場・ステータスのデータを保持する

import java.util.*;

static class GameData {
  public static final int PLAYERS = 2;  //プレイヤー数
  public static int cardSize;           //カードサイズ
  public static int fieldRowNum;        //場の列数
  public static int turn = -3;          //ターン (-3:起動前 -2:画像のローディング中 -1:初期化中 0:ゲームセット 1～:プレイ中のプレイヤー番号)
  private static int [] points;         //ポイント
  public static int winner;             //優勢のプレイヤー (0: 引き分け 1～: 優勢のプレイヤー番号)

  private static ArrayList <BoardSign> boardSigns;    //盤上の文字 (BoardSign)
  private static ArrayList <BoardSign> fieldSigns;    //場にある文字 (BoardSign)
  private static ArrayList <BoardSign> canPutCC;      //特定の文字・向きで置ける座標の組み合わせ (BoardSign)
  private static ArrayList <ScoredString> sStrings;   //加点文字列 (ScoredString)

  public static void clear() {
    boardSigns = new ArrayList <BoardSign>();
    fieldSigns = new ArrayList <BoardSign>();
    canPutCC = new ArrayList <BoardSign>();
    sStrings = new ArrayList <ScoredString>();
    points = new int [PLAYERS];
    winner = 0;
  }

  //初期化 (カードサイズと場の列数をセット)
  public static void init(int _cardSize, int _num) {
    cardSize = _cardSize;
    fieldRowNum = _num;
    clear();
  }

  //場に文字を追加する
  public static void addFieldSign(BoardSign _fs) {
    if (_fs.getPlayer() == 1) {
      fieldSigns.add(0, _fs);
    } else if (_fs.getPlayer() == 2) {
      fieldSigns.add(_fs);
    }
  }

  //指定プレイヤーのポイントを返す
  public static int getPoints(int _player) {
    return points[_player-1];
  }

  //プレイ中のプレイヤーのポイントを返す
  public static int getPoints() {
    return points[turn-1];
  }

  //盤上のBoardSignのリストを返す
  public static ArrayList <BoardSign> getBoardSigns() {
    return boardSigns;
  }

  //場の列数を返す
  public static int getFieldRowNum() {
    return fieldRowNum;
  }

  //場にある文字数を返す (全プレイヤー)
  public static int getFieldSignNum() {
    return fieldSigns.size();
  }

  //指定プレイヤーの場にある文字を返す
  public static ArrayList <BoardSign> getFieldSigns(int _player) {
    int from = 0, to = fieldSigns.size();
    if (_player == 1) {
      for (int i = 0; i < fieldSigns.size(); i++) {
        if (fieldSigns.get(i).getPlayer() != 1) {
          to = i;
          break;
        }
      }
    } else if (_player == 2) {
      for (int i = fieldSigns.size()-1; i >= 0; i--) {
        if (fieldSigns.get(i).getPlayer() != 2) {
          from = i+1;
          break;
        }
      }
    }
    return new ArrayList <BoardSign> ( fieldSigns.subList(from, to) );
  }

  //プレイ中のプレイヤーの場にある文字を返す
  public static ArrayList <BoardSign> getFieldSigns() {
    return getFieldSigns(turn);
  }

  /*
  指定した文字と向きで置ける座標を返す
    getCanPutPos(BoardSign)
  返り値: int[][座標x,y]
  */
  public static int [][] getCanPutPos(BoardSign _bs) {
    ArrayList <int []> pos = new ArrayList <int []>();

    for (int i = 0; i < canPutCC.size(); i++) {
      if ( canPutCC.get(i).equalsChDir(_bs) ) {
          pos.add( canPutCC.get(i).getPos() );
      }
    }

    return pos.toArray(new int [pos.size()][2]);
  }

  //置ける座標の数(手数)を返す
  public static int getCanPutCCSum() {
    return canPutCC.size();
  }

  /*
  指定プレイヤーの指定した連鎖の加点文字列の座標を返す
    getSStringsPos(連鎖数)
  返り値: int[加点文字列][座標][x,y] (座標は左上座標系)
  */
  public static ArrayList <ScoredString> getSStrings(int _chain) {
    ArrayList <ScoredString> ssList = new ArrayList <ScoredString>();	//座標

    for (ScoredString ss : sStrings) {
      if (ss.size() == _chain-1) {
        ssList.add(ss);
      }
    }

    return ssList;
  }

  //加点文字列の連鎖の最大値を返す
  public static int getSStringsMax() {
    if (sStrings.size() == 0) {
      return 0;
    }
    int maxChain = 0;
    for (ScoredString ss : sStrings) {
      if (ss.size() > maxChain) {
        maxChain = ss.size();
      }
    }
    return maxChain+1;
  }

  //指定プレイヤーのポイントをセット
  public static void setPoints(int _player, int _points) {
    points[_player-1] = _points;

    winner = 0;
    int maxPoints = 0;
    for (int i = 0; i < PLAYERS; i++) {
      if (maxPoints < points[i]) {
        maxPoints = points[i];
        winner = i+1;
      }
    }

    println("Points: " + _points + " (Winner: " + winner + "P)");
  }

  //プレイ中のプレイヤーのポイントをセット
  public static void setPoints(int _points) {
    setPoints(turn, _points);
  }

  //盤上に文字を追加
  public static void addBoardSign(BoardSign _bs) {
    boardSigns.add(_bs);
    println("Player " + _bs.getPlayer() + " put \"" + _bs.getChar() + ":" + _bs.getDir() + "\" at (" + _bs.getX() + ", " + _bs.getY() + ")");
  }

  //指定のBoardSignを場から削除する
  public static void removeFieldSign(BoardSign _bs) {
    fieldSigns.remove(_bs);
  }

  //置ける組み合わせをセット
  public static void setCanPutCC(ArrayList <BoardSign> _bs) {
    canPutCC = new ArrayList <BoardSign>(_bs);
    println("All Moves: " + canPutCC.size());
  }

  //加点文字列を追加 (指定のプレイヤーの加点文字列を予め削除してから追加)
  public static void addSStrings(int _player, ArrayList <ScoredString> _ssList) {
    for (int i = 0; i < sStrings.size(); i++) {
      if (sStrings.get(i).getPlayer() == _player) {
        sStrings.remove(i);
        i--;
      }
    }
    sStrings.addAll(_ssList);
		println(sStrings.size() + " ScoredStrings Were Found");
  }
}