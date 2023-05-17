package Indexer;
import CostumDataStructures.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

import opennlp.tools.stemmer.PorterStemmer;
public class IndexerForSingleDoc {

    //url data to be indexed
    URLData currentUrlData;
    Document currentDoc;
    Trie stoppingWord;
    String stoppingWordDictionaryFilePath;
    //To store all words in a document
    // "apple"-> {"word" = "apple", "df" = count, "data"= vector<WordData>}
    //List and maps for text storage
    //to store all the word with it data structure
    Map<String,WordToSearch> documentWordsDataMap;
    //list to store title Words
    List<String> titleWords;
    //list of lists to store headers words from <h1> -----> <h6>, every row represent a header
    List<List<String>> headerWords;
    //list to store the body words
    List<String> paragraphWords;
    boolean isSpam;
    double spamThreshold;
    PorterStemmer stemmer;

    int lengthOfDocument;
    IndexerForSingleDoc(){

        //build stopping word tries for fast search
        buildStoppingWord();

        //assign data members
        this.documentWordsDataMap = new HashMap<>();
        this.stemmer = new PorterStemmer();
        this.spamThreshold = 0.6;
    }

    public void index(URLData urlD){
        //clean up for the used data memebr
        this.currentUrlData = urlD;
        this.lengthOfDocument = 0;
        this.isSpam = false;
        parseDocumentFromLocalFile();
        extractAllData();
        addAllDataToDocumentWordsDataMap();
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

    public void extractAllData(){
        extractDataTitle();
        extractDataHeader();
        extractDataParagraph();
    }

    public void addAllDataToDocumentWordsDataMap(){
         addTitleToDocumentWordsDataMap();
         if(!isSpam)
            addHeadersToDocumentWordsDataMap();
        if(!isSpam)
            addParagraphToDocumentWordsDataMap();
        documentWordsDataMap.remove("");
    }

    public boolean isSpamDoc(){
        return isSpam;
    }
    public Map<String,WordToSearch> getWordHashTable(){
        return documentWordsDataMap;
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
        paragraphWords.addAll(removeStoppingWord(dataPreProcessingForListOfString(docBodyElements.select("td").eachText())));
        paragraphWords.addAll(removeStoppingWord(dataPreProcessingForListOfString(docBodyElements.select("dt").eachText())));
        lengthOfDocument += paragraphWords.size();
    }

    public String stemAndUpdate(String word, String element){
        word = stemmer.stem(word);
        WordToSearch currentWordToSearch = documentWordsDataMap.get(word);
        if(currentWordToSearch  == null)//if the word is not in the map yet
        {
            //create new word to search
            currentWordToSearch = new WordToSearch();
            currentWordToSearch.word = word;
            currentWordToSearch.df = 0;
            currentWordToSearch.dataMap = new HashMap<>();
        }
        WordData currentWordData = documentWordsDataMap.get(word).dataMap.get(this.currentUrlData.URL);
        if(currentWordData == null){
            currentWordData = new WordData();
            currentWordData.url = currentUrlData.URL;
            currentWordData.filepath = currentUrlData.FilePath;
            currentWordData.count = 0;
            currentWordData.lengthOfDoc = lengthOfDocument;
            currentWordData.popularity = currentUrlData.popularity;
            currentWordToSearch.df += 1;
        }
        //update the current word data
        currentWordData.count += 1;
        currentWordData.position.put(element , currentWordData.position.get(element)+1);
        currentWordToSearch.dataMap.put(this.currentUrlData.URL, currentWordData);
        documentWordsDataMap.put(word,currentWordToSearch);

        return word;
    }
    public void addTitleToDocumentWordsDataMap(){
        for(String word : titleWords)
        {
            String currentWordString = stemAndUpdate(word, "title");
            if(this.documentWordsDataMap.get(currentWordString).dataMap.get(currentWordString).count >= lengthOfDocument*spamThreshold)
            {
                isSpam = true;
                return;
            }
            if(currentWordString.length() <= 2){
                this.documentWordsDataMap.remove(currentWordString);
            }
        }
    }
    public void addHeadersToDocumentWordsDataMap(){

        for(int i = 0 ; i < 6 ; i++)
        {
            for (String word : headerWords.get(i))
            {
                String currentWordString = stemAndUpdate(word, "h"+(i+1));
                if(this.documentWordsDataMap.get(currentWordString).dataMap.get(currentWordString).count >= lengthOfDocument*spamThreshold)
                {
                    isSpam = true;
                    return;
                }
                if(currentWordString.length() <= 2){
                    this.documentWordsDataMap.remove(currentWordString);
                }
            }
        }
    }
    public void addParagraphToDocumentWordsDataMap(){

        for(String word : paragraphWords)
        {
            String currentWordString = stemAndUpdate(word, "body");
            if(this.documentWordsDataMap.get(currentWordString).dataMap.get(currentWordString).count >= lengthOfDocument*spamThreshold)
            {
                isSpam = true;
                return;
            }
            if(currentWordString.length() <= 2){
                this.documentWordsDataMap.remove(currentWordString);
            }
        }
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
        // Remove non-word characters and numbers
        s = s.replaceAll("[\\W\\d]", " ");

        //remove first and last spaces, remove any leading spaces
        s = s.trim().replaceAll(" +", " ");

        //convert the word to lower case;
        return s.toLowerCase();
    }
    public void buildStoppingWord(){
        stoppingWord = new Trie();
        stoppingWordDictionaryFilePath = "./src/main/java/Indexer/stoppingWords.txt";

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
