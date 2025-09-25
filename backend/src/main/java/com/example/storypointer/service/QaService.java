package com.example.storypointer.service;

import com.example.storypointer.api.dto.HighlightDto;
import com.example.storypointer.api.dto.QaResponse;
import com.example.storypointer.model.Transcript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QaService {

    private final AugmentationService augmentationService;

    public QaService(AugmentationService augmentationService) {
        this.augmentationService = augmentationService;
    }

    public QaResponse answer(Transcript transcript, String question) {
        List<HighlightDto> citations = augmentationService.highlightsForQuestion(transcript, question);
        String answer = citations.stream().map(HighlightDto::text).collect(Collectors.joining(" "));
        if (answer.isBlank()) {
            answer = "Insufficient transcript context.";
        }
        return new QaResponse(answer, citations);
    }
}
