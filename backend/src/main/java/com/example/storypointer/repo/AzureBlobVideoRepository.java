package com.example.storypointer.repo;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class AzureBlobVideoRepository implements VideoRepository {

    private final BlobContainerClient containerClient;

    public AzureBlobVideoRepository(String connectionString, String containerName) {
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
        if (!this.containerClient.exists()) {
            this.containerClient.create();
        }
    }

    @Override
    public void save(String videoId, InputStream data, long length, String contentType) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(videoId);
        blobClient.upload(data, length, true);
        blobClient.setHttpHeaders(new com.azure.storage.blob.models.BlobHttpHeaders().setContentType(contentType));
    }

    @Override
    public void saveRemote(String videoId, String sourceUrl) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(videoId);
        blobClient.upload(BinaryData.fromString(sourceUrl), true);
        blobClient.setHttpHeaders(new com.azure.storage.blob.models.BlobHttpHeaders().setContentType("text/plain"));
    }

    @Override
    public Optional<String> getLocation(String videoId) {
        BlobClient blobClient = containerClient.getBlobClient(videoId);
        if (!blobClient.exists()) {
            return Optional.empty();
        }
        return Optional.of(blobClient.getBlobUrl());
    }
}
