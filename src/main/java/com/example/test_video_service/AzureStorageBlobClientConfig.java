package com.example.test_video_service;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageBlobClientConfig {

    @Value("${spring.cloud.azure.storage.connection-string}")
    private String connectionString;

    @Value("${spring.cloud.azure.storage.container-name}")
    private String containerName;

    @Bean
    public BlobServiceAsyncClient blobServiceAsyncClient() {
        System.out.println("Using connection string: " + connectionString); // Debug print
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildAsyncClient();
    }

    @Bean
    public BlobContainerAsyncClient blobContainerAsyncClient(BlobServiceAsyncClient blobServiceAsyncClient) {
        return blobServiceAsyncClient.getBlobContainerAsyncClient(containerName);
    }
}
