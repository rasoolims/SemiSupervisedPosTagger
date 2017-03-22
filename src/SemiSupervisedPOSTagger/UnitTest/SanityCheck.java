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
        IndexMaps maps=FileManager.createIndexMaps(args[0], "_",args[1],"",Sentence.brownSize);
        ArrayList<Sentence> sentences= FileManager.readSentences(args[0],maps,"_");
        System.out.println("Read "+sentences.size()+" sentences.");
    }
}
