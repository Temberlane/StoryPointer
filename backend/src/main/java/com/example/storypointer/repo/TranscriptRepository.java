package com.example.storypointer.repo;

import com.example.storypointer.model.Transcript;

import java.io.IOException;
import java.util.Optional;

public interface TranscriptRepository {

    void save(Transcript transcript) throws IOException;

    Optional<Transcript> findByVideoId(String videoId) throws IOException;
}
