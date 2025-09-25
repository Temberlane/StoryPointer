package com.example.storypointer.service;

import com.example.storypointer.model.Transcript;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MockTranscriptionServiceTest {

    private final MockTranscriptionService service = new MockTranscriptionService();

    @Test
    void loadsSampleTranscript() throws Exception {
        Transcript transcript = service.transcribe("video", "memory://video");
        assertThat(transcript.videoId()).isEqualTo("video");
        assertThat(transcript.segments()).isNotEmpty();
    }
}
