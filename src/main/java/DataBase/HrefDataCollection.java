package DataBase;

import CostumDataStructures.URLData;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;
public class HrefDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> hrefDataCollection;
    //url
    //referenced by: url ID

    protected HrefDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.hrefDataCollection = this.SearchEngineDB.getCollection("Href");
    }

    /////////////////////////////////ANY QUERY ON HREF COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////
    public List<String> getHref(String URL)
    {
        MongoCursor<Document> cur = hrefDataCollection.find(Filters.eq("refBy", URL)).cursor();
        List<String> URLsRefByInput = new ArrayList<>();
        while (cur.hasNext())
        {
            Document doc = cur.next();
            URLsRefByInput.add(doc.getString("url"));
        }
        return URLsRefByInput;
    }

    public List<String> getURL(String Href)
    {
        MongoCursor<Document> cur = hrefDataCollection.find(Filters.eq("url", Href)).cursor();
        List<String> URLsRefByInput = new ArrayList<>();
        while (cur.hasNext())
        {
            Document doc = cur.next();
            URLsRefByInput.add(doc.getString("refBy"));
        }
        return URLsRefByInput;
    }

}
