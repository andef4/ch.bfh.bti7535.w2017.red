package ch.bfh.bti7535.w2017.red;


import ch.bfh.bti7535.w2017.red.preprocessing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String args[]) {
        ArrayList<String> positiveReviews = LoadFiles.getPositiveReviews();
        ArrayList<String> negativeReviews = LoadFiles.getNegativeReviews();

        List<Stream<String>> positiveWords = positiveReviews
                .stream()
                .map(Tokenize::tokenize)
                .collect(Collectors.toList());

        List<Stream<String>> negativeWords = negativeReviews
                .stream()
                .map(Tokenize::tokenize)
                .collect(Collectors.toList());

        Baseline algorithm = new Baseline();

        List<Sentiment> positiveSentiments = positiveWords.stream()
                .map(LowerCase::lowerCase)
                .map(Stopwords::removeStopwords)
                .map(Stem::stem)
                .map(algorithm::analyze)
                .collect(Collectors.toList());
        List<Sentiment> negativeSentiments = negativeWords.stream()
                .map(LowerCase::lowerCase)
                .map(Stopwords::removeStopwords)
                .map(Stem::stem)
                .map(algorithm::analyze)
                .collect(Collectors.toList());

        long falsePositive = positiveSentiments.stream()
                .filter(sentiment -> sentiment == Sentiment.NEGATIVE)
                .count();
        long falseNegative = negativeSentiments.stream()
                .filter(sentiment -> sentiment == Sentiment.POSITIVE)
                .count();

        System.out.printf("True Positive: %d\n", positiveSentiments.size() - falsePositive);
        System.out.printf("False Positive: %d\n", falsePositive);
        System.out.printf("True Negative: %d\n", negativeSentiments.size() - falseNegative);
        System.out.printf("False Negative: %d\n", falseNegative);
    }
}
