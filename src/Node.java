/*
	Main functionality of the PatriciaTree. All of the algorithmic logic is defined here.
	@author Aditya Saligrama
	@since  2017-10-13
*/

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Node
{
	// index of first difference between left and right child
	private int forkIndex;

	// left child has a 0 at position forkIndex, right child has a 1
	private Node[] children;

	// if a string doesn't terminate here, we use null at position 0 or 1
	// to show that; otherwise, that position is a bit array
	// size is always 2
	private List<int[]> terminates;

	// link to parent node for use in add method
	private Node parent;

	// constructor
	public Node (int _forkIndex, Node _parent)
	{
		forkIndex = _forkIndex;
		terminates = new ArrayList<int[]>();
		children = new Node[Constants.NODE_NUM_CHILDREN];
		parent = _parent;

		// add nulls for padding and to avoid IndexOutOfBoundsExceptions
		for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++)
			terminates.add(null);
	}

	// print number of nodes in the tree
	public int numChildren ()
	{
		int ret = 1;
		for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++) {
			if (children[i] != null)
				ret += children[i].numChildren();
		}

		return ret;
	}

	// verifiable size check for debugging purposes
	public void verifySize (List<String> checkList)
	{
		for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++) {
			if (terminates.get(i) != null)
				checkList.add(Utils.bitArrToStr(terminates.get(i)));
		}

		for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++) {
			if (children[i] != null)
				children[i].verifySize(checkList);
		}
	}

	// measure bit size of the tree
	public int byteSize ()
	{
		// JVM class header overhead
		int ret = Constants.LOGGER_JVM_CLASS_HEADER_SIZE_BYTES;

		// children array header overhead
		ret += Constants.LOGGER_JVM_ARRAY_HEADER_SIZE_BYTES;

		// terminates ArrayList overhead
		ret += Constants.LOGGER_JVM_REFERENCE_SIZE_BYTES +
			Constants.LOGGER_JVM_CLASS_HEADER_SIZE_BYTES + Constants.LOGGER_JVM_INT_SIZE_BYTES;

		// terminates capacity
		for (int[] terminal : terminates) {
			if (terminal != null) {
				ret += (Constants.LOGGER_JVM_ARRAY_HEADER_SIZE_BYTES +
					(Constants.UTILS_CHAR_BITLEN * terminal.length / Constants.LOGGER_BYTE_NUM_BITS));
			}
		}

		// children capacity
		for (Node child : children) {
			if (child != null) {
				ret += Constants.LOGGER_JVM_REFERENCE_SIZE_BYTES + child.byteSize();
			}
		}

		return ret;
	}

	// print all strings in the tree
	@Override
	public String toString ()
	{
		String ret = "";
		for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++) {
			if (terminates.get(i) != null) {
				ret += Utils.bitArrToStr(terminates.get(i)) + ", ";
			}
		}

		for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++) {
			if (children[i] != null) {
				ret += children[i].toString();
			}
		}

		return ret;
	}

	public String printNodes ()
	{
		String ret = forkIndex + " ";
		for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++) {
			if (children[i] != null)
				ret += children[i].printNodes() + " ";
		}

		return ret;
	}

	// search for a string within the tree
	public boolean findChild (int[] _toSearch, OperationLogger _opLog)
	{
		if (_toSearch.length == forkIndex + 1 && terminates != null) {
			// if the string to be searched has length equal to a string terminating here,
			// compare the strings and return their equality
			_opLog.incComparisons(_toSearch.length);
			return Arrays.equals(_toSearch, terminates.get(_toSearch[forkIndex]));
		} else if (_toSearch.length > forkIndex + 1) {
			// if the string's length is greater than forkIndex, recursively search
			// the relevant subnode
			if (children[_toSearch[forkIndex]] == null) {
				return false;
			} else {
				_opLog.incTraversals(1);
				children[_toSearch[forkIndex]].findChild(_toSearch, _opLog);
			}
		}

		// if the search string's length is shorter than forkIndex, then it is not in the tree
		return false;
	}

	// add a string to the tree
	// returns true if the tree was modified, false if not modified
	public boolean addChild (int[] _toAdd, OperationLogger _opLog)
	{
		Node toCompare = locateAddChild(_toAdd, _opLog);

		// deal with inserting into the root node
		if (toCompare.forkIndex == 0 && notNullSize(toCompare.children) < 2) {
			_opLog.incTraversals(1);
			toCompare.children[_toAdd[toCompare.forkIndex]] = new Node(_toAdd.length-1, toCompare);
			toCompare.children[_toAdd[toCompare.forkIndex]].terminates.set(_toAdd[_toAdd.length-1], _toAdd);
			return true;
		}

		// find first index of difference between _toAdd and comparison child
		int j = 0;
		int[] comp = firstNotNull(toCompare.terminates);
		int minIndex = Math.min(comp.length, _toAdd.length) - 1;
		boolean differenceFound = false;

		for (; j <= minIndex; j++) {
			_opLog.incComparisons(1);
			if (_toAdd[j] != comp[j]) {
				differenceFound = true;
				break;
			}
		}

		// fix indexing issue with for loop
		if (j > minIndex)
			j--;

		// deal with _toAdd already existing in some capacity:
		// _toAdd is a substring of (or fully matches) comp,
		// or the only difference is the last bit of _toAdd
		if (j == minIndex) {
			if (comp.length == _toAdd.length) {
				// in this case, _toAdd is exactly equal to comp,
				// or its last bit is different
				if (!differenceFound) {
					// if no difference was found, then _toAdd already exists
					return false;
				} else {
					// set _toAdd as right child of _toCompare
					toCompare.terminates.set(_toAdd[toCompare.forkIndex], _toAdd);
					return true;
				}
			} else if (comp.length < _toAdd.length) {
				// in this case comp is a substring of _toAdd, or its last bit is flipped
				// insertion should be the same as inserting into the root node
				_opLog.incTraversals(1);
				toCompare.children[_toAdd[toCompare.forkIndex]] = new Node(_toAdd.length-1, toCompare);
				toCompare.children[_toAdd[toCompare.forkIndex]].terminates.set(_toAdd[_toAdd.length-1], _toAdd);
				return true;
			}
		}

		// walk back to first parent node (toModify) encountered with index less than j
		// unlink toModify's comp[j] child, substitute with splice which contains
		// the unlinked child and another node containing _toAdd as children
		Node toModify = toCompare.walkBackToIndex(j, _opLog);
		_opLog.incTraversals(1);
		Node splice = new Node(j, toModify);
		splice.children[comp[j]] = toModify.children[comp[toModify.forkIndex]];
		splice.children[comp[j]].parent = splice;
		toModify.children[comp[toModify.forkIndex]] = splice;

		if (j == minIndex) {
			// if _toAdd is a substring of comp, then _toAdd becomes a terminal string of splice
			splice.terminates.set(_toAdd[j], _toAdd);
		} else {
			// otherwise we create a new child of splice that serves this purpose
			_opLog.incTraversals(1);
			splice.children[_toAdd[j]] = new Node(_toAdd.length-1, splice);
			splice.children[_toAdd[j]].terminates.set(_toAdd[_toAdd.length-1], _toAdd);
		}

		return true;
	}

	// remove a string from the tree
	// returns true if tree was modified (i.e. the string was in the tree),
	// false if it was not
	public boolean removeChild (int[] _toRemove, OperationLogger _opLog)
	{
		// find comparison node
		Node located = locateRemoveChild(_toRemove, _opLog);

		// if relevant child does not match _toRemove, return false as the tree did not change
		if (_toRemove.length == located.forkIndex + 1) {
			_opLog.incComparisons(_toRemove.length);
			if (Arrays.equals(located.terminates.get(_toRemove[located.forkIndex]), _toRemove))
				located.terminates.set(_toRemove[located.forkIndex], null);
		} else {
			return false;
		}

		// clean up the tree
		if (notNullSize(located.terminates) == 0) {
			if (notNullSize(located.children) == 0) {
				// if located is "orphaned" in that it has no terminal strings or children,
				// it can be removed
				_opLog.incTraversals(1);
				Node pointer = located.parent;
				located.parent.children[_toRemove[located.parent.forkIndex]] = null;
				if (notNullSize(pointer.children) == 1 && pointer.forkIndex != 0) {
					// if located's parent has only one child, then we can merge the child into itself,
					// as long as pointer isn't the root node
					_opLog.incTraversals(1);
					Node grandParent = pointer.parent;
					int childIndex = _toRemove[grandParent.forkIndex];
					int grandChildIndex = Math.abs(_toRemove[pointer.forkIndex] - 1);
					grandParent.children[childIndex] = grandParent.children[childIndex].children[grandChildIndex];
					grandParent.children[childIndex].parent = grandParent;
				}
			} else if (notNullSize(located.children) == 1) {
				// if located has only one child, then we can merge the child into itself
				_opLog.incTraversals(2);
				Node pointer = located.parent;
				pointer.children[_toRemove[pointer.forkIndex]] = firstNotNull(located.children);
				pointer.children[_toRemove[pointer.forkIndex]].parent = pointer;
			}
		}

		return true;
	}

	/*******

	Helper functions for add and remove

	*******/


	// returns the number of not null elements in the array; used for terminates
	private int notNullSize (List<int[]> _list)
	{
		int ret = 0;
		for (int[] i : _list) {
			if (i != null)
				ret++;
		}

		return ret;
	}

	// returns the number of not null elements in the array; used for children
	private int notNullSize (Node[] _subNodes)
	{
		int ret = 0;
		for (Node n : _subNodes) {
			if (n != null)
				ret++;
		}

		return ret;
	}

	// returns the first not null element in the array; used for terminates
	private int[] firstNotNull (List<int[]> _list)
	{
		for (int[] i : _list) {
			if (i != null)
				return i;
		}

		return null;
	}

	// returns the first not null element in the array; used for children
	private Node firstNotNull (Node[] _subNodes)
	{
		for (Node n : _subNodes) {
			if (n != null)
				return n;
		}

		return null;
	}

	// traverse back up the tree to a node with forkIndex less than _index
	private Node walkBackToIndex (int _index, OperationLogger _opLog)
	{
		Node pointer = this;
		// stop when a node with forkIndex less than _index, or the root node is reached
		while (pointer.forkIndex >= _index && pointer.forkIndex > 0) {
			_opLog.incTraversals(1);
			pointer = pointer.parent;
		}

		return pointer;
	}

	// locate empty or terminal node for comparison and/or insertion of _toAdd
	private Node locateAddChild (int[] _toAdd, OperationLogger _opLog)
	{
		Node pointer = this;
		while (pointer.forkIndex < _toAdd.length &&
			pointer.children[_toAdd[pointer.forkIndex]] != null) {
				// find index to first stop at
				_opLog.incTraversals(1);
				pointer = pointer.children[_toAdd[pointer.forkIndex]];
		}

		// return the first node we can find with a terminal string contained within if we can proceed
		if (notNullSize(pointer.children) > 0 && pointer.forkIndex != 0) {
			while (notNullSize(pointer.terminates) == 0) {
				_opLog.incTraversals(1);
				pointer = firstNotNull(pointer.children);
			}
		}

		// if we cannot continue (or we stop at the root node),
		// return this node (useful for inserting into an empty root node)
		return pointer;
	}

	// locate node for comparison/remove of _toRemove
	private Node locateRemoveChild (int[] _toRemove, OperationLogger _opLog)
	{
		Node pointer = this;
		while (pointer.forkIndex < _toRemove.length &&
			pointer.children[_toRemove[pointer.forkIndex]] != null) {
				// find index to first stop at
				_opLog.incTraversals(1);
				pointer = pointer.children[_toRemove[pointer.forkIndex]];
		}

		if (pointer.forkIndex >= _toRemove.length) {
			_opLog.incTraversals(1);
			pointer = pointer.parent;
		}

		return pointer;
	}
}
