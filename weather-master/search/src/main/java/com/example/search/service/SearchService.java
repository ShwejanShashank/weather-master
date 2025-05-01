package com.example.search.service;


import com.example.search.dto.StudentDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class SearchService {

    private final RestTemplate restTemplate;

    public SearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<List<StudentDto>> getStudentsFromSms() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                StudentDto[] students = restTemplate.getForObject("http://sms/api/students", StudentDto[].class);
                return Arrays.asList(students);
            } catch (Exception e) {
                System.out.println("Fallback for getStudentsFromSms: " + e.getMessage());
                return Collections.emptyList();
            }
        });
    }

    @Async
    public CompletableFuture<String> getDetailsPort() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return restTemplate.getForObject("http://details/port", String.class);
            } catch (Exception e) {
                System.out.println("Fallback for getDetailsPort: " + e.getMessage());
                return "Unknown";
            }
        });
    }

    public CompletableFuture<List<StudentDto>> getStudentsFallback() {
        System.out.println("Fallback triggered for SMS service!");
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    public CompletableFuture<String> getPortFallback() {
        System.out.println("Fallback triggered for Details service!");
        return CompletableFuture.completedFuture("Unknown");
    }
}
