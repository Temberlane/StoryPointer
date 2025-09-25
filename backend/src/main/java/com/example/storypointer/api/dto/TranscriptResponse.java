package com.example.storypointer.api.dto;

import java.util.List;

public record TranscriptResponse(
        String videoId,
        String language,
        List<TranscriptSegmentDto> segments,
        double durationSec,
        boolean diarization) {
}
