package DataBase;

import CostumDataStructures.WordData;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordsDataCollection {

    MongoDatabase SearchEngineDB;
    MongoCollection<Document> wordsDataCollection;
//       {
//        "word": "string",
//            "dataFrequency": "number",
//            "documents": [
//        {
//            "count": "number",
//                "url": "string",
//                "popularity": "number",
//                "lengthOfDocument": "number",
//                "filePath": "string",
//                "position": "number"
//        },
//        {
//            "count": "number",
//                "url": "string",
//                "popularity": "number",
//                "lengthOfDocument": "number",
//                "filePath": "string",
//                "position": "number"
//        },
//    ...
//  ]
//    }
    protected WordsDataCollection(MongoDatabase db){

        this.SearchEngineDB = db;
        this.wordsDataCollection = this.SearchEngineDB.getCollection("Words");
    }

    /////////////////////////////////ANY QUERY ON WORDS COLLECTION SHOULD BE WRITTEN HERE/////////////////////////////////

    public void updateWordToSearchData(String word, WordData data){
        Document newDocument = new Document()
                .append("count", data.count)
                .append("url", data.url)
                .append("lengthOfDocument", data.lengthOfDoc)
                .append("filePath", data.filepath)
                .append("position", data.position);
                //.append("popularity", data.popularity)
        Document query = new Document("word", word);
        //get the word needed for update
        Document result = wordsDataCollection.find(query).first();
        //if the word is already in the database
        if(result != null){
            //Increment the dataFrequency by 1
            int dataFrequency = result.getInteger("dataFrequency", 0);
            int updatedDataFrequency = dataFrequency + 1;

            Document update = new Document("$set", new Document("dataFrequency", updatedDataFrequency))
                    .append("$push", new Document("documents", newDocument));
            wordsDataCollection.updateOne(query, update);
        }
        //if the word is not in the database, add one
        else{
            Document newWord = new Document("word", word)
                    .append("dataFrequency", 1)
                    .append("documents", new ArrayList<>(List.of(newDocument)));
            wordsDataCollection.insertOne(newWord);
        }
    }
}
