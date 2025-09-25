package com.example.storypointer.ml;

import java.util.Locale;

public class MockInferenceRunner implements InferenceRunner {

    private final String modelVersion;

    public MockInferenceRunner(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    @Override
    public PredictionResult predict(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);
        if (normalized.contains("positive")) {
            return new PredictionResult("positive", 0.9);
        }
        if (normalized.contains("negative")) {
            return new PredictionResult("negative", 0.8);
        }
        return new PredictionResult("neutral", 0.6);
    }

    @Override
    public String modelVersion() {
        return modelVersion;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }
}
