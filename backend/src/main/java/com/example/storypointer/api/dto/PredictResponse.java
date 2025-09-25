package com.example.storypointer.api.dto;

import java.util.List;

public record PredictResponse(
        String videoId,
        String modelVersion,
        String prediction,
        double score,
        List<String> featuresUsed,
        List<HighlightDto> highlights) {
}
