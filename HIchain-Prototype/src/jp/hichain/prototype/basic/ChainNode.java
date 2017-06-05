package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChainNode {
	private final Square thisSquare;
	private Set<ChainNode> parents;
	private Set<ChainNode> children;
	private boolean valid = true;
	private boolean mature = false;

	public ChainNode(Square _thisSq) {
		thisSquare = _thisSq;
		parents = new HashSet<>();
		children = new HashSet<>();
		valid = !thisSquare.hasPluralChains();
	}

	public boolean isRoot() {
		return (parents.size() == 0);
	}

	public boolean isLeaf() {
		return (children.size() == 0);
	}

	public boolean isValid() {
		return valid;
	}

	public boolean isMature() {
		return mature;
	}

	public Square getSquare() {
		return thisSquare;
	}

	public Set<ChainNode> getParents() {
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

	public int getMaxLength() {
		int max = 1;
		max += getParentsMaxLength(0);
		max += getChildrenMaxLength(0);
		return max;
	}

	public void setMatureAll() {
		mature = true;
		parentsSetMature();
		childrenSetMature();
	}

	@Override
	public String toString() {
		String str = "";
		List<ChainNode> roots = getRoots();
		for (int i = 0; i < roots.size(); i++) {
			ChainNode root = roots.get(i);
			str += root.toScoredString("");
			if (i < roots.size()-1) str += "\n";
		}
		return str;
	}

	private String toScoredString(String inputStr) {
		String pos = thisSquare.getPosition().toString();
		if (mature) pos += "m";
		if (valid) pos += "v";
		if (isLeaf()) return (inputStr + pos);

		String currentStr = inputStr + pos + " -> ";
		String str = (inputStr == "") ? " > " : "";
		int i = 0;
		for (ChainNode child : children) {
			str += child.toScoredString(currentStr);
			if (i < children.size()-1) str += "\n > ";
			i++;
		}

		return str;
	}

	private List<ChainNode> getRoots() {
		List<ChainNode> roots = new ArrayList<>();
		searchRoots(roots);
		return roots;
	}

	private void searchRoots(List <ChainNode> nodes) {
		if (isRoot()) {
			nodes.add(this);
			return;
		}
		for (ChainNode parent : parents) {
			parent.searchRoots(nodes);
		}
	}

	private int getParentsMaxLength(int length) {
		if (isRoot()) {
			return length;
		}

		int max = 0;
		for (ChainNode node : parents) {
			int branchMax = node.getParentsMaxLength(length+1);
			if (max < branchMax) {
				max = branchMax;
			}
		}

		return max;
	}

	private int getChildrenMaxLength(int length) {
		if (isLeaf()) {
			return length;
		}

		int max = 0;
		for (ChainNode node : children) {
			int branchMax = node.getChildrenMaxLength(length+1);
			if (max < branchMax) {
				max = branchMax;
			}
		}

		return max;
	}

	private void parentsSetMature() {
		for (ChainNode node : parents) {
			node.mature = true;
			node.parentsSetMature();
		}
	}

	private void childrenSetMature() {
		for (ChainNode node : children) {
			node.mature = true;
			node.childrenSetMature();
		}
	}
}
