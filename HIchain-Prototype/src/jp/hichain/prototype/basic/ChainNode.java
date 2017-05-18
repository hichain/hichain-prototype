package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.List;

public class ChainNode {
	private final Square thisSquare;
	private List<ChainNode> parents;
	private List<ChainNode> children;

	public ChainNode(Square _thisSq) {
		thisSquare = _thisSq;
		parents = new ArrayList<>();
		children = new ArrayList<>();
	}

	public boolean isRoot() {
		return (parents.size() == 0);
	}

	public boolean isLeaf() {
		return (children.size() == 0);
	}

	public Square getSquare() {
		return thisSquare;
	}

	public List<ChainNode> getParent() {
		return parents;
	}

	public List<ChainNode> getChildren() {
		return children;
	}

	public void addParent(ChainNode _node) {
		parents.add(_node);
	}

	public void addChild(ChainNode _node) {
		children.add(_node);
	}

	@Override
	public String toString() {
		if (isLeaf()) {
			return thisSquare.getPosition().toString();
		}

		String string = "";
		for (ChainNode node : children) {
			string += " -> " + node.toString() + "\n";
		}
		return string;
	}
}
