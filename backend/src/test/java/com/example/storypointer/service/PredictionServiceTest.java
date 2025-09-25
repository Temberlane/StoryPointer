package com.example.storypointer.service;

import com.example.storypointer.ml.FeaturePreprocessor;
import com.example.storypointer.ml.InferenceRunner;
import com.example.storypointer.ml.PredictionResult;
import com.example.storypointer.model.Transcript;
import com.example.storypointer.model.TranscriptSegment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PredictionServiceTest {

    private InferenceRunner inferenceRunner;
    private PredictionService predictionService;

    @BeforeEach
    void setup() {
        inferenceRunner = Mockito.mock(InferenceRunner.class);
        predictionService = new PredictionService(inferenceRunner, new FeaturePreprocessor());
    }

    @Test
    void delegatesToInferenceRunner() {
        Transcript transcript = new Transcript("vid", "en", List.of(
                new TranscriptSegment(0, 1, "positive words", "S1")
        ), 1, true);
        when(inferenceRunner.predict(Mockito.anyString())).thenReturn(new PredictionResult("positive", 0.9));
        when(inferenceRunner.modelVersion()).thenReturn("test");
        when(inferenceRunner.isLoaded()).thenReturn(true);

        PredictionResult result = predictionService.predict(transcript, null);

        assertThat(result.label()).isEqualTo("positive");
        assertThat(predictionService.modelVersion()).isEqualTo("test");
        assertThat(predictionService.isReady()).isTrue();
    }
}
