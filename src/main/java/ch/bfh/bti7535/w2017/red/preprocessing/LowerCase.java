package ch.bfh.bti7535.w2017.red.preprocessing;

import java.util.stream.Stream;

/**
 * Convert all words to lower case
 */
public class LowerCase {
    public static Stream<String> lowerCase(Stream<String> stream) {
        return stream.map(String::toLowerCase);
    }
}
