package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> historyDataCollection;

    protected HistoryDataCollection(MongoDatabase db){
        this.SearchEngineDB = db;
        this.historyDataCollection = this.SearchEngineDB.getCollection("History");
    }

    /////////////////////////////////ANY QUERY ON HISTORY COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////

    public void InsertHistory(JSONArray history, String searchString, int rank) {
        // Transform JSONArray to a list of Documents
        JSONArray jsonArray = new JSONArray(history.toString());
        int length = jsonArray.length();
        List<Document> documents = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Document document = Document.parse(jsonArray.getJSONObject(i).toString());
            documents.add(document);
        }

        // Prepare the filter to check if searchText already exists
        Document filter = new Document("searchText", searchString);

        // Find the document with the matching filter
        Document existingDocument = historyDataCollection.find(filter).first();

        if (existingDocument != null) {
            // Update the existing document
            existingDocument.put("historyRank", existingDocument.getInteger("historyRank", 0) + 1);
            historyDataCollection.replaceOne(filter, existingDocument);
        } else {
            // Insert the new documents as a list
            Document resultDocument = new Document("result", documents)
                    .append("historyRank", rank)
                    .append("result", documents);
            historyDataCollection.insertOne(resultDocument);
        }
    }
//    public void InsertHistory(JSONObject history, String searchString, int rank){
//
//        //transform JSONObject to document
//        Document documentHistory = Document.parse(history.toString());
//
//        // Prepare the filter to check if searchText already exists
//        Document filter = new Document("searchText", searchString);
//
//        // Find the document with the matching filter
//        Document existingDocument = this.historyDataCollection.find(filter).first();
//
//        if (existingDocument != null) {
//            // Update the existing document
//            existingDocument.put("historyRank", existingDocument.getInteger("historyRank", 0) + 1);
//            this.historyDataCollection.replaceOne(filter, existingDocument);
//        } else {
//            // Insert the new document
//            this.historyDataCollection.insertOne(documentHistory);
//        }
//
//    }
}
