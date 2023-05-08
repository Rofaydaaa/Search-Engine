package Indexer;

import java.util.HashMap;
import java.util.Map;

//This class contains the data of a word in a specific document
public class WordData {
    public int count = 0;
    public String url;
    public double popularity;
    public int lengthOfDoc;
    public String filepath;
    public Map<String, Integer> position= new HashMap<String, Integer>();
}
