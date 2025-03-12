package com.example.test_video_service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class AzureStorageServiceTest {

    @Autowired
    private AzureStorageService storageService;

    private static final String AZURITE_IMAGE = "mcr.microsoft.com/azure-storage/azurite";
    private static final GenericContainer<?> AZURITE_CONTAINER = new GenericContainer<>(AZURITE_IMAGE)
            .withExposedPorts(10000);

    private static BlobContainerClient blobContainerClient;
    private static String CONNECTION_STRING;
    private static final String CONTAINER_NAME = RandomString.make().toLowerCase();

    @BeforeAll
    static void setup() {
        AZURITE_CONTAINER.start();

        Integer mappedPort = AZURITE_CONTAINER.getMappedPort(10000);
        CONNECTION_STRING =
                "DefaultEndpointsProtocol=http;"
                        + "AccountName=devstoreaccount1;"
                        + "AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;"
                        + "BlobEndpoint=http://127.0.0.1:" + mappedPort + "/devstoreaccount1;"; // Use mapped port

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(CONNECTION_STRING)
                .buildClient();

        blobContainerClient = blobServiceClient.createBlobContainer(CONTAINER_NAME);
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("de.rieckpil.azure.blob-storage.container-name", () -> CONTAINER_NAME);
        registry.add("de.rieckpil.azure.blob-storage.connection-string", () -> CONNECTION_STRING);
    }

    @Test
    void shouldSaveBlobSuccessfullyToContainer() throws IOException {
        String blobName = RandomString.make() + ".txt";
        String blobContent = RandomString.make(50);
        var blobToUpload = createTextFile(blobName, blobContent);

        storageService.save(blobToUpload);

        assertTrue(blobContainerClient.getBlobClient(blobName).exists());
    }

    @Test
    void shouldFetchSavedBlobSuccessfullyFromContainer() throws IOException {
        String blobName = RandomString.make() + ".txt";
        String blobContent = RandomString.make(50);
        var blobToUpload = createTextFile(blobName, blobContent);

        storageService.save(blobToUpload);
        var retrievedBlob = storageService.retrieve(blobName);

        assertEquals(blobContent, new String(retrievedBlob.getContentAsByteArray()));
    }

    @Test
    void shouldDeleteBlobFromContainer() throws IOException {
        String blobName = RandomString.make() + ".txt";
        String blobContent = RandomString.make(50);
        var blobToUpload = createTextFile(blobName, blobContent);

        storageService.save(blobToUpload);
        storageService.delete(blobName);

        assertFalse(blobContainerClient.getBlobClient(blobName).exists());
    }

    public static MultipartFile createTextFile(String fileName, String content) throws IOException {
        File tempFile = File.createTempFile(fileName, ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        return new MockMultipartFile(fileName, tempFile.getName(),
                "text/plain", Files.readAllBytes(tempFile.toPath()));
    }
}
