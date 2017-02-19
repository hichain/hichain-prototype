package jp.hichain.prototype.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.basic.SignImage;
import jp.hichain.prototype.basic.SignNum;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.concept.CardColor;

//文字データ (SN/PS/SI)
public class SignData {
	static private Map<Character, ChainSign> signs;

	static {
		signs = new HashMap<>();
	}

	/**
	 * 指定の文字のデータが存在するか返す
	 * @param _ch 文字のchar
	 * @return true/false
	 */
	static public boolean contains(char _ch) {
		return signs.containsKey(_ch);
	}

	/**
	 * BSを取得する
	 * @param _ch 文字のchar
	 * @return BS
	 */
	static public ChainSign get(char _ch) {
		return signs.get(_ch);
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
	static public Set <Character> getSigns() {
		return signs.keySet();
	}

	public static void add(ChainSign _sign) {
		signs.put(_sign.getSC(), _sign);
	}

	/**
	 * SNを取得する
	 * @param _ch 文字のchar
	 * @return SN
	 */
	static public SignNum getSN(char _ch) {
		return signs.get(_ch).getSN();
	}

	/**
	 * PSを取得する
	 * @param _ch 文字のchar
	 * @return PS
	 */
	static public SignPS getSPS(char _ch) {
		return signs.get(_ch).getSPS();
	}

	/**
	 * SIを取得する
	 * @param _ch 文字のchar
	 * @return SI
	 */
	static public SignImage getSI(CardColor color, char _ch) {
		return signs.get(_ch).getSI(color);
	}
}
