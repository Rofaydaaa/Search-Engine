package com.searchEngine.Search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleSuggestion {
    @Id
    private ObjectId id;
    private String searchText;
    private int historyRank;
    private List<SingleResult> result;

    public SingleSuggestion(String suggestion, int popularity) {
        this.searchText = suggestion;
        this.historyRank = popularity;
    }
}