package com.example.storypointer.api;

import com.example.storypointer.service.PredictionService;
import com.example.storypointer.service.TranscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final PredictionService predictionService;
    private final TranscriptionService transcriptionService;

    public HealthController(PredictionService predictionService, TranscriptionService transcriptionService) {
        this.predictionService = predictionService;
        this.transcriptionService = transcriptionService;
    }

    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/readyz")
    public ResponseEntity<Map<String, Object>> ready() {
        boolean modelReady = predictionService.isReady();
        boolean transcriptionReady = transcriptionService.isReady();
        if (!modelReady || !transcriptionReady) {
            return ResponseEntity.status(503).body(Map.of(
                    "status", "not_ready",
                    "modelReady", modelReady,
                    "transcriptionReady", transcriptionReady
            ));
        }
        return ResponseEntity.ok(Map.of(
                "status", "ready",
                "modelReady", true,
                "transcriptionReady", true
        ));
    }
}
