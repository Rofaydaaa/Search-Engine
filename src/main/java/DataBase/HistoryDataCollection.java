package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class HistoryDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> historyDataCollection;

    protected HistoryDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.historyDataCollection = this.SearchEngineDB.getCollection("History");
    }

    /////////////////////////////////ANY QUERY ON HISTORY COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////

}
