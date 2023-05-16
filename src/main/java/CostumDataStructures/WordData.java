package CostumDataStructures;

import org.bson.Document;
import java.util.HashMap;
import java.util.Map;

//This class contains the data of a word in a specific document
public class WordData {
    public int count = 0;
    public String url;
    public double popularity;
    public int lengthOfDoc;
    public String filepath;

    //For the same document, a word can appear multiple time in different location.
    //For the same word in the document, we will make a hashmap with keys for all the different positions
    //Starting from the title, h1, h2,......,body
    public Map<String, Integer> position= new HashMap<String, Integer>();

    public WordData(){
        position.put("title" , 0);
        position.put("h1" , 0);
        position.put("h2" , 0);
        position.put("h3" , 0);
        position.put("h4" , 0);
        position.put("h5" , 0);
        position.put("h6" , 0);
        position.put("body" , 0);
    }
}
