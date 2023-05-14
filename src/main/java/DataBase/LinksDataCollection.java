package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class LinksDataCollection {
    MongoDatabase SearchEngineDB;
    MongoCollection<Document> linksDataCollection;

    protected LinksDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.linksDataCollection = this.SearchEngineDB.getCollection("Links");
    }
}
