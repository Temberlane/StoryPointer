package com.example.storypointer.api;

import com.example.storypointer.api.dto.QaRequest;
import com.example.storypointer.api.dto.QaResponse;
import com.example.storypointer.model.Transcript;
import com.example.storypointer.service.QaService;
import com.example.storypointer.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QaController {

    private final VideoService videoService;
    private final QaService qaService;

    public QaController(VideoService videoService, QaService qaService) {
        this.videoService = videoService;
        this.qaService = qaService;
    }

    @PostMapping("/qa")
    public ResponseEntity<QaResponse> answer(@Valid @RequestBody QaRequest request) {
        Transcript transcript = videoService.getTranscript(request.videoId())
                .orElseThrow(() -> new ResourceNotFoundException("Transcript not available"));
        return ResponseEntity.ok(qaService.answer(transcript, request.question()));
    }
}
