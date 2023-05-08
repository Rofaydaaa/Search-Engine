package Indexer;

import java.util.Vector;

public class WordToSearch {
    public String word;
    //Document frequency (# of documents that contains this word)
    public int df;
    //array of documents containing the word
    public Vector<WordData> data = new Vector<>();
}

//word df{D1:count, url, popularity, lengthOfdocument, filepath, D2:    , D3: }