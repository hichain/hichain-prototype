package jp.hichain.prototype.basic;

import java.util.List;

import jp.hichain.prototype.concept.CardColor;

/**
 * プレイヤー
 * @author NT
 *
 */
public class Player {
	private String name;
	private CardColor cardColor;
	private List <Move> moves;
	private List <ChainSign> fieldSigns;

	public Player(String _name, CardColor _color) {
		name = _name;
		cardColor = _color;
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
}
