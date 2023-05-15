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
        Sort sort = Sort.by(Sort.Direction.DESC, "popularity");
        query.with(sort);
        List<SingleSuggestion> suggestions = mongoTemplate.find(query, SingleSuggestion.class);
        return suggestions;
    }

    @Transactional
    public SingleSuggestion addSuggestion(String suggestion) {
        Query query = new Query(Criteria.where("suggestion").is(suggestion));
        Update update = new Update().inc("popularity", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        SingleSuggestion suggestion1 = mongoTemplate.findAndModify(query, update, options, SingleSuggestion.class);
        return suggestion1;
    }
}