package com.example.test_video_service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.cloud.azure.storage.blob")
public record AzureConfigurationProperties (@NotBlank String accountName,
                                            @NotBlank String accountKey,
                                            @NotBlank String endpoint,
                                            @NotBlank String containerName){
}
