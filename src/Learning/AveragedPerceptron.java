/**
 * Copyright 2014, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 * Modified by Mohammad Sadegh Rasooli
 */

package Learning;

import Structures.InfoStruct;

import java.util.HashMap;

public class AveragedPerceptron {
    /**
     * This class tries to implement averaged Perceptron algorithm
     * Collins, Michael. "Discriminative training methods for hidden Markov models: Theory and experiments with Perceptron algorithms."
     * In Proceedings of the ACL-02 conference on Empirical methods in natural language processing-Volume 10, pp. 1-8.
     * Association for Computational Linguistics, 2002.
     * <p/>
     * The averaging update is also optimized by using the trick introduced in Hal Daume's dissertation.
     * For more information see the second chapter of his thesis:
     * Harold Charles Daume' III. "Practical Structured Learning Techniques for Natural Language Processing", PhD thesis, ISI USC, 2006.
     * http://www.umiacs.umd.edu/~hal/docs/daume06thesis.pdf
     */

    /**
     * For the weights for all features
     */
    public  HashMap<Integer, Float>[][] featureWeights;
    public int iteration;
    /**
     * This is the main part of the extension to the original perceptron algorithm which the averaging over all the history
     */
    public HashMap<Integer, Float>[][] averagedWeights;


    public AveragedPerceptron(final int tagSize, final int featSize) {
        featureWeights = new HashMap[tagSize][featSize];
        for (int i = 0; i < featureWeights.length; i++)
            for (int j = 0; j < featureWeights[i].length; j++)
                featureWeights[i][j] = new HashMap<Integer, Float>();
        iteration = 1;
        this.averagedWeights = new HashMap[tagSize][featSize];
        for (int i = 0; i < averagedWeights.length; i++)
            for (int j = 0; j < averagedWeights[i].length; j++)
                averagedWeights[i][j] = new HashMap<Integer, Float>();
    }

    private AveragedPerceptron(int tagSize, int featSize, HashMap<Integer, Float>[][] averagedWeights) {
        featureWeights = new HashMap[tagSize][featSize];
        for (int i = 0; i < featureWeights.length; i++)
            for (int j = 0; j < featureWeights[i].length; j++)
                featureWeights[i][j] = new HashMap<Integer, Float>();

        iteration = 1;
        this.averagedWeights = averagedWeights;
    }

    public AveragedPerceptron(InfoStruct info) {
         this(info.tagSize,info.featSize,info.averagedWeights);
    }

    public float changeWeight(int tagIndex,int featIndex, int featureName, float change) {
        if (featureName == -1)
            return 0;
       // System.out.println(tagIndex+" "+featIndex+" "+featureName+" -> "+change);
        HashMap<Integer, Float> map = featureWeights[tagIndex][featIndex];
        Float value = map.get(featureName);
        if (value != null)
            map.put(featureName, change + value);
        else
            map.put(featureName, change);

        map = averagedWeights[tagIndex][featIndex];
        value = map.get(featureName);
        if (value != null)
            map.put(featureName, (iteration * change) + value);
        else
            map.put(featureName, iteration * change);

        return change;
    }

    /**
     * Adds to the iterations
     */
    public void incrementIteration() {
        iteration++;
    }

    /**
     * Returns the score of the specific feature
     *
     * @param features the features in the current instance
     * @return
     */
    public float score(final int[] features, int tagIndex, boolean isDecode) {
        float score = 0;
        final HashMap<Integer, Float>[] weights;
        if (!isDecode) {
            weights = featureWeights[tagIndex];

        } else {
            weights = averagedWeights[tagIndex];
        }

        for (int i = 0; i < features.length; i++) {
            if(features[i]==-1)
                continue;
            Float value = (weights[i]).get(features[i]);

            if (value != null)
                score += value;
        }
        return score;
    }

    public float score(final int tagIndex, final int featIndex, final int feat, final boolean isDecode) {
        if(feat==-1)
            return 0;
        HashMap<Integer,Float> map=isDecode?averagedWeights[tagIndex][featIndex]:featureWeights[tagIndex][featIndex];

        if(map.containsKey(feat))
            return map.get(feat);

        return 0;
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < averagedWeights.length; i++)
            for (int j = 0; j < averagedWeights[i].length; j++)
                size += (averagedWeights[i][j]).size();
        return size;
    }

    public int featureSize() {
        return averagedWeights[0].length;
    }

    public int tagSize() {
        return averagedWeights.length;
    }

    public HashMap<Integer, Float>[][] getAveragedWeights() {
        HashMap<Integer, Float>[][] avg=new HashMap[tagSize()][featureSize()];
        for(int i=0;i<tagSize();i++){
            for(int j=0;j<featureSize();j++){
                HashMap<Integer,Float> w=featureWeights[i][j];
                HashMap<Integer,Float> a=averagedWeights[i][j];
                avg[i][j]=new HashMap<Integer, Float>();

                for(int key:w.keySet()){
                    float val=w.get(key);
                    float aVal=a.get(key);
                    float newVal=val-(aVal/iteration);
                    if(newVal!=0.0f)
                        avg[i][j].put(key,newVal);
                }
            }
        }
        return avg;
    }
}