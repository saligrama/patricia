/*
 * Copyright (c) 2017-2018 Aditya Saligrama.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package io.github.saligrama.util;

/**
 * PatriciaTree is a bit-alphabet based, space-optimized trie for string storage.
 *
 * Patricia Trees (also known as Crit-Bit Trees and Compressed Binary Trees) are
 * extremely space-efficient compared to standard tries due to their compression
 * of nodes with only one child. Therefore, no unnecessary nodes and edges are
 * stored, cutting down on both storage and in steps needed to traverse the trie.
 *
 * This PatriciaTree operates on a bit alphabet and can store any type of ASCII
 * text. It operates similarly to a Radix Tree, with radix r=2.
 *
 * @author Aditya Saligrama
 */
public class PatriciaTree
{
	private int size;
	private Node root;

	/**
	 * Construct a new PatriciaTree.
	 */
	public PatriciaTree() {
		root = new Node(0, null);
		size = 0;
	}

	/**
	 * @return size of the tree.
	 */
	public int size() {
		return size;
	}

	/**
	 * Check if a string exists in the tree.
	 *
	 * @param _toSearch string to search for within the tree
	 * @return true if _toAdd exists in the tree, false if it does not
	 */
	public boolean contains(String _toSearch) {
		return root.findChild(Utils.strToBitArr(_toSearch));
	}

	/**
	 * Add a string to the tree.
	 *
	 * @param _toAdd string to add to the tree
	 * @return true if the tree was modified (i.e. _toAdd did not already exist in the tree), false otherwise
	 */
	public boolean add(String _toAdd) {
		boolean ret = root.addChild(Utils.strToBitArr(_toAdd));
		if (ret)
			size++;
		return ret;
	}

	/**
	 * Remove a string from the tree.
	 *
	 * @param _toRemove string to remove from the tree
	 * @return true if the tree was modified (i.e. _toRemove was in the tree), false otherwise
	 */
	public boolean remove(String _toRemove) {
		boolean ret = root.removeChild(Utils.strToBitArr(_toRemove));
		if (ret)
			size--;
		return ret;
	}

	/**
	 * @return string representation of the tree
	 */
	@Override
	public String toString() {
		String ret = root.toString();
		return "[" + ret.substring(0, ret.length() - 2) + "]";
	}
}
