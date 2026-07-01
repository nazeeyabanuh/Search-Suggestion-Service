package com.trie;

import java.util.HashMap;
import java.util.Map;

public class t_node {

    Map<Character, t_node> children;
    boolean isEndOfWord;

    public t_node() {
        children = new HashMap<>();
        isEndOfWord = false;
    }

}