package com.example.storypointer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Storage storage = new Storage();
    private final Prediction prediction = new Prediction();
    private final Transcription transcription = new Transcription();

    public Storage getStorage() {
        return storage;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public Transcription getTranscription() {
        return transcription;
    }

    public static class Storage {
        private String blobConnection;
        private String videoContainer = "videos";
        private String transcriptContainer = "transcripts";
        private boolean inMemoryFallback = false;

        public String getBlobConnection() {
            return blobConnection;
        }

        public void setBlobConnection(String blobConnection) {
            this.blobConnection = blobConnection;
        }

        public String getVideoContainer() {
            return videoContainer;
        }

        public void setVideoContainer(String videoContainer) {
            this.videoContainer = videoContainer;
        }

        public String getTranscriptContainer() {
            return transcriptContainer;
        }

        public void setTranscriptContainer(String transcriptContainer) {
            this.transcriptContainer = transcriptContainer;
        }

        public boolean isInMemoryFallback() {
            return inMemoryFallback;
        }

        public void setInMemoryFallback(boolean inMemoryFallback) {
            this.inMemoryFallback = inMemoryFallback;
        }
    }

    public static class Prediction {
        private String modelPath = "model/model.onnx";
        private boolean mock = false;
        private String modelVersion = "unknown";

        public String getModelPath() {
            return modelPath;
        }

        public void setModelPath(String modelPath) {
            this.modelPath = modelPath;
        }

        public boolean isMock() {
            return mock;
        }

        public void setMock(boolean mock) {
            this.mock = mock;
        }

        public String getModelVersion() {
            return modelVersion;
        }

        public void setModelVersion(String modelVersion) {
            this.modelVersion = modelVersion;
        }
    }

    public static class Transcription {
        private String implementation = "mock";
        private boolean diarization = true;

        public String getImplementation() {
            return implementation;
        }

        public void setImplementation(String implementation) {
            this.implementation = implementation;
        }

        public boolean isDiarization() {
            return diarization;
        }

        public void setDiarization(boolean diarization) {
            this.diarization = diarization;
        }
    }
}
