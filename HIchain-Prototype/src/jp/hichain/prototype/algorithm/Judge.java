package jp.hichain.prototype.algorithm;

import jp.hichain.prototype.basic.AdBoardSign;
import jp.hichain.prototype.basic.CompleteBS;
import jp.hichain.prototype.basic.Move;

public class Judge {

	  /**
	   * PS判定
	   * ある座標に特定の文字を置いたときに置けるか判定する
	   * @param targetMove 目標座標のMove
	   * @param internalBS 置こうとしているBS
	   * @return 判定結果(-2: 相手の点で接していない | -1: 辺と辺で接している | 1: 置ける)
	   */
	  public static int canPut(Move targetMove, CompleteBS internalBS) {
	    int holdingPS = internalBS.getPS(); //持っている文字のPS
	    int aroundPS = getExternalPS(targetMove, internalBS);  //目標座標の外側のPS

	    //System.out.println("holdingPS: " + Integer.toBinaryString(holdingPS));
	    //System.out.println("aroundPS: " + Integer.toBinaryString(aroundPS));

	    int sum = holdingPS & aroundPS;
	    int sides = sum & 0x566A;  //辺だけ取り出す
	    //辺と辺で接している
	    if (sides != 0) {
	      return -1;
	    }
	    int points = sum & 0xA995; //点だけ取り出す
	    //相手の点で接してない
	    if (points == 0) {
	      return -2;
	    }
	    return 1;
	  }

	  /**
	   * 指定した座標の周りのPSを抽出する
	   * @param targetMove 目標座標のMove
	   * @param player 置こうとする文字のプレイヤー番号
	   * @return 外側のPS
	   */
	  private static int getExternalPS(Move targetMove, CompleteBS internalBS) {
	    int externalPS = 0; //生成する外側のPS
	    int [] partPS = new int [8];  //externalPSの区画8つ
	    int [] aroundPS = new int [8];//周りのPS

	    AdBoardSign [] aroundBS = targetMove.getAroundBS();
	    for (int i = 0; i < 8; i++) {
	      if (aroundBS[i] == null) {
	        aroundPS[i] = 0;
	        continue;
	      }
	      if (aroundBS[i].getPlayer() == internalBS.getPlayer()) {
	        aroundPS[i] = aroundBS[i].getPS() & 0x566A;  //辺だけを取り出す
	      } else {
	        aroundPS[i] = aroundBS[i].getPS(); //そのまま取り出す
	      }
	    }

	    //周りからPSを取得
	    partPS[0] = (aroundPS[0] & 0xE) << 11;
	    partPS[1] = (aroundPS[1] & 0x2A0) >>> 1;
	    partPS[2] = (aroundPS[2] & 0x7000) >>> 11;
	    partPS[3] = (aroundPS[3] & 0x540) << 1;
	    partPS[4] = ((aroundPS[1] & 0x800) << 4) | ((aroundPS[0] & 0x10) << 11) | ((aroundPS[4] & 0x1) << 15);
	    partPS[5] = ((aroundPS[2] & 0x8000) >>> 11) | ((aroundPS[5] & 0x800) >>> 7) | ((aroundPS[1] & 0x1) << 4);
	    partPS[6] = ((aroundPS[6] & 0x8000) >>> 15) | ((aroundPS[2] & 0x800) >>> 11) | ((aroundPS[3] & 0x10) >>> 4);
	    partPS[7] = ((aroundPS[3] & 0x8000) >>> 4) | ((aroundPS[7] & 0x10) << 7) | ((aroundPS[0] & 0x1) << 11);

	    //aroundにpartPSを入れていく
	    for (int i = 0; i < partPS.length; i++) {
	      //println(Integer.toBinaryString(partPS[i]));
	      externalPS += partPS[i];
	    }

	    return externalPS;
	  }
	}
