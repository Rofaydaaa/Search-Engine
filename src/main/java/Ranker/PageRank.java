package Ranker;

import CostumDataStructures.URLData;
import DataBase.DataBaseManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRank {

    DataBaseManager dbManager;
    //URL and OutBoundURLS
    Map<String,List<String>> AllHrefs = new HashMap<>();
    //URL and InBoundURLS
    Map<String,List<String>> AllParents = new HashMap<>();
    // url and popularity value
    Map<String,Double> popularity= new HashMap<>();

    double d = 0.85;

    public PageRank(DataBaseManager db){
    //public PageRank(){
        this.dbManager = db;
        List<String> URLS = dbManager.getUrlDataCollection().getAllURLs();

        for (String url:URLS){
            AllHrefs.put(url,dbManager.getHrefDataCollection().getHref(url)); //OutBoundURLS
            AllParents.put(url,dbManager.getHrefDataCollection().getURL(url)); //InBoundURLS
        }
        double value =0;
        for(int i =0; i<2;i++){
            int k = 0;
            for(String url : URLS)
            {
                k++;
                for(String parent : AllParents.get(url))
                {
                    value+=((popularity.get(parent)==null || AllHrefs.get(parent) == null )?0:popularity.get(parent))/AllHrefs.get(parent).size();
                }
                double pagerank=(1-d)+d*value;
                popularity.put(url,pagerank);
                value=0;
                System.out.print("Done with page rank url: ");
                System.out.println(k);
            }
        }
        dbManager.getUrlDataCollection().updatePopularity(popularity);
    }
//    public static void main(String []args) {
//
//        PageRank r1= new PageRank();
//        System.out.println("popularity " + r1.popularity);
//        System.out.println(r1.popularity.size());
//    }

}
