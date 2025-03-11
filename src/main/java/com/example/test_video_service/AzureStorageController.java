package com.example.test_video_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class AzureStorageController {
    private final AzureStorageService azureStorageService;

    public AzureStorageController(AzureStorageService azureStorageService) {
        this.azureStorageService = azureStorageService;
    }

    @GetMapping(value = "/{filename}")
    public ResponseEntity<String> streamingVideo(@PathVariable String fileName) {
        return ResponseEntity.ok(azureStorageService.getBlobUrl(fileName));
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadVideo(@RequestPart("video") MultipartFile video) {
        try {
            azureStorageService.save(video);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Missing file!");
        }
        return ResponseEntity.ok("File uploaded successfully");
    }
}
