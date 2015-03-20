package SemiSupervisedPOSTagger.Tagging;

import SemiSupervisedPOSTagger.Learning.AveragedPerceptron;
import SemiSupervisedPOSTagger.Structures.Pair;
import SemiSupervisedPOSTagger.Structures.Sentence;
import SemiSupervisedPOSTagger.Structures.SpecialWords;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/12/15
 * Time: 6:32 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class Viterbi {
    public static int[] thirdOrder(final Sentence sentence, final AveragedPerceptron perceptron, final boolean isDecode,final Tagger tagger) {
        int len = sentence.words.length + 1;

        float inf = Float.POSITIVE_INFINITY;

        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();
        // pai score values
        float pai[][][] = new float[len][tagSize][tagSize];
        float emissionScore[][] = new float[len - 1][tagSize];
        float bigramScore[][] = new float[tagSize][tagSize];
        float trigramScore[][][] = new float[tagSize][tagSize][tagSize];

        if (!isDecode) {
            for (int v = 0; v < tagSize; v++) {
                for (int u = 0; u < tagSize; u++) {
                    bigramScore[u][v] = perceptron.score(v, featSize - 3, u, isDecode);
                    for (int w = 0; w < tagSize; w++) {
                        int bigram = (w << 10) + u;
                        trigramScore[w][u][v] = perceptron.score(v, featSize - 2, bigram, isDecode);
                    }
                }
            }
        } else {
            bigramScore = tagger.bigramScore;
            trigramScore = tagger.trigramScore;
        }

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emissionScore[position][t] = perceptron.score(emissionFeatures, t, isDecode);
               // int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                if(cond!=-1)
                    emissionScore[position][t]+=perceptron.score(t,perceptron.featureSize()-1,cond ,isDecode) ;
            }
        }

        // back pointer
        int[][][] bp = new int[len][tagSize][tagSize];

        // initialization
        pai[0][0][0] = 0;
        for (int u = 1; u < tagSize; u++) {
            for (int v = 1; v < tagSize; v++) {
                pai[0][u][v] = -inf;
            }
        }

        for (int k = 1; k < len; k++) {
            for (int v = 2; v < tagSize; v++) {
                for (int u = 0; u < tagSize; u++) {
                    if (u == 1)
                        continue;
                    float max_val = -inf;
                    int argmax = 0;


                    for (int w = 0; w < tagSize; w++) {
                        if (w == 1 || (w == 0 && k > 1) || (k == 1 && w != 0))
                            continue;
                        float score = trigramScore[w][u][v] + bigramScore[u][v] + emissionScore[k - 1][v] + pai[k - 1][w][u];
                        if (score > max_val) {
                            max_val = score;
                            argmax = w;
                        }
                    }
                    pai[k][u][v] = max_val;
                    bp[k][u][v] = argmax;
                }
            }
        }

        int y1 = SpecialWords.start.value;
        int y2 = SpecialWords.start.value;
        float maxVal = -inf;
        if (sentence.words.length > 1) {
            for (int u = 2; u < tagSize; u++) {
                for (int v = 2; v < tagSize; v++) {
                    float score = bigramScore[v][1] + trigramScore[u][v][1] + pai[len - 1][u][v];
                    if (score > maxVal) {
                        maxVal = score;
                        y1 = u;
                        y2 = v;
                    }
                }
            }
        } else {
            for (int v = 2; v < tagSize; v++) {
                float score = bigramScore[v][SpecialWords.stop.value] + trigramScore[SpecialWords.start.value][v][SpecialWords.stop.value] + pai[len - 1][SpecialWords.start.value][v];
                if (score > maxVal) {
                    maxVal = score;
                    y2 = v;
                }
            }
        }

        int[] tags = new int[sentence.words.length];
        int index = sentence.words.length - 1;
        tags[index] = y2;
        index--;
        if (sentence.words.length > 1) {
            tags[index] = y1;
            index--;
        }
        for (int k = len - 3; k >= 1; k--) {
            int pr = bp[k + 2][tags[index + 1]][tags[index + 2]];
            tags[index] = pr;
            index--;
        }

        return tags;
    }

    public static Pair<int[],Float> thirdOrderWithScore(final Sentence sentence, final AveragedPerceptron perceptron, final boolean isDecode,final Tagger tagger) {
        int len = sentence.words.length + 1;

        float inf = Float.POSITIVE_INFINITY;

        int tagSize = perceptron.tagSize();
        int featSize = perceptron.featureSize();
        // pai score values
        float pai[][][] = new float[len][tagSize][tagSize];
        float emissionScore[][] = new float[len - 1][tagSize];
        float bigramScore[][] = new float[tagSize][tagSize];
        float trigramScore[][][] = new float[tagSize][tagSize][tagSize];

        if (!isDecode) {
            for (int v = 0; v < tagSize; v++) {
                for (int u = 0; u < tagSize; u++) {
                    bigramScore[u][v] = perceptron.score(v, featSize - 3, u, isDecode);
                    for (int w = 0; w < tagSize; w++) {
                        int bigram = (w << 10) + u;
                        trigramScore[w][u][v] = perceptron.score(v, featSize - 2, bigram, isDecode);
                    }
                }
            }
        } else {
            bigramScore = tagger.bigramScore;
            trigramScore = tagger.trigramScore;
        }

        for (int position = 0; position < sentence.words.length; position++) {
            int[] emissionFeatures = sentence.getEmissionFeatures(position, featSize);
            for (int t = 2; t < tagSize; t++) {
                emissionScore[position][t] = perceptron.score(emissionFeatures, t, isDecode);
              //  int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                int cond=perceptron.dictCondition(sentence.lowerWords[position],t);
                if(cond!=-1)
                    emissionScore[position][t]+=perceptron.score(t,perceptron.featureSize()-1,cond ,isDecode) ;
            }
        }

        // back pointer
        int[][][] bp = new int[len][tagSize][tagSize];

        // initialization
        pai[0][0][0] = 0;
        for (int u = 1; u < tagSize; u++) {
            for (int v = 1; v < tagSize; v++) {
                pai[0][u][v] = -inf;
            }
        }

        for (int k = 1; k < len; k++) {
            for (int v = 2; v < tagSize; v++) {
                for (int u = 0; u < tagSize; u++) {
                    if (u == 1)
                        continue;
                    float max_val = -inf;
                    int argmax = 0;


                    for (int w = 0; w < tagSize; w++) {
                        if (w == 1 || (w == 0 && k > 1) || (k == 1 && w != 0))
                            continue;
                        float score = trigramScore[w][u][v] + bigramScore[u][v] + emissionScore[k - 1][v] + pai[k - 1][w][u];
                        if (score > max_val) {
                            max_val = score;
                            argmax = w;
                        }
                    }
                    pai[k][u][v] = max_val;
                    bp[k][u][v] = argmax;
                }
            }
        }

        int y1 = SpecialWords.start.value;
        int y2 = SpecialWords.start.value;
        float maxVal = -inf;
        if (sentence.words.length > 1) {
            for (int u = 2; u < tagSize; u++) {
                for (int v = 2; v < tagSize; v++) {
                    float score = bigramScore[v][1] + trigramScore[u][v][1] + pai[len - 1][u][v];
                    if (score > maxVal) {
                        maxVal = score;
                        y1 = u;
                        y2 = v;
                    }
                }
            }
        } else {
            for (int v = 2; v < tagSize; v++) {
                float score = bigramScore[v][SpecialWords.stop.value] + trigramScore[SpecialWords.start.value][v][SpecialWords.stop.value] + pai[len - 1][SpecialWords.start.value][v];
                if (score > maxVal) {
                    maxVal = score;
                    y2 = v;
                }
            }
        }

        int[] tags = new int[sentence.words.length];
        int index = sentence.words.length - 1;
        tags[index] = y2;
        index--;
        if (sentence.words.length > 1) {
            tags[index] = y1;
            index--;
        }
        for (int k = len - 3; k >= 1; k--) {
            int pr = bp[k + 2][tags[index + 1]][tags[index + 2]];
            tags[index] = pr;
            index--;
        }

        return new Pair<int[], Float>(tags,maxVal);
    }

}
