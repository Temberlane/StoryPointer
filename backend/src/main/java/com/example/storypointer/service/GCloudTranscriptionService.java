package com.example.storypointer.service;

import com.example.storypointer.model.Transcript;
import com.example.storypointer.model.TranscriptSegment;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeakerDiarizationConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "app.transcription.implementation", havingValue = "gcloud")
public class GCloudTranscriptionService implements TranscriptionService {

    private final SpeechClient speechClient;

    public GCloudTranscriptionService() throws IOException {
        String jsonCredentials = System.getenv("GCLOUD_KEY_JSON");
        SpeechSettings.Builder builder = SpeechSettings.newBuilder();
        if (jsonCredentials != null && !jsonCredentials.isBlank()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(jsonCredentials.getBytes(StandardCharsets.UTF_8)));
            builder.setCredentialsProvider(FixedCredentialsProvider.create(credentials));
        }
        this.speechClient = SpeechClient.create(builder.build());
    }

    @Override
    public Transcript transcribe(String videoId, String videoLocation) throws Exception {
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setLanguageCode("en-US")
                .setEnableWordTimeOffsets(true)
                .setDiarizationConfig(SpeakerDiarizationConfig.newBuilder().setEnableSpeakerDiarization(true).build())
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(videoLocation).build();
        List<TranscriptSegment> segments = new ArrayList<>();
        List<SpeechRecognitionResult> results = speechClient.recognize(config, audio).getResultsList();
        for (SpeechRecognitionResult result : results) {
            SpeechRecognitionAlternative alternative = result.getAlternatives(0);
            double start = alternative.getWordsCount() > 0 ? alternative.getWords(0).getStartTime().getSeconds() + alternative.getWords(0).getStartTime().getNanos() / 1e9 : 0;
            double end = alternative.getWordsCount() > 0 ? alternative.getWords(alternative.getWordsCount() - 1).getEndTime().getSeconds() + alternative.getWords(alternative.getWordsCount() - 1).getEndTime().getNanos() / 1e9 : 0;
            String speaker = alternative.getWordsCount() > 0 && alternative.getWords(0).getSpeakerTag() > 0
                    ? "S" + alternative.getWords(0).getSpeakerTag()
                    : "S1";
            segments.add(new TranscriptSegment(start, end, alternative.getTranscript(), speaker));
        }
        double duration = segments.stream().mapToDouble(TranscriptSegment::end).max().orElse(0);
        return new Transcript(videoId, "en", segments, duration, true);
    }

    @Override
    public boolean isReady() {
        return speechClient != null;
    }

    @PreDestroy
    public void close() {
        speechClient.close();
    }
}
