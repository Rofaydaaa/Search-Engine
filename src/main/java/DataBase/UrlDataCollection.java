package DataBase;

import CostumDataStructures.URLData;
import com.mongodb.BasicDBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // get bulk all URLs to calculate popularity
    public List<String> getAllURLs() {
        List<String> urls = new ArrayList<>();

        try (MongoCursor<Document> cursor = urlDataCollection.find().projection(Projections.include("url")).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String url = doc.getString("url");
                urls.add(url);
            }
        }
        return urls;
    }
    // bulk Update popularity values
    public void updatePopularity(Map<String, Double> URLS) {
        List<WriteModel<Document>> bulkUpdate = new ArrayList<>();
        for (Map.Entry<String, Double> entry : URLS.entrySet()) {
            bulkUpdate.add(new UpdateOneModel<>(Filters.eq("url", entry.getKey()), Updates.set("popularity", entry.getValue())));
        }
        BulkWriteResult bulkWriteResult = urlDataCollection.bulkWrite(bulkUpdate);
    }

    // get URLData for a URL
    public URLData getURLData(String URLs) {
        MongoCursor<Document> cur = this.urlDataCollection.find(Filters.eq("url", URLs)).cursor();
        URLData Data = new URLData();
        while (cur.hasNext()) {
            Document doc = cur.next();
            Data.URL = (String) doc.get("url");
            Data.FilePath = (String) doc.get("filepath");
            Data.popularity = (double) doc.get("popularity");
        }
        return Data;
    }

    //Get URLs that aren't indexed before
    public List<URLData> getURLsDataNotIndexed() {
        MongoCursor<Document> cur = this.urlDataCollection.find(new BasicDBObject("indexed",false)).cursor();
        List<URLData> DataList = new ArrayList<>();
        while (cur.hasNext()) {
            Document doc = cur.next();
            URLData currentData = new URLData();
            currentData.URL = (String) doc.get("url");
            currentData.FilePath = (String) doc.get("filepath");
            currentData.popularity = (double) doc.get("popularity");
            DataList.add(currentData);
        }
        return DataList;
    }

    //After finishing indexing a URL, update his indexed to be 1
    public void updateIndex(String URL){
        urlDataCollection.updateOne(Filters.eq("url", URL), Updates.set("indexed", 1));
    }
    public void updateAllIndex() {
        urlDataCollection.updateMany(new Document(), Updates.set("indexed", false));
    }
}
