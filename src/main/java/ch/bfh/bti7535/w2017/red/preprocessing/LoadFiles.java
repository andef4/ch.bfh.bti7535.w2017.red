package ch.bfh.bti7535.w2017.red.preprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class LoadFiles {

    private static ArrayList<String> readFiles(String path) {
        ArrayList<String> texts = new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files == null) {
            throw new RuntimeException("Cannot list files in data directory");
        }
        for (File file : files) {
            try {
                String text = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                texts.add(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return texts;
    }

    public static ArrayList<String> getPositiveReviews() {
        return readFiles("data/txt_sentoken/pos/");
    }

    public static ArrayList<String> getNegativeReviews() {
        return readFiles("data/txt_sentoken/neg/");
    }
}
