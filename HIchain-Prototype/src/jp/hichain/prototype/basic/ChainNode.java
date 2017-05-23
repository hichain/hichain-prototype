package jp.hichain.prototype.basic;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		String str = "";
		List<ChainNode> roots = getRoots();
		for (int i = 0; i < roots.size(); i++) {
			ChainNode root = roots.get(i);
			str += " > " + root.toScoredString("");
			if (i < roots.size()-1) str += "\n";
		}
		return str;
	}

	private String toScoredString(String inputStr) {
		String pos = thisSquare.getPosition().toString();
		if (isLeaf()) return pos;

		String currentStr = inputStr + pos + " -> ";
		String str = currentStr;
		int i = 0;
		for (ChainNode child : children) {
			str += child.toScoredString(currentStr);
			i++;
		}

		str = " > " + str;
		return str;
	}

	private List<ChainNode> getRoots() {
		List<ChainNode> roots = new ArrayList<>();
		addRoots(roots);
		return roots;
	}

	private void addRoots(List <ChainNode> nodes) {
		if (isRoot()) {
			nodes.add(this);
			return;
		}
		for (ChainNode parent : parents) {
			parent.addRoots(nodes);
		}
	}
}
