package jp.hichain.prototype.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hichain.prototype.basic.CompleteBS;

//文字データ (SN/PS/SI)
public class SignData {
	static private List <CompleteBS> signs = new ArrayList<CompleteBS>();

	/**
	 * 指定の文字のデータが存在するか返す
	 * @param _player プレイヤー番号
	 * @param _ch 文字のchar
	 * @return true/false
	 */
	static public boolean contains(int _player, char _ch) {
		for (CompleteBS bs : signs) {
			if (bs.getPlayer() == _player && bs.getChar() == _ch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 文字数を返す (' 'も含む)
	 * @return 文字数
	 */
	static public int size() {
		return signs.size();
	}

	/**
	 * 文字のSetを返す
	 * @return 文字のSet
	 */
	static public Set <Character> getSignSet() {
		Set <Character> signSet = new HashSet<Character>();
		for (CompleteBS bs : signs) {
			signSet.add(bs.getChar());
		}
		return signSet;
	}

	public static void add(CompleteBS _bs) {
		signs.add(_bs);
	}

	/**
	 * BSを取得する
	 * @param _ch 文字のchar
	 * @return BS
	 */
	static public CompleteBS get(int _player, char _ch) {
		for (CompleteBS bs: signs) {
			if (bs.getPlayer() == _player && bs.getChar() == _ch) {
				return new CompleteBS(bs);
			}
		}
		return new CompleteBS(signs.get(' ') );
	}

	/**
	 * SNを取得する
	 * @param _ch 文字のchar
	 * @return SN
	 */
	static public int [] getSN(char _ch) {
		for (CompleteBS bs : signs) {
			if (bs.getChar() == _ch) {
				return bs.getNums();
			}
		}
		return signs.get(' ').getNums();
	}

	/**
	 * PSを取得する
	 * @param _ch 文字のchar
	 * @return PS
	 */
	static public int getPS(char _ch) {
		for (CompleteBS bs : signs) {
			if (bs.getChar() == _ch) {
				return bs.getPS();
			}
		}
		return signs.get(' ').getPS();
	}

	/**
	 * SIを取得する
	 * @param _ch 文字のchar
	 * @return SI
	 */
	static public BufferedImage getSI(int _player, char _ch) {
		for (CompleteBS bs : signs) {
			if (bs.getChar() == _ch && bs.getPlayer() == _player) {
				return bs.getImage();
			}
		}
		return signs.get(' ').getImage();
	}
}
