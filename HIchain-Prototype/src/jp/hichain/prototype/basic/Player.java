package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.hichain.prototype.concept.CardColor;

/**
 * プレイヤー
 * @author NT
 *
 */
public class Player {
	private static List<Player> players;

	private String name;
	private CardColor cardColor;
	private List <Move> moves;
	private List <ChainSign> fieldSigns;

	public Player(String _name, CardColor _color) {
		name = _name;
		cardColor = _color;
	}

	static {
		players = new ArrayList<>();
	}

	public static void add(Player player) {
		players.add(player);
	}

	public static Player get(String name) {
		for (Player player : players) {
			if (player.name == name) {
				return player;
			}
		}
		return null;
	}

	public static Player get(CardColor color) {
		for (Player player : players) {
			if (player.cardColor == color) {
				return player;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public CardColor getCardColor() {
		return cardColor;
	}

	public List<Move> getMoves() {
		return moves;
	}

	public Move getLastMove() {
		return moves.get(moves.size()-1);
	}

	public List<ChainSign> getFieldSigns() {
		return fieldSigns;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof Player) {
			Player player = (Player)obj;
			return (name == player.name && cardColor == player.cardColor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, cardColor);
	}

	@Override
	public String toString() {
		String string = "Player: " + name + " [" + cardColor.toString() + "]";
		return string;
	}
}
