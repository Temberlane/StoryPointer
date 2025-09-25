package com.example.storypointer.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PredictRequest(
        @NotBlank String videoId,
        String query) {
}
