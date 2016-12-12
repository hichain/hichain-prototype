package jp.hichain.prototype.basic;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//文字データ (SN/PS/SI)
public class SignData {
	static private List <CompleteBS> signs;

	/**
	 * 指定の文字のデータが存在するか返す
	 * @param _ch 文字
	 * @return true/false
	 */
	static public boolean contains(char _ch) {
		for (CompleteBS sign : signs) {
			if (sign.getChar() == _ch) {
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
		Set <Character> chars = new HashSet <Character> ();
		for (CompleteBS sign : signs) {
			chars.add(sign.getChar());
		}
		return chars;
	}

	/**
	 * 文字を追加する
	 * @param _bs BS
	 */
	static public void add(int _player, char _ch, BufferedImage _img, int [] num, int ps) {
		CompleteBS bs = new CompleteBS(_player, _ch, 0);

		signs.add();
	}

	/**
	 * 文字を取得する
	 * @param _ch 文字のchar
	 * @return BS
	 */
	static public CompleteBS get(char _ch) {
		for (CompleteBS sign : signs) {
			if (sign.getChar() == _ch) {
				return sign;
			}
		}
		return get(' ');
	}

  /**
   * 回転したSNを返す
   * @param _nums 4方向のSN
   * @param _dir 文字の向き(回転数・反時計回り)
   * @return SN
   */
  static private int [] rotateNum(int [] _nums, int _dir) {
    for (int i = 0; i < _dir; ++i) {
      int tmp = _nums[3];
      for (int j = _nums.length-1; j > 0; --j) {
        _nums[j] = _nums[j-1];
      }
      _nums[0] = tmp;
    }

    return _nums;
  }

  /**
   * 回転したPSを返す
   * @param _ps PS
   * @param _dir 文字の向き
   * @return PS
   */
  static private int rotatePS(int _ps, int _dir) {
    _dir %= 4;
    if (_dir <= 0) return _ps;

    int newPs = 0;

    for (int i = 0; i < 4; i++) {
      newPs ^= (_ps & (0x800 >>> 2*i)) << 4+i;    //0000 1000 0000 0000
      newPs ^= (_ps & (0x400 >>> 2*i)) >>> 7-i;   //0000 0100 0000 0000
      newPs ^= (_ps & (0x8000 >>> i)) >>> 11-3*i; //1000 0000 0000 0000
      newPs ^= (_ps & (0x8 >>> i)) << 2+3*i;      //0000 0000 0000 1000
    }

    return rotatePS(newPs, --_dir);
  }

  /**
   * 回転したSIを返す
   * @param _img SI
   * @param _dir 文字の向き
   * @return SI
   */
  static private BufferedImage rotateImage(BufferedImage _img, int _dir) {
    _dir %= 4;
    if (_dir <= 0) return _img;

    int r = _img.width;
    color [] pixels = _img.pixels.clone();
    for (int i = 0; i < _img.pixels.length; i++) {
      int j = (r-1-i/r) + r*(i%r);
      _img.pixels[i] = pixels[j];
    }

    return rotateImage(_img, --_dir);
  }
}
