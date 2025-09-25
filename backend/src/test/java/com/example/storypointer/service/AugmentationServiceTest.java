package com.example.storypointer.service;

import com.example.storypointer.api.dto.HighlightDto;
import com.example.storypointer.model.Transcript;
import com.example.storypointer.model.TranscriptSegment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AugmentationServiceTest {

    private final AugmentationService augmentationService = new AugmentationService();

    @Test
    void selectsSegmentsMostRelevantToQuery() {
        Transcript transcript = new Transcript("vid", "en", List.of(
                new TranscriptSegment(0, 4, "This is a positive story about success.", "S1"),
                new TranscriptSegment(4, 8, "Another neutral topic is discussed here.", "S1"),
                new TranscriptSegment(8, 12, "We close with positive highlights.", "S2")
        ), 12, true);

        List<HighlightDto> highlights = augmentationService.highlightsForPrediction(transcript, "positive", "positive success");

        assertThat(highlights).hasSize(3);
        assertThat(highlights.get(0).text()).contains("positive");
    }
}
