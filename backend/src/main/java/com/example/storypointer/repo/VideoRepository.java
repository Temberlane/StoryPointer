package com.example.storypointer.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface VideoRepository {

    void save(String videoId, InputStream data, long length, String contentType) throws IOException;

    void saveRemote(String videoId, String sourceUrl) throws IOException;

    Optional<String> getLocation(String videoId);
}
