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
        String trainPath = "/Users/msr/Desktop/wsj_tagging_data/dev.pos";
        String devPath = "/Users/msr/Desktop/wsj_tagging_data/test.pos";
        String modelPath = "/tmp/model";
        boolean useBeamSearch=false;
        int beamSize=0;
        if (args.length > 2) {
            trainPath = args[0];
            devPath = args[1];
            modelPath = args[2];
            if(args.length>3){
                useBeamSearch=true;
                beamSize=Integer.parseInt(args[3]);
            }
        }
        System.out.println("train_path: " + trainPath);
        System.out.println("dev_path: " + devPath);
        System.out.println("model_path: " + modelPath);
        System.out.println("use_beam_search: " + useBeamSearch);
        System.out.println("beam_size: " + beamSize);
        Trainer.train(trainPath,
                devPath, modelPath, 18, "_", 20, useBeamSearch, beamSize);
    }

}
