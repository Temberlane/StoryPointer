package com.example.storypointer.api;

import com.example.storypointer.api.dto.TranscriptResponse;
import com.example.storypointer.api.dto.TranscriptSegmentDto;
import com.example.storypointer.api.dto.VideoStatusResponse;
import com.example.storypointer.api.dto.VideoUploadResponse;
import com.example.storypointer.api.dto.VideoUploadUrlRequest;
import com.example.storypointer.model.Transcript;
import com.example.storypointer.model.VideoMetadata;
import com.example.storypointer.model.VideoStatus;
import com.example.storypointer.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(value = "/videos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoUploadResponse> uploadVideo(@RequestPart("file") MultipartFile file) throws IOException {
        VideoMetadata metadata = videoService.upload(file);
        return ResponseEntity.ok(new VideoUploadResponse(metadata.getVideoId(), VideoStatus.UPLOADED.name().toLowerCase()));
    }

    @PostMapping(value = "/videos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoUploadResponse> uploadVideoFromUrl(@Valid @RequestBody VideoUploadUrlRequest request) throws IOException {
        VideoMetadata metadata = videoService.uploadFromUrl(request.sourceUrl());
        return ResponseEntity.ok(new VideoUploadResponse(metadata.getVideoId(), VideoStatus.UPLOADED.name().toLowerCase()));
    }

    @GetMapping("/videos/{videoId}/status")
    public ResponseEntity<VideoStatusResponse> status(@PathVariable String videoId) {
        VideoMetadata metadata = videoService.getMetadata(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));
        String message = metadata.getMessage();
        return ResponseEntity.ok(new VideoStatusResponse(metadata.getStatus().name().toLowerCase(), message));
    }

    @GetMapping("/videos/{videoId}/transcript")
    public ResponseEntity<TranscriptResponse> transcript(@PathVariable String videoId) {
        VideoMetadata metadata = videoService.getMetadata(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));
        if (metadata.getStatus() != VideoStatus.READY) {
            throw new ResourceNotFoundException("Transcript not ready");
        }
        Transcript transcript = videoService.getTranscript(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Transcript not ready"));
        TranscriptResponse response = new TranscriptResponse(
                transcript.videoId(),
                transcript.language(),
                transcript.segments().stream().map(seg -> new TranscriptSegmentDto(seg.start(), seg.end(), seg.text(), seg.speaker())).collect(Collectors.toList()),
                transcript.durationSec(),
                transcript.diarization());
        return ResponseEntity.ok(response);
    }
}
