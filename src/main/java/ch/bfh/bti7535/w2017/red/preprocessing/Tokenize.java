package ch.bfh.bti7535.w2017.red.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Tokenize {
    public static Stream<String> tokenize(String text) {
        List<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            words.add(m.group());
        }
        return words.stream();
    }
}
