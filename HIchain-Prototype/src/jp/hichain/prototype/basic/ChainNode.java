package jp.hichain.prototype.basic;

import jp.hichain.prototype.concept.ScoredString;
import jp.hichain.prototype.ui.SignData;
import java.util.*;

public class ChainNode {
	private final Square square;
	private ChainCombination combination;
	private SignChar signChar;

	protected EnumMap<Relation, Set<ChainNode>> relationMap;
	private EnumMap<Relation, Integer> lengthMap;
	private boolean valid = true;
	private boolean mature = false;
	
	public ChainNode(Square thisSquare, ChainCombination combination) {
		this.square = thisSquare;
		this.combination = combination;
		signChar = this.square.getSign().getSN().get( combination.getSignDir() );
		relationMap = new EnumMap<Relation, Set<ChainNode>>(Relation.class) {{
			put(Relation.PARENT, new HashSet<>());
			put(Relation.CHILD, new HashSet<>());
		}};
		lengthMap = new EnumMap<Relation, Integer>(Relation.class) {{
			put(Relation.PARENT, 0);
			put(Relation.CHILD, 0);
		}};
		valid = !this.square.hasPluralChains();
	}

	//TODO: Square的にコンストラクタでソースを指定して自動で親子ノードを追加し長さもアップデートする
	public ChainNode(Square thisSquare, ChainCombination combination, ChainNode source, Relation relation) {

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

	public Square getSquare() {
		return square;
	}

	public ChainCombination getCombination() {
		return combination;
	}

	public SignChar getSignChar() {
		return signChar;
	}

	public Set<ChainNode> get(Relation relation) {
		return relationMap.get(relation);
	}

	public void add(ChainNode node, Relation relation) {
		relationMap.get(relation).add(node);
	}

	public int getLength(Relation relation) {
		return lengthMap.get(relation);
	}

	private void updateLength(Relation relation) {
		lengthMap.put(relation, getLength(relation)+1);
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

	public void setMatureAll(boolean _mature) {
		setMaturesAll(_mature, Relation.PARENT);
		setMaturesAll(_mature, Relation.CHILD);
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
		String sign = "";
		sign += signChar.get();
		sign += this.square.getPosition().toString();
		if (mature) sign += "m";
		if (valid) sign += "v";
		if (isEdgeOf(Edge.LEAF)) return (inputStr + sign);

		String currentStr = inputStr + sign + " -> ";
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

	protected void setMaturesAll(boolean mature, Relation relation) {
		setMature(mature);
		for (ChainNode node : get(relation)) {
			node.setMaturesAll(mature, relation);
		}
	}

	private void setValidAll(Relation relation, boolean valid) {
		setValid(valid);
		for (ChainNode node : get(relation)) {
			node.setValidAll(relation, valid);
		}
	}
}
