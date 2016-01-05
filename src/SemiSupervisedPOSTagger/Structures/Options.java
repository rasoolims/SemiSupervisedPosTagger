package SemiSupervisedPOSTagger.Structures;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 11:10 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class Options {
    public boolean useBeamSearch;
    public boolean train;
    public boolean tag;
    public boolean tagPartial;
    public int beamWidth;
    public int trainingIter;
    public String delim;
    public String modelPath;
    public String trainPath;
    public String devPath;
    public String inputPath;
    public String outputPath;
    public String clusterFile;
    public String scoreFile;
    public UpdateMode updateMode;
    public String tagDictionaryPath;
    public double C;

    public Options() {
        this.useBeamSearch = true;
        train = false;
        tag=false;
        tagPartial=false;
        beamWidth = 20;
        trainingIter = 20;
        delim="_";
        modelPath="";
        clusterFile="";
        trainPath="";
        devPath="";
        inputPath="";
        outputPath="";
        tagDictionaryPath="";
        updateMode = UpdateMode.maxViolation;
        C= -10;
    }

    public Options(String[] args){
        this();
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-viterbi"))
                useBeamSearch=false;
            if(args[i].startsWith("beam:"))
                beamWidth = Integer.parseInt(args[i].substring(args[i].indexOf("beam:")+5));
            if(args[i].startsWith("iter:"))
                trainingIter = Integer.parseInt(args[i].substring(args[i].indexOf("iter:")+5));
            if(args[i].equals("train"))
                train=true;
            if(args[i].equals("tag"))
                tag=true;
            if(args[i].equals("partial_tag"))
                tagPartial=true;
            if(args[i].equals("-model") && i<args.length-1)
                modelPath=args[i+1];
            if(args[i].equals("-input") && i<args.length-1) {
                trainPath = args[i + 1];
                inputPath = args[i + 1];
            }
            if(args[i].equals("-c") && i<args.length-1)
                if(args[i+1].equals("inf"))
                    C=Double.POSITIVE_INFINITY;
            else
                C=Double.parseDouble(args[i + 1]);
            if(args[i].equals("-output") && i<args.length-1)
                outputPath = args[i + 1];
            if(args[i].equals("-cluster") && i<args.length-1)
                clusterFile = args[i + 1];
            if(args[i].equals("-dict") && i<args.length-1)
                tagDictionaryPath = args[i + 1];
            if(args[i].equals("-score") && i<args.length-1)
                scoreFile = args[i + 1];
            if(args[i].equals("-dev") && i<args.length-1)
                devPath = args[i + 1];
            if(args[i].equals("-delim") && i<args.length-1)
                delim = args[i + 1];
            if (args[i].equals("-update:max_viol"))
                updateMode = UpdateMode.maxViolation;
            if (args[i].equals("-update:early"))
                updateMode = UpdateMode.early;
            if (args[i].equals("-update:standard"))
                updateMode = UpdateMode.standard;
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (train) {
            output.append("train iterations: " + trainingIter + "\n");
            output.append("train file: " + trainPath + "\n");
            output.append("model file: " + modelPath + "\n");
            output.append("C: " + C + "\n");
            output.append("dev file: " + devPath + "\n");
            output.append("cluster file: " + clusterFile + "\n");
            output.append("tag dict file: " + tagDictionaryPath + "\n");
            if (!useBeamSearch)
                output.append("using Viterbi algorithm\n");
            else {
                output.append("using beam search algorithm with beam size:" + beamWidth + " with " + updateMode + "\n");
            }
        } else if (tag || tagPartial) {
            output.append("input file: " + inputPath + "\n");
            output.append("output file: " + outputPath + "\n");
            output.append("model file: " + modelPath + "\n");
            output.append("tag dict file: " + tagDictionaryPath + "\n");
            output.append("score file: " + scoreFile + "\n");
        }
        return output.toString();
    }

    public String showHelp(){
        StringBuilder output = new StringBuilder();
        output.append("* Train a tagger:\n");
        output.append(">>  java -jar SemiSupervisedTagger.jar train -input [input-file] -model [model-file]\n");
        output.append("** Other Options:\n");
        output.append("     -dev [dev-file]  dev file address\n");
        output.append("     -cluster [cluster-file]  brown cluster file address\n");
        output.append("     -dict [tag-dict-file]  tag dictionary file address\n");
        output.append("     -c [c-value]  default:-10; put inf for infinity and negative value for original perceptron update\n");
        output.append("     -viterbi   if you want to use Viterbi decoding (default: beam decoding)\n");
        output.append("     -update:[mode]  for beam training; three #modes: max_viol, early, standard (default: max_viol)\n");
        output.append("     -delim [delim]   put delimiter string in [delim] for word tag separator (default _) e.g. -delim / \n");
        output.append("     beam:[#b]  put a number [#b] for beam size (default:5); e.g. beam:10\n");
        output.append("     iter:[#i]  put a number [#i] for training iterations (default:20); e.g. iter:10\n");
        output.append("\nNOTE: in every iteration the model file for that iteration will have the format [model-file].iter_#iter e.g. model.iter_3");
        output.append("\n\n");

        output.append("* Tag a file:\n");
        output.append(">>  java -jar SemiSupervisedTagger.jar tag -input [input-file] -model [model-file] -output [output-file]\n");
        output.append("** Other Options:\n");
        output.append("     -score [score-file]   score file path\n");
        output.append("     -delim [delim]   put delimiter string in [delim] for word tag separator (default _) e.g. -delim / \n");
        output.append("\n\n");

        output.append("* Tag a partially tagged file:\n");
        output.append(">>  java -jar SemiSupervisedTagger.jar partial_tag -input [input-file] -model [model-file] -output [output-file]\n");
        output.append("** For words with no tag information, put *** as the tag; e.g. After_IN our_*** discussion_*** ._.\n");
        output.append("** Other Options:\n");
        output.append("     -score [score-file]   score file path\n");
        output.append("     -delim [delim]   put delimiter string in [delim] for word tag separator (default _) e.g. -delim / \n");
        output.append("\n\n");

        return output.toString();
    }
}
