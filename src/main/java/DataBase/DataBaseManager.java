package DataBase;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DataBaseManager {
    MongoClient mongoClient;
    MongoDatabase SearchEngineDB;
    WordsDataCollection wordsDataCollection;
    HistoryDataCollection historyDataCollection;
    SpamDataCollection spamDataCollection;
    UrlDataCollection urlDataCollection;
    HrefDataCollection hrefDataCollection;
    LinksDataCollection linksDataCollection;

    public DataBaseManager(){

        //connect to the database configuration
        String username = System.getenv("MONGODB_USERNAME");
        String password = System.getenv("MONGODB_PASSWORD");
        String clusterHost = "cluster0.sxccutr.mongodb.net";
        String connectionString = "mongodb+srv://" + username + ":" + password + "@" + clusterHost + "/?retryWrites=true&w=majority";
        //String connectionString = "mongodb://localhost:27017";
        this.mongoClient = MongoClients.create(connectionString);

        //DataBase Creation
        this.SearchEngineDB = mongoClient.getDatabase("SearchEngine");

        //Collections Classes Creation
        this.wordsDataCollection = new WordsDataCollection(this.SearchEngineDB);
        this.spamDataCollection = new SpamDataCollection(this.SearchEngineDB);
        this.historyDataCollection = new HistoryDataCollection(this.SearchEngineDB);
        this.urlDataCollection = new UrlDataCollection(this.SearchEngineDB);
        this.hrefDataCollection = new HrefDataCollection(this.SearchEngineDB);
        this.linksDataCollection = new LinksDataCollection(this.SearchEngineDB);
    }

    ///////////////////////////////ANY COLLECTION QUERY SHOULD BE ACCESSED THROUH THE DATA BASE MANAGER/////////////////////////
    //EX: if you are in the indexer class, and you want to make a query on the url collection
    //you have to make a data member from DataBaseManager "let's give it a name as dbManager",
    // dbManager.getUrlDataCollection.<The_Query_You_Want> :)

    //Getters For Data Collections
    public WordsDataCollection getWordsDataCollection() {
        return wordsDataCollection;
    }

    public HistoryDataCollection getHistoryDataCollection() {
        return historyDataCollection;
    }

    public SpamDataCollection getSpamDataCollection() {
        return spamDataCollection;
    }

    public UrlDataCollection getUrlDataCollection() {
        return urlDataCollection;
    }

    public HrefDataCollection getHrefDataCollection() {
        return hrefDataCollection;
    }

}
