package com.example.storypointer.service;

import com.example.storypointer.model.Transcript;
import com.example.storypointer.model.VideoMetadata;
import com.example.storypointer.model.VideoStatus;
import com.example.storypointer.repo.TranscriptRepository;
import com.example.storypointer.repo.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VideoService {

    private static final Logger log = LoggerFactory.getLogger(VideoService.class);

    private final VideoRepository videoRepository;
    private final TranscriptRepository transcriptRepository;
    private final TranscriptionService transcriptionService;
    private final Map<String, VideoMetadata> metadataStore = new ConcurrentHashMap<>();

    public VideoService(VideoRepository videoRepository,
                        TranscriptRepository transcriptRepository,
                        TranscriptionService transcriptionService) {
        this.videoRepository = videoRepository;
        this.transcriptRepository = transcriptRepository;
        this.transcriptionService = transcriptionService;
    }

    public VideoMetadata upload(MultipartFile file) throws IOException {
        String videoId = UUID.randomUUID().toString();
        try (InputStream is = file.getInputStream()) {
            videoRepository.save(videoId, is, file.getSize(), file.getContentType());
        }
        VideoMetadata metadata = new VideoMetadata(videoId, VideoStatus.UPLOADED, null, videoRepository.getLocation(videoId).orElse(""));
        metadataStore.put(videoId, metadata);
        triggerTranscription(metadata);
        return metadata;
    }

    public VideoMetadata uploadFromUrl(String sourceUrl) throws IOException {
        String videoId = UUID.randomUUID().toString();
        videoRepository.saveRemote(videoId, sourceUrl);
        VideoMetadata metadata = new VideoMetadata(videoId, VideoStatus.UPLOADED, null, sourceUrl);
        metadataStore.put(videoId, metadata);
        triggerTranscription(metadata);
        return metadata;
    }

    public Optional<VideoMetadata> getMetadata(String videoId) {
        return Optional.ofNullable(metadataStore.get(videoId));
    }

    public Optional<Transcript> getTranscript(String videoId) {
        try {
            return transcriptRepository.findByVideoId(videoId);
        } catch (IOException e) {
            log.error("Failed to fetch transcript for {}", videoId, e);
            return Optional.empty();
        }
    }

    private void triggerTranscription(VideoMetadata metadata) {
        CompletableFuture.runAsync(() -> {
            metadata.setStatus(VideoStatus.TRANSCRIBING);
            try {
                Transcript transcript = transcriptionService.transcribe(metadata.getVideoId(), metadata.getLocation());
                transcriptRepository.save(transcript);
                metadata.setStatus(VideoStatus.READY);
            } catch (Exception e) {
                log.error("Transcription failed for {}", metadata.getVideoId(), e);
                metadata.setStatus(VideoStatus.ERROR);
                metadata.setMessage(e.getMessage());
            }
        });
    }
}
