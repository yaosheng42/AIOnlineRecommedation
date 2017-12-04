package com.seu.kse.service.recommender.model;

/**
 *
 * Created by yaosheng on 2017/5/26.
 */
public abstract class Word2Doc {
    protected static Paper2Vec paper2Vec;
    public abstract double[] calDocVec(String[] words);
}
