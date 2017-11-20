package ch.bfh.bti7535.w2017.red;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Baseline {

    private Set<String> positiveWords = null;
    private Set<String> negativeWords = null;

    public Baseline() {
        try {
            positiveWords = new HashSet<>(
                    Files.readAllLines(Paths.get("src/main/resources/positive-words.txt")));
            negativeWords = new HashSet<>(
                    Files.readAllLines(Paths.get("src/main/resources/negative-words.txt")));
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
