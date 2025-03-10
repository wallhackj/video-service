package com.example.test_video_service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AzureStorageService {
    private final BlobContainerClient blobContainerClient;

    public AzureStorageService(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    public void save(MultipartFile file) throws IOException {
        var blobName = file.getOriginalFilename();
        var blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.upload(file.getInputStream());
    }

    public String getBlobUrl(String blobName) {
        return "https://devstoreaccount1.blob.core.windows.net/" + blobContainerClient.getBlobContainerName() + "/" + blobName;
    }

    public InputStreamResource retrieve(String blobName) {
        var blobClient = getBlobClient(blobName);
        var inputStream = blobClient.downloadContent().toStream();
        return new InputStreamResource(inputStream);
    }

    public void delete(String blobName) {
        var blobClient = getBlobClient(blobName);
        blobClient.delete();
    }

    private BlobClient getBlobClient(String blobName) {
        var blobClient = blobContainerClient.getBlobClient(blobName);
        if (Boolean.FALSE.equals(blobClient.exists())) {
            throw new IllegalArgumentException("No blob exists with given name");
        }
        return blobClient;
    }
}
