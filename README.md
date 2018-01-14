Sentiment Analysis on IMDB Reviews
----------------------------------

### Implemented algorithms
All algorithms are in the package ch.bfh.bti7535.w2017.red.
* Naive bayes (NaiveBayesAlgorithm.java)
* Naive bayes with part-of-speech tagging (NaiveBayesAlgorithmPOS.java)
* Unweighted Baseline (unweightedbaseline/Main.java)
* Weighted Baseline (weightedbaseline/WeightedBaseline.java)
* Deep learning with LSTM and RNN neural network
  * Training and evaluation with the small 2000 reviews dataset (word2vecrnn/ourdataset/Word2VecSentimentRNN.java)
  * Training with the big 25000 dataset, evaluation with the small 2000 reviews dataset:
    * Training: word2vecrnn/dl4jdataset/Word2VecSentimentRNN.java
    * Evaluation: word2vecrnn/dl4jdataset/EvaluateOnOurDataset.java

### How to Run
The files referenced above all contain a main method which can be execute
from an IDE. To run the algorithms, the IMDB dataset has to be downloaded.
The class Download.java does this automatically.

The deep learning algorithms also require the GoogleNews WordVectors in
the data/ subdirectory. They can be downloaded here:
https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit?usp=sharing

The bigger (25000 reveiews) IMDB dataset is automatically downloaded by the Java
class.

The deep learning algorithms require quite a bit of memory, the maximum heap space has
to be increased with the "-Xm8G" JVM option (more memory is even better).
