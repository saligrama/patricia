/*
	This file provides functionality to keep track of resource usage of the PatriciaTree.
	@author Aditya Saligrama
	@since  2017-11-04
*/

public class Logger
{
	private int totalComparisons;
	private int totalTraversals;
	private int totalMemUse;

	// add operation count from operation logger
	public void addFromOperationLogger (OperationLogger _toAdd)
	{
		totalComparisons += _toAdd.comparisons();
		totalTraversals += _toAdd.traversals();
	}

	// get the size of the tree in bytes
	public int treeByteSize (PatriciaTree _tree, boolean _changed)
	{
		if (_changed)
			totalMemUse = _tree.byteSize();

		return totalMemUse;
	}

	// total comparisons for all operations
	public int comparisons ()
	{
		return totalComparisons;
	}

	// total traversals for all operations
	public int traversals ()
	{
		return totalTraversals;
	}
}
