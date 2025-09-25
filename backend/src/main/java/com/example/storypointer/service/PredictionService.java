package com.example.storypointer.service;

import com.example.storypointer.ml.FeaturePreprocessor;
import com.example.storypointer.ml.InferenceRunner;
import com.example.storypointer.ml.PredictionResult;
import com.example.storypointer.model.Transcript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final InferenceRunner inferenceRunner;
    private final FeaturePreprocessor featurePreprocessor;

    public PredictionService(InferenceRunner inferenceRunner, FeaturePreprocessor featurePreprocessor) {
        this.inferenceRunner = inferenceRunner;
        this.featurePreprocessor = featurePreprocessor;
    }

    public PredictionResult predict(Transcript transcript, String query) {
        String features = featurePreprocessor.prepare(transcript, query);
        log.debug("Running inference with feature length {}", features.length());
        return inferenceRunner.predict(features);
    }

    public String modelVersion() {
        return inferenceRunner.modelVersion();
    }

    public boolean isReady() {
        return inferenceRunner.isLoaded();
    }
}
