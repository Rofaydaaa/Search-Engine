package Indexer;
import DataBase.DataBaseManager;
import CostumDataStructures.*;
import java.util.List;

public class Indexer {

    DataBaseManager dbManager;
    Indexer(DataBaseManager db){
        this.dbManager = db;
    }

    public List<URLData> getUrlList(){
        return dbManager.getUrlDataCollection().getURLsDataNotIndexed();
    }
    public static void main(String[] args) {

        Indexer indexer = new Indexer(new DataBaseManager());
        List<URLData> urls = indexer.getUrlList();
        for (URLData url : urls){
            IndexerForSingleDoc indexerForSingleDoc = new IndexerForSingleDoc(url);
            indexerForSingleDoc.index();
            if(indexerForSingleDoc.isSpamDoc())
                //add to Spam db
                indexer.dbManager.getSpamDataCollection().insertSpamUrl(url.URL);
            else{
                //Add the wordDataMap to the wordsDataCollection

            }
        }
    }
}
