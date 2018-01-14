# -*- coding: utf-8 -*-
"""
@author: Michel Hosmann
Created 2018-01-08

Source: https://github.com/dipanjanS/text-analytics-with-python/
Adapted for Python 3.5

This script pre-processes and saves the source data as a more practical CSV file.

Change the directory path to your path which should point to the unzipped directory with reviews.
You can find the training corpus at http://ai.stanford.edu/~amaas/data/sentiment/

"""


import os
import pandas as pd
import numpy as np


labels = {'pos': 'positive', 'neg': 'negative'}


dataset = pd.DataFrame()
for directory in ('test', 'train'):
    for sentiment in ('pos', 'neg'):
        path = r'E:\\PycharmProjects\\DataScience\\{}\\{}'.format(directory, sentiment)
        for review_file in os.listdir(path):
            with open(os.path.join(path, review_file), 'r') as input_file:
                review = input_file.read()
            dataset = dataset.append([[review, labels[sentiment]]], ignore_index=True)
dataset.columns = ['review', 'sentiment']


indices = dataset.index.tolist()
np.random.shuffle(indices)
indices = np.array(indices)
dataset = dataset.reindex(index=indices)
dataset.to_csv('testing.csv', index=False)