package SemiSupervisedPOSTagger.Tagging;

import SemiSupervisedPOSTagger.Learning.AveragedPerceptron;
import SemiSupervisedPOSTagger.Structures.IndexMaps;
import SemiSupervisedPOSTagger.Structures.InfoStruct;
import SemiSupervisedPOSTagger.Structures.Pair;
import SemiSupervisedPOSTagger.Structures.Sentence;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/13/15
 * Time: 12:41 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class Tagger {
    float bigramScore[][] ;
    float trigramScore[][][];
   public AveragedPerceptron perceptron;
    IndexMaps maps;
    public boolean useBeamSearch;
    public int beamSize;
    
    public Tagger(String modelPath) throws  Exception{
        System.out.print("loading the model...");
        FileInputStream fos = new FileInputStream(modelPath);
        GZIPInputStream gz = new GZIPInputStream(fos);
        ObjectInputStream modelReader = new ObjectInputStream(gz);
        
        InfoStruct info = (InfoStruct) modelReader.readObject();
        this.perceptron=new AveragedPerceptron(info);
        this.maps=(IndexMaps) modelReader.readObject();
        int tagSize=perceptron.tagSize();
        int featSize=perceptron.featureSize();

        bigramScore = new float[tagSize][tagSize];
        trigramScore = new float[tagSize][tagSize][tagSize];

        for (int v = 0; v < perceptron.tagSize(); v++) {
            for (int u = 0; u < perceptron.tagSize(); u++) {
                bigramScore[u][v] = perceptron.score(v, featSize -3, u, true);
                for (int w = 0; w < tagSize; w++) {
                    int bigram = (w << 10) + u;
                    trigramScore[w][u][v] = perceptron.score(v, featSize -2, bigram, true);
                }
            }
        }
        this.useBeamSearch=info.useBeamSearch;
        this.beamSize=info.beamSize;
        
        System.out.print("done!\n");
        if (!info.useBeamSearch)
            System.out.print("using Viterbi algorithm\n");
        else
            System.out.print("using beam search algorithm with beam size: " + info.beamSize + "\n");

    }
    
    public String[] tag(final String line,  final String delim, final boolean usePartialInfo) {
        Sentence sentence = new Sentence(line, maps, delim);
        int[] tags = tag(sentence, usePartialInfo);
        String[] output = new String[tags.length];
        for (int i = 0; i < tags.length; i++)
            output[i] = maps.reversedMap[tags[i]];
        return output;
    }

    public static int[] tag(final Sentence sentence, final AveragedPerceptron classifier, final boolean isDecode, final boolean useBeamSearch, final int beamSize, final boolean usePartialInfo) {
        return useBeamSearch ?
                BeamTagger.thirdOrder(sentence, classifier, isDecode,beamSize,usePartialInfo,null):Viterbi.thirdOrder(sentence, classifier, isDecode,null);
    }

    public  int[] tag(final Sentence sentence,  final boolean usePartialInfo) {
        return useBeamSearch ?
                BeamTagger.thirdOrder(sentence, perceptron, true,beamSize,usePartialInfo,this):Viterbi.thirdOrder(sentence, perceptron, true,this);
    }

    public  int[] tag(final Sentence sentence,  final boolean usePartialInfo, final boolean isDecode) {
        return useBeamSearch ?
                BeamTagger.thirdOrder(sentence, perceptron, isDecode,beamSize,usePartialInfo,this):Viterbi.thirdOrder(sentence, perceptron, isDecode,this);
    }

    public  Pair<int[],Float> tagWithScore(final Sentence sentence,  final boolean usePartialInfo) {
        return useBeamSearch ?
                BeamTagger.thirdOrderWithScore(sentence, perceptron, true, beamSize, usePartialInfo, this):Viterbi.thirdOrderWithScore(sentence, perceptron, true,this);
    }

    public void tag(final String inputPath, final String outputPath,final String delim, final String scoreFile)throws Exception{
        BufferedReader reader=new BufferedReader(new FileReader(inputPath));
        BufferedWriter writer=new BufferedWriter(new FileWriter(outputPath));

        boolean putScore=false;
        BufferedWriter scoreWriter=null;
        if(scoreFile!=null && !scoreFile.equals("")) {
            putScore = true;
            scoreWriter=new BufferedWriter(new FileWriter(scoreFile));
        }
        
        int ln=0;
        String line;
        while((line=reader.readLine())!=null){
            ln++;
            if(ln%1000==0)
                System.out.print(ln+"...");
            String[] flds=line.trim().split(" ");
            ArrayList<String> words=new ArrayList<String>(flds.length);
            for(int i=0;i<flds.length;i++){
                if(flds[i].trim().length()==0)
                    continue;
                words.add(flds[i].trim());
            }
            Sentence sentence=new Sentence(words,maps);
                      
            Pair<int[],Float> ts=tagWithScore(sentence,false);
            int[] t=ts.first;
            String[] tags = new String[t.length];
            for (int i = 0; i < tags.length; i++)
                tags[i] = maps.reversedMap[t[i]];
            
            StringBuilder output=new StringBuilder();
            for(int i=0;i<tags.length;i++){
                output.append(words.get(i)+delim+tags[i]+" ");
            }
            writer.write(output.toString().trim()+"\n");
            
            
            if(putScore) {
                float normalizedScore=  ts.second/tags.length;
                scoreWriter.write(normalizedScore+"\n");
            }
        }
        System.out.print(ln+"\n");
        writer.flush();
        writer.close();
        if(putScore) {
            scoreWriter.flush();
            scoreWriter.close();
        }
    }

    public ArrayList<Pair<String[],Float>> getPossibleTagReplacements(Sentence sentence){
      ArrayList<Pair<int[],Float>> repls=  BeamTagger.getPossibleTagsByOneReplacement(sentence, perceptron, beamSize, this);
        ArrayList<Pair<String[],Float>> replacements=new ArrayList<Pair<String[],Float>>();
        for(Pair<int[],Float> rpl:repls) {
            String[] tags=new String[rpl.first.length];
            for(int i=0;i<rpl.first.length;i++){
                tags[i]=maps.reversedMap[rpl.first[i]];
            }
            replacements.add(new Pair<String[], Float>(tags,rpl.second));
        }
        return replacements;
    }
    
    public  void partialTag( final String inputPath, final String outputPath,final String delim,String scoreFile)throws Exception{
        BufferedReader reader=new BufferedReader(new FileReader(inputPath));
        BufferedWriter writer=new BufferedWriter(new FileWriter(outputPath));

        boolean putScore=false;
        BufferedWriter scoreWriter=null;
        if(scoreFile!=null && !scoreFile.equals("")) {
            putScore = true;
            scoreWriter=new BufferedWriter(new FileWriter(scoreFile));
        }
        
        int ln=0;
        String line;
        while((line=reader.readLine())!=null){
            line=line.trim();
            if(line.length()==0){
                writer.write("\n");
                continue;
            }
            ln++;
            if(ln%1000==0)
                System.out.print(ln+"...");
            Sentence sentence=new Sentence(line,maps,delim);

            Pair<int[],Float> ts=tagWithScore(sentence,true);
            int[] t=ts.first;
            
            String[] tags = new String[t.length];
            for (int i = 0; i < tags.length; i++)
                tags[i] = maps.reversedMap[t[i]];

            StringBuilder output=new StringBuilder();
            for(int i=0;i<tags.length;i++){
                output.append(sentence.wordStrs[i]+delim+tags[i]+" ");
            }
            writer.write(output.toString().trim()+"\n");

            if(putScore) {
                float normalizedScore=  ts.second/tags.length;
                scoreWriter.write(normalizedScore+"\n");
            }
        }
        System.out.print(ln+"\n");
        writer.flush();
        writer.close();

        if(putScore) {
            scoreWriter.flush();
            scoreWriter.close();
        }
    }


    public IndexMaps getMaps() {
        return maps;
    }
}
