package Indexer;

import CostumDataStructures.*;
import DataBase.DataBaseManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//https://javarevisited.blogspot.com/2014/09/how-to-parse-html-file-in-java-jsoup-example.html#axzz81fybWspY
public class Indexer {

    //url data to be indexed
    URLData currentUrlData;
    Document currentDoc;
    DataBaseManager dbManager;
    Trie stoppingWord;
    String stoppingWordDictionaryFilePath;
    //To store all words in a document
    // "apple"-> {"word" = "apple", "df" = count, "data"= vector<WordData>}
    //List and maps for text storage
    //to store all the word with it data structure
    Map<String,WordToSearch> documentDataMap = new HashMap<>();
    //list to store title Words
    List<String> titleWords;
    //list of lists to store headers words from <h1> -----> <h6>, every row represent a header
    List<List<String>> headerWords;
    //list to store the body words
    List<String> bodyWords;


    int lengthOfDocument;
    Indexer(URLData urlD, DataBaseManager db){

        //build stopping word tries for fast search
        buildStoppingWord();

        //assign data members
        this.dbManager = db;
        this.currentUrlData = urlD;
    }

    //This function download and parse the html document from the internet
    public void parseDocumentFromInternet(){
        try {
            currentDoc = Jsoup.connect(this.currentUrlData.URL).get();
        } catch (IOException e) { e.printStackTrace(); }
    }
    //This function parse the html document from the local file system
    public void parseDocumentFromLocalFile() {
        String filePath = this.currentUrlData.FilePath;

        try {
            currentDoc = Jsoup.parse(new File(this.currentUrlData.FilePath), "ISO-8859-1");
        } catch (IOException e) { e.printStackTrace();}
    }

    public void extractData(){

    }
    public void buildStoppingWord(){
        stoppingWord = new Trie();
        stoppingWordDictionaryFilePath = "stoppingWords.txt";

        try (FileReader fileReader = new FileReader(stoppingWordDictionaryFilePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String word;
            while ((word = bufferedReader.readLine()) != null) {
                stoppingWord.insert(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
