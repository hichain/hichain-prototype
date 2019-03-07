//加点文字列を比較する

import java.util.Comparator;

class SStringConparator implements Comparator<ScoredString> {

  public int compare(ScoredString a, ScoredString b) {
    //サイズを取得
    int sizeA = a.size();
    int sizeB = b.size();

    if (sizeA > sizeB) {
      return 1;
    } else if (sizeA == sizeB) {
      return 0;
    } else {
      return -1;
    }
  }

}
