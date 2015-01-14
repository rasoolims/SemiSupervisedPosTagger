package Tagging;

import Learning.AveragedPerceptron;
import Structures.IndexMaps;
import Structures.Sentence;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 12:41 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class Tagger {
    public static String[] tag(final String line, final IndexMaps maps, final AveragedPerceptron classifier, final boolean isDecode, final String delim, final boolean useBeamSearch, final int beamSize) {
        Sentence sentence = new Sentence(line, maps, delim);
        return tag(sentence, maps, classifier, isDecode, useBeamSearch, beamSize);
    }

    public static String[] tag(final Sentence sentence, final IndexMaps maps, final AveragedPerceptron classifier, final boolean isDecode, final boolean useBeamSearch, final int beamSize) {
        int[] tags = tag(sentence, classifier, isDecode, useBeamSearch, beamSize);
        String[] output = new String[tags.length];
        for (int i = 0; i < tags.length; i++)
            output[i] = maps.reversedMap[tags[i]];
        return output;
    }

    public static int[] tag(final Sentence sentence, final AveragedPerceptron classifier, final boolean isDecode, final boolean useBeamSearch, final int beamSize) {
        //todo write beam search
        return useBeamSearch ?
                BeamTagger.thirdOrder(sentence, classifier, isDecode,beamSize):Viterbi.thirdOrder(sentence, classifier, isDecode);
    }
}
