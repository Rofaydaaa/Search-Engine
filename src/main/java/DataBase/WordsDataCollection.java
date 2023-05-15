package DataBase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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

}
