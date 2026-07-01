package com.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trie {

    private final t_node root;

    public Trie() {
        root = new t_node();
    }

    public void insert(String word) {
        t_node current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                current.children.put(ch, new t_node());
            }
            current = current.children.get(ch);
        }
        current.isEndOfWord = true;
    }

    public boolean search(String word) {
        t_node current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return false;
            }
            current = current.children.get(ch);
        }
        return current.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        t_node current = root;
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return false;
            }
            current = current.children.get(ch);
        }
        return true;
    }

    private t_node getNode(String prefix) {
        t_node current = root;
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return null;
            }
            current = current.children.get(ch);
        }
        return current;
    }

    private void collectWords(t_node node,
                              String currentWord,
                              List<String> results,
                              int limit) {

        if (results.size() >= limit) {
            return;
        }

        if (node.isEndOfWord) {
            results.add(currentWord);
        }

        for (Map.Entry<Character, t_node> entry : node.children.entrySet()) {

            if (results.size() >= limit) {
                return;
            }

            collectWords(
                    entry.getValue(),
                    currentWord + entry.getKey(),
                    results,
                    limit
            );
        }
    }

    public List<String> autocomplete(String prefix) {
        return autocomplete(prefix, 10);
    }

    public List<String> autocomplete(String prefix, int limit) {

        List<String> results = new ArrayList<>();

        t_node node = getNode(prefix);

        if (node == null) {
            return results;
        }

        collectWords(node, prefix, results, limit);

        return results;
    }

    private void collectAllWords(t_node node, String currentWord, List<String> results) {
        if (node.isEndOfWord) {
            results.add(currentWord);
        }
        for (Map.Entry<Character, t_node> entry : node.children.entrySet()) {
            collectAllWords(entry.getValue(), currentWord + entry.getKey(), results);
        }
    }

    public List<String> fuzzySearch(String query, int maxDistance, int limit) {

        List<String> allWords = new ArrayList<>();
        collectAllWords(root, "", allWords);

        List<String> matches = new ArrayList<>();

        for (String word : allWords) {
            if (Math.abs(word.length() - query.length()) > maxDistance) {
                continue;
            }
            if (com.fuuzy.ldistance.compute(query, word) <= maxDistance) {
                matches.add(word);
                if (matches.size() >= limit) {
                    break;
                }
            }
        }

        return matches;
    }
}