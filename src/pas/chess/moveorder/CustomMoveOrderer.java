package src.pas.chess.moveorder;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;

import java.util.*;


// JAVA PROJECT IMPORTS
import src.pas.chess.moveorder.DefaultMoveOrderer;

public class CustomMoveOrderer
    extends Object
{

	/**
	 * TODO: implement me!
	 * This method should perform move ordering. Remember, move ordering is how alpha-beta pruning gets part of its power from.
	 * You want to see nodes which are beneficial FIRST so you can prune as much as possible during the search (i.e. be faster)
	 * @param nodes. The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
	 * @return The ordered nodes.
	 */
	public static List<DFSTreeNode> order(List<DFSTreeNode> nodes) {
		List<DFSTreeNode> capture = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> movement = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> castle = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> enpassant = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> promotepawn = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> other = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> sorted = new LinkedList<DFSTreeNode>();

		for(DFSTreeNode node : nodes) {
			if(node.getMove() != null) {
				switch (node.getMove().getType()) {
					case CAPTUREMOVE:
						capture.add(node);
					case CASTLEMOVE:
						castle.add(node);
					case PROMOTEPAWNMOVE:
						promotepawn.add(node);
					default:
						other.add(node);
				}
			} else {
				other.add(node);
			}
		}
		sorted.addAll(capture);
		sorted.addAll(castle);
		sorted.addAll(promotepawn);
		sorted.addAll(other);
		return sorted;
	}
}
