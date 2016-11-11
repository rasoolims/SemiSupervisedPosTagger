package SemiSupervisedPOSTagger.Training;

import SemiSupervisedPOSTagger.IO.FileManager;
import SemiSupervisedPOSTagger.Learning.AveragedPerceptron;
import SemiSupervisedPOSTagger.Structures.*;
import SemiSupervisedPOSTagger.Tagging.BeamTagger;
import SemiSupervisedPOSTagger.Tagging.Tagger;

import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/12/15
 * Time: 6:30 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */
public class Trainer {
    public static void train(final Options options, final int featSize,String tagDictionaryPath) throws Exception {
        IndexMaps maps = FileManager.createIndexMaps(options.trainPath, options.delim, options.clusterFile, tagDictionaryPath, Sentence.brownSize);
        int unknownIndex = -1;
        // if(maps.stringMap.containsKey("***"))
        //   unknownIndex=maps.stringMap.get("***");


        // reading train and dev sentences to a vector
        ArrayList<Sentence> train_sentences = FileManager.readSentences(options.trainPath, maps, options.delim);
        ArrayList<Sentence> dev_sentences = new ArrayList<Sentence>();
        if (options.devPath != "")
            dev_sentences = FileManager.readSentences(options.devPath, maps, options.delim);

        AveragedPerceptron classifier = new AveragedPerceptron(maps.tagSize, featSize, maps.getTagDictionary());
        for (int iter = 1; iter <= options.trainingIter; iter++) {
            System.out.print("\niter: " + iter + "\n");
            int corr = 0;
            int all = 0;
            //iterating over all training sentences
            for (int s = 0; s < train_sentences.size(); s++) {
                Sentence sen = train_sentences.get(s);
                if ((s + 1) % 1000 == 0)
                    System.out.print((s + 1) + " ");
                corr += trainIter(sen, classifier, options.useBeamSearch, options.beamWidth, featSize, options.updateMode, unknownIndex, options.C);
                all += sen.words.length;
                classifier.incrementIteration();
            }
            DecimalFormat format = new DecimalFormat("##.00");
            float accuracy = (float) corr * 100.0f / all;
            System.out.print("\ntrain accuracy: " + format.format(accuracy) + "\n");

            if (false) {
                InfoStruct info = new InfoStruct(classifier, options.useBeamSearch, options.beamWidth, maps.getTagDictionary(), classifier.getAvgPenalizerWeight(), true);
                InfoStruct info_no_avg = new InfoStruct(classifier, options.useBeamSearch, options.beamWidth, maps.getTagDictionary(), classifier.getAvgPenalizerWeight(), false);
                System.out.print("saving the model...");
                saveModel(maps, info, options.modelPath + ".iter_" + iter);
                saveModel(maps, info_no_avg, options.modelPath + ".no_avg.iter_" + iter);
                System.out.print("done!\n");

                if (dev_sentences.size() > 0) {
                    devIter(dev_sentences, options.modelPath + ".iter_" + iter);
                    // devIter(dev_sentences, options.modelPath + ".no_avg.iter_" + iter);
                }
            }
        }
        InfoStruct info = new InfoStruct(classifier, options.useBeamSearch, options.beamWidth, maps.getTagDictionary(), classifier.getAvgPenalizerWeight(), true);
        System.out.print("saving the model...");
        saveModel(maps, info, options.modelPath);
        System.out.print("done!\n");
    }

