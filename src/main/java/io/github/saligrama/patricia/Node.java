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

package io.github.saligrama.patricia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Node is an individual piece of a PatriciaTree. It has two children, an index of separation
 * between its left and right children, and up to two bit arrays that terminate at this node (i.e.
 * its length is given by forkIndex+1).
 *
 * <p>Note that a Node could theoretically be its own independent PatriciaTree. However, to optimize
 * the tree, it is necessary to backtrack to parent nodes on removal of a string in order to make
 * sure no redundant nodes are being stored.
 *
 * @author Aditya Saligrama
 * @version 1.0
 */
class Node {
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

  /** Construct a new Node. */
  protected Node(int _forkIndex, Node _parent) {
    forkIndex = _forkIndex;
    terminates = new ArrayList<int[]>();
    children = new Node[Constants.NODE_NUM_CHILDREN];
    parent = _parent;

    // add nulls for padding and to avoid IndexOutOfBoundsExceptions
    for (int i = 0; i < Constants.NODE_NUM_CHILDREN; i++) terminates.add(null);
  }

  /** @return string representation of the node and all child nodes */
  @Override
  public String toString() {
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

  /**
   * Search for a bit array in this node and recursively do so in child nodes.
   *
   * @param _toSearch bit array to search for
   * @return true if the bit array was found, false otherwise
   */
  protected boolean findChild(int[] _toSearch) {
    // locateRemoveChild finds a proper child to compare with, so we use it here
    Node toCompare = locateRemoveChild(_toSearch);
    int[] comp = firstNotNull(toCompare.terminates);
    if (comp == null || comp.length != _toSearch.length) {
      return false;
    } else {
      return Arrays.equals(comp, _toSearch);
    }
    return true;
  }

  /**
   * Add a bit array to the tree.
   *
   * @param _toAdd bit array to add to the tree
   * @return true if the tree was modified (i.e. _toAdd did not already exist in the tree), false
   *     otherwise
   */
  protected boolean addChild(int[] _toAdd) {
    Node toCompare = locateAddChild(_toAdd);

    // deal with inserting into the root node
    if (toCompare.forkIndex == 0 && notNullSize(toCompare.children) < 2) {
      toCompare.children[_toAdd[toCompare.forkIndex]] = new Node(_toAdd.length - 1, toCompare);
      toCompare.children[_toAdd[toCompare.forkIndex]].terminates.set(
          _toAdd[_toAdd.length - 1], _toAdd);
      return true;
    }

    // find first index of difference between _toAdd and comparison child
    int j = 0;
    int[] comp = firstNotNull(toCompare.terminates);
    int minIndex = Math.min(comp.length, _toAdd.length) - 1;
    boolean differenceFound = false;

    for (; j <= minIndex; j++) {
      if (_toAdd[j] != comp[j]) {
        differenceFound = true;
        break;
      }
    }

    // fix indexing issue with for loop
    if (j > minIndex) j--;

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
        toCompare.children[_toAdd[toCompare.forkIndex]] = new Node(_toAdd.length - 1, toCompare);
        toCompare.children[_toAdd[toCompare.forkIndex]].terminates.set(
            _toAdd[_toAdd.length - 1], _toAdd);
        return true;
      }
    }

    // walk back to first parent node (toModify) encountered with index less than j
    // unlink toModify's comp[j] child, substitute with splice which contains
    // the unlinked child and another node containing _toAdd as children
    Node toModify = toCompare.walkBackToIndex(j);
    Node splice = new Node(j, toModify);
    splice.children[comp[j]] = toModify.children[comp[toModify.forkIndex]];
    splice.children[comp[j]].parent = splice;
    toModify.children[comp[toModify.forkIndex]] = splice;

    if (j == minIndex) {
      // if _toAdd is a substring of comp, then _toAdd becomes a terminal string of splice
      splice.terminates.set(_toAdd[j], _toAdd);
    } else {
      // otherwise we create a new child of splice that serves this purpose
      splice.children[_toAdd[j]] = new Node(_toAdd.length - 1, splice);
      splice.children[_toAdd[j]].terminates.set(_toAdd[_toAdd.length - 1], _toAdd);
    }

