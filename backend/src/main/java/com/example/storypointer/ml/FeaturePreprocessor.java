package com.example.storypointer.ml;

import com.example.storypointer.model.Transcript;

public class FeaturePreprocessor {

    public String prepare(Transcript transcript, String query) {
        StringBuilder builder = new StringBuilder();
        transcript.segments().forEach(segment -> builder.append(segment.text()).append(' '));
        if (query != null && !query.isBlank()) {
            builder.append("\nQUERY: ").append(query);
        }
        return builder.toString().trim();
    }
}
