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
	public static boolean contains(char _ch) {
		return signs.containsKey(_ch);
	}

	/**
	 * BSを取得する
	 * @param _ch 文字のchar
	 * @return BS
	 */
	public static ChainSign get(char _ch) {
		ChainSign sign = null;
		try {
			sign = signs.get(_ch).clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return sign;
	}

	/**
	 * 文字数を返す (' 'も含む)
	 * @return 文字数
	 */
	public static int size() {
		return signs.size();
	}

	/**
	 * 文字のSetを返す
	 * @return 文字のSet
	 */
	public static Set <Character> getSigns() {
		return signs.keySet();
	}

	public static void add(ChainSign _sign) {
		signs.put(_sign.getSC().get(), _sign);
	}

	/**
	 * SNを取得する
	 * @param _ch 文字のchar
	 * @return SN
	 */
	public static SignNum getSN(char _ch) {
		return signs.get(_ch).getSN();
	}

	/**
	 * PSを取得する
	 * @param _ch 文字のchar
	 * @return PS
	 */
	public static SignPS getSPS(char _ch) {
		return signs.get(_ch).getSPS();
	}

	/**
	 * SIを取得する
	 * @param _ch 文字のchar
	 * @return SI
	 */
	public static SignImage getSI(CardColor color, char _ch) {
		return signs.get(_ch).getSI(color);
	}
}
