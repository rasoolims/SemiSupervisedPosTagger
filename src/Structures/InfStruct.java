package Structures;

import Learning.AveragedPerceptron;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 12:58 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class InfStruct implements Serializable {
    public HashMap<Integer, Float>[][] averagedWeights;
    public int tagSize;
    public int featSize;
    public int beamSize;
    public boolean useBeamSearch;

    public InfStruct(AveragedPerceptron perceptron,boolean useBeamSearch,int beamSize) {
        averagedWeights = perceptron.getAveragedWeights();
        tagSize = perceptron.tagSize();
        featSize = perceptron.featureSize();
        this.beamSize=beamSize;
        this.useBeamSearch=useBeamSearch;
    }
}
