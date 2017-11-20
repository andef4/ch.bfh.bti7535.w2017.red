package ch.bfh.bti7535.w2017.red;


import ch.bfh.bti7535.w2017.red.preprocessing.LoadFiles;

import java.util.ArrayList;

public class Main {
    public static void main(String args[]) {
        ArrayList<String> positiveReviews = LoadFiles.getPositiveReviews();
        ArrayList<String> negativeReviews = LoadFiles.getNegativeReviews();
    }
}
