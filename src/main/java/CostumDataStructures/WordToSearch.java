package CostumDataStructures;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class WordToSearch {
    public String word;
    //Document frequency (# of documents that contains this word)
    public int df;
    //array of documents containing the word
    public Vector<WordData> data = new Vector<>();

    // url          worddata
    public Map<String, WordData> dataMap = new HashMap<>();
}

//word df {D1:count, url, popularity, lengthOfDocument, filepath, D2:    , D3: }
//word df{D1:count, url, popularity, lengthOfDocument, filepath, D2:    , D3: }

