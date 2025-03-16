package com.example.test_video_service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobAccessPolicy;
import com.azure.storage.blob.models.BlobSignedIdentifier;
import com.azure.storage.blob.models.PublicAccessType;
import jakarta.servlet.MultipartConfigElement;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.time.OffsetDateTime;
import java.util.Collections;

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
        var containerClient = blobServiceClient.getBlobContainerClient(containerName);
        containerClient.createIfNotExists();

        BlobSignedIdentifier identifier = new BlobSignedIdentifier()
                .setId(containerName)
                .setAccessPolicy(new BlobAccessPolicy()
                        .setStartsOn(OffsetDateTime.now())
                        .setExpiresOn(OffsetDateTime.now().plusDays(1))
                        .setPermissions("racwdl"));

        try {
            containerClient.setAccessPolicy(PublicAccessType.CONTAINER, Collections.singletonList(identifier));
            System.out.println("Set Access Policy to 'Public read access for blobs only'.");
        } catch (UnsupportedOperationException err) {
            System.out.printf("Set Access Policy failed because: %s\n", err);
        }
        return containerClient;
    }



    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(100000000L));
        factory.setMaxRequestSize(DataSize.ofBytes(100000000L));
        return factory.createMultipartConfig();
    }
}
