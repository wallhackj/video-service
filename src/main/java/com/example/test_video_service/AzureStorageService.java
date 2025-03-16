package com.example.test_video_service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> listAllFiles() {
        return blobContainerClient.listBlobs().stream().map(BlobItem::getName).collect(Collectors.toList());
    }

//    public String getBlobUrl(String blobName) {
//        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
//        BlobSasPermission blobSasPermission = new BlobSasPermission().setReadPermission(true);
//        BlobServiceSasSignatureValues serviceSasValues = new BlobServiceSasSignatureValues(expiryTime, blobSasPermission);
//        return "http://127.0.0.1:10000/devstoreaccount1/" + blobContainerClient.getBlobContainerName() + "/" + blobName + "?" + blobContainerClient.generateSas(serviceSasValues);
//    }

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
