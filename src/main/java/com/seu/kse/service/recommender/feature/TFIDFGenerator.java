package com.seu.kse.service.recommender.feature;



import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.documentiterator.DocumentIterator;

import org.deeplearning4j.text.stopwords.StopWords;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class TFIDFGenerator {

    private DocumentIterator documentIterator;
    public TFIDFGenerator(DocumentIterator documentIterator){
        this.documentIterator = documentIterator;
    }

    public TfidfVectorizer train(){

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        TfidfVectorizer TFIDF = new TfidfVectorizer.Builder()
                .setMinWordFrequency(3)
                .setStopWords(StopWords.getStopWords())
                .setIterator(documentIterator)
                .setTokenizerFactory(t)
                .build();
        TFIDF.fit();
        return TFIDF;
    }

}
