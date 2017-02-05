package jp.hichain.prototype.ui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import jp.hichain.prototype.algorithm.Judge;
import jp.hichain.prototype.basic.CompleteBS;
import jp.hichain.prototype.basic.Move;
import jp.hichain.prototype.basic.SignNum;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.PS;
import jp.hichain.prototype.concept.SignDir;

public class Main {

	public static final String SIGNDATAPATH = ".\\data\\SignData.csv";	//文字データのパス
	public static final String [] SIGNIMAGEPATH = {	//SIのパス (色の数分)
			".\\data\\cards\\D",
			".\\data\\cards\\R"
	};
	public static char [][] fieldsigns = {	//場の文字 (プレイヤー別)
		{
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '*'
		},
		{
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '*'
		}
	};

	public static void main(String[] args) {
		testAroundDir();
	}

	private static void testAroundDir() {
		AroundDir rootDir = AroundDir.NORTH;
		AroundDir [] dirs = rootDir.getRoute(AroundDir.SOUTH);
		for (AroundDir dir : dirs) {
			System.out.println(dir);
		}
	}

	private static void testJudge() {
		Move rootMove = new Move(1, 1);
		rootMove.putSign( SignData.get(1, 'A') );
		rootMove.rotate(2);
		System.out.println( Integer.toBinaryString(rootMove.getPS()) );

		Move move = new Move(rootMove, 2);
		CompleteBS holdingBS = new CompleteBS( SignData.get(2, 'A') );
		System.out.println( Judge.canPut(move, holdingBS) );
	}

	private static void testSign() {
		File signdataFile = new File(SIGNDATAPATH);
		File [] signimageFile = new File [SIGNIMAGEPATH.length];
		for (int i = 0; i < SIGNIMAGEPATH.length; i++) {
			signimageFile[i] = new File(SIGNIMAGEPATH[i]);
		}
		Set <Character> signSet = getSignSet(fieldsigns);
		signSet.add(' ');
		loadSignData( signdataFile, signimageFile, signSet );

		CompleteBS bs = SignData.get(1, 'A');
		bs.resize(200);
		bs.rotate(3);

		SIPanel panel = new SIPanel(bs);
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setTitle("SI Frame");
		frame.setSize(520, 540);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static Set <Character> getSignSet(char [][] _signs) {
		Set <Character> signSet = new HashSet<Character>();
		for (char [] chs : _signs) {
			for (char ch : chs) {
				signSet.add(ch);
			}
		}
		return signSet;
	}

	/**
	 * 文字データをロードする
	 * @param _dataPath SN、PSの入ったCSVのパス
	 * @param _imagePath SIのパス (色の数分)
	 * @param _signs ロードする文字
	 */
	private static void loadSignData(File _dataPath, File [] _imagePath, Set <Character> _signs) {
		System.out.println("Loading SignData: ");
		int players = _imagePath.length;	//プレイヤー数

		BufferedReader brData = null;
		try {
			brData = new BufferedReader( new FileReader(_dataPath) );

			SignDir [] dirsHeader = new SignDir[4];
			PS [] psHeader = new PS [16];

			String line = brData.readLine();
			line = brData.readLine();
			String [] header = line.split(",", 0);
			for (int i = 0; i < dirsHeader.length; i++) {
				dirsHeader[i] = SignDir.getEnum(header[i+1]);
			}
			for (int i = 0; i < psHeader.length; i++) {
				psHeader[i] = PS.getEnum(header[i+5]);
			}

			while ( (line = brData.readLine()) != null) {
				SignNum signNum = new SignNum();
				SignPS signPS = new SignPS();

				String [] data = line.split(",", 0); // 行をカンマ区切りで配列に変換

				//文字データの分解
				char ch = data[0].toCharArray()[0];	//文字
				//読み込む文字のリストになかったら次
				if (!_signs.contains(ch)) {
					continue;
				}
				for (int i = 0; i < 4; i++) {

				}
				char [] nums = {	//文字番号
						data[1].toCharArray()[0],
						data[2].toCharArray()[0],
						data[3].toCharArray()[0],
						data[4].toCharArray()[0],
				};
				int ps = Integer.decode(data[5]); //PS

				char fullwidthCh = getFullWidthChar(ch); //全角文字

				boolean success = true;	//画像の読み込みに成功したか
				BufferedImage [] images = new BufferedImage [players]; //SI

				//画像をプレイヤー数分読み込む
				for (int j = 0; j < players; j++) {
					File imageFile = new File(_imagePath[j].getPath() + "\\" + fullwidthCh + ".png");
					try {
						images[j] = ImageIO.read( imageFile );
					} catch (IOException | IllegalArgumentException  e) {
						System.out.println("'" + ch + "' Image was not found!");
						success = false;
						break;
					}
				}

				if (!success) {
					continue;
				}

				//SignDataへ文字データを追加
				for (int j = 0; j < players; j++) {
					SignData.add(j+1, ch, nums, ps, images[j]);
				}

				System.out.println( "'" + ch  + "' {SN:(" + data[1] + "," + data[2] + "," + data[3] + "," + data[4] + ") PS:" + data[5] + "}" );
			}

			brData.close();
			System.out.println("SignData Loaded");
		} catch (IOException e) {
			System.out.println("Wrong Path of SignData");
		}
	}

	/**
	 * 半角から全角に変換する
	 * @param _ch 半角文字
	 * @return 全角文字
	 */
	private static char getFullWidthChar(char _ch) {
		if (_ch >= 'A' && _ch <= 'Z') {
			return (char)('Ａ' + _ch - 'A');
		} else if (_ch >= '0' && _ch <= '9') {
			return (char)('０' + _ch - '0');
		} else {
			switch (_ch) {
			case ' ':
				return '　';
			case '*':
				return '＊';
			}
		}
		return '　';
	}
}
