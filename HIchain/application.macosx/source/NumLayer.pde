//一方向の文字番号レイヤー
//加点文字列の検索
class NumLayer {
	int [][] num;	//文字番号
	ArrayList <ScoredString> strings = new ArrayList<ScoredString>();	//加点文字列群
	ScoredString string;	//加点文字列

	int [] godStringNum;	//勝利確定文字列
	int godStringNumLevel = 0;	//勝利確定文字列がどこまで成立しているか
	boolean hitGodString = false;	//勝利確定文字列がヒットしたか

	int asteNum = -1;	//アスタリスクが連番列上でなりうる文字番号

	/*
	[方向に関して]
	0:北 1:西 2:南 3:東
	(それぞれの方角にカードが上を向く)
	*/

	NumLayer(int _size, int [] _godStringNum) {
		num = new int [_size][_size];
		godStringNum = _godStringNum;
	}

	//文字番号を代入
	void subNum(int _num, int _x, int _y) {
		num[_x][_y] = _num;
		//println("(" + _x + ", " + _y + ") set " + _num);
	}

	//勝利確定文字列がヒットしたか返す
	boolean hitGodString() {
		return hitGodString;
	}

	//指定の盤の範囲から加点文字列群を返す
	ArrayList <ScoredString> calScoredStrings(int [][] _bRange) {
		strings = new ArrayList<ScoredString>();	//初期化

		//連番を検索
		//println("[Calculate Consecutive Signs]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(0, i, j);
			}
		}

		//ぞろ目を検索
		//println("[Calculate Repdigit]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(1, i, j);
			}
		}

		//勝利確定文字列を検索
		//println("[Calculate God String]");
		for (int j = _bRange[0][1]; j <= _bRange[1][1]; ++j) {
			for (int i = _bRange[0][0]; i <= _bRange[1][0]; ++i) {
				calChains(2, i, j);
			}
		}

		return strings;
	}

	/*
	calChains(検索する加点文字列の種類, ルートx座標, y座標)
	○ 指定した座標から連なる連番列を検索
	1. ルート座標が*の場合はreturn
	2. 連番[_kind == 0]						4方向から昇順に連番を見つける (降順に連番があれば返る)
	2. ぞろ目[_kind == 1]				 4方向からぞろ目を見つける (ぞろ目列が周りに2つ以上ある場合は返る)
	2. 勝利確定文字列[_kind == 0] 4方向から勝利確定文字列を先頭から見つける
	3.	(この時点で指定の座標=ルート座標が決定)
	4. ルート座標から昇順に連番か調べる (再帰処理)
	*/
	void calChains(int _kind, int _x, int _y) {
		int root = num[_x][_y];	//ルート座標

		if (root == 27) {	//ルート座標がアスタリスクなら
			return;
		}

		int [] sum = calTwoChains(_x, _y, -1);	//ルートから連番を計算

		boolean hitChains = false;	//加点文字列がヒットしたか
		switch (_kind) {
			case 0 :	//連番を検索
				for (int i = 0; i < 4; ++i) {
					if (sum[i] == -1) {	//iの方向において降順に連番なら
						return;
					} else if (sum[i] == 1) {	//昇順に連番なら
						hitChains = true;
						//break;
					} else if (sum[i] == 2) {	//アスタリスクに接続なら
						asteNum = (root-1)%27;	//アスタリスクが"降順に"連番になりうる文字を代入
						//A→Zの処理
						if (asteNum == 0) {
							asteNum = 26;
						}
						//目標座標までの差分を更新
						int nextDx = (int)cos(-0.5*(i+1)*PI);
						int nextDy = (int)sin(-0.5*(i+1)*PI);
						int [] asteSum = calTwoChains(_x + nextDx, _y + nextDy, (i+2)%4);
						for (int j = 0; j < 4; j++) {
							//降順に連番なら
							if (asteSum[i] == -1) {
								return;
							}
						}
						hitChains = true;
					}
				}
			break;
			case 1 :	//ぞろ目を検索
				for (int i = 0; i < 4; i++) {
					if (sum[i] == -2 || sum[i] == 2) {	//iの方向においてぞろ目なら
						//println("hitChains");
						if (!hitChains) {
								hitChains = true;
						} else {	//ぞろ目列が周りに2つ以上ある場合は返る
							return;
						}
					}
				}
			break;
			case 2 :	//勝利確定文字列を計算
				godStringNumLevel = 0;	//勝利確定文字列の先頭から検索する
				for (int i = 0; i < 4; i++) {
					if (sum[i] == 3 || sum[i] == 2) {
						hitChains = true;
						break;
					}
				}
			break;
		}

		//どの方向においても連番でなかったら
		if (!hitChains) {
			return;
		}

		//この時点で(_x, _y)が加点文字列のルート座標であることが決定
		string = new ScoredString(_x, _y, _kind);
		calHighChains(_kind, _x, _y);	//ルートから高い順に加点文字列を探索
	}

	/*
	calHighChains(ルートx座標, y座標, 目標座標までのx座標差分, 目標座標までのy座標差分, 前の目標座標の向き)
	○ 指定した座標から2連鎖以上の昇順の加点文字列を検索
	---
	root(ルート座標):		 連番列の始点座標
	current(現在座標):		ここから4方向に調べる
	next(目標座標):				現在座標から調べる周りの座標
	---
		1. 現在座標から4方向に調べる
		2. ヒットしたらstringに登録 (目標座標がアスタリスクならアスタリスクが加点文字列になり得る文字番号を代入)
		3. 目標座標を現在座標として再帰
		4. 1～3をループ 加点文字列が見つからなくなったらその前の座標が加点文字列の終点
		5. 加点文字列の終点を見つけたら、stringをstringsに追加 (一つも連番列が見つからない場合は追加しない)
	返り値: boolean型 指定の加点文字列がヒットしたか
	*/
	boolean calHighChains(int _kind, int _x, int _y, int _dx, int _dy, int _preDir) {
		boolean hitChains = false;	//連鎖を発見したか
		int current = num[_x+_dx][_y+_dy];	//現在座標の文字番号

		//勝利確定文字列の終点のときfalseを返す
		if (_kind == 2 && godStringNumLevel == godStringNum.length-1) {
			return false;
		}

		int [] sum = calTwoChains(_x+_dx, _y+_dy, _preDir);	//現在座標から連番を計算

		for (int i = 0; i < 4; ++i) {
			boolean consecutive = (sum[i] == 1 || sum[i] == 2);	//高い順に連番か
			boolean identical = (sum[i] == -2 || sum[i] == 2);	//ぞろ目か
			boolean god = (sum[i] == 3 || sum[i] == 2);					//勝利確定文字列か

			//指定の加点文字列がヒットしたら
			if ( (_kind == 0 && consecutive) || (_kind == 1 && identical) || (_kind == 2 && god)) {
				hitChains = true;
				string.add(i);	//座標を登録
				//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") add " + i);

				//勝利確定文字列なら次のレベルにする
				if (_kind == 2) {
					godStringNumLevel++;
					//println("godStringNumLevel++: " + godStringNumLevel);
				}

				//目標座標がアスタリスクの場合
				if (sum[i] == 2) {
					switch (_kind) {
						case 0 :	//連番
							asteNum = (current+1)%27;
							if (asteNum == 0) {	//Z→Aの処理
								asteNum++;
							}
						break;
						case 1 :	//ぞろ目
							asteNum = current;
						break;
						case 2 :	//勝利確定文字列
							asteNum = godStringNum[godStringNumLevel];
						break;
					}
				}

				//目標座標までの差分を更新
				int nextDx = _dx + (int)cos(-0.5*(i+1)*PI);
				int nextDy = _dy + (int)sin(-0.5*(i+1)*PI);

				boolean nextHitChains = calHighChains(_kind, _x, _y, nextDx, nextDy, (i+2)%4);	//目標の座標で再帰

				//連番を見つけられなかった場合 (連鎖の終点) (*1)
				if (!nextHitChains) {
					if (sum[i] == 2) {	//アスタリスクが連番列の終点の場合は無効
						hitChains = false;
					} else if (string.size() != 0) {	//2連鎖以上なら
						//加点文字列群に登録
						if (_kind == 2) {
							//勝利確定文字列が終点の場合
							if (godStringNumLevel == godStringNum.length-1) {
								strings.add(new ScoredString(string));
								hitGodString = true;
							}
						} else {
							strings.add(new ScoredString(string));
						}
					}
				}

				//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") removed " + string.get(string.size()-1));
				string.remove(string.size()-1);	//最後のstringの座標を削除
				//勝利確定文字列のレベルを下げる
				if (_kind == 2) {
					godStringNumLevel--;
					//println("godStringNumLevel--: " + godStringNumLevel);
				}
			}
		}

		//現在座標がアスタリスクのとき連鎖が見つからない場合､
		//*が終点となってしまうのでfalseを返して*を追加せずにその前の文字で連鎖の終点処理(*1)を行う (1つ戻る)
		if (!hitChains && current == 27) {
			if (current == 27) {
				hitChains = false;
			}
			//println("(" + _x + ", " + _y + ") -> (" + (_x+_dx) + ", " + (_y+_dy) + ") end");
		}

		return hitChains;
	}

	//calHighChainsのルート座標から調べるバージョン (第3～5引数省略)
	boolean calHighChains(int _kind, int _x, int _y) {
		return calHighChains(_kind, _x, _y, 0, 0, -1);
	}

	/*
	○ calTwoChainを4方向行う
		calTwoChains(現在のx座標, y座標, 前の目標座標の向き(ルート座標の場合は-1))
	※ 前の目標座標の計算は省いている
	*/
	int [] calTwoChains(int _x, int _y, int _preDir) {
		int [] sum = new int [4];

		for (int i = 0; i < sum.length; ++i) {
			//前の目標座標を調べないようにする
			if (i == _preDir) {
				sum[i] = 0;
				continue;
			}

			int xd = (int)cos(-0.5*(i+1)*PI);
			int yd = (int)sin(-0.5*(i+1)*PI);
			if (_x+xd < 0 || _x+xd > num.length || _y+yd < 0 || _y+yd > num[0].length) {
				sum[i] = 0;
				continue;
			}

			sum[i] = calTwoChain(_x, _y, xd, yd);
		}

		return sum;
	}

	/*
	calTwoChain(現在x座標, y座標, 連番を調べる向きx, y) (向きは0～3)
	○ 2連鎖の判定をする
	返り値: 方向順に格納された連番の状態
		-2: ぞろ目
		-1: 降順に連番
		0: 連番なし
		1: 昇順に連番
		2: アスタリスクに接続
		3: 勝利確定文字列に連番
	*/
	int calTwoChain(int _x, int _y, int _dx, int _dy) {
		int current = num[_x][_y];			//現在座標の文字番号
		int next = num[_x+_dx][_y+_dy];	//目標座標の文字番号

		//現在座標がアスタリスクの場合はasteNumから取得してくる
		if (current == 27) {
			current = asteNum;
		}

		//現在座標と目標座標のどちらかが0なら返す
		if (current == 0 || next == 0) {
			return 0;
		}
		//目標座標がアスタリスクの場合
		if (next == 27) {
			return 2;
		}

		//連番の判定
		if (next - current == 1 || (next == 1 && current == 26)) {	//昇順に連番
			return 1;
		} else if (next - current == -1 || (next == 26 && current == 1)) {	//降順に連番
			return -1;
		}
		//ぞろ目の判定
		if (next == current) {
			//println("repdigit");
			return -2;
		}
		//勝利確定文字列の判定
		if (current == godStringNum[godStringNumLevel] && next == (godStringNum[godStringNumLevel+1])) {
			return 3;
		}

		return 0;
	}
}
