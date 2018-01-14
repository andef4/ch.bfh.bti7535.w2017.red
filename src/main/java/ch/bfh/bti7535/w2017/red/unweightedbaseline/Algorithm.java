package ch.bfh.bti7535.w2017.red.unweightedbaseline;


import ch.bfh.bti7535.w2017.red.preprocessing.Stem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple algorithm which checks if a word is in the positive or
 * negative word list (found in src/main/resources). If it is,
 * the result value (int) will be increased/decreased by 1.
 * If the result is positive or zero, the sentiment is positive,
 * if negative the sentiment is negative.
 */
public class Algorithm {

    private Set<String> positiveWords = null;
    private Set<String> negativeWords = null;

    /**
     * Constructor which loads the two word lists from disk
     * @param stem If the words in the word list should be stemmed
     */
    public Algorithm(boolean stem) {
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

    /**
     * Run the algorithm on the words
     * @param words input words
     * @return if the sentiment of the words is positive or negative
     */
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
