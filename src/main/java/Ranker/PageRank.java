package Ranker;

import CostumDataStructures.URLData;
import DataBase.DataBaseManager;

import java.util.List;
import java.util.Map;

public class PageRank {

    DataBaseManager dbManager;
    //URL and OutBoundURLS
    Map<String,List<String>> AllHrefs;
    //URL and InBoundURLS
    Map<String,List<String>> AllParents;
    // url and popularity value
    Map<String,Double> popularity;

    double d = 0.85;

    public PageRank(DataBaseManager db){

        this.dbManager = db;
        List<String> URLS = dbManager.getUrlDataCollection().getAllURLs();

        for (String url:URLS){
            AllHrefs.put(url,dbManager.getHrefDataCollection().getHref(url)); //OutBoundURLS
            AllParents.put(url,dbManager.getHrefDataCollection().getURL(url)); //InBoundURLS
        }
        double value =0;
        for(int i =0; i<2000;i++){
            for(String url : URLS)
            {
                for(String parent : AllParents.get(url))
                {
                    value+=((popularity.get(parent)==null)?0:popularity.get(parent))/AllHrefs.get(parent).size();
                }
                double pagerank=(1-d)+d*value;
                popularity.put(url,pagerank);
                value=0;
            }
        }
        dbManager.getUrlDataCollection().updatePopularity(popularity);
    }
}
