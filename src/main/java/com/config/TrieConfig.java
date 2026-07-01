package com.config;

import com.trie.Trie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrieConfig {

    @Bean
    public Trie trie() {
        return new Trie();
    }
}