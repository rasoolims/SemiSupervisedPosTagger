package Structures;

import Learning.AveragedPerceptron;

import java.util.HashMap;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 12:58 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class InfStruct {
    public HashMap<Integer, Float>[][] averagedWeights;
    public int tagSize;
    public int featSize;

    public InfStruct(AveragedPerceptron perceptron) {
        averagedWeights = perceptron.getAveragedWeights();
        tagSize = perceptron.tagSize();
        featSize = perceptron.featureSize();
    }
}
