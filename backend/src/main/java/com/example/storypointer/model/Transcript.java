package com.example.storypointer.model;

import java.util.List;

public record Transcript(
        String videoId,
        String language,
        List<TranscriptSegment> segments,
        double durationSec,
        boolean diarization) {
}
