package com.example.storypointer.api;

import com.example.storypointer.api.dto.HighlightDto;
import com.example.storypointer.api.dto.PredictRequest;
import com.example.storypointer.api.dto.PredictResponse;
import com.example.storypointer.ml.PredictionResult;
import com.example.storypointer.model.Transcript;
import com.example.storypointer.service.AugmentationService;
import com.example.storypointer.service.PredictionService;
import com.example.storypointer.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PredictionController {

    private final PredictionService predictionService;
    private final VideoService videoService;
    private final AugmentationService augmentationService;

    public PredictionController(PredictionService predictionService,
                                VideoService videoService,
                                AugmentationService augmentationService) {
        this.predictionService = predictionService;
        this.videoService = videoService;
        this.augmentationService = augmentationService;
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictResponse> predict(@Valid @RequestBody PredictRequest request) {
        Transcript transcript = videoService.getTranscript(request.videoId())
                .orElseThrow(() -> new ResourceNotFoundException("Transcript not available"));
        PredictionResult result = predictionService.predict(transcript, request.query());
        List<HighlightDto> highlights = augmentationService.highlightsForPrediction(transcript, result.label(), request.query());
        List<String> features = new ArrayList<>();
        features.add("transcript_text");
        if (request.query() != null && !request.query().isBlank()) {
            features.add("query");
        }
        PredictResponse response = new PredictResponse(
                request.videoId(),
                predictionService.modelVersion(),
                result.label(),
                result.score(),
                features,
                highlights
        );
        return ResponseEntity.ok(response);
    }
}
