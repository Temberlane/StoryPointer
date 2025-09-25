package com.example.storypointer.ml;

public interface InferenceRunner {
    PredictionResult predict(String text);

    String modelVersion();

    boolean isLoaded();
}
