package com.example.storypointer.api.dto;

import java.util.List;

public record QaResponse(
        String answer,
        List<HighlightDto> citations) {
}
