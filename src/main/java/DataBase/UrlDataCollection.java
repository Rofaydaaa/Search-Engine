package DataBase;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import CostumDataStructures.*;

import java.util.ArrayList;
import java.util.List;

public class UrlDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> urlDataCollection;
    //url
    //visited
    //indexed
    //filepath
    //popularity, rank whatever be decided later
    protected UrlDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.urlDataCollection = this.SearchEngineDB.getCollection("URL");
    }

    /////////////////////////////////ANY QUERY ON URL COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////


    //Get URLs that aren't indexed before
    public List<URLData> getURLsDataNotIndexed() {
        MongoCursor<Document> cur = this.urlDataCollection.find(new BasicDBObject("indexed",false)).cursor();
        List<URLData> DataList = new ArrayList<>();
        while (cur.hasNext()) {
            Document doc = cur.next();
            URLData currentData = new URLData();
            currentData.URL = (String) doc.get("url");
            currentData.FilePath = (String) doc.get("filepath");
            //currentData.popularity = (double) doc.get("popularity");
            DataList.add(currentData);
        }
        return DataList;
    }

    //After finishing indexing a URL, update his indexed to be 1
    public void updateIndex(String URL){
        urlDataCollection.updateOne(Filters.eq("url", URL), Updates.set("indexed", 1));
    }

}
