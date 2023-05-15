package com.searchEngine.Search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultsService {
    @Autowired
    private ResultsRepository resultsRepository;
    public List<Results> searchResults(){
        return resultsRepository.findAll();
    }
}
