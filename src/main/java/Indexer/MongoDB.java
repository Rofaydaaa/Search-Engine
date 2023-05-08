package Indexer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.bson.Document;
public class MongoDB {
    MongoClient mongoClient;
    MongoDatabase indexerDB;
    MongoCollection<Document> wordsDataCollection;
    //word
    //df
    //{document:
    //  {
    //     count
    //     url
    //     popularity
    //     lengthOfdocument
    //     filepath
    //     position
    //     }
    //  }
    MongoCollection<Document> spamDataCollection;
    MongoCollection<Document> URl;
    //url
    //visited
    //indexed
    //filepath
    //
    MongoCollection<Document> href;
    //url
    //referenced by: url ID

    public MongoDB(){
        //connect to the database
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.indexerDB = mongoClient.getDatabase("Indexer");
        this.wordsDataCollection = this.indexerDB.getCollection("Words");
        this.spamDataCollection = this.indexerDB.getCollection("Spam");
        this.spamDataCollection = this.indexerDB.getCollection("Spam");
    }

    public static void main(String[] args){
        MongoDB db = new MongoDB();
        System.out.println("connection done");
        Document document = new Document("word", "example")
                .append("count", 1);
        db.wordsDataCollection.insertOne(document);

        Document document1 = new Document("word", "example1")
                .append("count", 3);
        db.wordsDataCollection.insertOne(document1);
    }
}
