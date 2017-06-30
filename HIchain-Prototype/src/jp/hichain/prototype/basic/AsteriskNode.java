package jp.hichain.prototype.basic;

import java.util.*;

/**
 * Created by NT on 2017/06/28.
 */
public class AsteriskNode extends ChainNode {
	private Set<ChainPair> pairs;

	public AsteriskNode(Square _thisSq) {
		super(_thisSq);
		pairs = new HashSet<>();
	}

	public boolean isEdgeOf(Edge edge, ChainNode sourceNode) {
		Set<ChainNode> nextNodes = get(sourceNode, edge.getRelation().getOpposite());
		return nextNodes.size() == 0;
	}

	public boolean isMature(ChainNode sourceNode, Relation relation) {
		return !isEdgeOf(relation.getEdge(), sourceNode);
	}

	public ChainPair getAlonePair(ChainNode node) {
		for (ChainPair pair : pairs) {
			if (pair.getAloneNode() == node) return pair;
		}
		return null;
	}

	public Set<ChainPair> getAlonePairs() {
		Set<ChainPair> outputPairs = new HashSet<>();
		for (ChainPair pair : pairs) {
			if (pair.isAlone()) outputPairs.add(pair);
		}
		return outputPairs;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean isEdgeOf(Edge edge) {
		assert false : "Not allow isEdgeOf(Edge), Use isEdfeOf(Edge, ChainNode, ChainNode.Relation) from an asterisk node";
		return super.isEdgeOf(edge);
	}

	//relation: sourceNodeからみたこのノードとの関係
	public Set<ChainNode> get(ChainNode sourceNode, Relation relation) {
		Set<ChainNode> outputNodes = new HashSet<>();
		for (ChainPair pair : pairs) {
			if (pair.get(relation.getOpposite()) == sourceNode && !pair.isAlone()) {
				outputNodes.add( pair.get(relation) );
			}
		}
		return outputNodes;
	}

	@Override
	public Set<ChainNode> get(Relation relation) {
		assert false : "Not allow get(ChainNode.Relation), Use get(ChainNode, Relation) from an asterisk node";
		return super.get(relation);
	}

	//matureになる*に対してノードを追加する
	public void add(ChainNode node, ChainNode sourceNode, Relation relation) {
		super.add(node, relation);
		ChainPair pair = new ChainPair( getAlonePair(sourceNode), node, relation );
		addPair(pair);
	}

	//immatureになる*に対してノードを追加する
	@Override
	public void add(ChainNode node, Relation relation) {
		super.add(node, relation);
		addPair( new ChainPair(node) );
	}

	private void addPair(ChainPair chainPair) {
		pairs.add(chainPair);
	}

	@Override
	protected String toString(String inputStr, ChainNode sourceNode) {
		String pos = getSquare().getPosition().toString();
		if (isMature(sourceNode, Relation.CHILD)) pos += "m";
		if (isValid()) pos += "v";
		if (isEdgeOf(Edge.LEAF, sourceNode)) return (inputStr + pos);

		String currentStr = inputStr + pos + " -> ";
		String str = (inputStr.equals("")) ? " > " : "";
		Iterator<ChainNode> iterator = get(sourceNode, Relation.CHILD).iterator();
		while (iterator.hasNext()){
			ChainNode child = iterator.next();
			str += child.toString(currentStr, this);
			if (iterator.hasNext()) str += "\n > ";
		}

		return str;
	}

	@Override
	protected List<ChainNode> getEdges(Edge edge) {
		List<ChainNode> edgeNodes = new ArrayList<>();
		for (ChainPair pair : pairs) {
			if (pair.isAlone()) {
				search(edgeNodes, edge, pair.getAloneNode());
			} else {
				search(edgeNodes, edge, pair.get(Relation.PARENT));
			}
		}
		return edgeNodes;
	}

	@Override
	protected void search(List<ChainNode> nodes, Edge edge, ChainNode sourceNode) {
		if(this.isEdgeOf(edge, sourceNode)) {
			nodes.add(this);
			return;
		}

		for (ChainNode parent : get(edge.getRelation())) {
			parent.search(nodes, edge, this);
		}
	}
}
