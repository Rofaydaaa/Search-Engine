package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

public class HistoryDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> historyDataCollection;

    protected HistoryDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.historyDataCollection = this.SearchEngineDB.getCollection("History");
    }

    /////////////////////////////////ANY QUERY ON HISTORY COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////

    public void InsertHistory(JSONObject history, String searchString){

        //transform JSONObject to document
        Document documentHistory = Document.parse(history.toString());

        // Prepare the filter to check if searchText already exists
        Document filter = new Document("searchText", searchString);

        // Find the document with the matching filter
        Document existingDocument = this.historyDataCollection.find(filter).first();

        if (existingDocument != null) {
            // Update the existing document
            existingDocument.put("historyRank", existingDocument.getInteger("historyRank", 0) + 1);
            this.historyDataCollection.replaceOne(filter, existingDocument);
        } else {
            // Insert the new document
            this.historyDataCollection.insertOne(documentHistory);
        }

    }
}
