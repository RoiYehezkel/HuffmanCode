package huffman;

import java.util.Comparator;

/**
 * Assignment 1
 * Submitted by: 
 * Student 1: Sofia Naer 	ID# 333815397
 * Student 2: Roi Yehezkel 	ID# 315331959
 */

// copied from the internet(geeksforgeeks)
class MyComparator implements Comparator<Node> {

	@Override
	public int compare(Node o1, Node o2) {
		return o1.freq - o2.freq;
	}

}