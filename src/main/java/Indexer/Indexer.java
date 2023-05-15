package Indexer;

import CostumDataStructures.*;
import DataBase.DataBaseManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

import opennlp.tools.stemmer.PorterStemmer;
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
    List<String> paragraphWords;

    PorterStemmer stemmer;

    int lengthOfDocument;
    Indexer(URLData urlD, DataBaseManager db){

        //build stopping word tries for fast search
        buildStoppingWord();

        //assign data members
        this.dbManager = db;
        this.currentUrlData = urlD;

        this.stemmer = new PorterStemmer();
        lengthOfDocument = 0;
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
            currentDoc = Jsoup.parse(new File(filePath), "UTF-8");
        } catch (IOException e) { e.printStackTrace();}
    }

    public void extractData(){

        extractDataTitle();
        extractDataHeader();
        extractDataParagraph();
    }
    

    public void extractDataTitle(){
        String titleFullString = currentDoc.title();
        titleFullString = dataPreProcessingForString(titleFullString);
        List<String> tempTitleWords = new ArrayList<>(Arrays.asList(titleFullString.split(" ")));
        titleWords = removeStoppingWord(tempTitleWords);
        lengthOfDocument += titleWords.size();
    }
    public void extractDataHeader(){
        headerWords = new ArrayList<List<String>>();
        Elements docBodyElements = currentDoc.body().getAllElements();
        //List for every Header from h1 to h6
        for(int i = 0 ; i < 6 ; i++)
        {
            headerWords.add(new ArrayList<String>());
            //extract text from header element
            Elements headerElements = docBodyElements.select("h"+(i+1));

            headerWords.set(i, removeStoppingWord(dataPreProcessingForListOfString(headerElements.eachText())));
            lengthOfDocument += headerWords.get(i).size();
        }
    }
    public void extractDataParagraph(){
        paragraphWords = new ArrayList<>();
        Elements docBodyElements = currentDoc.body().getAllElements();
        paragraphWords.addAll(removeStoppingWord(dataPreProcessingForListOfString(docBodyElements.select("p").eachText())));
        paragraphWords.addAll(removeStoppingWord(dataPreProcessingForListOfString(docBodyElements.select("span").eachText())));
        paragraphWords.addAll(removeStoppingWord(dataPreProcessingForListOfString(docBodyElements.select("li").eachText())));
        paragraphWords.addAll(removeStoppingWord(dataPreProcessingForListOfString(docBodyElements.select("dt").eachText())));
        lengthOfDocument += paragraphWords.size();
    }
    public List<String> dataPreProcessingForListOfString(List<String> listBeforePreProcessing){
        List<String> preprocessedWords = new ArrayList<>();
        for(String fullString : listBeforePreProcessing){
            String preprocessedWord = dataPreProcessingForString(fullString);
            preprocessedWords.addAll(Arrays.asList(preprocessedWord.split(" ")));
        }
        return preprocessedWords;
    }
    public String dataPreProcessingForString(String s){
        // \W, is equivalent to [^a-zA-Z0-9] regular expression
        // replace any non-word character with spaces
        s = s.replaceAll("\\W", " ");

        //remove first and last spaces, remove any leading spaces
        s = s.trim().replaceAll(" +", " ");

        //convert the word to lower case;
        return s.toLowerCase();
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

    public boolean isStoppingWord(String s){
        return stoppingWord.search(s);
    }

    public List<String> removeStoppingWord(List<String> wordsWithStoppingWords){
        List <String> wordsWithoutStoppingWords = new ArrayList<>();
        for(String word : wordsWithStoppingWords)
        {
            if(!isStoppingWord(word))
            {
                wordsWithoutStoppingWords.add(word);
            }
        }
        return wordsWithoutStoppingWords;
    }
}
