package com.example.storypointer.api.dto;

import jakarta.validation.constraints.NotBlank;

public record VideoUploadUrlRequest(@NotBlank String sourceUrl) {
}
