package ch.bfh.bti7535.w2017.red.word2vecrnn;

import ch.bfh.bti7535.utilities.RandomSplitList;
import ch.bfh.bti7535.utilities.Tuple;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**Example: Given a movie review (raw text), classify that movie review as either positive or negative based on the words it contains.
 * This is done by combining Word2Vec vectors and a recurrent neural network model. Each word in a review is vectorized
 * (using the Word2Vec model) and fed into a recurrent neural network.
 * Training data is the "Large Movie Review Dataset" from http://ai.stanford.edu/~amaas/data/sentiment/
 * This data set contains 25,000 training reviews + 25,000 testing reviews
 *
 * Process:
 * 1. Automatic on first run of example: Download data (movie reviews) + extract
 * 2. Load existing Word2Vec model (for example: Google News word vectors. You will have to download this MANUALLY)
 * 3. Load each each review. Convert words to vectors + reviews to sequences of vectors
 * 4. Train network
 *
 * With the current configuration, gives approx. 83% accuracy after 1 epoch. Better performance may be possible with
 * additional tuning.
 *
 * NOTE / INSTRUCTIONS:
 * You will have to download the Google News word vector model manually. ~1.5GB
 * The Google News vector model available here: https://code.google.com/p/word2vec/
 * Download the GoogleNews-vectors-negative300.bin.gz file
 * Then: set the WORD_VECTORS_PATH field to point to this location.
 *
 * @author Alex Black
 */
public class Word2VecSentimentRNN {
    /** Location to save and extract the training/testing data */
    public static final String DATA_PATH = "./data/txt_sentoken/";
    /** Location (local file system) for the Google News vectors. Set this manually. */
    public static final String WORD_VECTORS_PATH = "./data/GoogleNews-vectors-negative300.bin.gz";


    public static void main(String[] args) throws Exception {
        int nEpochs = 100;
        int batchSize = 64;     //Number of examples in each minibatch
        int vectorSize = 300;   //Size of the word vectors. 300 in the Google News model
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this
        final int seed = 0;     //Seed for reproducibility

        File vectorFile = new File(WORD_VECTORS_PATH);
        if (!vectorFile.exists()) {
            System.out.println("Please download the GoogleNews vectors to ./data/GoogleNews-vectors-negative300.bin.gz");
            System.out.println("https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit?usp=sharing");
            return;
        }

        // DataSetIterators for training and testing respectively
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));

        Tuple<List<File>, List<File>> positiveFiles = getFiles("pos");
        Tuple<List<File>, List<File>> negativeFiles = getFiles("neg");

        SentimentExampleIterator train = new SentimentExampleIterator(positiveFiles.first(),
                negativeFiles.first(), wordVectors, batchSize, truncateReviewsToLength);
        SentimentExampleIterator test = new SentimentExampleIterator(positiveFiles.second(),
                negativeFiles.second(), wordVectors, batchSize, truncateReviewsToLength);

        Nd4j.getMemoryManager().setAutoGcWindow(10000);  //https://deeplearning4j.org/workspaces

        //Set up network configuration
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(Updater.ADAM)  //To configure: .updater(Adam.builder().beta1(0.9).beta2(0.999).build())
                .regularization(true).l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0)
                .learningRate(2e-2)
                .trainingWorkspaceMode(WorkspaceMode.SEPARATE).inferenceWorkspaceMode(WorkspaceMode.SEPARATE)   //https://deeplearning4j.org/workspaces
                .list()
                .layer(0, new GravesLSTM.Builder().nIn(vectorSize).nOut(256)
                        .activation(Activation.TANH).build())
                .layer(1, new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(256).nOut(2).build())
                .pretrain(false).backprop(true).build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));

        System.out.println("Starting training");
        for (int i = 0; i < nEpochs; i++) {
            net.fit(train);
            train.reset();
            System.out.println("Epoch " + i + " complete. Starting evaluation:");
            Evaluation evaluation = net.evaluate(test);
            System.out.println(evaluation.stats());
        }
    }

    private static Tuple<List<File>, List<File>> getFiles(String subDirectory) {
        File directory = Paths.get(DATA_PATH, subDirectory).toFile();
        File[] filesArray = directory.listFiles();
        if (filesArray == null) {
            throw new RuntimeException("negative files not found!");
        }
        List<File> files = Arrays.asList(filesArray);
        return RandomSplitList.randomSplitList(files, 0.8);
    }
}
