package com.searchEngine.Search;

import Indexer.QueryProcessing;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/results")
public class ResultsController {
    @Autowired
    private ResultsService resultsService;
    private String searchQuery;

    @PostMapping
    public ResponseEntity<Object> addSuggestion(@RequestBody Map<String, String> payload){
        ObjectMapper objectMapper = new ObjectMapper();
        String searchQuery = payload.get("suggestion");
        QueryProcessing queryProcessing = new QueryProcessing(searchQuery);
        JSONArray results = queryProcessing.getResultJSONList();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("time", queryProcessing.getSearchTime());
        results.put(jsonObject1);
        String response = results.toString();
        Object jsonObject;
        try {
            jsonObject = objectMapper.readValue(response, Object.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Object>(jsonObject, HttpStatus.OK);
    }
}
