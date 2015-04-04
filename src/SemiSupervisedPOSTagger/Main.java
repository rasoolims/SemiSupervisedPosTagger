package SemiSupervisedPOSTagger;

import SemiSupervisedPOSTagger.Structures.Options;
import SemiSupervisedPOSTagger.Tagging.Tagger;
import SemiSupervisedPOSTagger.Training.Trainer;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 1:07 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Options options = new Options(args);

        System.out.println(options);

      //  options.train=true;
       // options.trainPath="/tmp/en2de.tag.projection.full";
      //  options.modelPath="/tmp/model";
        
        if (options.train && options.trainPath != "" && options.modelPath != "")
            Trainer.train(options, 32,options.tagDictionaryPath);
        else if (options.tag && options.inputPath != "" && options.modelPath != "" && options.outputPath != "") {
            Tagger tagger=new Tagger(options.modelPath);
            tagger.tag( options.inputPath, options.outputPath, options.delim,options.scoreFile);
        }
        else if (options.tagPartial && options.inputPath != "" && options.modelPath != "" && options.outputPath != "") {
            Tagger tagger=new Tagger(options.modelPath);
            tagger.partialTag( options.inputPath, options.outputPath, options.delim,options.scoreFile);
        }
        else
            System.out.println(options.showHelp());
    }
}
