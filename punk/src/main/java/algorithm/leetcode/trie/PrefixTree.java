package algorithm.leetcode.trie;

public class PrefixTree {

    TrieNode root;

    public PrefixTree() {
        root = new TrieNode();
    }

    public void insert(String word) {
        char[] chars = word.toCharArray();
        TrieNode node = root;

        for (char c : chars) {
            if (!node.contains(c)) {
                node.link(c, new TrieNode());
            }

            node = node.next(c);
        }
        node.isEnd = true;
    }

    private TrieNode searchPrefix(String word) {
        char[] chars = word.toCharArray();
        TrieNode node = root;

        for (char c : chars) {
            if (node.contains(c)) {
                node = node.next(c);
            } else {
                return null;
            }
        }

        return node;
    }

    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        TrieNode node = searchPrefix(prefix);
        return node != null;
    }

    static class TrieNode {
        TrieNode[] links = new TrieNode[26];
        boolean isEnd;

        public boolean contains(char c) {
            return links[c - 'a'] != null;
        }

        public TrieNode next(char c) {
            return links[c - 'a'];
        }

        public void link(char c, TrieNode node) {
            links[c - 'a'] = node;
        }
    }
}
