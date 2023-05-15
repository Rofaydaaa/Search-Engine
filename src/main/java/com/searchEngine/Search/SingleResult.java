package com.searchEngine.Search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleResult {
    @Id
    private ObjectId id;
    private String title;
    private String url;
    private String paragraph;
}