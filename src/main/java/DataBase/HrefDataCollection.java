package DataBase;

import CostumDataStructures.URLData;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

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
    public String getHref(String URL)
    {

    }

}