    private static int trainIter(final Sentence sen, AveragedPerceptron classifier, final boolean useBeamSearch, final int beamSize, final int featSize, final UpdateMode updateMode, final int unknownIndex, final   double C) {
        int corr = 0;
        if (useBeamSearch || updateMode.value == updateMode.standard.value) {
          //  TaggingState predictedState = BeamTagger.thirdOrder(sen, classifier, beamSize, updateMode,unknownIndex);
           //todo
            ArrayList<TaggingState> bestStates= BeamTagger.thirdOrderNBest(sen, classifier, beamSize, updateMode, unknownIndex,2);

            TaggingState predictedState=bestStates.get(0);
            int[] predictedTags = predictedState.tags;
            int currentPosition = predictedState.currentPosition;
            assert (predictedTags.length == sen.tags.length);

            boolean same = true;
            for (int t = 0; t < predictedTags.length; t++) {
                int predicted = predictedTags[t];
                int gold = sen.tags[t];
                if (gold!=unknownIndex && predicted != gold ) {
                    same = false;
                } else
                    corr++;
            }

            // updating weights
            if (!same) {
                updateWeights(sen, classifier, predictedTags, featSize, currentPosition, unknownIndex);
            }  else if(predictedState.score-bestStates.get(1).score<=C){
                updateWeights(sen, classifier, bestStates.get(1).tags, featSize, currentPosition, unknownIndex);
            }
        } else {
            int[] predictedTags = Tagger.tag(sen, classifier, false, useBeamSearch, beamSize, false);
            assert (predictedTags.length == sen.tags.length);

            boolean same = true;
            for (int t = 0; t < predictedTags.length; t++) {
                int predicted = predictedTags[t];
                int gold = sen.tags[t];
                if (predicted != gold && gold != unknownIndex) {
                    same = false;
                } else
                    corr++;
            }

            // updating weights
            if (!same) {
                updateWeights(sen, classifier, predictedTags, featSize, predictedTags.length, unknownIndex);
            }
        }
        return corr;
    }

    private static void updateWeights(final Sentence sen, AveragedPerceptron classifier, final int[] predictedTags, final int featSize, final int currentPosition, final int unknownIndex) {
        for (int t = 0; t < currentPosition; t++) {
            int predicted = predictedTags[t];
            int gold = sen.tags[t];
            int predictedPrevTag = 0;
            int predicted_prev2_tag = 0;
            int goldPrevTag = 0;
            int gold_prev2_tag = 0;

            if (t > 0) {
                predictedPrevTag = predictedTags[t - 1];
                goldPrevTag = sen.tags[t - 1];
                if (t > 1) {
                    predicted_prev2_tag = predictedTags[t - 2];
                    gold_prev2_tag = sen.tags[t - 2];
                }
            }

            if ((gold != predicted || predictedPrevTag != goldPrevTag || predicted_prev2_tag != gold_prev2_tag)
                    && gold!=unknownIndex && gold_prev2_tag!=unknownIndex && goldPrevTag!=unknownIndex) {
                int[] predicted_features = sen.getFeatures(t, predicted_prev2_tag, predictedPrevTag, featSize);
                int[] gold_features = sen.getFeatures(t, gold_prev2_tag, goldPrevTag, featSize);

                for (int f = 0; f < featSize-1; f++) {
                    int pfeat = predicted_features[f];
                    int gfeat = gold_features[f];

                    if(pfeat!=gfeat || predicted!=gold) {
                        if (pfeat != -1)
                            classifier.changeWeight(predicted, f, pfeat, -1);

                        if (gfeat != -1)
                                classifier.changeWeight(gold, f, gfeat, +1);
                    }
                }
                
               if(gold != predicted ){
                //   int gCond=classifier.dictCondition(sen.lowerWords[t],gold);
                   int gCond=classifier.dictCondition(sen.lowerWords[t],gold);
                   if(gCond!=-1)
                       classifier.changeWeight(gold, classifier.featureSize()-1, gCond, 1);
                   
                  // int pCond=classifier.dictCondition(sen.lowerWords[t],predicted);
                   int pCond=classifier.dictCondition(sen.lowerWords[t],predicted);
                   if(pCond!=-1)
                       classifier.changeWeight(predicted, classifier.featureSize()-1, pCond, -1);
               }
                
            }
        }


        if (currentPosition >= predictedTags.length - 1) {
            int predictedPrevTag = 0;
            int predicted_prev2_tag = 0;
            int goldPrevTag = 0;
            int gold_prev2_tag = 0;
            int t = predictedTags.length;

            if (t > 0) {
                predictedPrevTag = predictedTags[t - 1];
                goldPrevTag = sen.tags[t - 1];
                if (t > 1) {
                    predicted_prev2_tag = predictedTags[t - 2];
                    gold_prev2_tag = sen.tags[t - 2];
                }
            }

            if ((predictedPrevTag != goldPrevTag || predicted_prev2_tag != gold_prev2_tag ) && gold_prev2_tag!=unknownIndex && goldPrevTag!=unknownIndex) {
                int[] predicted_features = sen.getFeatures(t, predicted_prev2_tag, predictedPrevTag, featSize);
                int[] gold_features = sen.getFeatures(t, gold_prev2_tag, goldPrevTag, featSize);

                for (int f = featSize - 3; f < featSize-1; f++) {
                    int pfeat = predicted_features[f];
                    int gfeat = gold_features[f];

                    if(pfeat!=gfeat) {
                        if (pfeat != -1)
                            classifier.changeWeight(1, f, pfeat, -1);

                        if (gfeat != -1)
                            classifier.changeWeight(1, f, gfeat, +1);
                    }
                }
            }
        }
    }

