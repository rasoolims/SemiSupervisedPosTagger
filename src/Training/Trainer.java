package Training;

import IO.FileManager;
import Learning.AveragedPerceptron;
import Structures.IndexMaps;
import Structures.InfStruct;
import Structures.Sentence;
import Tagging.Tagger;

import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/12/15
 * Time: 6:30 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class Trainer {
    public static void train(final String trainFilePath, final String devFilePath, final String modelPath, final int featSize, final String delim, final int max_iterations, final boolean useBeamSearch, final int beamSize) throws Exception {
        IndexMaps maps = FileManager.createIndexMaps(trainFilePath, delim);   //todo maps should be saved to memory

        // reading train and dev sentences to a vector
        ArrayList<Sentence> train_sentences = FileManager.readSentences(trainFilePath, maps, delim);
        ArrayList<Sentence> dev_sentences = FileManager.readSentences(devFilePath, maps, delim);

        AveragedPerceptron classifier = new AveragedPerceptron(maps.tagSize, featSize);
        for (int iter = 1; iter <= max_iterations; iter++) {
            System.out.print("\niter: " + iter + "\n");
            int corr = 0;
            int all = 0;
            //iterating over all training sentences
            for (int s = 0; s < train_sentences.size(); s++) {
                Sentence sen = train_sentences.get(s);
                if ((s + 1) % 1000 == 0)
                    System.out.print((s + 1) + " ");
                int[] predicted_tags = Tagger.tag(sen, classifier, false, useBeamSearch, beamSize);

                assert (predicted_tags.length == sen.tags.length);

                boolean same = true;
                for (int t = 0; t < predicted_tags.length; t++) {
                    int predicted = predicted_tags[t];
                    int gold = sen.tags[t];
                    if (predicted != gold) {
                        same = false;
                    } else
                        corr++;
                    all++;
                }

                // updating weights
                if (!same) {
                    for (int t = 0; t < predicted_tags.length; t++) {
                        int predicted = predicted_tags[t];
                        int gold = sen.tags[t];
                        int predicted_prev_tag = 0;
                        int predicted_prev2_tag = 0;
                        int gold_prev_tag = 0;
                        int gold_prev2_tag = 0;

                        if (t > 0) {
                            predicted_prev_tag = predicted_tags[t - 1];
                            gold_prev_tag = sen.tags[t - 1];
                            if (t > 1) {
                                predicted_prev2_tag = predicted_tags[t - 2];
                                gold_prev2_tag = sen.tags[t - 2];
                            }
                        }

                        if (gold != predicted || predicted_prev_tag != gold_prev_tag || predicted_prev2_tag != gold_prev2_tag) {
                            int[] predicted_features = sen.getFeatures(t, predicted_prev2_tag, predicted_prev_tag, featSize);
                            int[] gold_features = sen.getFeatures(t, gold_prev2_tag, gold_prev_tag, featSize);

                            for (int f = 0; f < featSize; f++) {
                                int pfeat = predicted_features[f];
                                int gfeat = gold_features[f];

                                if (pfeat != -1)
                                    classifier.changeWeight(predicted, f, pfeat, -1);

                                if (gfeat != -1)
                                    classifier.changeWeight(gold, f, gfeat, +1);
                            }
                        }
                    }


                    int predictedPrevTag = 0;
                    int predicted_prev2_tag = 0;
                    int goldPrevTag = 0;
                    int gold_prev2_tag = 0;
                    int t = predicted_tags.length;

                    if (t > 0) {
                        predictedPrevTag = predicted_tags[t - 1];
                        goldPrevTag = sen.tags[t - 1];
                        if (t > 1) {
                            predicted_prev2_tag = predicted_tags[t - 2];
                            gold_prev2_tag = sen.tags[t - 2];
                        }
                    }

                    if (predictedPrevTag != goldPrevTag || predicted_prev2_tag != gold_prev2_tag) {
                        int[] predicted_features = sen.getFeatures(t, predicted_prev2_tag, predictedPrevTag, featSize);
                        int[] gold_features = sen.getFeatures(t, gold_prev2_tag, goldPrevTag, featSize);

                        for (int f = featSize - 2; f < featSize; f++) {
                            int pfeat = predicted_features[f];
                            int gfeat = gold_features[f];

                            if (pfeat != -1)
                                classifier.changeWeight(1, f, pfeat, -1);

                            if (gfeat != -1)
                                classifier.changeWeight(1, f, gfeat, +1);
                        }
                    }
                }

                classifier.incrementIteration();
            }
            float accuracy = (float) corr * 100.0f / all;
            System.out.print("\ntrain accuracy: " + accuracy + "\n");

            InfStruct info = new InfStruct(classifier,useBeamSearch,beamSize);
            System.out.print("saving the model...");
            saveModel(maps,info,modelPath+".iter_"+iter);
            System.out.print("done!\n");

            AveragedPerceptron perceptron = new AveragedPerceptron(info);
            System.out.print("\ndecoding...");
            corr = 0;
            all = 0;
            int exact = 0;

            long start = System.currentTimeMillis();

            for (int s = 0; s < dev_sentences.size(); s++) {
                Sentence sen = dev_sentences.get(s);
                if ((s + 1) % 1000 == 0)
                    System.out.print((s + 1) + " ");
                int[] predictedTags = Tagger.tag(sen, perceptron, true, useBeamSearch, beamSize);

                assert (predictedTags.length == sen.tags.length);

                boolean same = true;
                for (int t = 0; t < predictedTags.length; t++) {
                    int predicted = predictedTags[t];
                    int gold = sen.tags[t];
                    if (predicted != gold) {
                        same = false;
                    } else
                        corr++;
                    all++;
                }
                if (same)
                    exact++;
            }
            long end = System.currentTimeMillis();
            DecimalFormat format = new DecimalFormat("##.00");

            float duration = (float) (end - start) / dev_sentences.size();
            System.out.print("\nduration " + format.format(duration) + " ms per sentence\n");

            accuracy = (float) corr * 100.0f / all;
            float exact_match = (float) exact * 100.0f / dev_sentences.size();
            System.out.print("dev accuracy is " + format.format(accuracy) + "\n");
            System.out.print("dev exact match is " +  format.format(exact_match) + "\n");
        }
    }

    public static void saveModel(IndexMaps maps,InfStruct info,String modelPath) throws Exception{
        ObjectOutput writer = new ObjectOutputStream(new FileOutputStream(modelPath));
        writer.writeObject(info);
        writer.writeObject(maps);
        writer.flush();
        writer.close();
    }
}
