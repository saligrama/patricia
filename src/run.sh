#!/usr/bin/env bash

# PROBLEM 11
# This is a simple run script for the PatriciaTree

echo "Compiling Constants.java"
javac Constants.java
echo "Compiling Utils.java"
javac Utils.java
echo "Compiling Logger.java"
javac Logger.java
echo "Compiling Node.java"
javac Node.java
echo "Compiling Patricia.java"
javac PatriciaTree.java
echo "Compiling Client.java"
javac Client.java
echo "Running client code"
java Client
