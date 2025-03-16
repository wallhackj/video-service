package com.example.test_video_service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {

    private final AzureStorageService azureStorageService;

    public FileController(AzureStorageService azureStorageService) {
        this.azureStorageService = azureStorageService;
    }

    @GetMapping
    public String listFiles(Model model) {
        List<String> files = azureStorageService.listAllFiles();
        model.addAttribute("files", files);
        return "file-list";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        azureStorageService.save(file);
        return "redirect:/files";
    }

    @GetMapping("/delete/{blobName}")
    public String deleteFile(@PathVariable String blobName) {
        azureStorageService.delete(blobName);
        return "redirect:/files";
    }

    @GetMapping("/download/{blobName}")
    public ResponseEntity<InputStreamResource> streamFile(@PathVariable String blobName) {
        InputStreamResource fileContent = azureStorageService.retrieve(blobName);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(fileContent);
    }

    @GetMapping("/video-player")
    public String showVideoPlayer(@RequestParam String filename, Model model) {
        model.addAttribute("filename", filename);
        return "video-player";
    }
}
