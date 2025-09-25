package com.example.storypointer.service;

import com.example.storypointer.api.dto.HighlightDto;
import com.example.storypointer.model.Transcript;
import com.example.storypointer.model.TranscriptSegment;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AugmentationService {

    private static final int MAX_HIGHLIGHTS = 3;
    private final JaccardSimilarity similarity = new JaccardSimilarity();

    public List<HighlightDto> highlightsForPrediction(Transcript transcript, String label, String query) {
        String focus = (query != null && !query.isBlank()) ? query : label;
        return rankSegments(transcript, focus);
    }

    public List<HighlightDto> highlightsForQuestion(Transcript transcript, String question) {
        return rankSegments(transcript, question);
    }

    private List<HighlightDto> rankSegments(Transcript transcript, String focus) {
        if (focus == null || focus.isBlank()) {
            focus = transcript.segments().stream().map(TranscriptSegment::text).limit(1).collect(Collectors.joining(" "));
        }
        final String normalizedFocus = focus.toLowerCase(Locale.ROOT);
        return transcript.segments().stream()
                .map(segment -> new SegmentScore(segment, scoreSegment(segment, normalizedFocus)))
                .sorted(Comparator.comparing(SegmentScore::score).reversed())
                .limit(MAX_HIGHLIGHTS)
                .map(seg -> new HighlightDto(seg.segment().text(), seg.segment().start(), seg.segment().end()))
                .collect(Collectors.toList());
    }

    private double scoreSegment(TranscriptSegment segment, String focus) {
        String normalized = segment.text().toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            return 0.0;
        }
        return similarity.apply(normalized, focus);
    }

    private record SegmentScore(TranscriptSegment segment, double score) {
    }
}
