package ch.bfh.bti7535.w2017.red.algorithm;


import ch.bfh.bti7535.w2017.red.Sentiment;
import ch.bfh.bti7535.w2017.red.preprocessing.Stem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaselineUnweighted {

    private Set<String> positiveWords = null;
    private Set<String> negativeWords = null;

    public BaselineUnweighted(boolean stem) {
        try {
            List<String> positiveWordsList = Files.readAllLines(Paths.get("src/main/resources/positive-words.txt"));
            List<String> negativeWordsList = Files.readAllLines(Paths.get("src/main/resources/negative-words.txt"));
            if (stem) {
                positiveWords = Stem.stem(positiveWordsList.stream()).collect(Collectors.toSet());
                negativeWords = Stem.stem(negativeWordsList.stream()).collect(Collectors.toSet());
            } else {
                positiveWords = new HashSet<>(positiveWordsList);
                negativeWords = new HashSet<>(negativeWordsList);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read positive/negative words file");
        }
    }

    public Sentiment analyze(Stream<String> words) {
        List<String> wordList = words.collect(Collectors.toList());
        int value = 0;
        for (String word : wordList) {
            if (positiveWords.contains(word)) {
                value++;
            } else if (negativeWords.contains(word)) {
                value--;
            }
        }
        if (value >= 0) {
            return Sentiment.POSITIVE;
        } else {
            return Sentiment.NEGATIVE;
        }
    }
}
