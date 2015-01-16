package Structures;

import java.util.ArrayList;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/8/15
 * Time: 4:37 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class Sentence {
    public int[] words;
    public String[] wordStrs;
    public int[] tags;

    public int[][] prefixes;
    public int[][] suffixes;

    public boolean[] containsNumber;
    public boolean[] containsHyphen;
    public boolean[] containsUpperCaseLetter;

    public Sentence (final String line,final IndexMaps maps,final String delim){
        String[] split=line.trim().split(" ");
        ArrayList<String> words=new ArrayList<String>();
        ArrayList<String> tags=new ArrayList<String>();
        try {
            for (int i = 0; i < split.length; i++) {
                int index = split[i].lastIndexOf(delim);
                words.add(split[i].substring(0, index));
                tags.add(split[i].substring(index + 1));
            }
        }catch (Exception ex){
            System.out.print("HERE!");
        }
        this.words=new int[words.size()];
        this.tags=new int[tags.size()];
        this.wordStrs=new String[words.size()];
        prefixes=new int[words.size()][4];
        suffixes=new int[words.size()][4];
        containsNumber=new boolean[words.size()];
        containsHyphen=new boolean[words.size()];
        containsUpperCaseLetter=new boolean[words.size()];

        for(int i=0;i<words.size();i++){
            String word=words.get(i);
            this.wordStrs[i]=word;
            String lowerWord=word.toLowerCase();
            if(maps.stringMap.containsKey(word))
                this.words[i]=  maps.stringMap.get(word);
            else
                this.words[i]=SpecialWords.unknown.value;

            for(int p=0;p<Math.min(4,word.length());p++) {
                String prefix = lowerWord.substring(0, p + 1);
                String suffix = lowerWord.substring(word.length() - p - 1);

                if(maps.stringMap.containsKey(prefix))
                    prefixes[i][p]=maps.stringMap.get(prefix);
                else
                    prefixes[i][p]=SpecialWords.unknown.value;

                if(maps.stringMap.containsKey(suffix))
                    suffixes[i][p]=maps.stringMap.get(suffix);
                else
                    suffixes[i][p]=SpecialWords.unknown.value;
            }
            if(word.length()<4){
                for(int p=word.length();p<4;p++){
                    prefixes[i][p]=SpecialWords.unknown.value;
                    suffixes[i][p]=SpecialWords.unknown.value;
                }
            }

            boolean hasUpperCase=false;
            boolean hasHyphen=false;
            boolean hasNumber=false;
            for(char c:word.toCharArray()){
                if(!hasUpperCase && Character.isUpperCase(c))
                    hasUpperCase=true;
                if(!hasHyphen &&  c=='-')
                    hasHyphen=true;
                if(!hasNumber && Character.isDigit(c))
                    hasNumber=true;
                if(hasHyphen && hasNumber && hasUpperCase)
                    break;
            }

            containsHyphen[i]=hasHyphen;
            containsNumber[i]=hasNumber;
            containsUpperCaseLetter[i]=hasUpperCase;

            if (tags.get(i).equals("***"))//for unknown tag
                this.tags[i]=SpecialWords.unknown.value;
            if(maps.stringMap.containsKey(tags.get(i)))
                this.tags[i]=  maps.stringMap.get(tags.get(i));
            else
                this.tags[i]=SpecialWords.unknown.value;
        }
    }

    public Sentence(ArrayList<String> words,IndexMaps maps){
        this.words=new int[words.size()];
        this.tags=new int[words.size()];
        prefixes=new int[words.size()][4];
        suffixes=new int[words.size()][4];
        containsNumber=new boolean[words.size()];
        containsHyphen=new boolean[words.size()];
        containsUpperCaseLetter=new boolean[words.size()];

        for(int i=0;i<words.size();i++){
            String word=words.get(i);
            if(maps.stringMap.containsKey(word))
                this.words[i]=  maps.stringMap.get(word);
            else
                this.words[i]=SpecialWords.unknown.value;

            for(int p=0;p<Math.min(4,word.length());p++) {
                String prefix = word.substring(0, p + 1);
                String suffix = word.substring(word.length() - p - 1);

                if(maps.stringMap.containsKey(prefix))
                    prefixes[i][p]=maps.stringMap.get(prefix);
                else
                    prefixes[i][p]=SpecialWords.unknown.value;

                if(maps.stringMap.containsKey(suffix))
                    suffixes[i][p]=maps.stringMap.get(suffix);
                else
                    suffixes[i][p]=SpecialWords.unknown.value;
            }
            if(word.length()<4){
                for(int p=word.length();p<4;p++){
                    prefixes[i][p]=SpecialWords.unknown.value;
                    suffixes[i][p]=SpecialWords.unknown.value;
                }
            }

            boolean hasUpperCase=false;
            boolean hasHyphen=false;
            boolean hasNumber=false;

            for(char c:word.toCharArray()){
                if(!hasUpperCase && Character.isUpperCase(c))
                    hasUpperCase=true;
                if(!hasHyphen &&  c=='-')
                    hasHyphen=true;
                if(!hasNumber && Character.isDigit(c))
                    hasNumber=true;
                if(hasHyphen && hasNumber && hasUpperCase)
                    break;
            }

            containsHyphen[i]=hasHyphen;
            containsNumber[i]=hasNumber;
            containsUpperCaseLetter[i]=hasUpperCase;
            this.tags[i]=SpecialWords.unknown.value;
        }
    }

    public int[] getEmissionFeatures(final int position, final int featSize){
        int[] features=new int[featSize];
        int index=0;
        int length=words.length;

        int currentWord=0;
        if(position>=0 && position<length)
            currentWord=words[position];
        else if(position>=length)
            currentWord=1;

        features[index++]=currentWord;

        if(position>=0 && position<length){
            for(int i=0;i<4;i++){
                features[index++]=prefixes[position][i];
                features[index++]=suffixes[position][i];
            }
            features[index++]=(containsHyphen[position])?1:SpecialWords.unknown.value;
            features[index++]=(containsNumber[position])?1:SpecialWords.unknown.value;
            features[index++]=(containsUpperCaseLetter[position])?1:SpecialWords.unknown.value;

        }  else{
            for(int i=0;i<11;i++){
                features[index++]=SpecialWords.unknown.value;
            }
        }

        int prevWord=SpecialWords.start.value;
        int prev2Word=SpecialWords.start.value;
        int nextWord=SpecialWords.stop.value;
        int next2Word=SpecialWords.stop.value;

        int prevPosition=position-1;
        if(prevPosition>=0){
            prevWord=words[prevPosition];
            int prev2Position=prevPosition-1;
            if(prev2Position>=0)
                prev2Word=words[prev2Position];
        }

        int nextPosition=position+1;
        if(nextPosition<length){
            nextWord=words[nextPosition];
            int next2Position=nextPosition+1;
            if(next2Position<length)
                next2Word=words[next2Position];
        }

        features[index++]=prevWord;
        features[index++]=prev2Word;
        features[index++]=nextWord;
        features[index++]=next2Word;

        return  features;

    }

    public int[] getFeatures(final int position, final int prev2Tag, final int prevTag, final int featSize){
        int[] features=getEmissionFeatures(position,featSize);
        int index=featSize-2;

        features[index++]=prevTag;

        int bigram=  (prev2Tag<<10) +  prevTag;
        features[index++]=bigram;

        return  features;
    }

}
