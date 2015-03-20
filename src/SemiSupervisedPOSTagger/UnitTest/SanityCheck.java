package SemiSupervisedPOSTagger.UnitTest;

import SemiSupervisedPOSTagger.IO.FileManager;
import SemiSupervisedPOSTagger.Structures.IndexMaps;
import SemiSupervisedPOSTagger.Structures.Sentence;

import java.util.ArrayList;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/8/15
 * Time: 6:16 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class SanityCheck {
    public static void main(String[] args) throws Exception{
        IndexMaps maps=FileManager.createIndexMaps("/Users/msr/Projects/SemiSupervisedPosTagger/SemiSupervisedPosTagger/SemiSupervisedPosTagger/sample_file/train.tag", "_"
        ,"/Users/msr/Desktop/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.txt","");

      ArrayList<Sentence> sentences= FileManager.readSentences("/Users/msr/Projects/SemiSupervisedPosTagger/SemiSupervisedPosTagger/SemiSupervisedPosTagger/sample_file/train.tag",maps,"_");
    }
}
