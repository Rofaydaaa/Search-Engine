package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class SpamDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> spamDataCollection;
    //url

    protected SpamDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.spamDataCollection = this.SearchEngineDB.getCollection("Spam");
    }

    /////////////////////////////////ANY QUERY ON SPAM COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////

}
