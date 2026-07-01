package com.loader;

import com.model.Word;
import com.model.WordRepository;
import com.trie.Trie;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DictionaryLoader {

    private static final int BATCH_SIZE = 1000;

    private final WordRepository wordRepository;
    private final Trie trie;

    public DictionaryLoader(WordRepository wordRepository, Trie trie) {
        this.wordRepository = wordRepository;
        this.trie = trie;
    }

    @PostConstruct
    public void init() {
        if (wordRepository.count() == 0) {
            loadFromFileIntoDatabase();
        } else {
            System.out.println("Database already populated, skipping file load.");
        }
        loadFromDatabaseIntoTrie();
    }

    private void loadFromFileIntoDatabase() {
        long start = System.currentTimeMillis();
        int totalLoaded = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("words.txt").getInputStream(),
                        StandardCharsets.UTF_8))) {

            List<Word> batch = new ArrayList<>(BATCH_SIZE);
            String line;

            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    batch.add(new Word(word));
                }

                if (batch.size() == BATCH_SIZE) {
                    wordRepository.saveAll(batch);
                    totalLoaded += batch.size();
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                wordRepository.saveAll(batch);
                totalLoaded += batch.size();
            }

            long seconds = (System.currentTimeMillis() - start) / 1000;
            System.out.println("Loaded " + totalLoaded + " words into database in " + seconds + "s");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load words.txt into database", e);
        }
    }

    private void loadFromDatabaseIntoTrie() {
        long start = System.currentTimeMillis();
        List<Word> words = wordRepository.findAll();

        for (Word word : words) {
            trie.insert(word.getText());
        }

        long seconds = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Loaded " + words.size() + " words from database into Trie in " + seconds + "s");
    }
}