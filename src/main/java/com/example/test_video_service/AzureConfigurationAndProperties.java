package com.example.test_video_service;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.cloud.azure.storage")
public class AzureConfigurationAndProperties {
    @NotBlank
    @Value("connection-string")
    private String connectionString;

    @NotBlank
    @Value("containerName")
    private String containerName;

    @Bean
    public BlobServiceAsyncClient blobServiceAsyncClient() {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildAsyncClient();
    }

    @Bean
    public BlobContainerAsyncClient blobContainerAsyncClient() {
        return blobServiceAsyncClient().getBlobContainerAsyncClient(containerName);
    }
}
