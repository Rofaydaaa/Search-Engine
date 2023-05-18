package Indexer;
import DataBase.DataBaseManager;
import CostumDataStructures.*;
import Ranker.PageRank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Indexer {

    DataBaseManager dbManager;
    Map<String, WordToSearch> answerMap;
    Vector<String> spamUrls;

    Indexer(DataBaseManager db) {

        this.dbManager = db;
        this.answerMap = new HashMap<>();
        this.spamUrls = new Vector<>();
    }

    public List<URLData> getUrlList() {
        return dbManager.getUrlDataCollection().getURLsDataNotIndexed();
    }

    public static void main(String[] args) {

        Indexer indexer = new Indexer(new DataBaseManager());
        //Start Ranking the page first.
        PageRank pageRank = new PageRank(indexer.dbManager);
        System.out.println("Done With PageRanker");
        List<URLData> urls = indexer.getUrlList();
        IndexerForSingleDoc indexerForSingleDoc = new IndexerForSingleDoc();
        int i = 0;
        for (URLData url : urls) {
            i++;
            indexerForSingleDoc.index(url);
            //Insert doc by doc
            //better to insert them all at one
            if (indexerForSingleDoc.isSpamDoc())
                //add to Spam the vector
                indexer.spamUrls.add(url.URL);
            //{
            //Add the wordDataMap to the wordsDataCollection
//                Map<String, WordData> map = indexerForSingleDoc.getWordHashTable();
//
//                for (Map.Entry<String, WordData> entry : map.entrySet()) {
//                    indexer.dbManager.getWordsDataCollection().updateWordToSearchData(entry.getKey(), entry.getValue());
//                }
//
//                //update index URL
//                indexer.dbManager.getUrlDataCollection().updateIndex(url.URL);
//            }
            System.out.print("Done indexing document: ");
            System.out.println(i);
        }
        //Insert the spam to the database
        //check is there is spam
        if (!indexer.spamUrls.isEmpty())
            indexer.dbManager.getSpamDataCollection().insertVectorSpamUrls(indexer.spamUrls);
        if (!urls.isEmpty()) {
            //Insert the hashMap to the table
            indexer.dbManager.getWordsDataCollection().insertWordHashMap(indexerForSingleDoc.getWordHashTable());
            //update indexed
            indexer.dbManager.getUrlDataCollection().updateAllIndex();
        }
    }
}

//To test a specific web page
//        Indexer indexer = new Indexer(new DataBaseManager());
//        //Test a single document from the internet
//        URLData url = new URLData();
//        url.URL = "https://www.google.com.eg/";
//        url.FilePath = "./src/main/java/Indexer/gg.html";
//        IndexerForSingleDoc indexerForSingleDoc = new IndexerForSingleDoc(url);
//        indexerForSingleDoc.index();
//        if(indexerForSingleDoc.isSpamDoc())
//            //add to Spam db
//            indexer.dbManager.getSpamDataCollection().insertSpamUrl(url.URL);
//        else{
//            //Add the wordDataMap to the wordsDataCollection
//            Map<String,WordData> map = indexerForSingleDoc.getWordHashTable();
//
//            for(Map.Entry<String, WordData> entry : map.entrySet()) {
//                indexer.dbManager.getWordsDataCollection().updateWordToSearchData(entry.getKey(), entry.getValue());
//            }
//
//            //update index URL
//            indexer.dbManager.getUrlDataCollection().updateIndex(url.URL);
//
//        }



//Insert url by url
//            if(indexerForSingleDoc.isSpamDoc())
//                //add to Spam db
//                indexer.dbManager.getSpamDataCollection().insertSpamUrl(url.URL);
//            else{
//                //Add the wordDataMap to the wordsDataCollection
//                Map<String,WordData> map = indexerForSingleDoc.getWordHashTable();
//
//                for(Map.Entry<String, WordData> entry : map.entrySet()) {
//                    indexer.dbManager.getWordsDataCollection().updateWordToSearchData(entry.getKey(), entry.getValue());
//                }
//
//                //update index URL
//                indexer.dbManager.getUrlDataCollection().updateIndex(url.URL);