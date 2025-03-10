package com.example.test_video_service;

import com.azure.storage.blob.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageBlobClientConfig {
    @NotBlank
    @Value("${spring.cloud.azure.storage.connection-string}")
    private String connectionString;

    @NotBlank
    @Value("${spring.cloud.azure.storage.container-name}")
    private String containerName;

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    @Bean
    public BlobContainerClient blobContainerClient(BlobServiceClient blobServiceClient) {
        return blobServiceClient.getBlobContainerClient(containerName);
    }
}
