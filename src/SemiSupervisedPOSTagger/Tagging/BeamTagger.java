package SemiSupervisedPOSTagger.Tagging;

import SemiSupervisedPOSTagger.Learning.AveragedPerceptron;
import SemiSupervisedPOSTagger.Structures.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 7:02 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */
public class BeamTagger {
    public static int[] thirdOrder(final Sentence sentence, final AveragedPerceptron perceptron, final boolean isDecode, int beamWidth,final boolean usePartialInfo,final Tagger tagger){
        int len = sentence.words.length + 1;

        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();

        ArrayList<Integer> allTags=new ArrayList<Integer>(tagSize-2);
        for(int i=2;i<tagSize;i++)
            allTags.add(i);

        // pai score values
        float emission_score[][] = new float[len - 1][tagSize];
        float bigramScore[][] = new float[tagSize][tagSize];
        float trigramScore[][][] = new float[tagSize][tagSize][tagSize];
        if(!isDecode) {
            for (int v = 0; v < tagSize; v++) {
                for (int u = 0; u < tagSize; u++) {
                    bigramScore[u][v] = perceptron.score(v, featSize - 3, u, isDecode);
                    for (int w = 0; w < tagSize; w++) {
                        int bigram = (w << 10) + u;
                        trigramScore[w][u][v] = perceptron.score(v, featSize -2, bigram, isDecode);
                    }
                }
            }
        }else{
            bigramScore=tagger.bigramScore;
            trigramScore=tagger.trigramScore;
        }

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emission_score[position][t] = perceptron.score(emissionFeatures, t, isDecode);
               // int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                if(cond!=-1)
                emission_score[position][t]+=perceptron.score(t,perceptron.featureSize()-1,cond ,isDecode) ;
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
                int prev3Tag=currentPosition>2?state.tags[currentPosition-3]:0;

                ArrayList<Integer> possibleTags=new ArrayList<Integer>();
                if(sentence.tags[i]==-1 || ! usePartialInfo)
                    possibleTags=allTags;
                else
                    possibleTags.add(sentence.tags[i]);

                for(int tagDecision : possibleTags) {
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
            int prev3Tag = currentPosition > 2 ? state.tags[currentPosition - 3] : 0;
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


    public static Pair<int[],Float> thirdOrderWithScore(final Sentence sentence, final AveragedPerceptron perceptron, final boolean isDecode, int beamWidth,final boolean usePartialInfo,final Tagger tagger){
        int len = sentence.words.length + 1;

        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();

        ArrayList<Integer> allTags=new ArrayList<Integer>(tagSize-2);
        for(int i=2;i<tagSize;i++)
            allTags.add(i);

        // pai score values
        float emission_score[][] = new float[len - 1][tagSize];
        float bigramScore[][] = new float[tagSize][tagSize];
        float trigramScore[][][] = new float[tagSize][tagSize][tagSize];
        if(!isDecode) {
            for (int v = 0; v < tagSize; v++) {
                for (int u = 0; u < tagSize; u++) {
                    bigramScore[u][v] = perceptron.score(v, featSize -3, u, isDecode);
                    for (int w = 0; w < tagSize; w++) {
                        int bigram = (w << 10) + u;
                        trigramScore[w][u][v] = perceptron.score(v, featSize -2, bigram, isDecode);
                    }
                }
            }
        }else{
            bigramScore=tagger.bigramScore;
            trigramScore=tagger.trigramScore;
        }

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emission_score[position][t] = perceptron.score(emissionFeatures, t, isDecode);
              //  int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                if(cond!=-1)
                    emission_score[position][t]+=perceptron.score(t,perceptron.featureSize()-1,cond ,isDecode) ;
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
                int prev3Tag=currentPosition>2?state.tags[currentPosition-3]:0;

                ArrayList<Integer> possibleTags=new ArrayList<Integer>();
                if(sentence.tags[i]==-1 || ! usePartialInfo)
                    possibleTags=allTags;
                else
                    possibleTags.add(sentence.tags[i]);

                for(int tagDecision : possibleTags) {
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
            int prev3Tag = currentPosition > 2 ? state.tags[currentPosition - 3] : 0;
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
        return new Pair<int[], Float>(beam.get(beamNum).tags,elements.last().score);
    }


    /**
     * This is useful for pruning some tags for specific manually
     * @param exceptedTags  list of excepted tags that should be pruned
     * @return
     */
    public static Pair<int[],Float> thirdOrderWithPruning(final Sentence sentence, final AveragedPerceptron perceptron,  int beamWidth,final Tagger tagger,HashMap<Integer,HashSet<Integer>> exceptedTags){
        int len = sentence.words.length + 1;

        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();

        ArrayList<Integer> allTags=new ArrayList<Integer>(tagSize-2);
        for(int i=2;i<tagSize;i++)
            allTags.add(i);

        // pai score values
        float emission_score[][] = new float[len - 1][tagSize];

        float bigramScore[][]=tagger.bigramScore;
        float trigramScore[][][]=tagger.trigramScore;

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emission_score[position][t] = perceptron.score(emissionFeatures, t, true);
           //     int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                if(cond!=-1)
                    emission_score[position][t]+=perceptron.score(t,perceptron.featureSize()-1,cond ,true) ;
            }
        }

        ArrayList<TaggingState> beam=new ArrayList<TaggingState>();
        TaggingState initialState=new TaggingState(sentence.words.length);
        beam.add(initialState);

        for(int i=0;i<sentence.words.length;i++){
            TreeSet<BeamElement> elements=new TreeSet<BeamElement>();

            for(int b=0;b<beam.size();b++) {
                TaggingState state = beam.get(b);
                int currentPosition = state.currentPosition;
                int prevTag = currentPosition > 0 ? state.tags[currentPosition - 1] : 0;
                int prev2Tag = currentPosition > 1 ? state.tags[currentPosition - 2] : 0;
                int prev3Tag = currentPosition > 2 ? state.tags[currentPosition - 3] : 0;

                ArrayList<Integer> possibleTags = new ArrayList<Integer>();
                if (exceptedTags.containsKey(i)) {
                    for (int t : allTags)
                        if (!exceptedTags.get(i).contains(t))
                            possibleTags.add(t);
                } else
                    possibleTags = allTags;

                for (int tagDecision : possibleTags) {
                    float es = emission_score[currentPosition][tagDecision];
                    float bs = bigramScore[prevTag][tagDecision];
                    float ts = trigramScore[prev2Tag][prevTag][tagDecision];
                    float score = es + bs + ts + state.score;
                    BeamElement element = new BeamElement(tagDecision, score, b);
                    elements.add(element);
                    if (elements.size() > beamWidth)
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
            int prev3Tag = currentPosition > 2 ? state.tags[currentPosition - 3] : 0;
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
        return new Pair<int[], Float>(beam.get(beamNum).tags,elements.last().score);
    }
    
    public static ArrayList<Pair<int[],Float>> getPossibleTagsByOneReplacement(final Sentence sentence, final AveragedPerceptron perceptron,  int beamWidth,final Tagger tagger) {
        ArrayList<Pair<int[],Float>> allTags = new ArrayList<Pair<int[],Float>>();
        Pair<int[],Float> bestTags = thirdOrderWithPruning(sentence, perceptron, beamWidth,  tagger, new HashMap<Integer, HashSet<Integer>>());
        allTags.add(bestTags);

        for (int i = 0; i < bestTags.first.length; i++) {
            HashMap<Integer, HashSet<Integer>> exceptedTags = new HashMap<Integer, HashSet<Integer>>();
            HashSet<Integer> exceptions = new HashSet<Integer>();
            exceptions.add(bestTags.first[i]);
            exceptedTags.put(i, exceptions);
            allTags.add(thirdOrderWithPruning(sentence, perceptron, beamWidth, tagger, exceptedTags));
        }
        return allTags;
    }
    
    
    public static TaggingState thirdOrder(final Sentence sentence, final AveragedPerceptron perceptron, int beamWidth, UpdateMode updateMode, int unknownIndex) {
        int len = sentence.words.length + 1;
        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();
        
        boolean isPartial=false;
        for(int tag:sentence.tags)
        if(tag==unknownIndex){
            isPartial=true;
            break;
        }

        float maxViolation = Float.NEGATIVE_INFINITY;
        TaggingState maxViolState = new TaggingState(sentence.words.length);
        TaggingState goldState = new TaggingState(sentence.words.length);


        // pai score values
        float emission_score[][] = new float[len - 1][tagSize];
        float bigramScore[][] = new float[tagSize][tagSize];
        float trigramScore[][][] = new float[tagSize][tagSize][tagSize];

        for (int v = 0; v < tagSize; v++) {
            for (int u = 0; u < tagSize; u++) {
                bigramScore[u][v] = perceptron.score(v, featSize - 3, u, false);
                for (int w = 0; w < tagSize; w++) {
                    int bigram = (w << 10) + u;
                    trigramScore[w][u][v] = perceptron.score(v, featSize - 2, bigram, false);
                }
            }
        }

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emission_score[position][t] = perceptron.score(emissionFeatures, t, false);
             //   int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                if(cond!=-1)
                    emission_score[position][t]+=perceptron.score(t,perceptron.featureSize()-1,cond ,false) ;
            }
        }

        ArrayList<TaggingState> beam = new ArrayList<TaggingState>();
        TaggingState initialState = new TaggingState(sentence.words.length);
        beam.add(initialState);

        for (int i = 0; i < sentence.words.length; i++) {
            TreeSet<BeamElement> elements = new TreeSet<BeamElement>();


            for (int b = 0; b < beam.size(); b++) {
                TaggingState state = beam.get(b);
                int currentPosition = state.currentPosition;
                int prevTag = currentPosition > 0 ? state.tags[currentPosition - 1] : 0;
                int prev2Tag = currentPosition > 1 ? state.tags[currentPosition - 2] : 0;

                for (int tagDecision = 2; tagDecision < tagSize; tagDecision++) {
                    float es = emission_score[currentPosition][tagDecision];
                    float bs = bigramScore[prevTag][tagDecision];
                    float ts = trigramScore[prev2Tag][prevTag][tagDecision];
                    float score = es + bs + ts + state.score;
                    BeamElement element = new BeamElement(tagDecision, score, b);
                    elements.add(element);
                    if (elements.size() > beamWidth)
                        elements.pollFirst();
                }
            }
            
                if (sentence.tags[goldState.currentPosition] != unknownIndex) {
                    int prevTag = goldState.currentPosition > 0 ? goldState.tags[goldState.currentPosition - 1] : 0;
                    int prev2Tag = goldState.currentPosition > 1 ? goldState.tags[goldState.currentPosition - 2] : 0;
                    if (prevTag != unknownIndex && prev2Tag != unknownIndex) {
                        float es = emission_score[goldState.currentPosition][sentence.tags[goldState.currentPosition]];
                        float bs = bigramScore[prevTag][goldState.tags[goldState.currentPosition]];
                        float ts = trigramScore[prev2Tag][prevTag][sentence.tags[goldState.currentPosition]];
                        float score = es + bs + ts + goldState.score;
                        goldState.score = score;
                    }
                }
                goldState.tags[goldState.currentPosition] = sentence.tags[goldState.currentPosition];
                goldState.currentPosition++;

            ArrayList<TaggingState> newBeam = new ArrayList<TaggingState>();

            boolean oracleInBeam = false;
            for (BeamElement element : elements) {
                TaggingState state = beam.get(element.beamNum).clone();
                state.tags[state.currentPosition++] = element.tagDecision;
                state.score = element.score;
                newBeam.add(state);
                if (updateMode.value != updateMode.standard.value && !oracleInBeam) {
                    boolean same = true;
                    for (int j = 0; j <= state.currentPosition; j++) {
                        if (sentence.tags[i] != state.tags[i] && sentence.tags[i]!=unknownIndex) {
                            same = false;
                            break;
                        }
                    }
                    if (same)
                        oracleInBeam = true;
                }
            }

            if (updateMode.value != updateMode.standard.value && !oracleInBeam) {
                float viol = elements.last().score - goldState.score;
                if (viol > maxViolation) {
                    maxViolation = viol;
                    maxViolState = newBeam.get(newBeam.size() - 1);
                    if (updateMode.value == updateMode.early.value) {
                        return maxViolState;
                    }
                }
            }

            beam = newBeam;
        }


        TreeSet<BeamElement> elements = new TreeSet<BeamElement>();
        for (int b = 0; b < beam.size(); b++) {
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

        int prevTag = goldState.currentPosition > 0 ? goldState.tags[goldState.currentPosition - 1] : 0;
        int prev2Tag = goldState.currentPosition > 1 ? goldState.tags[goldState.currentPosition - 2] : 0;
       if(prev2Tag!=unknownIndex && prevTag!=unknownIndex) {
           float bs = bigramScore[prevTag][1];
           float ts = trigramScore[prev2Tag][prevTag][1];
           float score = bs + ts + goldState.score;
           goldState.score = score;
       }


        int beamNum = elements.last().beamNum;
        TaggingState lastState = beam.get(beamNum);
        float viol = lastState.score - goldState.score;
        if (viol > maxViolation) {
            maxViolState = lastState;
        }

        if (updateMode.value != updateMode.maxViolation.value || isPartial)
            return lastState;

        return maxViolState;
    }

    public static ArrayList<TaggingState> thirdOrderNBest(final Sentence sentence, final AveragedPerceptron perceptron, int beamWidth, UpdateMode updateMode, int unknownIndex, int n) {
        int len = sentence.words.length + 1;
        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();

        boolean isPartial=false;
        for(int tag:sentence.tags)
            if(tag==unknownIndex){
                isPartial=true;
                break;
            }


        ArrayList<TaggingState> maxStates=new ArrayList<TaggingState>(n);
        
        
        float maxViolation = Float.NEGATIVE_INFINITY;
        TaggingState goldState = new TaggingState(sentence.words.length);


        // pai score values
        float emission_score[][] = new float[len - 1][tagSize];
        float bigramScore[][] = new float[tagSize][tagSize];
        float trigramScore[][][] = new float[tagSize][tagSize][tagSize];

        for (int v = 0; v < tagSize; v++) {
            for (int u = 0; u < tagSize; u++) {
                bigramScore[u][v] = perceptron.score(v, featSize - 3, u, false);
                for (int w = 0; w < tagSize; w++) {
                    int bigram = (w << 10) + u;
                    trigramScore[w][u][v] = perceptron.score(v, featSize - 2, bigram, false);
                }
            }
        }

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emission_score[position][t] = perceptron.score(emissionFeatures, t, false);
                //   int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                if(cond!=-1)
                    emission_score[position][t]+=perceptron.score(t,perceptron.featureSize()-1,cond ,false) ;
            }
        }

        ArrayList<TaggingState> beam = new ArrayList<TaggingState>();
        TaggingState initialState = new TaggingState(sentence.words.length);
        beam.add(initialState);

        for (int i = 0; i < sentence.words.length; i++) {
            TreeSet<BeamElement> elements = new TreeSet<BeamElement>();


            for (int b = 0; b < beam.size(); b++) {
                TaggingState state = beam.get(b);
                int currentPosition = state.currentPosition;
                int prevTag = currentPosition > 0 ? state.tags[currentPosition - 1] : 0;
                int prev2Tag = currentPosition > 1 ? state.tags[currentPosition - 2] : 0;

                for (int tagDecision = 2; tagDecision < tagSize; tagDecision++) {
                    float es = emission_score[currentPosition][tagDecision];
                    float bs = bigramScore[prevTag][tagDecision];
                    float ts = trigramScore[prev2Tag][prevTag][tagDecision];
                    float score = es + bs + ts + state.score;
                    BeamElement element = new BeamElement(tagDecision, score, b);
                    elements.add(element);
                    if (elements.size() > beamWidth)
                        elements.pollFirst();
                }
            }

            if (sentence.tags[goldState.currentPosition] != unknownIndex) {
                int prevTag = goldState.currentPosition > 0 ? goldState.tags[goldState.currentPosition - 1] : 0;
                int prev2Tag = goldState.currentPosition > 1 ? goldState.tags[goldState.currentPosition - 2] : 0;
                if (prevTag != unknownIndex && prev2Tag != unknownIndex) {
                    float es = emission_score[goldState.currentPosition][sentence.tags[goldState.currentPosition]];
                    float bs = bigramScore[prevTag][goldState.tags[goldState.currentPosition]];
                    float ts = trigramScore[prev2Tag][prevTag][sentence.tags[goldState.currentPosition]];
                    float score = es + bs + ts + goldState.score;
                    goldState.score = score;
                }
            }
            goldState.tags[goldState.currentPosition] = sentence.tags[goldState.currentPosition];
            goldState.currentPosition++;

            ArrayList<TaggingState> newBeam = new ArrayList<TaggingState>();

            boolean oracleInBeam = false;
            for (BeamElement element : elements) {
                TaggingState state = beam.get(element.beamNum).clone();
                state.tags[state.currentPosition++] = element.tagDecision;
                state.score = element.score;
                newBeam.add(state);
                if (updateMode.value != updateMode.standard.value && !oracleInBeam) {
                    boolean same = true;
                    for (int j = 0; j <= state.currentPosition; j++) {
                        if (sentence.tags[i] != state.tags[i] && sentence.tags[i]!=unknownIndex) {
                            same = false;
                            break;
                        }
                    }
                    if (same)
                        oracleInBeam = true;
                }
            }

            if (updateMode.value != updateMode.standard.value && !oracleInBeam) {
                float viol = elements.last().score - goldState.score;
                if (viol > maxViolation) {
                    maxViolation = viol;
                    maxStates=new ArrayList<TaggingState>(n);

                    for(int j=1;j<=n;j++){
                        maxStates.add(newBeam.get(newBeam.size() - j));
                    }
                    if (updateMode.value == updateMode.early.value) {
                        return maxStates;
                    }
                }
            }

            beam = newBeam;
        }


        TreeSet<BeamElement> elements = new TreeSet<BeamElement>();
        for (int b = 0; b < beam.size(); b++) {
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

        int prevTag = goldState.currentPosition > 0 ? goldState.tags[goldState.currentPosition - 1] : 0;
        int prev2Tag = goldState.currentPosition > 1 ? goldState.tags[goldState.currentPosition - 2] : 0;
        if(prev2Tag!=unknownIndex && prevTag!=unknownIndex) {
            float bs = bigramScore[prevTag][1];
            float ts = trigramScore[prev2Tag][prevTag][1];
            float score = bs + ts + goldState.score;
            goldState.score = score;
        }


        int beamNum = elements.last().beamNum;
        TaggingState lastState = beam.get(beamNum);
        float viol = lastState.score - goldState.score;
        if (viol > maxViolation) {
            maxStates=new ArrayList<TaggingState>(n);

            for(int j=1;j<=n;j++){
                maxStates.add(beam.get(beam.size() - j));
            }
        }

        if (updateMode.value != updateMode.maxViolation.value || isPartial) {
            maxStates=new ArrayList<TaggingState>(n);

            for(int i=1;i<=n;i++){
                maxStates.add(beam.get(beam.size() - i));
            }
              return maxStates;
        }

        return maxStates;
    }


}
