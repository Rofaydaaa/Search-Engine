package com.searchEngine.Search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleSuggestion {
    @Id
    private ObjectId id;
    private String suggestion;
    private int popularity;

    public SingleSuggestion(String suggestion, int popularity) {
        this.suggestion = suggestion;
        this.popularity = popularity;
    }
}