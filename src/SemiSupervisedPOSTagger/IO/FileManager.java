package SemiSupervisedPOSTagger.IO;

import SemiSupervisedPOSTagger.Structures.IndexMaps;
import SemiSupervisedPOSTagger.Structures.Sentence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/8/15
 * Time: 6:50 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class FileManager {
    public static ArrayList<Sentence> readSentences(String filePath, IndexMaps maps,String delim) throws Exception{
        System.out.print("reading sentences...");
        BufferedReader reader=new BufferedReader(new FileReader(filePath));
        String line;
        ArrayList<Sentence> sentences=new ArrayList<Sentence>();
        while((line=reader.readLine())!=null){
            if(line.trim().length()>0)
                sentences.add(new Sentence(line,maps,delim));
        }
        System.out.print("done!\n");
        return sentences;
    }

    public static IndexMaps createIndexMaps(String filePath, String delim,String clusterFile,String tagDictionaryPath) throws Exception{
        System.out.print("creating index maps...");
        HashMap<String, Integer> stringMap=new HashMap<String, Integer>();
        HashMap<String,Integer> clusterMap=new HashMap<String, Integer>();
        HashMap<Integer,Integer> cluster4Map=new HashMap<Integer, Integer>();
        HashMap<Integer,Integer> cluster6Map=new HashMap<Integer, Integer>();

        BufferedReader reader=new BufferedReader(new FileReader(filePath));

        HashSet<String> tags = new HashSet<String>();
        HashSet<String> words=new HashSet<String>();

        String line;
        while((line=reader.readLine())!=null){
            String[] split=line.trim().split(" ");
            for(int i=0;i<split.length;i++){
                if(split[i].contains(delim)){
                    int delimIndex=split[i].lastIndexOf(delim);
                    String word=split[i].substring(0,delimIndex);

                    for(int p=0;p<Math.min(4,word.length());p++){
                        String prefix=word.substring(0,p+1);
                        String suffix=word.substring(word.length()-p-1);
                        words.add(prefix);
                        words.add(suffix);
                    }

                    String tag=split[i].substring(delimIndex+1);
                    tags.add(tag);
                    words.add(word);
                }
            }
        }

        // 0 and 1 are reserved for stop and start
        int index=2;
        stringMap.put("<<START>>",0);
        stringMap.put("<<STOP>>", 1);

        for(String t:tags){
            stringMap.put(t,index++);
        }

        if(clusterFile.length()>0){
            reader = new BufferedReader(new FileReader(clusterFile));
            while ((line = reader.readLine()) != null) {
                String[] spl = line.trim().split("\t");
                if (spl.length > 2) {
                    String cluster = spl[0];
                    String word=spl[1];
                    String prefix4=cluster.substring(0,Math.min(4,cluster.length()));
                    String prefix6=cluster.substring(0,Math.min(6,cluster.length()));
                    int clusterNum=index;

                    if (!stringMap.containsKey(cluster)) {
                        clusterMap.put(word,index);
                        stringMap.put(cluster, index++);
                    }else{
                        clusterNum= stringMap.get(cluster);
                        clusterMap.put(word,clusterNum);
                    }

                    int pref4Id=index;
                    if (!stringMap.containsKey(prefix4)) {
                        stringMap.put(prefix4, index++);
                    }  else{
                        pref4Id=stringMap.get(prefix4);
                    }

                    int pref6Id=index;
                    if (!stringMap.containsKey(prefix6)) {
                        stringMap.put(prefix6, index++);
                    }else{
                        pref6Id=stringMap.get(prefix6);
                    }

                    cluster4Map.put(clusterNum,pref4Id);
                    cluster6Map.put(clusterNum,pref6Id);
                }
            }
        }
        
        for(String w:words){
            if(!stringMap.containsKey(w))
                stringMap.put(w,index++);
            if(!stringMap.containsKey(w.toLowerCase()))
               stringMap.put(w.toLowerCase(),index++);
        }

        System.out.println(stringMap.size());
        String[] reversedMap=new String[stringMap.size()];
        for(String k:stringMap.keySet()){
            reversedMap[stringMap.get(k)]=k;
        }

        int tagSize=tags.size()+2;
        System.out.print("done!\n");

        HashMap<Integer,HashSet<Integer>> tagDictionary=new HashMap<Integer, HashSet<Integer>>();
        if(tagDictionaryPath!=null && !tagDictionaryPath.equals("")) {
            BufferedReader tagDictionaryReader = new BufferedReader(new FileReader(tagDictionaryPath));
            while ((line = tagDictionaryReader.readLine()) != null) {
                String[] spl = line.split("\t");
                if (spl.length == 2) {
                    String w = spl[0].toLowerCase();
                    String t = spl[1];

                    if (!stringMap.containsKey(w))
                        stringMap.put(w, index++);
                    if (!stringMap.containsKey(t))
                        stringMap.put(t, index++);

                    int wi = stringMap.get(w);
                    int ti = stringMap.get(t);

                    if (!tagDictionary.containsKey(wi))
                        tagDictionary.put(wi, new HashSet<Integer>());
                    tagDictionary.get(wi).add(ti);
                }
            }
        }
        
        return  new IndexMaps(tagSize,stringMap,reversedMap,cluster4Map,cluster6Map,clusterMap,tagDictionary);
    }
}
