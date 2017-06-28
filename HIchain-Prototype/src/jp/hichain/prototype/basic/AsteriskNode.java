package jp.hichain.prototype.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
			if (pair.get(relation.getOpposite()) == sourceNode) {
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
}
