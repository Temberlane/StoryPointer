package com.example.storypointer.repo;

import com.example.storypointer.model.Transcript;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTranscriptRepository implements TranscriptRepository {

    private final Map<String, Transcript> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Transcript transcript) {
        storage.put(transcript.videoId(), transcript);
    }

    @Override
    public Optional<Transcript> findByVideoId(String videoId) {
        return Optional.ofNullable(storage.get(videoId));
    }
}
