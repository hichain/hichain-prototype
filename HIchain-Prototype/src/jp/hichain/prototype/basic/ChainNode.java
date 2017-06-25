package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.ui.SignData;
import java.util.*;

public class ChainNode {
	private final Square thisSquare;
	private final ChainCombination combination;
	private final SignChar signChar;
	protected EnumMap<Relation, Set<ChainNode>> relationMap;
	private boolean valid = true;
	private boolean mature = false;
	private final boolean asterisk;

	public ChainNode(Square _thisSq, ChainCombination _combination) {
		thisSquare = _thisSq;
		combination = _combination;
		signChar = _thisSq.getSign().getSN().get( _combination.getSignDir() );

		relationMap = new EnumMap<Relation, Set<ChainNode>>(Relation.class) {{
			put(Relation.PARENT, new HashSet<>());
			put(Relation.CHILD, new HashSet<>());
		}};
		valid = !thisSquare.hasPluralChains();
		asterisk = (signChar.get() == '*');
	}

	public ChainNode(Square _thisSq, ChainCombination _combination, SignChar _signChar) {
		thisSquare = _thisSq;
		combination = _combination;
		signChar = _signChar;

		relationMap = new EnumMap<Relation, Set<ChainNode>>(Relation.class) {{
			put(Relation.PARENT, new HashSet<>());
			put(Relation.CHILD, new HashSet<>());
		}};
		valid = !thisSquare.hasPluralChains();
		asterisk = true;
	}

	public enum Relation {
		PARENT,
		CHILD;

		private Edge edge;
		static {
			PARENT.edge = Edge.ROOT;
			CHILD.edge = Edge.LEAF;
		}

		public Edge getEdge() {
			return edge;
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

	public Square getSquare() {
		return thisSquare;
	}

	public ChainCombination getCombination() {
		return combination;
	}

	public SignChar getSignChar() {
		return  signChar;
	}

	public boolean isEdgeOf(Edge edge) {
		return relationMap.get( edge.getRelation() ).size() == 0;
	}

	public Set<ChainNode> get(Relation relation) {
		return relationMap.get(relation);
	}

	public void add(ChainNode node, Relation relation) {
		relationMap.get(relation).add(node);
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

	public void setMatureAll(boolean _mature) {
		setMaturesAll(Relation.PARENT, _mature);
		setMaturesAll(Relation.CHILD, _mature);
	}

	public void setValidAll(boolean _valid) {
		setValidAll(Relation.PARENT, _valid);
		setValidAll(Relation.CHILD, _valid);
	}

	public boolean isAsterisk() {
		return asterisk;
	}

	@Override
	public String toString() {
		String str = "";
		List<ChainNode> roots = getEdges(Edge.ROOT);
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
		if (isEdgeOf(Edge.LEAF)) return (inputStr + pos);

		String currentStr = inputStr + pos + " -> ";
		String str = (inputStr.equals("")) ? " > " : "";
		int i = 0;
		for (ChainNode child : get(Relation.CHILD)) {
			str += child.toScoredString(currentStr);
			if (i < relationMap.get(Relation.CHILD).size()-1) str += "\n > ";
			i++;
		}

		return str;
	}

	private List<ChainNode> getEdges(Edge edge) {
		List<ChainNode> roots = new ArrayList<>();
		search(roots, edge);
		return roots;
	}

	private void search(List <ChainNode> nodes, Edge edge) {
		if (isEdgeOf(edge)) {
			nodes.add(this);
			return;
		}
		for (ChainNode parent : get(edge.getRelation())) {
			parent.search(nodes, edge);
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

	private void init(Square _thisSq, ChainCombination _combination, SignChar _signChar) {

	}
}
