package com.example.storypointer.service;

import com.example.storypointer.model.Transcript;

public interface TranscriptionService {
    Transcript transcribe(String videoId, String videoLocation) throws Exception;

    boolean isReady();
}
