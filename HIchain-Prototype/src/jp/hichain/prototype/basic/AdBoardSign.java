package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * 拡張BoardSign
 * 周り8方向のBSとSSの連鎖情報を持つ (相対参照アルゴリズム)
 * AroundBS = 周りのBS
 * AroundDir = AroundBSの方向 (上下左右角4つ)
 * source = アクセス元のAroundDir (AdBSでないので注意)
 * @author Tokiwa
 */
public class AdBoardSign extends CompleteBS {
	/**
	 * 文字の方向と相対座標の対応表
	 */
	public static final int [][] DIRPOSTABLE = {
		{0, -1}, {-1, 0}, {0, 1}, {1, 0}, {-1, -1}, {-1, 1}, {1, 1}, {1, -1}
	};
	private int source;
	private AdBoardSign [] aroundBS;

	private List <Chain> chains; //SSの連鎖

	/**
	 * 盤上の空のBS
	 * [_source:_sourceDir]に新しいAdBSを作る
	 * @param _sourceBS sourceのAdBS
	 * @param _source sourceから見た方向
	 */
	public AdBoardSign (AdBoardSign _sourceBS, int _source) {
		super(0, 0);  //仮に0,0に置く\
		setPos( convertAbsolutePos(_sourceBS.getPos(), _source) ); //絶対座標の代入
		setSource(_sourceBS, _source); //周りのAdBSと接続する
		chains = new ArrayList <Chain>();
	}

	/**
	 * 盤上の空のBS (ルート座標限定)
	 * 絶対座標で指定して新しいAdBSを作る
	 * ルートでのみ使用する 盤に1つだけ
	 * @param _x x座標 (絶対)
	 * @param _y y座標 (絶対)
	 */
	AdBoardSign (int _x, int _y) {
		super(_x, _y);
		aroundBS = new AdBoardSign[8];
		chains = new ArrayList <Chain>();
	}

	/**
	 * AroundDirの反対方向を返す
	 * @param _dir AroundDir
	 * @return 反対のAroundDir
	 */
	public static int getOppositeDir(int _dir) {
		return (_dir+2)%4 + ( (_dir <= 3) ? 0 : 4 );
	}

	/**
	 * sourceを返す
	 * @return source
	 */
	public int getSourceDir() {
		return source;
	}

	/**
	 * sourceのAdBSを返す
	 * @return sourceのAdBS
	 */
	public AdBoardSign getSource() {
		return aroundBS[source];
	}

	/**
	 * 指定した方向のAroundBSを返す
	 * @param _dir AroundBSの方向
	 * @return AroundBS
	 */
	public AdBoardSign getAroundBS(int _dir) {
		return aroundBS[_dir];
	}

	/**
	 * AroundBSを全て返す
	 * @return 全てのAroundBS
	 */
	public AdBoardSign [] getAroundBS() {
		return aroundBS;
	}

	/**
	 * 指定した方向にAroundBSがあるか返す
	 * @param _dir AroundBSの方向
	 * @return true/false
	 */
	public boolean hasAround(int _dir) {
		return aroundBS[_dir] != null;
	}

	/**
	 * 指定した条件を満たすSSの連鎖を取得する
	 * @param _signDir 文字の向き
	 * @param _ssKind SSの種類
	 * @return SSの連鎖
	 */
	public Chain [] getChains(int _signDir, int _ssKind) {
		ArrayList <Chain> extractedChains = new ArrayList <Chain>();
		for (Chain chain : chains) {
			if (chain.getSignDir() == _signDir && chain.getSSKind() == _ssKind) {
				extractedChains.add(chain);
			}
		}
		return extractedChains.toArray(new Chain[extractedChains.size()]);
	}

	/**
	 * sourceを設定する
	 * 周りのAdBSと接続する
	 * @param _sourceBS sourceのAdBS
	 * @param _source source
	 */
	private void setSource(AdBoardSign _sourceBS, int _source) {
		source = _source;
		aroundBS = AroundSearcher.getAroundBS(_sourceBS, getOppositeDir(_source));
		for (int i = 0; i < aroundBS.length; i++) {
			if (aroundBS[i] != null) {
				aroundBS[i].setAroundBS(this, getOppositeDir(i));
			}
		}
	}

