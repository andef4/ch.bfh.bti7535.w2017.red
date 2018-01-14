package ch.bfh.bti7535.w2017.red.word2vecrnn.ourdataset;

import ch.bfh.bti7535.w2017.red.utilities.RandomSplitList;
import ch.bfh.bti7535.w2017.red.utilities.Tuple;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
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
 *
 * Process:
 * 1. Load files with positive and negative reviews from the ./data/txt_sentoken directory
 * 2. Randomly split up loaded files into train (80 %) and test(20 %) dataset
 * 3. Load existing Word2Vec model (Google News word vectors. You will have to download this MANUALLY)
 * 4. Load each review. Convert words to vectors + reviews to sequences of vectors
 * 5. Train network
 *
 * @author Alex Black
 * @author Fabio Anderegg
 */
public class Word2VecSentimentRNN {
    /** Location to save and extract the training/testing data */
    public static final String DATA_PATH = "./data/txt_sentoken/";

    /** Location (local file system) for the Google News vectors. */
    public static final String WORD_VECTORS_PATH = "./data/GoogleNews-vectors-negative300.bin.gz";

    public static void main(String[] args) throws Exception {
        int nEpochs = 100;
        int batchSize = 64; // Number of examples in each minibatch
        int vectorSize = 300; // Size of the word vectors. 300 in the Google News model
        int truncateReviewsToLength = 2000; // Truncate reviews with length (# words) greater than this
        final int seed = 0; // Random seed for reproducibility

        // Load google news vectors
        File vectorFile = new File(WORD_VECTORS_PATH);
        if (!vectorFile.exists()) {
            System.out.println("Please download the GoogleNews vectors to ./data/GoogleNews-vectors-negative300.bin.gz");
            System.out.println("https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit?usp=sharing");
            return;
        }
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(vectorFile);

        // split up dataset files into 80% training data and 20% test/evaluation data
        Tuple<List<File>, List<File>> positiveFiles = getFiles("pos");
        Tuple<List<File>, List<File>> negativeFiles = getFiles("neg");

        // DataSetIterators for training and testing respectively
        SentimentExampleIterator train = new SentimentExampleIterator(positiveFiles.first(),
                negativeFiles.first(), wordVectors, batchSize, truncateReviewsToLength);
        SentimentExampleIterator test = new SentimentExampleIterator(positiveFiles.second(),
                negativeFiles.second(), wordVectors, batchSize, truncateReviewsToLength);

        Nd4j.getMemoryManager().setAutoGcWindow(10000);  //https://deeplearning4j.org/workspaces

        /* Set up network configuration
         * Two layer network with 300 input neurons, 256 middle neurons and two output neurons (positive/negative)
         */
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

            // save network to file system
            ModelSerializer.writeModel(net, String.format("./data/rnn-%d.obj", i), true);
        }
    }

    /**
     * Lists dataset files split up into 80% training and 20% test/evaluation data
     * @param subDirectory The subdirectory of DATA_PATH to read files from
     * @return A tuple with lists of training and test files
     */
    private static Tuple<List<File>, List<File>> getFiles(String subDirectory) {
        File directory = Paths.get(DATA_PATH, subDirectory).toFile();
        File[] filesArray = directory.listFiles();
        if (filesArray == null) {
            throw new RuntimeException("files not found!");
        }
        List<File> files = Arrays.asList(filesArray);
        return RandomSplitList.randomSplitList(files, 0.8);
    }
}