    private static void devIter(ArrayList<Sentence> dev_sentences, String modelPath) throws Exception{
        Tagger tagger=new Tagger(modelPath);
        System.out.print("\ndecoding...");
        int corr = 0;
        int all = 0;
        int exact = 0;

        long start = System.currentTimeMillis();

        for (int s = 0; s < dev_sentences.size(); s++) {
            Sentence sen = dev_sentences.get(s);
            if ((s + 1) % 1000 == 0)
                System.out.print((s + 1) + " ");
            int[] predictedTags = tagger.tag(sen,  false);

            assert (predictedTags.length == sen.tags.length);

            boolean same = true;
            for (int t = 0; t < predictedTags.length; t++) {
                int predicted = predictedTags[t];
                int gold = sen.tags[t];
                if (predicted == gold) {
                    corr++;
                } else {
                    same = false;
                }
                all++;
            }
            if (same)
                exact++;
        }
        long end = System.currentTimeMillis();
        DecimalFormat format = new DecimalFormat("##.00");

        float duration = (float) (end - start) / dev_sentences.size();
        System.out.print("\nduration " + format.format(duration) + " ms per sentence\n");

        float accuracy = (float) corr * 100.0f / all;
        float exact_match = (float) exact * 100.0f / dev_sentences.size();
        System.out.print("dev accuracy is " + format.format(accuracy) + "\n");
        System.out.print("dev exact match is " + format.format(exact_match) + "\n");
    }


    private static void devIter(ArrayList<Sentence> dev_sentences, String modelPath, AveragedPerceptron perceptron) throws Exception{
        Tagger tagger=new Tagger(modelPath);
        tagger.perceptron=perceptron;
        System.out.print("\ndecoding...");
        int corr = 0;
        int all = 0;
        int exact = 0;

        long start = System.currentTimeMillis();

        for (int s = 0; s < dev_sentences.size(); s++) {
            Sentence sen = dev_sentences.get(s);
            if ((s + 1) % 1000 == 0)
                System.out.print((s + 1) + " ");
            int[] predictedTags = tagger.tag(sen,  false,false);

            assert (predictedTags.length == sen.tags.length);

            boolean same = true;
            for (int t = 0; t < predictedTags.length; t++) {
                int predicted = predictedTags[t];
                int gold = sen.tags[t];
                if (predicted == gold) {
                    corr++;
                } else {
                    same = false;
                }
                all++;
            }
            if (same)
                exact++;
        }
        long end = System.currentTimeMillis();
        DecimalFormat format = new DecimalFormat("##.00");

        float duration = (float) (end - start) / dev_sentences.size();
        System.out.print("\nduration " + format.format(duration) + " ms per sentence\n");

        float accuracy = (float) corr * 100.0f / all;
        float exact_match = (float) exact * 100.0f / dev_sentences.size();
        System.out.print("dev accuracy is " + format.format(accuracy) + "\n");
        System.out.print("dev exact match is " + format.format(exact_match) + "\n");
    }

    
    public static void saveModel(IndexMaps maps, InfoStruct info, String modelPath) throws Exception {
        FileOutputStream fos = new FileOutputStream(modelPath);
        GZIPOutputStream gz = new GZIPOutputStream(fos);
        ObjectOutput writer = new ObjectOutputStream(gz);
        writer.writeObject(info);
        writer.writeObject(maps);
        writer.flush();
        writer.close();
    }
}
