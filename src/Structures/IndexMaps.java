package Structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
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
    private HashMap<Integer,Integer>  brown4Clusters;
    private HashMap<Integer,Integer>  brown6Clusters;
    private HashMap<String,Integer>  brownFullClusters;

    public IndexMaps(int tagSize, HashMap<String, Integer> stringMap, String[] reversedMap,
                     HashMap<Integer,Integer>  brown4Clusters,HashMap<Integer,Integer>  brown6Clusters,HashMap<String,Integer>  brownFullClusters) {
        this.tagSize = tagSize;
        this.stringMap = stringMap;
        this.reversedMap = reversedMap;
        this.brown4Clusters=brown4Clusters;
        this.brown6Clusters=brown6Clusters;
        this.brownFullClusters=brownFullClusters;
    }

    public int[] clusterIds(String word){
        int[] ids=new int[3];
        ids[0]=SpecialWords.unknown.value;
        ids[1]=SpecialWords.unknown.value;
        ids[2]=SpecialWords.unknown.value;
        if(brownFullClusters.containsKey(word))
            ids[0]=brownFullClusters.get(word);

        if(ids[0]>0){
            ids[1]= brown4Clusters.get(ids[0]);
            ids[2]= brown6Clusters.get(ids[0]);
        }
        return ids;
    }

    public boolean hasClusters(){
        if(brownFullClusters!=null && brownFullClusters.size()>0)
            return true;
        return false;
    }
}
