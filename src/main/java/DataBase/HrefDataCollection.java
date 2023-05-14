package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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


}
