package ch.bfh.bti7535.w2017.red.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Tokenize {
    private static Stream<String> tokenize(Stream<String> lines) {
        List<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);
        lines.forEach(line -> {
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                words.add(m.group());
            }
        });
        return words.stream();
    }
}
