package Structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 1/8/15
 * Time: 4:56 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */
public class IndexMaps {
    public HashMap<String,Integer> stringMap;
    public String[] reversedMap;
    public final int tagSize;

    public IndexMaps(int tagSize, HashMap<String, Integer> stringMap, String[] reversedMap) {
        this.tagSize = tagSize;
        this.stringMap = stringMap;
        this.reversedMap = reversedMap;
    }
}
