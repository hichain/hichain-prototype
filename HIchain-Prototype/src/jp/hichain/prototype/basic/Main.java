package jp.hichain.prototype.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {

	}

	/*
	文字のデータをロードする (必ず他のインスタンス化より先に行うこと)
	  loadSignData(ロードする画像の入ったパス[色別], 場に置く文字の配列)
	指定した場に置く文字の画像のみロードし、
	charとPImageのセットをSignDataに送る (SignData内でそのほかの文字データがロードされる)
	 */
	private void loadSignData(File _dataPath, File [] _imagePath, char [][] _fieldSigns) {
		System.out.println("Loading SignData: ");

		try {
			BufferedReader brData = new BufferedReader( new FileReader(_dataPath) );
			BufferedReader [] brImages = new BufferedReader [_imagePath.length];
			for (int i = 0; i < _imagePath.length; i++) {
				brImages[i] = new BufferedReader( new FileReader(_imagePath[i]) );
			}

			String line;
			while ((line = brData.readLine()) != null) {
				String [] data = line.split(",", 0); // 行をカンマ区切りで配列に変換
				if (data[0].equals("Sign")) {
					continue;
				}

				char ch = data[0].toCharArray()[0];

			}
			brData.close();

		} catch (IOException e) {
			System.out.println(e);
		}
/*
		//画像の追加
		for (char ch : signs) {
			PImage [] img = new PImage[2];
			char newCh = '　';
			//半角→全角変換
			if (ch >= 'A' && ch <= 'Z') {
				newCh = (char)('Ａ' + ch - 'A');
			} else if (ch >= '0' && ch <= '9') {
				newCh = (char)('０' + ch - '0');
			} else {
				switch (ch) {
				case ' ':
					newCh = '　';
					break;
				case '*':
					newCh = '＊';
					break;
				}
			}
			for (int i = 0; i < 2; i++) {
				img[i] = loadImage(_imagePath[i] + "/" + newCh + ".png"); //画像をロードする
			}
			SignData.addImage(ch, img); //SignDataに画像を代入
			print("\'" + String.valueOf(ch) + "\' ");
		}
		print("\n");
*/
	}
}