	/**
	 * AroundBSをセット
	 * @param _aroundSource AroundBS
	 * @param _sourceDir 自分から見たAroundBSの向き
	 */
	public void setAroundBS(AdBoardSign _aroundBS, int _aroundDir) {
		aroundBS[_aroundDir] = _aroundBS;
	}

	/**
	 * SSの連鎖を追加
	 * @param _chain 連鎖
	 */
	public void addChain(Chain _chain) {
		chains.add(_chain);
	}

	/**
	 * 絶対座標とAroundDirからその向きにある絶対座標を返す
	 * @param _pos 絶対座標
	 * @param _dir AroundDir
	 * @return 絶対座標
	 */
	public static int [] convertAbsolutePos(int [] _pos, int _dir) {
		int [] relativePos = DIRPOSTABLE[_dir]; //相対座標
		int [] newPos = {
				_pos[0] + relativePos[0], _pos[1] + relativePos[1]
		};
		//0行、0列を飛ばす (相対座標に2をかける)
		for (int i = 0; i < 2; i++) {
			if (newPos[i] == 0) {
				newPos[i] += (_pos[i] == 1) ? -1 : 1;
			}
		}
		return newPos;
	}

	/**
	 * AroundBS探索
	 * AroundBSをsourceのBSから探索する
	 * @author Tokiwa
	 */
	private static class AroundSearcher {
		/**
		 * 0/4の方向にあるsourceからAroundBSを探索するときに通る道順
		 * [index1]: centerから見たAroundBSの向き
		 * [index2]: 道順
		 */
		public final static int [][] AROUNDROADS0 = {
			{}, {5}, {5, 6}, {6}, {1}, {5, 2}, {6, 2}, {3}
		};
		public final static int [][] AROUNDROADS4 = {
			{3}, {2}, {2, 6}, {3, 6}, {}, {2, 2}, {2, 6, 3}, {3, 3}
		};

		/**
		 * AroundBS探索
		 * あるAdBSのAroundBSを取得する
		 * centerを周りを求めるAdBSとする
		 * @param sourceBS centerに隣接しているAdBS (ここから探索する)
		 * @param dir centerから見たsourceの方向
		 * @return AroundBS
		 */
		static AdBoardSign [] getAroundBS(AdBoardSign sourceBS, int dir) {
			AdBoardSign [] aroundBS = new AdBoardSign [8];

			int [][] roads; //道順
			if (dir < 4) {
				roads = getAroundRoads(AROUNDROADS0, dir);
			} else {
				roads = getAroundRoads(AROUNDROADS4,  dir-4);
			}
			for (int i = 0; i < 8; i++) {
				aroundBS[i] = getAroundBS(sourceBS, roads[i]);
			}

			return aroundBS;
		}

		/**
		 * 指定の道順を辿ってAroundBSを探索する
		 * @param sourceBS sourceのBS
		 * @param road 道順
		 * @return AroundBS
		 */
		private static AdBoardSign getAroundBS(AdBoardSign sourceBS, int [] road) {
			AdBoardSign goalBS = sourceBS;

			for (int dir : road) {
				if (!goalBS.hasAround(dir)) {
					goalBS = null;
					break;
				}
				goalBS = goalBS.getAroundBS(dir);
			}

			return goalBS;
		}

		/**
		 * 道順を回転させる
		 * @param roads 道順
		 * @param turns 回転数
		 * @return 道順
		 */
		private static int [][] getAroundRoads(int [][] roads, int turns) {
			if (turns == 0) return roads;

			for (int j = 0; j < roads.length; j++) {
				for (int i = 0; i < roads[j].length; i++) {
					roads[j][i] = (roads[j][i] + 1) % 4 + ((roads[j][i] <= 3) ? 0 : 4);
				}
			}
			int [] tmp = roads[3];
			for (int j = 3; j > 0; j--) {
				roads[j] = roads[j-1];
			}
			roads[0] = tmp;
			tmp = roads[7];
			for (int j = 7; j > 4; j--) {
				roads[j] = roads[j-1];
			}
			roads[4] = tmp;

			return getAroundRoads(roads, --turns);
		}
	}
}
