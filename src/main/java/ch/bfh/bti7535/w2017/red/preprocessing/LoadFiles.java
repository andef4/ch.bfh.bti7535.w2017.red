package ch.bfh.bti7535.w2017.red.preprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Load review files from disk by listing files
 * in the data/txt_sentoken directory.
 */
public class LoadFiles {

    /**
     * Load reviews from disk
     * @param path The path from where the files should be read from
     * @return A list of strings, every string contains the content of a single file
     */
    private static ArrayList<String> readFiles(String path) {
        ArrayList<String> texts = new ArrayList<>();

        // list files in the given directory
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files == null) {
            throw new RuntimeException("Cannot list files in data directory");
        }

        // iterate over files and read their content
        for (File file : files) {
            try {
                // read files as UTF-8
                String text = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                texts.add(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return texts;
    }

    /**
     * Load all positive reviews from disk
     * @return A list of strings, every string contains the content of a single file
     */
    public static ArrayList<String> getPositiveReviews() {
        return readFiles("data/txt_sentoken/pos/");
    }

    /**
     * Load all negative reviews from disk
     * @return A list of strings, every string contains the content of a single file
     */
    public static ArrayList<String> getNegativeReviews() {
        return readFiles("data/txt_sentoken/neg/");
    }
}
