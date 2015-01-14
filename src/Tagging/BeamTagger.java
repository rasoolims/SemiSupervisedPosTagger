package Tagging;

import Learning.AveragedPerceptron;
import Structures.BeamElement;
import Structures.Sentence;
import Structures.SpecialWords;
import Structures.TaggingState;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 7:02 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class BeamTagger {
    public static int[] thirdOrder(final Sentence sentence, final AveragedPerceptron perceptron, final boolean isDecode, int beamWidth){
        int len = sentence.words.length + 1;
        float inf = Float.POSITIVE_INFINITY;

        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();

        // pai score values
        float emission_score[][] = new float[len - 1][tagSize];
        float bigramScore[][] = new float[tagSize][tagSize];
        float trigramScore[][][] = new float[tagSize][tagSize][tagSize];

        for (int v = 0; v < tagSize; v++) {
            for (int u = 0; u < tagSize; u++) {
                bigramScore[u][v] = perceptron.score(v, featSize - 2, u, isDecode);
                for (int w = 0; w < tagSize; w++) {
                    int bigram = (w << 10) + u;
                    trigramScore[w][u][v] = perceptron.score(v, featSize - 1, bigram, isDecode);
                }
            }
        }

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emission_score[position][t] = perceptron.score(emissionFeatures, t, isDecode);
            }
        }

        ArrayList<TaggingState> beam=new ArrayList<TaggingState>();
        TaggingState initialState=new TaggingState(sentence.words.length);
        beam.add(initialState);

        for(int i=0;i<sentence.words.length;i++){
            TreeSet<BeamElement> elements=new TreeSet<BeamElement>();

            for(int b=0;b<beam.size();b++){
                TaggingState state= beam.get(b);
                int currentPosition=state.currentPosition;
                int prevTag=currentPosition>0?state.tags[currentPosition-1]:0;
                int prev2Tag=currentPosition>1?state.tags[currentPosition-2]:0;

                for(int tagDecision=2;tagDecision<tagSize;tagDecision++) {
                    float es=emission_score[currentPosition][tagDecision];
                    float bs=bigramScore[prevTag][tagDecision];
                    float ts=trigramScore[prev2Tag][prevTag][tagDecision];
                    float score=es+bs+ts+state.score;
                    BeamElement element = new BeamElement(tagDecision,score,b);
                    elements.add(element);
                    if(elements.size()>beamWidth)
                        elements.pollFirst();
                }
            }

            ArrayList<TaggingState> newBeam=new ArrayList<TaggingState>();

            for(BeamElement element:elements){
                TaggingState state=beam.get(element.beamNum).clone();
                state.tags[state.currentPosition++]= element.tagDecision;
                state.score=element.score;
                newBeam.add(state);
            }
            beam=newBeam;
        }


        TreeSet<BeamElement> elements=new TreeSet<BeamElement>();
        for(int b=0;b<beam.size();b++) {
            TaggingState state = beam.get(b);
            int currentPosition = state.currentPosition;
            int prevTag = currentPosition > 0 ? state.tags[currentPosition - 1] : 0;
            int prev2Tag = currentPosition > 1 ? state.tags[currentPosition - 2] : 0;
            int tagDecision = SpecialWords.stop.value;
            float bs = bigramScore[prevTag][tagDecision];
            float ts = trigramScore[prev2Tag][prevTag][tagDecision];
            float score = bs + ts + state.score;
            BeamElement element = new BeamElement(tagDecision, score, b);
            elements.add(element);
            if (elements.size() > beamWidth)
                elements.pollFirst();
        }

        int beamNum=elements.last().beamNum;
         return beam.get(beamNum).tags;
    }
}
