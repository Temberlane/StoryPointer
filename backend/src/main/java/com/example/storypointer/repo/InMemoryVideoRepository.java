package com.example.storypointer.repo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVideoRepository implements VideoRepository {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();
    private final Map<String, String> locations = new ConcurrentHashMap<>();

    @Override
    public void save(String videoId, InputStream data, long length, String contentType) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data.transferTo(buffer);
        storage.put(videoId, buffer.toByteArray());
        locations.put(videoId, "memory://" + videoId);
    }

    @Override
    public void saveRemote(String videoId, String sourceUrl) {
        storage.put(videoId, sourceUrl.getBytes(StandardCharsets.UTF_8));
        locations.put(videoId, sourceUrl);
    }

    @Override
    public Optional<String> getLocation(String videoId) {
        return Optional.ofNullable(locations.get(videoId));
    }

    public Optional<byte[]> getContent(String videoId) {
        return Optional.ofNullable(storage.get(videoId));
    }
}
