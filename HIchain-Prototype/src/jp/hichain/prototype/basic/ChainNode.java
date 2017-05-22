package jp.hichain.prototype.basic;

import java.util.HashSet;
import java.util.Set;

public class ChainNode {
	private final Square thisSquare;
	private Set<ChainNode> parents;
	private Set<ChainNode> children;

	public ChainNode(Square _thisSq) {
		thisSquare = _thisSq;
		parents = new HashSet<>();
		children = new HashSet<>();
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

	public Set<ChainNode> getParent() {
		return parents;
	}

	public Set<ChainNode> getChildren() {
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
		return toIncrementalString("");
	}

	private String toIncrementalString(String str) {
		String pos = thisSquare.getPosition().toString();
		if (isLeaf()) {
			return pos;
		}

		String newStr = str;
		String increment = pos + " -> ";
		int i = 0;
		for (ChainNode node : children) {
			newStr += increment + node.toIncrementalString(str);
			if (i < children.size()-1) {
				newStr += "\n > " + str;
			}
			i++;
		}
		return newStr;
	}
}
