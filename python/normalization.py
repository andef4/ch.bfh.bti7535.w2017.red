# -*- coding: utf-8 -*-
"""
@author: Michel Hosmann
Created 2018-01-08

Source: https://github.com/dipanjanS/text-analytics-with-python/
Adapted for Python 3.5
"""


import re
import nltk
import string
import unicodedata
from bs4 import BeautifulSoup
from contractions import CONTRACTIONS
from nltk.corpus import wordnet as wn
from nltk.stem import WordNetLemmatizer


stopword_list = nltk.corpus.stopwords.words('english')
stopword_list = stopword_list + ['mr', 'mrs', 'come', 'go', 'get', 'tell', 'listen', 'one', 'two', 'three',
                                 'four', 'five', 'six', 'seven', 'eight', 'nine', 'zero', 'join', 'find', 'make',
                                 'say', 'ask', 'tell', 'see', 'try', 'back', 'also']
wnl = WordNetLemmatizer()


def tokenize_text(text):
    tokens = nltk.word_tokenize(text)
    tokens = [token.strip() for token in tokens]
    return tokens


def expand_contractions(text, contraction_mapping):
    contractions_pattern = re.compile('({})'.format('|'.join(contraction_mapping.keys())), flags=re.IGNORECASE | re.DOTALL)

    def expand_match(contraction):
        match = contraction.group(0)
        first_char = match[0]
        expanded_contraction = contraction_mapping.get(match) \
            if contraction_mapping.get(match) \
            else contraction_mapping.get(match.lower())
        expanded_contraction = first_char + expanded_contraction[1:]
        return expanded_contraction

    expanded_text = contractions_pattern.sub(expand_match, text)
    expanded_text = re.sub("'", "", expanded_text)
    return expanded_text


def pos_tag_text(text):
    def penn_to_wn_tags(pos_tag):
        if pos_tag.startswith('J'):
            return wn.ADJ
        elif pos_tag.startswith('V'):
            return wn.VERB
        elif pos_tag.startswith('N'):
            return wn.NOUN
        elif pos_tag.startswith('R'):
            return wn.ADV
        else:
            return None

    tagged_text = nltk.pos_tag(tokenize_text(text))
    tagged_lower_text = [(word.lower(), penn_to_wn_tags(pos_tag))
                         for word, pos_tag in
                         tagged_text]
    return tagged_lower_text


def lemmatize_text(text):
    pos_tagged_text = pos_tag_text(text)
    lemmatized_tokens = [wnl.lemmatize(word, pos_tag) if pos_tag else word for word, pos_tag in pos_tagged_text]
    lemmatized_text = ' '.join(lemmatized_tokens)
    return lemmatized_text


def remove_special_characters(text):
    tokens = tokenize_text(text)
    pattern = re.compile('[{}]'.format(re.escape(string.punctuation)))
    filtered_tokens = filter(None, [pattern.sub(' ', token) for token in tokens])
    filtered_text = ' '.join(filtered_tokens)
    return filtered_text


def remove_stopwords(text):
    tokens = tokenize_text(text)
    filtered_tokens = [token for token in tokens if token not in stopword_list]
    filtered_text = ' '.join(filtered_tokens)
    return filtered_text


def keep_text_characters(text):
    filtered_tokens = []
    tokens = tokenize_text(text)
    for token in tokens:
        if re.search('[a-zA-Z]', token):
            filtered_tokens.append(token)
    filtered_text = ' '.join(filtered_tokens)
    return filtered_text


def strip_html_tags(text):
    if text is None:
        return None
    else:
        return ''.join(BeautifulSoup(text).findAll(text=True))


def normalize_accented_characters(text):
    text = unicodedata.normalize('NFKD', text).encode('ascii', 'ignore')
    text = text.decode("utf-8")
    return text


def unescape_html(text):
    text = text.replace("&lt;", "<")
    text = text.replace("&gt;", ">")
    # this has to be last:
    text = text.replace("&amp;", "&")
    return text


def normalize_corpus(corpus, lemmatize=True, only_text_chars=False, tokenize=False):
    """
    This is where a lot of the magic happens. For every corpus passed to this function, it will (in this order):
    - normalize accented characters
    - unescape and remove html
    - expand contractions according to contractions.py
    - if set: lemmatize according to nltk pos-tag
    - else: lowercase
    - remove special characters (string.punctuation)
    - remove stopwords (nltk.corpus.stopwords plus some additions)
    - if set: remove all non-text characters
    - if set: as tokens (words)
    - else: not tokenized
    :param corpus: list of texts (string)
    :param lemmatize: perform pos-tagging and lemmatizing if True (slower)
    :param only_text_chars: remove all non-text characters if True
    :param tokenize: return tokens (words)
    :return: a normalized corpus
    """
    normalized_corpus = []
    for index, text in enumerate(corpus):
        # print("Normalizing text nr. " + str(index))
        text = normalize_accented_characters(text)
        text = unescape_html(text)
        text = strip_html_tags(text)
        text = expand_contractions(text, CONTRACTIONS)
        if lemmatize:
            text = lemmatize_text(text)
        else:
            text = text.lower()
        text = remove_special_characters(text)
        text = remove_stopwords(text)
        if only_text_chars:
            text = keep_text_characters(text)
        if tokenize:
            text = tokenize_text(text)
            normalized_corpus.append(text)
        else:
            normalized_corpus.append(text)

    return normalized_corpus
