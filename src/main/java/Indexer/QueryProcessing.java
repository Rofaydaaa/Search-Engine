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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryProcessing {
    Ranker ranker;
    Trie stoppingWord;
    DataBaseManager dbManager;

    String currentStringToSearch;
    PorterStemmer stemmer;
    Map<String, WordToSearch> rankingWordsInSearch;
    JSONArray returnedJsonArray;
    Map<WordData, Double> rankingResults;
    List<WordData> correctResults;
    List<String> searchWordsList;
    List<String> stemmedSearchWordsList;

    //To store the extracted string from the search in case of and/or/not
    String extractedString1;
    String extractedString2;
    boolean isValidSearch;
    double startTime;
    double endTime;

    public QueryProcessing(String stringToSearch) {
        this.dbManager = new DataBaseManager();
        this.currentStringToSearch = stringToSearch;
        this.stemmer = new PorterStemmer();
        this.rankingWordsInSearch = new HashMap<>();
        this.rankingResults = new HashMap<>();
        this.returnedJsonArray = new JSONArray();
        this.correctResults = new Vector<>();
        this.stemmedSearchWordsList = new Vector<>();
        this.extractedString1 = "";
        this.extractedString2 = "";
        //execute the queryProcessing functionality directly from the constructor
        buildStoppingWord();
        startTime = System.currentTimeMillis();
        if (processSearchString()) {
            rankSearchWords();
            isValidSearch = true;
            processTheRankerResult();
        } else {
            isValidSearch = false;
        }
        endTime = System.currentTimeMillis();
    }

    public boolean processSearchString() {

        String s = dataPreProcessing(this.currentStringToSearch);
        this.searchWordsList = List.of(s.split(" "));
        this.searchWordsList = removeStoppingWord(this.searchWordsList);
        for (int i = 0; i < this.searchWordsList.size(); i++) {
            String w = this.searchWordsList.get(i);
            w = stemmer.stem(w);
            if (w.length() > 2)
                this.stemmedSearchWordsList.add(w);
        }
        if (this.stemmedSearchWordsList.isEmpty())
            return false;
        rankingWordsInSearch = dbManager.getWordsDataCollection().getWordToSearch(this.stemmedSearchWordsList);
        return true;
    }

    public void rankSearchWords() {
        ranker = new Ranker(this.dbManager, rankingWordsInSearch);
        ranker.CalculateRelevance();
        rankingResults = ranker.sortUrls();
    }

    public String dataPreProcessing(String s) {
        // \W, is equivalent to [^a-zA-Z0-9] regular expression
        // replace any non-word character with spaces
        s = s.replaceAll("[\\W\\d]", " ");

        //remove first and last spaces, remove any leading spaces
        s = s.trim().replaceAll(" +", " ");

        //convert the word to lower case;
        return s.toLowerCase();


    }

    public void buildStoppingWord() {
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

    public boolean isStoppingWord(String s) {

        return stoppingWord.search(s);
    }

    public List<String> removeStoppingWord(List<String> wordsWithStoppingWords) {
        List<String> wordsWithoutStoppingWords = new ArrayList<>();
        for (String word : wordsWithStoppingWords) {
            if (!isStoppingWord(word)) {
                wordsWithoutStoppingWords.add(word);
            }
        }
        return wordsWithoutStoppingWords;
    }

    public boolean containAnd() {
        // Create the regular expression pattern
        String pattern = "\"([^\"]+)\"\\s+and\\s+\"([^\"]+)\"";
        return extractPattern(pattern);
    }

    public boolean containOr() {
        // Create the regular expression pattern
        String pattern = "\"([^\"]+)\"\\s+or\\s+\"([^\"]+)\"";
        return extractPattern(pattern);
    }

    public boolean containNot() {
        // Create the regular expression pattern
        String pattern = "\"([^\"]+)\"\\s+not\\s+\"([^\"]+)\"";
        return extractPattern(pattern);
    }

    public boolean extractPattern(String pattern) {
        // Create a Pattern object
        Pattern regex = Pattern.compile(pattern);
        // Create a Matcher object
        Matcher matcher = regex.matcher(this.currentStringToSearch);

        // Check if the input string matches the pattern
        if (matcher.find()) {
            this.extractedString1 = matcher.group(1);
            this.extractedString2 = matcher.group(2);
            return true;
        } else
            return false;
    }

    public void processTheRankerResult() {

        //check if phraseSearching
        if (this.currentStringToSearch.startsWith("\"") && this.currentStringToSearch.endsWith("\"")) {
            //check for and
            if (containAnd()) {
                List<WordData> l1 = phraseSearch(this.extractedString1);
                List<WordData> l2 = phraseSearch(this.extractedString2);
                // Combine the 2 list
                this.correctResults = l1;
                this.correctResults.addAll(l2);
                // Remove duplicates using HashSet
                HashSet<WordData> set = new HashSet<>(this.correctResults);
                this.correctResults.clear();
                this.correctResults.addAll(set);
            }
            //check for or
            else if (containOr()) {
                List<WordData> l1 = phraseSearch(this.extractedString1);
                List<WordData> l2 = phraseSearch(this.extractedString2);
                // Get the intersection of the two lists
                l1.retainAll(l2);
                this.correctResults = l1;

            }
            //check for not
            else if (containNot()) {
                List<WordData> l1 = phraseSearch(this.extractedString1);
                List<WordData> l2 = phraseSearch(this.extractedString2);
                //exclude l2 from l1
                l1.removeAll(l2);
                this.correctResults = l1;
            } else {
                this.correctResults = phraseSearch(this.currentStringToSearch.substring(1, this.currentStringToSearch.length() - 1));
            }
            //No document have the same exact match
            if (this.correctResults.size() == 0) {
                this.isValidSearch = false;
            }
        } else {
            originalSearch();
        }
    }

    public void originalSearch() {
        this.correctResults.addAll(rankingResults.keySet());
    }

    public void createJsonObject() {

        //the JSON object has the following fields
        // "search Text":"how to solve your bug in 3 days only"
//         "title":"solving bugs"
//         "URL":"programmingGeeksGeek.com"
//         "paragraph":"100 coffee cup + 2 hours sleep/day + lots of cry + stack overflow = solved bug in 3 days"
        for (WordData url : this.correctResults) {
            JSONObject jsonObject = new JSONObject();
            if (url.url == null)
                continue;
            jsonObject.put("title", getPageTitle(url));
            jsonObject.put("URL", url.url);
            jsonObject.put("paragraph", getParagraph(url));
            this.returnedJsonArray.put(jsonObject);
        }
        //Insert in the dataBase
        this.dbManager.getHistoryDataCollection().InsertHistory(this.returnedJsonArray, this.currentStringToSearch, 1);
    }

    public JSONArray getResultJSONList() {
        if (isValidSearch) {
            createJsonObject();
            endTime = System.currentTimeMillis();
            return this.returnedJsonArray;
        } else {
            endTime = System.currentTimeMillis();
            return new JSONArray(); //this will return an empty json array
        }
    }

    public double getSearchTime() {

        return endTime - startTime;
    }

    public String getPageTitle(WordData urlData) {

        String filePath = urlData.filepath;
        String pageTitle = "";
        try {
            // Connect to the web page and retrieve its HTML
            Document document = Jsoup.parse(new File(filePath), "UTF-8");
            pageTitle = document.title();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageTitle;
    }

    public String getParagraph(WordData urlData) {
        String filePath = urlData.filepath;
        String pageParagraph = "";
        try {
            // Connect to the web page and retrieve its HTML
            Document document = Jsoup.parse(new File(filePath), "UTF-8");

            // Define the order of element types to search
            String[] elementTypes = {"h1", "h2", "h3", "h4", "h5", "h6", "li", "td", "dt", "small", "a", "div", "p", "span"};

            // Iterate over each element type
            for (String elementType : elementTypes) {
                // Select elements of the current type
                Elements elements = document.select(elementType);

                // Iterate over the selected elements
                for (Element element : elements) {
                    // Get the text of the element
                    String elementText = element.text();

                    // Check if any of the strings in the list are present in the element
                    for (String searchString : this.stemmedSearchWordsList) {
                        if (elementText.toLowerCase().contains(searchString.toLowerCase())) {
//                            int firstIndex = elementText.indexOf(searchString.toLowerCase());
//                            if (firstIndex > 125 && elementText.length() > 250) {
//                                pageParagraph = elementText.substring(firstIndex - 125, firstIndex + 120);
//                            } else if (elementText.length() < 250) {
//                                pageParagraph = elementText;
//                            } else if (firstIndex < 125 && elementText.length() > 250) {
//                                pageParagraph = elementText.substring(0, 250);
//                            }
                            pageParagraph = elementText;
                            if(pageParagraph.length() > 300)
                                pageParagraph = extractTextWithWord(pageParagraph, searchString, 300);
                            break;
                        }

                        // If a matching element is found, exit the loop
                        if (!pageParagraph.isEmpty()) {
                            break;
                        }
                    }

                    // If a matching element is found, exit the loop
                    if (!pageParagraph.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageParagraph;
    }


    public List<WordData> phraseSearch(String searchString) {
        //iterate over all the returned urls coming from the ranker i.e. this.rankingResults
        //get the first element that has the same word as the phraseSearch and start comparing from here
        //if they are the same add it to this array (this.correctResults), if not then continue
        //NOTE: if no web pages are found, set the this.isValidSearch = false;
        List<WordData> correctRes = new ArrayList<>();
        for (Map.Entry<WordData, Double> entry : this.rankingResults.entrySet()) {
            WordData urlData = entry.getKey();
            String filePath = urlData.filepath;
            if (filePath == null)
                continue;
            try {
                Document currentDoc = Jsoup.parse(new File(filePath), "UTF-8");
                String docText = currentDoc.text();
                String[] docWords = docText.split(" ");
                String[] searchWords = searchString.split(" ");
                int searchWordsIndex = 0;
                int docWordsIndex = 0;
                int searchWordsLength = searchWords.length;
                int docWordsLength = docWords.length;
                while (searchWordsIndex < searchWordsLength && docWordsIndex < docWordsLength) {
                    if (searchWords[searchWordsIndex].equalsIgnoreCase(docWords[docWordsIndex])) {
                        searchWordsIndex++;
                        docWordsIndex++;
                    } else {
                        searchWordsIndex = 0;
                        docWordsIndex++;
                    }


                }
                if (searchWordsIndex == searchWordsLength) {
                    correctRes.add(urlData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return correctRes;
    }

    public String extractTextWithWord(String text, String word, int size) {
        // Split the text into words
        String[] words = text.split("\\s+");

        // Find the index of the word
        int wordIndex = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equalsIgnoreCase(word)) {
                wordIndex = i;
                break;
            }
        }

        // If the word is found
        if (wordIndex != -1) {
            // Calculate the start and end indices for the extracted part
            int startIndex = Math.max(0, wordIndex - (size / 2));
            int endIndex = Math.min(words.length - 1, wordIndex + (size / 2));

            // Extract the part of the text
            StringBuilder extractedText = new StringBuilder();
            for (int i = startIndex; i <= endIndex; i++) {
                extractedText.append(words[i]).append(" ");
            }
            return extractedText.toString().trim();
        }

        // If the word is not found, return an empty string
        return "";
    }
}


