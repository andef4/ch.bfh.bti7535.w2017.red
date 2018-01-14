# -*- coding: utf-8 -*-
"""
@author: Michel Hosmann
Created 2018-01-08

Source: https://github.com/dipanjanS/text-analytics-with-python/
Adapted for Python 3.5
"""
import pandas as pd
import numpy as np
from normalization import normalize_corpus
from utils import build_feature_matrix, save_pickle, load_pickle
from utils import display_evaluation_metrics, display_confusion_matrix, display_classification_report

# Classifiers
from sklearn.naive_bayes import MultinomialNB, BernoulliNB
from sklearn.linear_model import LogisticRegression, SGDClassifier
from sklearn.svm import SVC, LinearSVC, NuSVC


# Generate from data and save to pickle - if False, a saved pickle will be loaded
SAVE_NEW_CORPORA = False
SAVE_NEW_FEATURES = False
SAVE_NEW_MODELS = False


# Filename presets for pickle-files
CORPORA_FILENAME_PRESET = 'DataScience_IMDB_Corpus'
FEATURES_FILENAME_PRESET = 'DataScience_IMDB_Features'
MODEL_FILENAME_PRESET = 'DataScience_IMDB_Model'


"""
# Source data: training and test data set as .csv-file (2 columns: "review" (text) and "sentiment" (positive/negative)
# You can generate such a file with the script in data_extraction.py
"""
train_data = pd.read_csv('E:\\PycharmProjects\\DataScience\\training.csv')
test_data = pd.read_csv('E:\\PycharmProjects\\DataScience\\testing.csv')


train_reviews = np.array(train_data['review'])
train_sentiments = np.array(train_data['sentiment'])
test_reviews = np.array(test_data['review'])
test_sentiments = np.array(test_data['sentiment'])


# Normalizing training data
# *************************
if SAVE_NEW_CORPORA:
    # Save file for later use
    normalized_train_reviews = normalize_corpus(train_reviews, lemmatize=True, only_text_chars=True)
    filename = CORPORA_FILENAME_PRESET + "_Normalized_Training_Reviews.pickle"
    save_pickle(normalized_train_reviews, filename)
else:
    # Load previously saved corpus
    filename = CORPORA_FILENAME_PRESET + "_Normalized_Training_Reviews.pickle"
    normalized_train_reviews = load_pickle(filename)


# Feature extraction
# ******************
if SAVE_NEW_FEATURES:
    print("Building feature matrix...")
    # Generate new feature set
    vectorizer, train_features = build_feature_matrix(documents=normalized_train_reviews, feature_type='tfidf',
                                                      ngram_range=(1, 1), min_df=0.0, max_df=1.0)
    # Save to pickle files for later use
    filename = FEATURES_FILENAME_PRESET + "_Training_Features.pickle"
    save_pickle(train_features, filename)
    filename = FEATURES_FILENAME_PRESET + "_Training_Vectorizer.pickle"
    save_pickle(vectorizer, filename)
else:
    # Load previously saved features
    filename = FEATURES_FILENAME_PRESET + "_Training_Features.pickle"
    train_features = load_pickle(filename)
    filename = FEATURES_FILENAME_PRESET + "_Training_Vectorizer.pickle"
    vectorizer = load_pickle(filename)


# Building classifier models
# **************************
if SAVE_NEW_MODELS:
    print("Training models")
    classifiers = [MultinomialNB(), BernoulliNB(), LogisticRegression(), SGDClassifier, SVC(), LinearSVC(), NuSVC()]
    for c in classifiers:
        print("Training " + c.__class__.__name__ + " classifier")
        c.fit(train_features, train_sentiments)
        filename = MODEL_FILENAME_PRESET + "_" + c.__class__.__name__ + ".pickle"
        save_pickle(c, filename)
else:
    print("Loading Classifiers")
    classifiers = []
    filename = MODEL_FILENAME_PRESET + "_MultinomialNB.pickle"
    classifiers.append(load_pickle(filename))
    filename = MODEL_FILENAME_PRESET + "_BernoulliNB.pickle"
    classifiers.append(load_pickle(filename))
    filename = MODEL_FILENAME_PRESET + "_LogisticRegression.pickle"
    classifiers.append(load_pickle(filename))
    filename = MODEL_FILENAME_PRESET + "_SGDClassifier.pickle"
    classifiers.append(load_pickle(filename))
    filename = MODEL_FILENAME_PRESET + "_SVC.pickle"
    classifiers.append(load_pickle(filename))
    filename = MODEL_FILENAME_PRESET + "_LinearSVC.pickle"
    classifiers.append(load_pickle(filename))
    filename = MODEL_FILENAME_PRESET + "_NuSVC.pickle"
    classifiers.append(load_pickle(filename))


# normalize reviews
# *****************
if SAVE_NEW_CORPORA:
    normalized_test_reviews = normalize_corpus(test_reviews, lemmatize=True, only_text_chars=True)
    filename = CORPORA_FILENAME_PRESET + "_Normalized_Test_Reviews.pickle"
    save_pickle(normalized_test_reviews, filename)
else:
    # Load previously saved corpus
    filename = CORPORA_FILENAME_PRESET + "_Normalized_Test_Reviews.pickle"
    normalized_test_reviews = load_pickle(filename)


# Extract features
# ****************
test_features = vectorizer.transform(normalized_test_reviews)


# Make predictions and display results
# ************************************
for c in classifiers:
    print("***********************")
    print("Classifier: " + c.__class__.__name__)
    predicted_sentiments = c.predict(test_features)
    print("\n")
    display_evaluation_metrics(true_labels=test_sentiments, predicted_labels=predicted_sentiments, positive_class='positive')
    print("\n")
    display_confusion_matrix(true_labels=test_sentiments, predicted_labels=predicted_sentiments, classes=['positive', 'negative'])
    print("\n")
    display_classification_report(true_labels=test_sentiments, predicted_labels=predicted_sentiments, classes=['positive', 'negative'])
