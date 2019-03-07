//全方向の文字番号レイヤー
//ポイント計算､加点文字列の取得

import java.util.*;

class NumLayers {
	boolean hitGodString = false;	//勝利確定文字列がヒットしたか
	ArrayList <ScoredString> sStrings = new ArrayList <ScoredString>();	//全方向の加点文字列

	NumLayer [] numLayer = new NumLayer[4];	//各方向の文字番号レイヤー

	//NumLayers(盤のサイズ, 勝利確定文字列の文字番号)
	NumLayers(int _size, int [] _godStringNum) {
		for (int i = 0; i < numLayer.length; ++i) {
			numLayer[i] = new NumLayer(_size, _godStringNum);
		}
	}

	//勝利確定文字列がヒットしたか
	boolean hitGodString() {
		return hitGodString;
	}

	//文字番号を代入
	void putSignNum(int [] _num, int _x, int _y) {
		for (int i = 0; i < numLayer.length; ++i) {
			numLayer[i].subNum(_num[i], _x, _y);
		}
	}

	//加点文字列からポイントを換算する (3連鎖以上の加点文字列)
	//※先にcalScoredStrings()をしないと更新されません
	int calPoints() {
		int points = 0;	//ポイント

		for (ScoredString ss : sStrings) {
			if (ss.getKind() == 0 || ss.getKind() == 2) {	//2連鎖を省く
				if (ss.size() >= 2)	points += (int)pow(ss.size()+1, 2);
			} else if (ss.getKind() == 1) {
				if (ss.size() >= 1)	points += (int)pow(ss.size()+1, 2);
			}
		}

		return points;
	}

	/*
	○ 指定範囲の全ての加点文字列を返す(連鎖の小さい順) (2連鎖を含む)
		calScoredStrings(検索する盤の範囲)
	*/
	ArrayList <ScoredString> calScoredStrings(int [][] _bRange) {
		//println(" - START Calculating SStrings - (" + _bRange[0][0] + ", " + _bRange[0][1] + ") ~ (" + _bRange[1][0] + ", " + _bRange[1][1] + ")");

		sStrings = new ArrayList<ScoredString>();	//初期化

		//numLayerから各方向の加点文字列を取得
		for (int i = 0; i < numLayer.length; ++i) {
			//println("  - START " + i + " Direction -");
			sStrings.addAll( new ArrayList <ScoredString> (numLayer[i].calScoredStrings(_bRange)) );	//i方向の加点文字列を取得し代入
			//勝利確定文字列がヒットしたらtrue
			if (numLayer[i].hitGodString()) {
				hitGodString = true;
				//println("  ! A God String Was Found");
			}
			//println("  - END " + i + " Direction -");
		}

		//連鎖の大きい順(降順)に並び替える
		Collections.sort(sStrings, new SStringConparator());
		Collections.reverse(sStrings);

		//等しい加点文字列を除く
		for (int i = 0; i < sStrings.size(); ++i) {
			ScoredString sString = new ScoredString(sStrings.get(i));
			for (int j = i+1; j < sStrings.size(); ++j) {
				if ( sString.includingStrings(sStrings.get(j)) ) {
					//println("The same chain was removed");
					sStrings.remove(j);
					j--;
				}
			}
		}


		//println(" - END Calculating SStrings -");

		Collections.reverse(sStrings);	//昇順に並び替える
		return sStrings;
	}

}
