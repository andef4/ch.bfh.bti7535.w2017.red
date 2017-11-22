package ch.bfh.bti7535.w2017.red;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.util.Random;

public class NaiveBayesAlgorithm {


    public static void main(String[] args) throws Exception {
        // generate dataset from files
        TextDirectoryLoader loader = new TextDirectoryLoader();
        loader.setDirectory(new File("data/txt_sentoken/"));
        Instances dataSet = loader.getDataSet();

        // convert loaded text files into a word vector
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(dataSet);
        dataSet = Filter.useFilter(dataSet, filter);

        NaiveBayes naiveBayes = new NaiveBayes();

        Evaluation eval = new Evaluation(dataSet);
        eval.crossValidateModel(naiveBayes, dataSet, 10, new Random(1));

        System.out.println(eval.toSummaryString());
    }
}
