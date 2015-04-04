package SemiSupervisedPOSTagger.Structures;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/8/15
 * Time: 4:56 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */
public class IndexMaps  implements Serializable {
    public HashMap<String,Integer> stringMap;
    public String[] reversedMap;
    public final int tagSize;
    private   HashMap<Integer,Integer>[]  brownNClusters;
    private HashMap<String,Integer>  brownFullClusters;
    private HashMap<Integer,HashSet<Integer>> tagDictionary;

    public IndexMaps(int tagSize, HashMap<String, Integer> stringMap, String[] reversedMap,
                     HashMap<Integer,Integer>[]  brownNClusters,HashMap<String,Integer>  brownFullClusters,HashMap<Integer,HashSet<Integer>> tagDictionary) {
        this.tagSize = tagSize;
        this.stringMap = stringMap;
        this.reversedMap = reversedMap;
        this.brownNClusters=brownNClusters;
        this.brownFullClusters=brownFullClusters;
        this.tagDictionary=tagDictionary;
    }

    public int[] clusterIds(String word){
        int[] ids=new int[Sentence.brownSize+1];
        for(int i=0;i<Sentence.brownSize+1;i++)
            ids[i]=SpecialWords.unknown.value;
        
        if(brownFullClusters.containsKey(word))
            ids[0]=brownFullClusters.get(word);

        if(ids[0]>0){
            for(int j=1;j<Sentence.brownSize+1;j++)
                ids[j]=brownNClusters[j-1].get(ids[0]);
        }
        return ids;
    }

    public boolean hasClusters(){
        if(brownFullClusters!=null && brownFullClusters.size()>0)
            return true;
        return false;
    }

    public HashMap<Integer, HashSet<Integer>> getTagDictionary() {
        return tagDictionary;
    }
}