    return true;
  }

  /**
   * Remove a bit array from the tree.
   *
   * @param _toRemove bit array to remove from the tree
   * @return true if the tree was modified (i.e. _toRemove was in the tree), false otherwise
   */
  protected boolean removeChild(int[] _toRemove) {
    // find comparison node
    Node located = locateRemoveChild(_toRemove);

    // if relevant child does not match _toRemove, return false as the tree did not change
    if (_toRemove.length == located.forkIndex + 1) {
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
        Node pointer = located.parent;
        located.parent.children[_toRemove[located.parent.forkIndex]] = null;
        if (notNullSize(pointer.children) == 1 && pointer.forkIndex != 0) {
          // if located's parent has only one child, then we can merge the child into itself,
          // as long as pointer isn't the root node
          Node grandParent = pointer.parent;
          int childIndex = _toRemove[grandParent.forkIndex];
          int grandChildIndex = Math.abs(_toRemove[pointer.forkIndex] - 1);
          grandParent.children[childIndex] =
              grandParent.children[childIndex].children[grandChildIndex];
          grandParent.children[childIndex].parent = grandParent;
        }
      } else if (notNullSize(located.children) == 1) {
        // if located has only one child, then we can merge the child into itself
        Node pointer = located.parent;
        pointer.children[_toRemove[pointer.forkIndex]] = firstNotNull(located.children);
        pointer.children[_toRemove[pointer.forkIndex]].parent = pointer;
      }
    }

    return true;
  }

  /**
   * Calculate the number of elements in _list that are not null. Used to determine various
   * conclusions about the terminates list; i.e. it is a factor in determining whether a node is
   * orphaned.
   *
   * @param _terminates a terminates list
   * @return number of elements that are not null in _list
   */
  private int notNullSize(List<int[]> _terminates) {
    int ret = 0;
    for (int[] i : _terminates) {
      if (i != null) ret++;
    }

    return ret;
  }

  /**
   * Calculate the number of elements in _children that are not null. Used to determine various
   * conclusions about the terminates list; i.e. it is a factor in determining whether a node is
   * orphaned.
   *
   * @param _children an array of child nodes
   * @return number of elements that are not null in _children
   */
  private int notNullSize(Node[] _children) {
    int ret = 0;
    for (Node n : _children) {
      if (n != null) ret++;
    }

    return ret;
  }

  /**
   * Find the first non-null element in the _terminates list.
   *
   * @param _terminates a terminates list
   * @return first non-null bit array in _terminates
   */
  private int[] firstNotNull(List<int[]> _terminates) {
    for (int[] i : _terminates) {
      if (i != null) return i;
    }

    return null;
  }

  /**
   * Find the first non-null element in the _children array.
   *
   * @param _children a list of child nodes
   * @return first non-null bit array in _children
   */
  private Node firstNotNull(Node[] _children) {
    for (Node n : _children) {
      if (n != null) return n;
    }

    return null;
  }

  /**
   * Traverse back up the tree to a node with forkIndex less than _index. Used as part of the
   * addChild() method to clean up after inserting a bit array.
   *
   * @param _index index to compare to forkIndex of parent nodes
   * @return a Node representing the first location such that its forkIndex is less than _index
   */
  private Node walkBackToIndex(int _index) {
    Node pointer = this;
    // stop when a node with forkIndex less than _index, or the root node is reached
    while (pointer.forkIndex >= _index && pointer.forkIndex > 0) {
      pointer = pointer.parent;
    }

    return pointer;
  }

  /**
   * Locate empty or terminal node for comparison and/or insertion of _toAdd.
   *
   * @param _toAdd bit array to be used to search for a location to add it to the tree
   * @return a Node representing such a location
   */
  private Node locateAddChild(int[] _toAdd) {
    Node pointer = this;
    while (pointer.forkIndex < _toAdd.length
        && pointer.children[_toAdd[pointer.forkIndex]] != null) {
      // find index to first stop at
      pointer = pointer.children[_toAdd[pointer.forkIndex]];
    }

    // return the first node we can find with a terminal string contained within if we can proceed
    if (notNullSize(pointer.children) > 0 && pointer.forkIndex != 0) {
      while (notNullSize(pointer.terminates) == 0) {
        pointer = firstNotNull(pointer.children);
      }
    }

    // if we cannot continue (or we stop at the root node),
    // return this node (useful for inserting into an empty root node)
    return pointer;
  }

  /**
   * Locate empty or terminal node for comparison and/or removal of _toRemove. Also used to search
   * for a bit array to check if the tree contains it in the findChild() method
   *
   * @param _toRemove bit array to be used to search for a location to remove it from the tree
   * @return a Node representing such a location
   */
  private Node locateRemoveChild(int[] _toRemove) {
    Node pointer = this;
    while (pointer.forkIndex < _toRemove.length
        && pointer.children[_toRemove[pointer.forkIndex]] != null) {
      // find index to first stop at
      pointer = pointer.children[_toRemove[pointer.forkIndex]];
    }

    if (pointer.forkIndex >= _toRemove.length) {
      pointer = pointer.parent;
    }

    return pointer;
  }
}
