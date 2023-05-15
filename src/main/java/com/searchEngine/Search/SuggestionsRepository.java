package com.searchEngine.Search;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@CrossOrigin(origins = "*", allowedHeaders="*")
public interface SuggestionsRepository extends MongoRepository<SingleSuggestion, ObjectId> {

}
