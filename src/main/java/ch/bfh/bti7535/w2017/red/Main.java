package ch.bfh.bti7535.w2017.red;


import ch.bfh.bti7535.w2017.red.preprocessing.LoadFiles;
import ch.bfh.bti7535.w2017.red.preprocessing.LowerCase;
import ch.bfh.bti7535.w2017.red.preprocessing.Stem;
import ch.bfh.bti7535.w2017.red.preprocessing.Tokenize;

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
                .map(Stem::stem)
                .map(LowerCase::lowerCase)
                .collect(Collectors.toList());
        negativeWords.stream()
                .map(Stem::stem)
                .map(LowerCase::lowerCase)
                .collect(Collectors.toList());
    }
}
