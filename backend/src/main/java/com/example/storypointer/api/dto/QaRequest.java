package com.example.storypointer.api.dto;

import jakarta.validation.constraints.NotBlank;

public record QaRequest(
        @NotBlank String videoId,
        @NotBlank String question) {
}
