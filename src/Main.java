import Structures.Options;
import Tagging.Tagger;
import Training.Trainer;

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


        if (options.train && options.trainPath != "" && options.modelPath != "")
            Trainer.train(options, 18);
        else if (options.tag && options.inputPath != "" && options.modelPath != "" && options.outputPath != "")
            Tagger.tag(options.modelPath, options.inputPath, options.outputPath, options.delim);
        else if (options.tagPartial && options.inputPath != "" && options.modelPath != "" && options.outputPath != "")
            Tagger.partialTag(options.modelPath, options.inputPath, options.outputPath, options.delim);
        else
            System.out.println(options.showHelp());
    }
}
