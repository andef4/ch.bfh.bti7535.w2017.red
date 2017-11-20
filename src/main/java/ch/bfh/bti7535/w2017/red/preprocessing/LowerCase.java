package ch.bfh.bti7535.w2017.red.preprocessing;

import opennlp.tools.stemmer.PorterStemmer;

import java.util.stream.Stream;

public class LowerCase {
    public static Stream<String> lowerCase(Stream<String> stream) {
        return stream.map(String::toLowerCase);
    }
}
