package ch.bfh.bti7535.w2017.red;


import ch.bfh.bti7535.w2017.red.algorithm.BaselineUnweighted;
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

        BaselineUnweighted algorithm = new BaselineUnweighted();

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
        long truePositive = positiveSentiments.size() - falsePositive;
        long falseNegative = negativeSentiments.stream()
                .filter(sentiment -> sentiment == Sentiment.POSITIVE)
                .count();
        long trueNegative = negativeSentiments.size() - falseNegative;

        long total = truePositive + falsePositive + trueNegative + falseNegative;
        double accuracy = (truePositive + trueNegative) / (double)total * 100.0;

        System.out.printf("True Positive: %d\n", truePositive);
        System.out.printf("False Positive: %d\n", falsePositive);
        System.out.printf("True Negative: %d\n", trueNegative);
        System.out.printf("False Negative: %d\n", falseNegative);
        System.out.println();
        System.out.printf("Accuracy: %.2f %%\n", accuracy);

    }
}
