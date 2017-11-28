package ch.bfh.bti7535.w2017.red;


import ch.bfh.bti7535.w2017.red.algorithm.BaselineUnweighted;
import ch.bfh.bti7535.w2017.red.preprocessing.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Evaluate the Baseline and find out which preprocessor is good/bad.
 * LowerCase: Does not make a difference, input text is already lower case
 * StopWords: Does not make a difference because StopWords are not in the Positive/Negative list
 * Stemming: Reduces accuracy
 * Stemming of negative/positive words: Reduces accuracy
 */
public class Baseline {
    private List<List<String>> positiveWords;
    private List<List<String>> negativeWords;
    private BaselineUnweighted stemmedAlgorithm;
    private BaselineUnweighted algorithm;

    public static void main(String args[]) {
        Baseline baseline = new Baseline();
        baseline.loadAndTokenize();

        System.out.println("lowercase, stopwords, stem, stem words");
        for (boolean stemWords : new Boolean[]{true, false}) {
            for (boolean stem : new Boolean[]{true, false}) {
                for (boolean lowerCase : new Boolean[]{true, false}) {
                    for (boolean stopWords : new Boolean[]{true, false}) {
                        System.out.printf("%b %b %b %b: %f\n", lowerCase, stopWords, stem, stemWords,
                                baseline.evaluate(lowerCase, stopWords, stem, stemWords));
                    }
                }
            }
        }
    }

    public void loadAndTokenize() {
        ArrayList<String> positiveReviews = LoadFiles.getPositiveReviews();
        ArrayList<String> negativeReviews = LoadFiles.getNegativeReviews();

        this.positiveWords = positiveReviews
                .stream()
                .map(Tokenize::tokenize)
                .collect(Collectors.toList());

        this.negativeWords = negativeReviews
                .stream()
                .map(Tokenize::tokenize)
                .collect(Collectors.toList());

        this.stemmedAlgorithm = new BaselineUnweighted(true);
        this.algorithm = new BaselineUnweighted(false);
    }

    public double evaluate(boolean lowerCase, boolean stopWords, boolean stem, boolean stemWords) {
        BaselineUnweighted algorithm;
        if (stemWords) {
            algorithm = this.stemmedAlgorithm;
        } else {
            algorithm = this.algorithm;
        }

        Stream<Stream<String>> positiveStream = positiveWords.stream().map(Collection::stream);
        Stream<Stream<String>> negativeStream = negativeWords.stream().map(Collection::stream);
        if (lowerCase) {
            positiveStream = positiveStream.map(LowerCase::lowerCase);
            negativeStream = negativeStream.map(LowerCase::lowerCase);
        }
        if (stopWords) {
            positiveStream = positiveStream.map(Stopwords::removeStopwords);
            negativeStream = negativeStream.map(Stopwords::removeStopwords);
        }
        if (stem) {
            positiveStream = positiveStream.map(Stem::stem);
            negativeStream = negativeStream.map(Stem::stem);
        }

        List<Sentiment> positiveSentiments = positiveStream
                .map(algorithm::analyze)
                .collect(Collectors.toList());

        List<Sentiment> negativeSentiments = negativeStream
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
        return (truePositive + trueNegative) / (double) total * 100.0;
    }
}
