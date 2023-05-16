package Indexer;

import DataBase.DataBaseManager;
import Ranker.*;
import CostumDataStructures.*;
import opennlp.tools.stemmer.PorterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class QueryProcessing {
    Ranker ranker;
    Trie stoppingWord;
    DataBaseManager dbManager;

    String currentStringToSearch;
    PorterStemmer stemmer;
    Map<String, WordToSearch> rankingWordsInSearch;
    JSONArray returnedJsonArray;
    Map<URLData, Double> rankingResults;
    List<URLData> correctResults;
    boolean isValidSearch;
    double startTime;
    double endTime;
    public QueryProcessing(String stringToSearch){
        this.dbManager = new DataBaseManager();
        this.currentStringToSearch = stringToSearch;
        this.stemmer = new PorterStemmer();
        this.rankingWordsInSearch =  new HashMap<>();
        this.rankingResults = new HashMap<>();
        this.returnedJsonArray = new JSONArray();
        this.correctResults = new Vector<>();
        //execute the queryProcessing functionality directly from the constructor
        buildStoppingWord();
        startTime = System.currentTimeMillis();
        if(processSearchString()){
            rankSearchWords();
            isValidSearch = true;
            processTheRankerResult();
        }
        else{
            isValidSearch = false;
        }
        endTime = System.currentTimeMillis();
    }
    public boolean processSearchString(){

        String s = dataPreProcessing(currentStringToSearch);
        List<String> searchWordsList = List.of(s.split(" "));
        searchWordsList = removeStoppingWord(searchWordsList);
        searchWordsList.remove("");
        if(searchWordsList.isEmpty())
            return false;
        for(String word : searchWordsList){
            WordToSearch wts = dbManager.getWordsDataCollection().getWordToSearch(word);
            if(wts != null){
                rankingWordsInSearch.put(word,wts);
            }
        }
        return true;
    }

    public void rankSearchWords(){
        ranker = new Ranker(this.dbManager, rankingWordsInSearch);
        ranker.CalculateRelevance();
        rankingResults = ranker.sortUrls();
    }
    public String dataPreProcessing(String s){
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
        String stoppingWordDictionaryFilePath = "./src/main/java/Indexer/stoppingWords.txt";

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

    public void processTheRankerResult(){

        //check if phraseSearching
        if(this.currentStringToSearch.startsWith("\"") && this.currentStringToSearch.endsWith("\"")){
            phraseSearch();
        }
        else{
            originalSearch();
        }
    }

    public void originalSearch(){
        this.correctResults.addAll(rankingResults.keySet());
    }
    public void createJsonObject(){

        //the JSON object has the following fields
        // "search Text":"how to solve your bug in 3 days only"
        // "title":"solving bugs"
        // "URL":"programmingGeeksGeek.com"
        // "paragraph":"100 coffee cup + 2 hours sleep/day + lots of cry + stack overflow = solved bug in 3 days"
        for(URLData url : this.correctResults){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", getPageTitle(url));
            jsonObject.put("URL", url.URL);
            jsonObject.put("paragraph", getParagraph(url));
            this.returnedJsonArray.put(jsonObject);
        }
        //Insert in the dataBase
        this.dbManager.getHistoryDataCollection().InsertHistory(this.returnedJsonArray, this.currentStringToSearch, 1);
    }

    public JSONArray getResultJSONList(){
        if(isValidSearch) {
            createJsonObject();
            return this.returnedJsonArray;
        }
        else
            return new JSONArray(); //this will return an empty json array
    }

    public double getSearchTime(){

        return endTime - startTime;
    }
    public String getPageTitle(URLData urlData){

        String filePath = urlData.FilePath;
        String pageTitle = "";
        try {
            // Connect to the web page and retrieve its HTML
            Document document = Jsoup.parse(new File(filePath), "UTF-8");
            pageTitle = document.title();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  pageTitle;
    }

    public String getParagraph(URLData urlData) {

        return "";
//        String filePath = urlData.FilePath;
//        try {
//            Document currentDoc = Jsoup.parse(new File(filePath), "UTF-8");
//
//        } catch (IOException e) {
//            e.printStackTrace();:
//        }
    }

    public void phraseSearch(){
        //itirate over all the returned urls comming from the ranker i.e this.rankingResults
        //get the first element that has the same word as the phraseSearch and start comparing from here
        //if they are the same add it to this array (this.correctResults), if not then continue
        //NOTE: if no web pages are found, set the this.isValidSearch = false;
    }
}
