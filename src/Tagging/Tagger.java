package Tagging;

import Learning.AveragedPerceptron;
import Structures.IndexMaps;
import Structures.InfStruct;
import Structures.Sentence;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 12:41 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class Tagger {
    public static String[] tag(final String line, final IndexMaps maps, final AveragedPerceptron classifier, final boolean isDecode, final String delim, final boolean useBeamSearch, final int beamSize) {
        Sentence sentence = new Sentence(line, maps, delim);
        return tag(sentence, maps, classifier, isDecode, useBeamSearch, beamSize);
    }

    public static String[] tag(final Sentence sentence, final IndexMaps maps, final AveragedPerceptron classifier, final boolean isDecode, final boolean useBeamSearch, final int beamSize) {
        int[] tags = tag(sentence, classifier, isDecode, useBeamSearch, beamSize);
        String[] output = new String[tags.length];
        for (int i = 0; i < tags.length; i++)
            output[i] = maps.reversedMap[tags[i]];
        return output;
    }

    public static int[] tag(final Sentence sentence, final AveragedPerceptron classifier, final boolean isDecode, final boolean useBeamSearch, final int beamSize) {
        //todo write beam search
        return useBeamSearch ?
                BeamTagger.thirdOrder(sentence, classifier, isDecode,beamSize):Viterbi.thirdOrder(sentence, classifier, isDecode);
    }

    public static void tag(final String modelPath, final String inputPath, final String outputPath,final String delim)throws Exception{
        BufferedReader reader=new BufferedReader(new FileReader(inputPath));
        BufferedWriter writer=new BufferedWriter(new FileWriter(outputPath));
        System.out.print("loading the model...");
        ObjectInput modelReader = new ObjectInputStream(new FileInputStream(modelPath));
        InfStruct info=(InfStruct) modelReader.readObject();
        AveragedPerceptron perceptron=new AveragedPerceptron(info);
        IndexMaps maps=(IndexMaps) modelReader.readObject();
        System.out.print("done!\n");

        int ln=0;
        String line;
        while((line=reader.readLine())!=null){
            ln++;
            if(ln%1000==0)
                System.out.print(ln+"...");
            String[] flds=line.trim().split(" ");
            ArrayList<String> words=new ArrayList<String>(flds.length);
            for(int i=0;i<flds.length;i++){
                if(flds[i].length()==0)
                    continue;
                words.add(flds[i]);
            }
            Sentence sentence=new Sentence(words,maps);

            String[] tags=tag(sentence,maps,perceptron,true,info.useBeamSearch,info.beamSize);

            StringBuilder output=new StringBuilder();
            for(int i=0;i<tags.length;i++){
                output.append(words.get(i)+delim+tags[i]+" ");
            }
            writer.write(output.toString().trim()+"\n");
        }
        System.out.print(ln+"\n");
        writer.flush();
        writer.close();
    }
}
