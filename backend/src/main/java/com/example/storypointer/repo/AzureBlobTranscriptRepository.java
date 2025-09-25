package com.example.storypointer.repo;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.example.storypointer.model.Transcript;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

public class AzureBlobTranscriptRepository implements TranscriptRepository {

    private final BlobContainerClient containerClient;
    private final ObjectMapper objectMapper;

    public AzureBlobTranscriptRepository(String connectionString, String containerName, ObjectMapper objectMapper) {
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
        if (!this.containerClient.exists()) {
            this.containerClient.create();
        }
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Transcript transcript) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(transcript.videoId() + ".json");
        byte[] data = objectMapper.writeValueAsBytes(transcript);
        blobClient.upload(BinaryData.fromBytes(data), true);
        blobClient.setHttpHeaders(new com.azure.storage.blob.models.BlobHttpHeaders().setContentType("application/json"));
    }

    @Override
    public Optional<Transcript> findByVideoId(String videoId) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(videoId + ".json");
        if (!blobClient.exists()) {
            return Optional.empty();
        }
        byte[] content = blobClient.downloadContent().toBytes();
        return Optional.of(objectMapper.readValue(content, Transcript.class));
    }
}
