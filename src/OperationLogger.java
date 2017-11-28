/*
  Provides CPU (traversal and comparison) time and memory usage functionality for a single operation
	@author Aditya Saligrama
	@since  2017-11-25
*/

public class OperationLogger
{
	private int comparisons;
	private int traversals;

	// constructor
	public OperationLogger ()
	{
		comparisons = 0;
		traversals = 0;
	}

	// increase number of comparisons
	public void incComparisons (int _toAdd)
	{
		comparisons += _toAdd;
	}

	// increase number of traversals
	public void incTraversals (int _toAdd)
	{
		traversals += _toAdd;
	}

	// return comparisons used for an operation
	public int comparisons ()
	{
		return comparisons;
	}

	// return traversals used for an operation
	public int traversals ()
	{
		return traversals;
	}
}
