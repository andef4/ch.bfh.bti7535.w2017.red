package ch.bfh.bti7535.w2017.red.word2vecrnn.dl4jdataset;

import ch.bfh.bti7535.w2017.red.word2vecrnn.ourdataset.SentimentExampleIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class EvaluateOnOurDataset {
    public static final String DATA_PATH = "./data/txt_sentoken/";
    public static final String WORD_VECTORS_PATH = "./data/GoogleNews-vectors-negative300.bin.gz";

    public static void main(String[] args) throws IOException {
        int batchSize = 64;     //Number of examples in each minibatch
        int truncateReviewsToLength = 1000;  //Truncate reviews with length (# words) greater than this

        Nd4j.getMemoryManager().setAutoGcWindow(10000);

        File vectorFile = new File(WORD_VECTORS_PATH);
        if (!vectorFile.exists()) {
            System.out.println("Please download the GoogleNews vectors to ./data/GoogleNews-vectors-negative300.bin.gz");
            System.out.println("https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit?usp=sharing");
            return;
        }

        System.out.println("Loading WordVector model...");
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));

        File directory = Paths.get(DATA_PATH, "pos").toFile();
        List<File> positiveFiles = Arrays.asList(directory.listFiles());
        directory = Paths.get(DATA_PATH, "neg").toFile();
        List<File> negativeFiles = Arrays.asList(directory.listFiles());

        SentimentExampleIterator test = new SentimentExampleIterator(positiveFiles,
                negativeFiles, wordVectors, batchSize, truncateReviewsToLength);

        System.out.println("Loading network...");
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork("./src/main/resources/rnn-25k-epoch-3.obj");

        System.out.println("Starting evaluation...");
        Evaluation evaluation = net.evaluate(test);
        System.out.println(evaluation.stats());
    }
}
