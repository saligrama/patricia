/*
	Provides front-end (i.e., user-accessible) functionality for the PatriciaTree.
	@author Aditya Saligrama
	@since  2017-11-04
*/

public class PatriciaTree
{
	private int size;
	private Node root;

	// constructor
	public PatriciaTree ()
	{
		root = new Node(0, null);
		size = 0;
	}

	// size of the tree
	public int size ()
	{
		return size;
	}

	// number of nodes in the tree
	public int numNodes ()
	{
		return root.numChildren();
	}

	// size of the tree in bytes
	public int byteSize ()
	{
		return root.byteSize();
	}

	// search for a string in the tree
	public boolean find (String _toSearch)
	{
		return root.findChild(Utils.strToBitArr(_toSearch), new OperationLogger());
	}

	// search for a string in the tree (measuring traversals and comparisons)
	public boolean find (String _toSearch, OperationLogger _opLog)
	{
		return root.findChild(Utils.strToBitArr(_toSearch), _opLog);
	}

	// add a string to the tree
	public boolean add (String _toAdd)
	{
		boolean ret = root.addChild(Utils.strToBitArr(_toAdd), new OperationLogger());
		if (ret)
			size++;
		return ret;
	}

	// add a string to the tree (measuring traversals and comparisons)
	public boolean add (String _toAdd, OperationLogger _opLog)
	{
		boolean ret = root.addChild(Utils.strToBitArr(_toAdd), _opLog);
		if (ret)
			size++;
		return ret;
	}


	// remove a string from the tree
	public boolean remove (String _toRemove)
	{
		boolean ret = root.removeChild(Utils.strToBitArr(_toRemove), new OperationLogger());
		if (ret)
			size--;
		return ret;
	}

	// remove a string from the tree (measuring traversals and comparisons)
	public boolean remove (String _toRemove, OperationLogger _opLog)
	{
		boolean ret = root.removeChild(Utils.strToBitArr(_toRemove), _opLog);
		if (ret)
			size--;
		return ret;
	}

	// print all strings in the tree
	@Override
	public String toString ()
	{
		String ret = root.toString();
		return "[" + ret.substring(0, ret.length() - 2) + "]";
	}

	// print all nodes in the tree
	public String printNodes ()
	{
		return root.printNodes();
	}
}
