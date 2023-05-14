package Ranker;

import CostumDataStructures.WordToSearch;
import DataBase.DataBaseManager;

import java.util.HashMap;
import java.util.Map;

public class Ranker {
    DataBaseManager dbManager;
    Map<String,WordToSearch> WordData;

    int TotalNumberOfURLS = 6000;

    public Map<String, Double> tf_IDF= new HashMap<>(){{put("", 0.0);}};
    Ranker(DataBaseManager db,Map<String,WordToSearch> wd){
        this.dbManager = db;
        this.WordData = wd;
    }

    public void CalculateRelevance() {
        for (Map.Entry<String,WordToSearch> entry : WordData.entrySet()) {
            String word = entry.getKey();
            double IDF = (double)TotalNumberOfURLS/(entry.getValue().df);
            for (int i=0;i<entry.getValue().data.size();i++){
                double TF = (double)entry.getValue().data.get(i).count/entry.getValue().data.get(i).lengthOfDoc;
                if (tf_IDF.containsKey(entry.getValue().data.get(i).url)) {
                    tf_IDF.put(entry.getValue().data.get(i).url, tf_IDF.get(entry.getValue().data.get(i).url) + IDF*TF*calculatePositionsWeight(word,i));
                } else {
                    tf_IDF.put(entry.getValue().data.get(i).url, IDF*TF);
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

}

