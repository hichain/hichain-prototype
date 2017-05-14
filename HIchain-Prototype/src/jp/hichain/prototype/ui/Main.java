package jp.hichain.prototype.ui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import jp.hichain.prototype.algorithm.ChainSearcher;
import jp.hichain.prototype.algorithm.Converter;
import jp.hichain.prototype.algorithm.Judge;
import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.SignChar;
import jp.hichain.prototype.basic.SignImage;
import jp.hichain.prototype.basic.SignNum;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.CardColor;
import jp.hichain.prototype.concept.PS;
import jp.hichain.prototype.concept.SignDir;

public class Main {

	//文字データのパス
	public static final String SIGNDATAPATH = ".\\data\\SignData.csv";
	//SIのパス (色別)
	public static final Map<CardColor, String> SIGNIMAGEPATH = new HashMap<CardColor, String>() {{
		put(CardColor.BLACK, ".\\data\\cards\\D");
		put(CardColor.RED, ".\\data\\cards\\R");
	}};

	public static void main(String[] args) {
		init();
		createPlayers();

		Square.init();
		long start = System.currentTimeMillis();
		//testJudge();
		//testChainSearch();
		testConvert();
		long end = System.currentTimeMillis();
		System.out.println((end-start) + " ms");
	}

	private static void testConvert() {
		Converter.init(3, 2, 5)	;

		Player player = Player.get("1P");
		Square root = Square.get(-1, -1);
		root.make(player, SignData.get('H'));
		Square around1 = root.getAround(AroundDir.SOUTH);
		around1.make(player, SignData.get('I'));
		Square around2 = around1.getAround(AroundDir.SOUTH);
		around2.make(player, SignData.get('J'));
		System.out.println( Converter.getPointsAll(player) );
	}

	private static void testChainSearch() {
		Square root = Square.get(-1, -1);
		root.make(Player.get("1P"), SignData.get('H'));
		Square around = root.getAround(AroundDir.NORTH);
		around.make(Player.get("1P"), SignData.get('I'));

		System.out.println("\nStart ChainSearching...\n");

		int hits = ChainSearcher.search(root, AroundDir.NORTH);

		System.out.println("\nFinal Result: " + hits + " chains hit");
	}

	private static void testJudge() {
		Square root = Square.get(-1, -1);
		root.make(Player.get("1P"), SignData.get('O'));
		Square around = root.getAround(AroundDir.NORTH);

		ChainSign holdingSign = SignData.get('H');

		System.out.println("\nStart Judging...\n");

		PS.Contact result = Judge.getContact(
			Player.get("2P"), holdingSign, around
		);

		System.out.println("\nFinal Result: " + result + "\n");
	}

	private static void createPlayers() {
		Player.add( new Player("1P", CardColor.BLACK) );
		Player.add( new Player("2P", CardColor.RED) );
	}

	private static void init() {
		File signdataFile = new File(SIGNDATAPATH);
		Map<CardColor, File> signImageFiles = new HashMap<>();
		for (Map.Entry<CardColor, String> entry : SIGNIMAGEPATH.entrySet()) {
			signImageFiles.put(
				entry.getKey(), new File( entry.getValue() )
			);
		}

		loadSignData( signdataFile, signImageFiles);
/*
		ChainSign sign = SignData.get('A');
		sign.resize(0.8);

		SIPanel panel = new SIPanel(bs);
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setTitle("SI Frame");
		frame.setSize(520, 540);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
*/
	}

	private static Set <Character> getSignSet(char [] _signs) {
		Set <Character> signSet = new HashSet<Character>();
		for (char ch : _signs) {
			signSet.add(ch);
		}
		return signSet;
	}

	/**
	 * 文字データをロードする
	 * @param _dataPath SN、PSの入ったCSVのパス
	 * @param _imagePath SIのパス (色の数分)
	 * @param _signs ロードする文字
	 */
	private static void loadSignData(File _dataPath, Map<CardColor, File> _imagePath) {
		System.out.println("Loading SignData: ");

		BufferedReader brData = null;
		try {
			brData = new BufferedReader( new FileReader(_dataPath) );

			SignDir [] dirsHeader = new SignDir[4];
			PS [] psHeader = new PS[16];

			String line = brData.readLine();
			line = brData.readLine();
			String [] header = line.split(",", 0);
			for (int i = 0; i < dirsHeader.length; i++) {
				dirsHeader[i] = SignDir.valueOf(header[i+1]);
			}
			for (int i = 0; i < psHeader.length; i++) {
				psHeader[i] = PS.valueOf(header[i+5]);
			}

			while ( (line = brData.readLine()) != null) {
				SignNum signNum = new SignNum();
				SignPS signPS = new SignPS();
				Map<CardColor, SignImage> signImages = new HashMap<>();

				String [] data = line.split(",", 0); // 行をカンマ区切りで配列に変換

				char ch = data[0].charAt(0);
				//読み込む文字のリストになかったら次
				if (!SignChar.contains(ch)) {
					continue;
				}

				//文字データの分解
				SignChar signChar = SignChar.get(ch);	//文字
				//SNを代入
				for (int i = 0; i < 4; i++) {
					char partSN = data[i+1].charAt(0);
					if (partSN == ' ') continue;
					signNum.put(dirsHeader[i], SignChar.get(partSN));
				}
				//SignPSを代入
				for (int i = 0; i < 16; i++) {
					if (Integer.parseInt(data[i+5]) == 1) {
						signPS.add(psHeader[i]);
					}
				}

				char fullwidthCh = getFullWidthChar(signChar.get()); //全角文字

				boolean success = true;	//画像の読み込みに成功したか

				//画像をプレイヤー数分読み込む
				for (Map.Entry<CardColor, File> entry : _imagePath.entrySet()) {
					File imageFile = new File(entry.getValue().getPath() + "\\" + fullwidthCh + ".png");
					try {
						BufferedImage image = ImageIO.read( imageFile );
						signImages.put(entry.getKey(), new SignImage(image) );
					} catch (IOException | IllegalArgumentException  e) {
						System.out.println("'" + signChar.get() + "' Image was not found!");
						success = false;
						break;
					}
				}

				if (!success) {
					continue;
				}

				//SignDataへ文字データを追加
				SignData.add( new ChainSign(signChar, signPS, signNum, signImages) );

				System.out.print("'" + signChar.get() + "' ");
			}

			brData.close();
			System.out.println("\nSignData Loaded");
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
