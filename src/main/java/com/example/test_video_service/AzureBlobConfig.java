package com.example.test_video_service;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfig {
    @Autowired
    private AzureConfigurationProperties azureConfigurationProperties;

    @Bean
    public BlobServiceAsyncClient blobServiceAsyncClient() {
        return new BlobServiceClientBuilder()
                .endpoint(azureConfigurationProperties.endpoint())
                .sasToken(azureConfigurationProperties.accountKey())
                .buildAsyncClient();
    }

    @Bean
    public BlobContainerAsyncClient blobContainerAsyncClient() {
        return blobServiceAsyncClient().getBlobContainerAsyncClient(azureConfigurationProperties.containerName());
    }
}
