package Ranker;

import CostumDataStructures.WordData;
import CostumDataStructures.WordToSearch;
import CostumDataStructures.URLData;
import DataBase.DataBaseManager;

import java.util.*;

public class Ranker {
    DataBaseManager dbManager;
    Map<String,WordToSearch> WordData;
    int TotalNumberOfURLS = 6000;

    public Map<WordData, Double> tf_IDF= new HashMap<>();
    public Ranker(DataBaseManager db,Map<String,WordToSearch> wd){
        this.dbManager = db;
        this.WordData = wd;
    }

    public void CalculateRelevance() {
        for (Map.Entry<String,WordToSearch> entry : WordData.entrySet()) {
            String word = entry.getKey();
            double IDF = (double)TotalNumberOfURLS/(entry.getValue().df);
            for (int i=0;i<entry.getValue().data.size();i++){
                double TF = (double)entry.getValue().data.get(i).count/entry.getValue().data.get(i).lengthOfDoc;
                if (tf_IDF.containsKey(entry.getValue().data.get(i))) {
                    tf_IDF.put(entry.getValue().data.get(i), tf_IDF.get(entry.getValue().data.get(i)) + IDF*TF*calculatePositionsWeight(word,i));
                } else {
                    tf_IDF.put(entry.getValue().data.get(i), IDF*TF*calculatePositionsWeight(word,i));
                }
            }
        }
    }

    // Return weight a specific word in a specific doc
    public double calculatePositionsWeight(String word,int doc)
    {
        double totalWeight= 0;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("title")==null)?0:WordData.get(word).data.get(doc).position.get("title")*15;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("h1")==null)?0:WordData.get(word).data.get(doc).position.get("h1")*6;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("h2")==null)?0:WordData.get(word).data.get(doc).position.get("h2")*5;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("h3")==null)?0:WordData.get(word).data.get(doc).position.get("h3")*4;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("h4")==null)?0:WordData.get(word).data.get(doc).position.get("h4")*3;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("h5")==null)?0:WordData.get(word).data.get(doc).position.get("h5")*2;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("h6")==null)?0:WordData.get(word).data.get(doc).position.get("h6")*1.5;
        totalWeight+=(WordData.get(word).data.get(doc).position.get("body")==null)?0:WordData.get(word).data.get(doc).position.get("body");
        return totalWeight;
    }

    // sort the urls according to their relevance and include popularity in score
    public Map<WordData, Double> sortUrls(){
        Map<WordData, Double> sortedUrls = new HashMap<>();
        for (Map.Entry<WordData, Double> entry : tf_IDF.entrySet()) {
            sortedUrls.put(entry.getKey(), entry.getValue()*entry.getKey().popularity);
        }
        // sort the urls according to their relevance
        sortedUrls = sortedUrls.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return sortedUrls;
    }

//    public static void main(String []args) {
//        DataBaseManager db = new DataBaseManager();
//        Map<String,WordToSearch> WordData;
//        WordData = db.getWordsDataCollection().getWordMapToSearch();
//        Ranker r1= new Ranker(db,WordData);
//        Map<URLData, Double> test = r1.sortUrls();
//        // print map
//        for (Map.Entry<URLData, Double> entry : test.entrySet()) {
//            System.out.println(entry.getKey().URL + " " + entry.getValue());
//        }
    }


