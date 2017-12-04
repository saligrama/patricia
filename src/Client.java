/*
	This file provides client functionality (REPL) for the PatriciaTree.
	@author Aditya Saligrama
	@since  2017-11-04
*/

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Client
{
	public static void main (String[] args) throws IOException
	{
		PatriciaTree tree = new PatriciaTree();

		System.out.println();
		System.out.println("============");
		System.out.println("INSTRUCTIONS");
		System.out.println("============");

		System.out.println("Type \"" + Constants.CLIENT_ADD_COMMAND + "\" without quotes, followed by a word, to add it to the tree.");
		System.out.println("Type \"" + Constants.CLIENT_REMOVE_COMMAND + "\" without quotes, followed by a word, to remove it from the tree.");
		System.out.println("Type \"" + Constants.CLIENT_FIND_COMMAND + "\" without quotes, followed by a word, to see if the word exists in the tree");
		System.out.println("Type \"" + Constants.CLIENT_ADD_FILE_COMMAND + "\" followed by an absolute or relative file path without quotes to input a file containing words to add to the tree.");
		System.out.println("Type \"" + Constants.CLIENT_REMOVE_FILE_COMMAND + "\" followed by an absolute or relative file path without quotes to input a file containing words to remove from the tree.");
		System.out.println("Type \"" + Constants.CLIENT_PRINT_COMMAND + " " + Constants.CLIENT_PRINT_STAT_COMMAND + "\" to print statistics (number of strings, memory usage, total traversals, etc.) about the tree.");
		System.out.println("Type \"" + Constants.CLIENT_PRINT_COMMAND + " " + Constants.CLIENT_PRINT_TREE_COMMAND + "\" to print the tree itself.");
		System.out.println("Type \"" + Constants.CLIENT_QUIT_COMMAND + "\" without quotes to quit");
		System.out.println();

		System.out.print("Input > ");

		Scanner in = new Scanner(System.in);
		String next = in.next();

		Logger logger = new Logger();

		while (!next.equals(Constants.CLIENT_QUIT_COMMAND)) {
			next = next.toUpperCase();
			if (next.equals(Constants.CLIENT_ADD_COMMAND)) {
				addToTreeStat(tree, in.next(), logger);
			} else if (next.equals(Constants.CLIENT_REMOVE_COMMAND)) {
				removeFromTreeStat(tree, in.next(), logger);
			} else if (next.equals(Constants.CLIENT_FIND_COMMAND)) {
				String word = in.next().toUpperCase().replaceAll("[^A-Za-z]", "");
				OperationLogger opLog = new OperationLogger();
				boolean ret = tree.find(word, opLog);
				System.out.println();
				System.out.println((ret ? "Successfully found " : "Failed to find ") + "word " + word + " in tree");
				System.out.println("Traversals needed for search operation: " + opLog.traversals());
				System.out.println("Bitwise comparisons needed for search operation: " + opLog.comparisons());
				System.out.println();

				logger.addFromOperationLogger(opLog);
			} else if (next.equals(Constants.CLIENT_ADD_FILE_COMMAND)) {
				fileIn(tree, in.next(), logger);
			} else if (next.equals(Constants.CLIENT_REMOVE_FILE_COMMAND)) {
				fileRemove(tree, in.next(), logger);
			} else if (next.equals(Constants.CLIENT_PRINT_COMMAND)) {
				next = in.next().toUpperCase();
				if (next.equals(Constants.CLIENT_PRINT_STAT_COMMAND)) {
					System.out.println();
					System.out.println("Number of strings in tree: " + tree.size());
					System.out.println("Number of nodes in tree: " + tree.numNodes());
					System.out.println("Memory usage in bytes: " + logger.treeByteSize(tree, false));
					System.out.println("Total number of traversals: " + logger.traversals());
					System.out.println("Total number of bitwise comparisons: " + logger.comparisons());
					System.out.println();
				} else if (next.equals(Constants.CLIENT_PRINT_TREE_COMMAND)) {
					System.out.println();
					System.out.println(tree);
					System.out.println();
				} else {
					System.out.println("Unknown operation: " + Constants.CLIENT_PRINT_COMMAND + " " + next);
				}
			} else {
				System.out.println("Unknown operation: " + next);
			}
			System.out.print("Input > ");
			next = in.next();
		}
	}

	public static void addToTreeStat (PatriciaTree _tree, String _word, Logger _logger)
	{
		String sanitized = _word.replaceAll("[^A-Za-z]", "");
		OperationLogger opLog = new OperationLogger();
		boolean ret = _tree.add(sanitized, opLog);
		System.out.println();
		System.out.println("Add word \"" + sanitized.toUpperCase() + "\" to tree " + ((ret) ? "succeeded" : "failed (word already exists in the tree)"));
		System.out.println("Number of strings in tree: " + _tree.size());
		System.out.println("Number of nodes in tree: " + _tree.numNodes());


		_logger.addFromOperationLogger(opLog);

		System.out.println("Size of the tree in bytes: " + _logger.treeByteSize(_tree, true));
		System.out.println("Traversals needed for add operation: " + opLog.traversals());
		System.out.println("Bitwise comparisons needed for add operation: " + opLog.comparisons());

		System.out.println();
	}

	public static void removeFromTreeStat (PatriciaTree _tree, String word, Logger _logger)
	{
		String sanitized = word.replaceAll("[^A-Za-z]", "");
		OperationLogger opLog = new OperationLogger();
		boolean ret = _tree.remove(sanitized, opLog);
		System.out.println();
		System.out.println("Remove word \"" + sanitized.toUpperCase() + "\" from tree " + ((ret) ? "succeeded" : "failed (word doesn't exist in the tree)"));
		System.out.println("Number of strings in tree: " + _tree.size());
		System.out.println("Number of nodes in tree: " + _tree.numNodes());

		_logger.addFromOperationLogger(opLog);

		System.out.println("Size of the tree in bytes: " + _logger.treeByteSize(_tree, true));
		System.out.println("Traversals needed for remove operation: " + opLog.traversals());
		System.out.println("Bitwise comparisons needed for remove operation: " + opLog.comparisons());

		System.out.println();
	}

	public static void fileIn (PatriciaTree _tree, String _filePath, Logger _logger) throws IOException
	{
		OperationLogger finalLogger = new OperationLogger();
		Scanner in = new Scanner(new File(_filePath));
		while (in.hasNext()) {
			OperationLogger tempLogger = new OperationLogger();
			_tree.add(in.next().replaceAll("[^A-Za-z]", ""), tempLogger);
			finalLogger.incComparisons(tempLogger.comparisons());
			finalLogger.incTraversals(tempLogger.traversals());
		}
		in.close();

		_logger.addFromOperationLogger(finalLogger);

		System.out.println();
		System.out.println("Added words from file \"" + _filePath + "\" to tree");
		System.out.println("Number of strings in tree: " + _tree.size());
		System.out.println("Number of nodes in tree: " + _tree.numNodes());

		System.out.println("Size of the tree in bytes: " + _logger.treeByteSize(_tree, true));
		System.out.println("Traversals needed for add operation: " + finalLogger.traversals());
		System.out.println("Bitwise comparisons needed for add operation: " + finalLogger.comparisons());

		System.out.println();
	}

	public static void fileRemove (PatriciaTree _tree, String _filePath, Logger _logger) throws IOException
	{
		OperationLogger finalLogger = new OperationLogger();
		Scanner in = new Scanner(new File(_filePath));
		while (in.hasNext()) {
			OperationLogger tempLogger = new OperationLogger();
			_tree.remove(in.next().replaceAll("[^A-Za-z]", ""), tempLogger);
			finalLogger.incComparisons(tempLogger.comparisons());
			finalLogger.incTraversals(tempLogger.traversals());
		}
		in.close();

		_logger.addFromOperationLogger(finalLogger);

		System.out.println();
		System.out.println("Removed words from file \"" + _filePath + "\" from tree");
		System.out.println("Number of strings in tree: " + _tree.size());
		System.out.println("Number of nodes in tree: " + _tree.numNodes());

		System.out.println("Size of the tree in bytes: " + _logger.treeByteSize(_tree, true));
		System.out.println("Traversals needed for remove operation: " + finalLogger.traversals());
		System.out.println("Bitwise comparisons needed for remove operation: " + finalLogger.comparisons());

		System.out.println();
	}
}
