package IO;

import Structures.IndexMaps;
import Structures.Sentence;

import java.io.BufferedReader;
import java.io.FileReader;
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

    public static IndexMaps createIndexMaps(String filePath, String delim) throws Exception{
        System.out.print("creating index maps...");
        HashMap<String, Integer> stringMap=new HashMap<String, Integer>();

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
        for(String w:words){
            if(!tags.contains(w))
                stringMap.put(w,index++);
        }

        String[] reversedMap=new String[stringMap.size()];
        for(String k:stringMap.keySet()){
            reversedMap[stringMap.get(k)]=k;
        }

        int tagSize=tags.size()+2;
        System.out.print("done!\n");

        return  new IndexMaps(tagSize,stringMap,reversedMap);
    }
}
