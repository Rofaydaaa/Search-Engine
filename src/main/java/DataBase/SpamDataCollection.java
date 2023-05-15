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

    public void insertSpamUrl(String url){
        spamDataCollection.insertOne(new Document("url" ,url));
    }

    //check if url is in spam collection so that not indexing it if found it again in url that needs to be indexed
    public boolean isInSpamCollection(String url) {
        Document query = new Document("url", url);
        Document result = spamDataCollection.find(query).first();
        return result != null;
    }
}
