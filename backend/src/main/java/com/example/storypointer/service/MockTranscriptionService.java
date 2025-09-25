package com.example.storypointer.service;

import com.example.storypointer.model.Transcript;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalOnProperty(name = "app.transcription.implementation", havingValue = "mock", matchIfMissing = true)
public class MockTranscriptionService implements TranscriptionService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Transcript transcribe(String videoId, String videoLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource("sample-transcript.json");
        Transcript transcript = objectMapper.readValue(resource.getInputStream(), Transcript.class);
        return new Transcript(videoId, transcript.language(), transcript.segments(), transcript.durationSec(), transcript.diarization());
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
