package com.example.storypointer.model;

public class VideoMetadata {
    private final String videoId;
    private VideoStatus status;
    private String message;
    private final String location;

    public VideoMetadata(String videoId, VideoStatus status, String message, String location) {
        this.videoId = videoId;
        this.status = status;
        this.message = message;
        this.location = location;
    }

    public String getVideoId() {
        return videoId;
    }

    public VideoStatus getStatus() {
        return status;
    }

    public void setStatus(VideoStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }
}
