package com.example.test_video_service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class AzureStorageService {
    private final BlobContainerClient blobContainerClient;
    private final ReentrantLock lock = new ReentrantLock();

    public AzureStorageService(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    public void save(MultipartFile file) {
        var blobName = file.getOriginalFilename();
        lock.lock();
        try (InputStream inputStream = file.getInputStream()){
            var blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.upload(inputStream, true); //idempotent
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public List<String> listAllFiles() {
        lock.lock();
        try {
            return blobContainerClient.listBlobs()
                    .stream()
                    .map(BlobItem::getName)
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }

    }

//    public String getBlobUrl(String blobName) {
//        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
//        BlobSasPermission blobSasPermission = new BlobSasPermission().setReadPermission(true);
//        BlobServiceSasSignatureValues serviceSasValues = new BlobServiceSasSignatureValues(expiryTime, blobSasPermission);
//        return "http://127.0.0.1:10000/devstoreaccount1/" + blobContainerClient.getBlobContainerName() + "/" + blobName + "?" + blobContainerClient.generateSas(serviceSasValues);
//    }

    public InputStreamResource retrieve(String blobName) {
        lock.lock();
        try {
            var blobClient = getBlobClient(blobName);
            var inputStream = blobClient.downloadContent().toStream();
            return new InputStreamResource(inputStream);
        } finally {
            lock.unlock();
        }
    }

    public void delete(String blobName) {
        lock.lock();
        try {
            var blobClient = getBlobClient(blobName);
            blobClient.delete();
        } finally {
            lock.unlock();
        }
    }

    private BlobClient getBlobClient(String blobName) {
        var blobClient = blobContainerClient.getBlobClient(blobName);
        if (Boolean.FALSE.equals(blobClient.exists())) {
            throw new IllegalArgumentException("No blob exists with given name");
        }
        return blobClient;
    }
}
