package ch.bfh.bti7535.w2017.red.preprocessing;

import opennlp.tools.stemmer.PorterStemmer;

import java.util.stream.Stream;

/**
 * Stem all words with the OpenNLP Porter stemmer
 */
public class Stem {
    /**
     * Stem all words with the OpenNLP Porter stemmer
     * @param stream A stream of strings containing words
     * @return A stream of strings where every word was stemmed
     */
    public static Stream<String> stem(Stream<String> stream) {
        PorterStemmer stemmer = new PorterStemmer();
        return stream.map(stemmer::stem);
    }
}
