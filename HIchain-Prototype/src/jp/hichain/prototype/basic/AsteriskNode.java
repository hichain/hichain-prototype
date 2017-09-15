package jp.hichain.prototype.basic;

import java.util.*;
import jp.hichain.prototype.concept.Chain;
import jp.hichain.prototype.concept.Chain.Relation;

/**
 * Created by NT on 2017/06/28.
 * @deprecated アスタリスクの実装が未完成
 */
public class AsteriskNode extends ChainNode {
	private Set<ChainPair> pairs;

	public AsteriskNode(Square _thisSq, ChainCombination combination) {
		super(_thisSq, combination);
		pairs = new HashSet<>();
	}

	public boolean isEdgeOf(Relation relation, ChainNode sourceNode) {
		Set<ChainNode> nextNodes = get(sourceNode, relation);
		return nextNodes.size() == 0;
	}

	public boolean isMature(ChainNode sourceNode, Relation relation) {
		return !isEdgeOf(relation, sourceNode);
	}

	public Set<ChainPair> getPairs() {
		return pairs;
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
	public boolean isEdgeOf(Relation relation) {
		assert false : "Not allow isEdgeOf(Relation), Use isEdfeOf(Relation, ChainNode, Chain.Relation) from an asterisk node";
		return super.isEdgeOf(relation);
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
	protected void add(ChainNode node, ChainNode sourceNode, Relation relation) {
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
		String sign = "";
		sign += getSignChar().get();
		sign += getSquare().getPosition().toString();
		if (isMature(sourceNode, Relation.CHILD)) sign += "m";
		if (isValid()) sign += "v";
		if (isEdgeOf(Relation.CHILD, sourceNode)) return (inputStr + sign);

		String currentStr = inputStr + sign + " -> ";
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
	protected List<ChainNode> getEdges(Relation relation) {
		List<ChainNode> edgeNodes = new ArrayList<>();
		for (ChainPair pair : pairs) {
			if (pair.isAlone()) {
				search(edgeNodes, relation, pair.getAloneNode());
			} else {
				search(edgeNodes, relation, pair.get(Relation.PARENT));
			}
		}
		return edgeNodes;
	}

	@Override
	protected void search(List<ChainNode> nodes, Relation relation, ChainNode sourceNode) {
		if(this.isEdgeOf(relation, sourceNode)) {
			nodes.add(this);
			return;
		}

		for (ChainNode parent : get(relation)) {
			parent.search(nodes, relation, this);
		}
	}

	protected void setMaturesAll(boolean mature, Relation relation, ChainNode sourceNode) {
		for (ChainNode node : get(sourceNode, relation)) {
			node.setMaturesAll(mature, relation);
		}
	}
}
