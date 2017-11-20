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

        positiveWords.stream()
                .map(LowerCase::lowerCase)
                .map(Stopwords::removeStopwords)
                .map(Stem::stem)
                .collect(Collectors.toList());
        negativeWords.stream()
                .map(LowerCase::lowerCase)
                .map(Stopwords::removeStopwords)
                .map(Stem::stem)
                .collect(Collectors.toList());
    }
}
