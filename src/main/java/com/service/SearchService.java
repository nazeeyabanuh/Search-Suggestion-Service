package com.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.trie.Trie;

@Service
public class SearchService {

    private final Trie trie;

    public SearchService(Trie trie) {
        this.trie = trie;
    }

    public List<String> searchSuggestions(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        String normalized = query.trim().toLowerCase();

        List<String> exactMatches = trie.autocomplete(normalized, 10);

        if (!exactMatches.isEmpty()) {
            return exactMatches;
        }

        // No prefix matches found — fall back to fuzzy (typo-tolerant) search
        return trie.fuzzySearch(normalized, 2, 10);
    }
}