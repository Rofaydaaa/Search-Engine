package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SpamDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> spamDataCollection;
    //url

    protected SpamDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.spamDataCollection = this.SearchEngineDB.getCollection("Spam");
    }

    /////////////////////////////////ANY QUERY ON SPAM COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////

    public void insertSingleSpamUrl(String url){
        spamDataCollection.insertOne(new Document("url" ,url));
    }

    public void insertVectorSpamUrls(Vector<String> urls) {
        List<Document> documents = new ArrayList<>();
        for (String url : urls) {
            documents.add(new Document("url", url));
        }

        spamDataCollection.insertMany(documents);
    }
    //check if url is in spam collection so that not indexing it if found it again in url that needs to be indexed
    public boolean isInSpamCollection(String url) {
        Document query = new Document("url", url);
        Document result = spamDataCollection.find(query).first();
        return result != null;
    }
}
