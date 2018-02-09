package com.princeton.algorithms2.week4;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class BoggleSolver {

	private TwoSixWayTrie<String> dict;
	private Set<String> dictSet;
	private Set<String> cachedAnswer;
	private static int Q = 'Q'-'A';
	private static int U = 'U'-'A';


	// Initializes the data structure using the given array of strings as the dictionary.
	// (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
	public BoggleSolver(String[] dictionary) {
		if (dictionary == null) {
			throw new IllegalArgumentException();
		}

		dict = new TwoSixWayTrie<>();
		dictSet = new HashSet<>();
		cachedAnswer = new TreeSet<>();

		for (String word : dictionary) {
			this.dict.put(word, word);
			this.dictSet.add(word);
		}
	}

	// Returns the set of all valid words in the given Boggle board, as an Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board) {
		if (board == null) {
			throw new IllegalArgumentException();
		}

		cachedAnswer.clear();

		solveBoard(board);

		return cachedAnswer;
	}

	private void solveBoard(BoggleBoard board) {
		int row = board.rows();
		int col = board.cols();
		Node root = this.dict.root;
		char[][] boardM = new char[row][col];
		String str = "";
		for (int i=0; i<row; i++) {
			for (int j=0; j<col; j++) {
				boardM[i][j] = board.getLetter(i, j);
			}
		}

		Set<String> sol = new HashSet<>();
		for (int i=0; i<row; i++) {
			for (int j=0; j<col; j++) {
				boolean[] visited = new boolean[row*col];
				dfs(boardM, i, j, visited, str, root, sol);
			}
		}

		// prune the answer
		for (String word : sol) {
			if (word.length() >= 3) {
				cachedAnswer.add(word);
			}
		}
	}

	private void dfs(char[][] board, int i, int j, boolean[] visited, String str, Node x, Set<String> sol) {
		// only add solution if solution does not exist yet
		int iw = board.length;
		int jw=board[0].length;
		visited[i*jw+j] = true;
		int c = board[i][j]-'A';
		// if there is NOT a child for this char, means not prefix
		if (x.next[c] == null) {
			visited[i*jw+j] = false;
			return;
		}
		// special case Qu
		if (c==Q && (x.next[c].next[U] ==null)) {
			visited[i*jw+j] = false;
			return;
		}

		//check for potential prefix, special case first
		if (c==Q && x.next[c].next[U].val != null) {
			String dictWord=(String)x.next[c].val + 'u';
			sol.add(dictWord);
		}
		// it is a prefix, it might be a solution
		else if (x.next[c].val != null) {
			String dictWord=(String)x.next[c].val;
			sol.add(dictWord);
		}

		// update node to the next one
		Node nextNode = x.next[c];
		// update string
		str = str+c;
		if(c==Q) {
			nextNode = nextNode.next[U];
			str = str + 'U';
		}

		// right
		if (j+1<jw && !visited[i*jw+(j+1)]) {
			dfs(board, i, j+1, visited, str, nextNode, sol);
		}
		// down
		if (i+1<iw && !visited[(i+1)*jw+j]) {
			dfs(board, i+1, j, visited, str, nextNode, sol);
		}
		// up
		if (i-1>=0 && !visited[(i-1)*jw+ j]) {
			dfs(board, i-1, j, visited, str, nextNode, sol);
		}
		// left
		if (j-1>=0 && !visited[i*jw+(j-1)]) {
			dfs(board, i, j-1, visited, str, nextNode, sol);
		}
		// up-right
		if ((j+1<jw) && (i-1>=0) && !visited[(i-1)*jw+(j+1)]) {
			dfs(board, i-1, j+1, visited, str, nextNode, sol);
		}
		// up-left
		if ((j-1>=0) && (i-1>=0) && !visited[(i-1)*jw+(j-1)]) {
			dfs(board, i-1, j-1, visited, str, nextNode, sol);
		}
		// down-right
		if ((j+1<jw) && (i+1<iw) && !visited[(i+1)*jw+(j+1)]) {
			dfs(board, i+1, j+1, visited, str, nextNode, sol);
		}
		// down-left
		if ((j-1>=0) && (i+1<iw) && !visited[(i+1)*jw+(j-1)]) {
			dfs(board, i+1, j-1, visited, str, nextNode, sol);
		}
		visited[i*jw+j] = false;
	}

	// Returns the score of the given word if it is in the dictionary, zero otherwise.
	// (You can assume the word contains only the uppercase letters A through Z.)
	public int scoreOf(String word) {
		if (word == null) {
			throw new IllegalArgumentException();
		}
		if (!dictSet.contains(word)) {
			return 0;
		}
		int wordLength = word.length();
		if (wordLength >= 8) {
			return 11;
		} else if (wordLength >= 7) {
			return 5;
		} else if (wordLength >= 6) {
			return 3;
		} else if (wordLength >= 5) {
			return 2;
		} else if (wordLength >= 3) {
			return 1;
		} else {
			return 0;
		}
	}

	// R-way trie node
	private static int R = 26;
    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

	private class TwoSixWayTrie<Value> {
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
			x.next[c-'A'] = put(x.next[c-'A'], key, val, d+1);
			return x;
		}
	}


	public static void main(String[] args) {
	    In in = new In(args[0]);
	    String[] dictionary = in.readAllStrings();
	    BoggleSolver solver = new BoggleSolver(dictionary);
	    BoggleBoard board = new BoggleBoard(args[1]);
	    int score = 0;
	    for (String word : solver.getAllValidWords(board)) {
	        StdOut.println(word);
	        score += solver.scoreOf(word);
	    }
	    StdOut.println("Score = " + score);
	}
}
