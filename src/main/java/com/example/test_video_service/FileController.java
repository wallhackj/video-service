package com.example.test_video_service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/files")
public class FileController {
    private final AzureStorageService azureStorageService;

    public FileController(AzureStorageService azureStorageService) {
        this.azureStorageService = azureStorageService;
    }

    @GetMapping
    public CompletableFuture<String> listFiles(Model model) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> files = azureStorageService.listAllFiles();
            model.addAttribute("files", files);
            return "file-list";
        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    @PostMapping("/upload")
    public CompletableFuture<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            azureStorageService.save(file);
            return "redirect:/files";
        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    @GetMapping("/delete/{blobName}")
    public CompletableFuture<String> deleteFile(@PathVariable String blobName) {
        return CompletableFuture.supplyAsync(() -> {
            azureStorageService.delete(blobName);
            return "redirect:/files";
        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    @GetMapping("/download/{blobName}")
    public CompletableFuture<ResponseEntity<InputStreamResource>> streamFile(@PathVariable String blobName) {
        return CompletableFuture.supplyAsync(() -> {
            InputStreamResource fileContent = azureStorageService.retrieve(blobName);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("video/mp4"))
                    .body(fileContent);
        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    @GetMapping("/video-player")
    public String showVideoPlayer(@RequestParam String filename, Model model) {
        model.addAttribute("filename", filename);
        return "video-player";
    }
}
