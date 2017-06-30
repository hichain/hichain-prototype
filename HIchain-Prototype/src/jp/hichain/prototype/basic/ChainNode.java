package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.ui.SignData;
import java.util.*;

public class ChainNode {
	private final Square thisSquare;
	protected EnumMap<Relation, Set<ChainNode>> relationMap;
	private boolean valid = true;
	private boolean mature = false;

	public ChainNode(Square _thisSq) {
		thisSquare = _thisSq;
		relationMap = new EnumMap<Relation, Set<ChainNode>>(Relation.class) {{
			put(Relation.PARENT, new HashSet<>());
			put(Relation.CHILD, new HashSet<>());
		}};
		valid = !thisSquare.hasPluralChains();
	}

	public enum Relation {
		PARENT,
		CHILD;

		private Edge edge;
		private Relation opposite;
		static {
			PARENT.edge = Edge.ROOT;
			CHILD.edge = Edge.LEAF;
			PARENT.opposite = CHILD;
			CHILD.opposite = PARENT;
		}

		public Edge getEdge() {
			return edge;
		}

		public Relation getOpposite() {
			return opposite;
		}
	}

	public enum Edge {
		ROOT,
		LEAF;

		private Relation relation;
		static {
			ROOT.relation = Relation.PARENT;
			LEAF.relation = Relation.CHILD;
		}

		public Relation getRelation() {
			return relation;
		}
	}

	public boolean isEdgeOf(Edge edge) {
		return relationMap.get( edge.getRelation() ).size() == 0;
	}

	public boolean isValid() { return valid; }

	public void setValid(boolean _valid) {
		valid = _valid;
	}

	public boolean isMature() {
		return mature;
	}

	public void setMature(boolean _mature) {
		mature = _mature;
	}

	public Square getSquare() {
		return thisSquare;
	}

	public Set<ChainNode> get(Relation relation) {
		return relationMap.get(relation);
	}

	public void add(ChainNode node, Relation relation) {
		relationMap.get(relation).add(node);
	}

	public void setMatureAll(boolean _mature) {
		setMaturesAll(Relation.PARENT, _mature);
		setMaturesAll(Relation.CHILD, _mature);
	}

	public void setValidAll(boolean _valid) {
		setValidAll(Relation.PARENT, _valid);
		setValidAll(Relation.CHILD, _valid);
	}

	@Override
	public String toString() {
		String str = "";
		List<ChainNode> roots = getEdges(Edge.ROOT);
		for (int i = 0; i < roots.size(); i++) {
			ChainNode root = roots.get(i);
			str += root.toString("", null);
			if (i < roots.size()-1) str += "\n";
		}
		return str;
	}

	protected String toString(String inputStr, ChainNode sourceNode) {
		String pos = thisSquare.getPosition().toString();
		if (mature) pos += "m";
		if (valid) pos += "v";
		if (isEdgeOf(Edge.LEAF)) return (inputStr + pos);

		String currentStr = inputStr + pos + " -> ";
		String str = (inputStr.equals("")) ? " > " : "";
		int i = 0;
		for (ChainNode child : get(Relation.CHILD)) {
			str += child.toString(currentStr, this);
			if (i < relationMap.get(Relation.CHILD).size()-1) str += "\n > ";
			i++;
		}

		return str;
	}

	protected List<ChainNode> getEdges(Edge edge) {
		List<ChainNode> edgeNodes = new ArrayList<>();
		search(edgeNodes, edge, null);
		return edgeNodes;
	}

	protected void search(List <ChainNode> nodes, Edge edge, ChainNode sourceNode) {
		if (isEdgeOf(edge)) {
			nodes.add(this);
			return;
		}

		for (ChainNode parent : get(edge.getRelation())) {
			parent.search(nodes, edge, this);
		}
	}

	private void setMaturesAll(Relation relation, boolean mature) {
		setMature(mature);
		for (ChainNode node : get(relation)) {
			node.setMaturesAll(relation, mature);
		}
	}

	private void setValidAll(Relation relation, boolean valid) {
		setValid(valid);
		for (ChainNode node : get(relation)) {
			node.setValidAll(relation, valid);
		}
	}
}
