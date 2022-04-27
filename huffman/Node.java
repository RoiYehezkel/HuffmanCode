package huffman;

/**
 * Assignment 1
 * Submitted by: 
 * Student 1: Sofia Naer 	ID# 333815397
 * Student 2: Roi Yehezkel 	ID# 315331959
 */
public class Node {
	int freq;
	Byte ch;
	Byte ch2;
	Node left;
	Node right;

	public Node(byte ch, int freq) // constructor for leaf of one character
	{
		this.freq = freq;
		this.ch = ch;
		this.ch2 = null;
		left = null;
		right = null;
	}

	public Node(byte ch, byte ch2, int freq) // constructor for leaf of two character
	{
		this.freq = freq;
		this.ch = ch;
		this.ch2 = ch2;
		left = null;
		right = null;
	}

	public Node(int freq, Node left, Node right) // constructor for inside node
	{
		this.freq = freq;
		this.ch = null;
		this.ch2 = null;
		this.left = left;
		this.right = right;
	}
}
