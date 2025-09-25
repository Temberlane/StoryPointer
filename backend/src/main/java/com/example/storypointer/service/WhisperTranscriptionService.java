package com.example.storypointer.service;

import com.example.storypointer.model.Transcript;
import com.example.storypointer.model.TranscriptSegment;
import com.example.storypointer.util.ProcessRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@ConditionalOnProperty(name = "app.transcription.implementation", havingValue = "whisper")
public class WhisperTranscriptionService implements TranscriptionService {

    private static final String WHISPER_BINARY = "/usr/local/bin/whisper";
    private final ProcessRunner processRunner;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WhisperTranscriptionService(ProcessRunner processRunner) {
        this.processRunner = processRunner;
    }

    @Override
    public Transcript transcribe(String videoId, String videoLocation) throws Exception {
        Path tempDir = Files.createTempDirectory("whisper-");
        List<String> command = List.of(
                WHISPER_BINARY,
                videoLocation,
                "--language", "en",
                "--output_format", "json",
                "--output_dir", tempDir.toString(),
                "--model", "base"
        );
        processRunner.run(command);
        Path jsonFile;
        try (var stream = Files.list(tempDir)) {
            jsonFile = stream.filter(path -> path.toString().endsWith(".json"))
                    .findFirst()
                    .orElseThrow(() -> new IOException("Whisper output missing"));
        }
        JsonNode root = objectMapper.readTree(jsonFile.toFile());
        List<TranscriptSegment> segments = new ArrayList<>();
        Iterator<JsonNode> iterator = root.get("segments").elements();
        while (iterator.hasNext()) {
            JsonNode node = iterator.next();
            segments.add(new TranscriptSegment(
                    node.get("start").asDouble(),
                    node.get("end").asDouble(),
                    node.get("text").asText().trim(),
                    node.has("speaker") ? node.get("speaker").asText() : "S1"
            ));
        }
        double duration = segments.isEmpty() ? 0 : segments.get(segments.size() - 1).end();
        return new Transcript(videoId, root.path("language").asText("en"), segments, duration, root.path("diarization").asBoolean(false));
    }

    @Override
    public boolean isReady() {
        return Files.exists(Path.of(WHISPER_BINARY));
    }
}
