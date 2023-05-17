package com.searchEngine.Search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "History")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleSuggestion {
    @Id
    private ObjectId id;
    private String suggestion;
    private int historyRank;
    private List<SingleResult> result;

    public SingleSuggestion(String suggestion, int popularity) {
        this.suggestion = suggestion;
        this.historyRank = popularity;
    }
}