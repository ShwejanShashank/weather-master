package com.example.search.controller;

import com.example.search.dto.StudentDto;
import com.example.search.response.GeneralResponse;
import com.example.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class SearchController {

    @Autowired
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<GeneralResponse<Map<String, Object>>>> getSearchData() {
        CompletableFuture<List<StudentDto>> studentsFuture = searchService.getStudentsFromSms();
        CompletableFuture<String> portFuture = searchService.getDetailsPort();

        return CompletableFuture.allOf(studentsFuture, portFuture)
                .thenApply(v -> {
                    Map<String, Object> data = new HashMap<>();
                    try {
                        data.put("students", studentsFuture.get());
                        data.put("port", portFuture.get());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    GeneralResponse<Map<String, Object>> response = new GeneralResponse<>(200, Instant.now(), data);
                    return ResponseEntity.ok(response);
                });
    }
}
