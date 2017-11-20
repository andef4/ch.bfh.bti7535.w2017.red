package ch.bfh.bti7535.w2017.red.preprocessing;

import opennlp.tools.stemmer.PorterStemmer;

import java.util.stream.Stream;

public class Stem {
    private static Stream<String> stem(Stream<String> stream) {
        PorterStemmer stemmer = new PorterStemmer();
        return stream.map(stemmer::stem);
    }
}
