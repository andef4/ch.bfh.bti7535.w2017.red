package ch.bfh.bti7535.w2017.red.preprocessing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Remove stopwords from a list of words.
 * The stopwords are loaded from src/main/resources/stopwords.txt
 */
public class Stopwords {
    public static Stream<String> removeStopwords(Stream<String> stream) {
        List<String> stopwordList;
        try {
            stopwordList = Files.readAllLines(Paths.get("src/main/resources/stopwords.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read stopwords file");
        }

        HashSet<String> stopwords = new HashSet<>(stopwordList);
        List<String> wordsWithoutStopwords = new ArrayList<>();
        List<String> words = stream.collect(Collectors.toList());

        for (String word : words) {
            if (!stopwords.contains(word) && word.length() > 1) {
                wordsWithoutStopwords.add(word);
            }
        }
        return wordsWithoutStopwords.stream();
    }
}
