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
        String trainPath = "/Users/msr/Projects/SemiSupervisedPosTagger/SemiSupervisedPosTagger/SemiSupervisedPosTagger/sample_file/train.tag";
        String devPath = "/Users/msr/Projects/SemiSupervisedPosTagger/SemiSupervisedPosTagger/SemiSupervisedPosTagger/sample_file/dev.tag";
        String modelPath = "/tmp/model";
        if (args.length > 2) {
            trainPath = args[0];
            devPath = args[1];
            modelPath = args[2];
        }
        System.out.println("train_path: " + trainPath);
        System.out.println("dev_path: " + devPath);
        System.out.println("model_path: " + modelPath);
        Trainer.train(trainPath,
                devPath, modelPath, 18, "_", 20, false, 0);
    }

}
