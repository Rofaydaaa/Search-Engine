package com.searchEngine.Search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuggestionsService {
    @Autowired
    private SuggestionsRepository suggestionsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<SingleSuggestion> getAllSuggestionsSortedByPopularity() {
        Query query = new Query();
        Sort sort = Sort.by(Sort.Direction.DESC, "historyRank");
        query.with(sort);
        query.fields().exclude("result");
        List<SingleSuggestion> suggestions = mongoTemplate.find(query, SingleSuggestion.class);
        return suggestions;
    }
}