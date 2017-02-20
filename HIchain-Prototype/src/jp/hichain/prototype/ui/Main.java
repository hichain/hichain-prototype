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

import jp.hichain.prototype.algorithm.Judge;
import jp.hichain.prototype.basic.ChainSign;
import jp.hichain.prototype.basic.Move;
import jp.hichain.prototype.basic.Player;
import jp.hichain.prototype.basic.SignImage;
import jp.hichain.prototype.basic.SignNum;
import jp.hichain.prototype.basic.SignPS;
import jp.hichain.prototype.concept.CardColor;
import jp.hichain.prototype.concept.Direction;
import jp.hichain.prototype.concept.PS;

public class Main {

	//文字データのパス
	public static final String SIGNDATAPATH = ".\\data\\SignData.csv";
	//SIのパス (色別)
	public static final Map<CardColor, String> SIGNIMAGEPATH = new HashMap<CardColor, String>() {{
		put(CardColor.BLACK, ".\\data\\cards\\D");
		put(CardColor.RED, ".\\data\\cards\\R");
	}};
	//読み込む文字
	public static char [] LOADINGSIGNS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '*'
	};
	public static Map<String, Player> players;

	public static void main(String[] args) {
		init();
		createPlayers();

		long start = System.currentTimeMillis();
		testJudge(Direction.SOUTH, "1P", 'X', "2P", 'H');
		long end = System.currentTimeMillis();
		System.out.println((end-start) + " ms");
	}

	private static void testJudge(final Direction root_target, final String rootPL, final char rootSC, final String holdingPL, final char holdingSC) {
		Move root = new Move(
			players.get(rootPL), SignData.get(rootSC)
		);
		Move sign = new Move(
			root, root_target
		);
		//rootの下にsignがある

		ChainSign holdingSign = SignData.get(holdingSC);

		System.out.println("\nStart Judging...\n");

		PS.Contact result = Judge.getContact(
			players.get(holdingPL), holdingSign, sign
		);

		System.out.println("\nFinal Result: " + result + "\n");
	}

	private static void createPlayers() {
		players = new HashMap<String, Player>() {{
			put("1P", new Player("1P", CardColor.BLACK));
			put("2P", new Player("2P", CardColor.RED));
		}};
	}

	private static void init() {
		File signdataFile = new File(SIGNDATAPATH);
		Map<CardColor, File> signImageFiles = new HashMap<>();
		for (Map.Entry<CardColor, String> entry : SIGNIMAGEPATH.entrySet()) {
			signImageFiles.put(
				entry.getKey(), new File( entry.getValue() )
			);
		}
		Set <Character> signSet = getSignSet(LOADINGSIGNS);
		signSet.add(' ');

		loadSignData( signdataFile, signImageFiles, signSet );
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
	private static void loadSignData(File _dataPath, Map<CardColor, File> _imagePath, Set <Character> _signs) {
		System.out.println("Loading SignData: ");

		BufferedReader brData = null;
		try {
			brData = new BufferedReader( new FileReader(_dataPath) );

			Direction [] dirsHeader = new Direction[4];
			Direction [] psHeader = new Direction[16];

			String line = brData.readLine();
			line = brData.readLine();
			String [] header = line.split(",", 0);
			for (int i = 0; i < dirsHeader.length; i++) {
				dirsHeader[i] = Direction.valueOf(header[i+1]);
			}
			for (int i = 0; i < psHeader.length; i++) {
				psHeader[i] = Direction.valueOf(header[i+5]);
			}

			while ( (line = brData.readLine()) != null) {
				SignNum signNum = new SignNum();
				SignPS signPS = new SignPS();
				Map<CardColor, SignImage> signImages = new HashMap<>();

				String [] data = line.split(",", 0); // 行をカンマ区切りで配列に変換

				//文字データの分解
				char signChar = data[0].toCharArray()[0];	//文字
				//読み込む文字のリストになかったら次
				if (!_signs.contains(signChar)) {
					continue;
				}
				//SNを代入
				for (int i = 0; i < 4; i++) {
					signNum.add(dirsHeader[i], data[i+1].toCharArray()[0]);
				}
				//SignPSを代入
				for (int i = 0; i < 16; i++) {
					if (Integer.parseInt(data[i+5]) == 1) {
						signPS.add(psHeader[i]);
					}
				}

				char fullwidthCh = getFullWidthChar(signChar); //全角文字

				boolean success = true;	//画像の読み込みに成功したか

				//画像をプレイヤー数分読み込む
				for (Map.Entry<CardColor, File> entry : _imagePath.entrySet()) {
					File imageFile = new File(entry.getValue().getPath() + "\\" + fullwidthCh + ".png");
					try {
						BufferedImage image = ImageIO.read( imageFile );
						signImages.put(entry.getKey(), new SignImage(image) );
					} catch (IOException | IllegalArgumentException  e) {
						System.out.println("'" + signChar + "' Image was not found!");
						success = false;
						break;
					}
				}

				if (!success) {
					continue;
				}

				//SignDataへ文字データを追加
				SignData.add( new ChainSign(signChar, signPS, signNum, signImages) );

				System.out.print("'" + signChar + "' ");
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
