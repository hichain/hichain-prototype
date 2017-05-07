package jp.hichain.prototype.basic;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.AroundDir.Axis;
import jp.hichain.prototype.concept.Direction.Relation;

/**
 * マス
 * 周囲マスへのアクセス、
 * @author NT
 *
 */
public class Square {
	private static Square ROOT;
	private static Map<Position, Square> godMap;

	private BoardSign thisBS;
	private Square source;
	private EnumMap <AroundDir, Square> around;
	private Position position;

	/**
	 * ルートマス
	 */
	public Square(BoardSign _thisBS) {
		ROOT = this;
		init(thisBS);
		position = new Position(-1, -1);
		godMap.put(position, this);
		createAroundAll();
	}

	/**
	 * 通常のマス
	 * @param _source ソースSquare
	 * @param _dir  ソースからみた自身(this)のAroundDir
	 */
	public Square(BoardSign _thisBS, Square _source, AroundDir _dir) {
		init(_thisBS);
		position = new Position(
			_source.getPosition(Axis.VERTICAL) + _dir.getComp(Axis.VERTICAL),
			_source.getPosition(Axis.HORIZONTAL) + _dir.getComp(Axis.HORIZONTAL)
		);
		godMap.put(position, this);
		source = _source;
		setArounds(_source, _dir);
	}

	static {
		godMap = new HashMap<>();
	}

	/**
	 * 絶対座標で指定してSquareを返す
	 * @param _v VERTICAL
	 * @param _h HORIZONTAL
	 * @return Square
	 */
	public static Square get(int _v, int _h) {
		return godMap.get( new Position(_v, _h) );
	}

	/**
	 * ルートを返す
	 * @return Square
	 */
	public static Square getRoot() {
		return ROOT;
	}

	/**
	 * このSquareのBSを返す
	 * @return BoardSign
	 */
	public BoardSign getBS() {
		return thisBS;
	}

	/**
	 * ソースSquareを返す
	 * @return Square
	 */
	public Square getSource() {
		return source;
	}

	/**
	 * 指定の方角のSquareを返す
	 * @param _dir 自身(this)からみたAroudDir
	 * @return 周囲Square
	 */
	public Square getAround(AroundDir _dir) {
		return around.get(_dir);
	}

	/**
	 * 周囲Squareを全て返す
	 * @return 周囲Square Map
	 */
	public EnumMap <AroundDir, Square> getAroundAll() {
		return around;
	}

	/**
	 * 座標を返す
	 * @param _axis 軸
	 * @return 座標
	 */
	public int getPosition(Axis _axis) {
		return position.get(_axis);
	}

	/**
	 * 周囲Squareを追加
	 * @param _square Square
	 * @param _dir 自身(this)からみた_squareの方向
	 */
	public void addAround(AroundDir _dir, Square _square) {
		around.put(_dir, _square);
	}

	protected void createAroundAll() {
		for (AroundDir dir : AroundDir.values()) {
			Square square = getAround(dir);
			if (square == null) {
				BoardSign bs = new BoardSign(thisBS, dir);
				addAround(dir, bs.getSquare());
			}
		}
	}

	private void setArounds(Square _source, AroundDir _sourceDir) {
		addAround(_sourceDir.get(Relation.OPPOSITE), _source);
		for (AroundDir dir : AroundDir.values()) {
			int v = getPosition(Axis.VERTICAL) + dir.getComp(Axis.VERTICAL);
			int h = getPosition(Axis.HORIZONTAL) + dir.getComp(Axis.HORIZONTAL);
			addAround(dir, get(v, h));
		}
	}

	private void init(BoardSign _thisBS) {
		thisBS = _thisBS;
		around = new EnumMap<>(AroundDir.class);
		for (AroundDir dir : AroundDir.values()) {
			around.put(dir, null);
		}
	}
}
