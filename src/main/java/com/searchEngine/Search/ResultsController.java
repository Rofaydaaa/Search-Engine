package com.searchEngine.Search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultsController {
    @Autowired
    private ResultsService resultsService;
    @GetMapping
    public ResponseEntity<List<Results>> returnResults() {
        return new ResponseEntity<List<Results>>(resultsService.searchResults(), HttpStatus.OK);
    }
}
