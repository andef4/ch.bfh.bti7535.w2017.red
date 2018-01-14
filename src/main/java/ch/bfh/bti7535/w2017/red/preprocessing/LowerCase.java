package ch.bfh.bti7535.w2017.red.preprocessing;

import java.util.stream.Stream;

/**
 * Convert all words to lower case
 */
public class LowerCase {
    /**
     * Convert all words to lower case
     * @param stream A stream of strings containing words
     * @return A stream of strings where every word converted to lower case
     */
    public static Stream<String> lowerCase(Stream<String> stream) {
        return stream.map(String::toLowerCase);
    }
}
