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
                    .append("searchText", searchString);
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
    public static void main(String[] args) {

        //this is for testing the function
        // Java object 1
        String title1 = "solving bugs";
        String URL1 = "programmingGeeksGeek.com";
        String paragraph1 = "100 coffee cup + 2 hours sleep/day + lots of cry + stack overflow = solved bug in 3 days";

// Create the JSON object and set the field values
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("title", title1);
        jsonObject1.put("URL", URL1);
        jsonObject1.put("paragraph", paragraph1);

        String title3 = "debugging code";
        String URL3 = "codingGeeksGeek.com";
        String paragraph3 = "10 cups of coffee + 4 hours sleep/day + minimal crying + thorough debugging = fixed code in 1 week";

// Create the JSON object and set the field values
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("title", title3);
        jsonObject3.put("URL", URL3);
        jsonObject3.put("paragraph", paragraph3);

        JSONArray returnedJsonArray = new JSONArray();
        returnedJsonArray.put(jsonObject1);
        returnedJsonArray.put(jsonObject3);
        DataBaseManager db = new DataBaseManager();
        db.getHistoryDataCollection().InsertHistory(returnedJsonArray, "how to solve a bug in 1 day", 1);
    }
}
