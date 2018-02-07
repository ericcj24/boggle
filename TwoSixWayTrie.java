package com.princeton.algorithms2.week4;


public class TwoSixWayTrie<Value> {

	private static final int R = 256;

	// R-way trie node
    static class Node {
        Object val;
        Node[] next = new Node[R];
    }

	Node root;
	public TwoSixWayTrie() {
		root = new Node();
	}

	public void put(String key, Value val) {
		root = put(root, key, val, 0);
	}

	private Node put(Node x, String key, Value val, int d) {
		if (x == null) {
			x = new Node();
		}
		if (d==key.length()) {
			x.val = val;
			return x;
		}
		char c = key.charAt(d);
		x.next[c] = put(x.next[c], key, val, d+1);
		return x;
	}

}

