package jp.hichain.prototype.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class Main {

	public static void main(String[] args) {

	}

	/*
	文字のデータをロードする (必ず他のインスタンス化より先に行うこと)
	  loadSignData(ロードする画像の入ったパス[色別], 場に置く文字の配列)
	指定した場に置く文字の画像のみロードし、
	charとPImageのセットをSignDataに送る (SignData内でそのほかの文字データがロードされる)
	 */
	private void loadSignData(File _dataPath, File [] _imagePath, Set <Character> _signs) {
		System.out.println("Loading SignData: ");

		try {
			BufferedReader brData = new BufferedReader( new FileReader(_dataPath) );

			String line;
			while ((line = brData.readLine()) != null) {
				String [] data = line.split(",", 0); // 行をカンマ区切りで配列に変換
				if (data[0].equals("Sign")) {
					continue;
				}

				//文字データの分解
				char ch = data[0].toCharArray()[0];
				if (!_signs.contains(ch)) {
					continue;
				}
				int [] nums = {
						Integer.decode(data[1]),
						Integer.decode(data[2]),
						Integer.decode(data[3]),
						Integer.decode(data[4])
				};
				int ps = Integer.parseInt(data[5]);

				char newCh = '　';	//SIのファイル名
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

				boolean success = false;
				for (int j = 0; j < _imagePath.length; j++) {
					File imageFile = new File(_imagePath[j].getPath() + "\\" + newCh + ".png");
					try {
						BufferedReader brImage = new BufferedReader( new FileReader(imageFile) );

					} catch (IOException e) {
						System.out.println("'" + ch + "' Image was not found!");
						break;
					}
				}
			}
			brData.close();

		} catch (IOException e) {
			System.out.println("Wrong Path of SignData");
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
