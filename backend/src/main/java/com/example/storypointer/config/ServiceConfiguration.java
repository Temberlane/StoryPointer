package com.example.storypointer.config;

import com.example.storypointer.ml.FeaturePreprocessor;
import com.example.storypointer.ml.InferenceRunner;
import com.example.storypointer.ml.MockInferenceRunner;
import com.example.storypointer.ml.OnnxInferenceRunner;
import com.example.storypointer.repo.AzureBlobTranscriptRepository;
import com.example.storypointer.repo.AzureBlobVideoRepository;
import com.example.storypointer.repo.InMemoryTranscriptRepository;
import com.example.storypointer.repo.InMemoryVideoRepository;
import com.example.storypointer.repo.TranscriptRepository;
import com.example.storypointer.repo.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class ServiceConfiguration {

    @Bean
    public VideoRepository videoRepository(AppProperties properties) {
        if (properties.getStorage().isInMemoryFallback() || properties.getStorage().getBlobConnection() == null || properties.getStorage().getBlobConnection().isBlank()) {
            return new InMemoryVideoRepository();
        }
        return new AzureBlobVideoRepository(properties.getStorage().getBlobConnection(), properties.getStorage().getVideoContainer());
    }

    @Bean
    public TranscriptRepository transcriptRepository(AppProperties properties, ObjectMapper objectMapper) {
        if (properties.getStorage().isInMemoryFallback() || properties.getStorage().getBlobConnection() == null || properties.getStorage().getBlobConnection().isBlank()) {
            return new InMemoryTranscriptRepository();
        }
        return new AzureBlobTranscriptRepository(properties.getStorage().getBlobConnection(), properties.getStorage().getTranscriptContainer(), objectMapper);
    }

    @Bean
    public InferenceRunner inferenceRunner(AppProperties properties) {
        if (properties.getPrediction().isMock()) {
            return new MockInferenceRunner(properties.getPrediction().getModelVersion());
        }
        return new OnnxInferenceRunner(Path.of(properties.getPrediction().getModelPath()), properties.getPrediction().getModelVersion());
    }

    @Bean
    public FeaturePreprocessor featurePreprocessor() {
        return new FeaturePreprocessor();
    }
}
